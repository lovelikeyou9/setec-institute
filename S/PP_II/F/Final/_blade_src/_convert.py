"""Dev tool: Blade (_blade_src) -> Jinja2 (templates). Run:  python _blade_src/_convert.py"""
import os
import re
import sys

SRC = os.path.dirname(os.path.abspath(__file__))
DST = os.path.join(os.path.dirname(SRC), "templates")

# ----------------------------------------------------------------------------
# PHP expression -> Jinja expression
# ----------------------------------------------------------------------------
TOKEN = re.compile(r'''
 (?P<ws>\s+)
|(?P<cast>\((?:int|float|string|bool|array)\))
|(?P<str>'(?:\\.|[^'\\])*'|"(?:\\.|[^"\\])*")
|(?P<num>\d+(?:\.\d+)?)
|(?P<var>\$[A-Za-z_]\w*)
|(?P<ident>\\?[A-Za-z_][\w\\]*(?:::[A-Za-z_]\w*)?)
|(?P<op>\?->|->|===|!==|==|!=|<=|>=|\+\+|--|&&|\|\||\?\?|=>|[-+*/%<>!?:.,()\[\]=])
''', re.X)


def tokenize(src):
    pos, out = 0, []
    while pos < len(src):
        m = TOKEN.match(src, pos)
        if not m:
            raise ValueError(f"cannot tokenize {src[pos:pos+30]!r} in {src!r}")
        pos = m.end()
        kind = m.lastgroup
        if kind == "ws":
            continue
        out.append((kind, m.group(kind)))
    out.append(("eof", ""))
    return out


class Parser:
    def __init__(self, src):
        self.src = src
        self.toks = tokenize(src)
        self.i = 0

    def peek(self, k=0):
        return self.toks[min(self.i + k, len(self.toks) - 1)]

    def next(self):
        t = self.toks[self.i]
        self.i += 1
        return t

    def accept(self, val):
        if self.peek()[1] == val and self.peek()[0] in ("op", "ident"):
            self.i += 1
            return True
        return False

    def expect(self, val):
        if not self.accept(val):
            raise ValueError(f"expected {val!r} got {self.peek()} in {self.src!r}")

    # --- grammar
    def parse(self):
        e = self.ternary()
        if self.peek()[0] != "eof":
            raise ValueError(f"trailing tokens {self.peek()} in {self.src!r}")
        return e

    def ternary(self):
        c = self.coalesce()
        if self.peek() == ("op", "?"):
            self.next()
            if self.peek() == ("op", ":"):
                self.next()
                b = self.ternary()
                return ("elvis", c, b)
            a = self.ternary()
            self.expect(":")
            b = self.ternary()
            return ("tern", c, a, b)
        return c

    def coalesce(self):
        a = self.or_()
        if self.peek() == ("op", "??"):
            self.next()
            b = self.coalesce()
            return ("coal", a, b)
        return a

    def _bin(self, sub, ops):
        left = sub()
        while self.peek()[0] == "op" and self.peek()[1] in ops:
            op = self.next()[1]
            left = ("bin", op, left, sub())
        return left

    def or_(self):
        return self._bin(self.and_, ("||",))

    def and_(self):
        return self._bin(self.eq, ("&&",))

    def eq(self):
        return self._bin(self.rel, ("==", "!=", "===", "!=="))

    def rel(self):
        return self._bin(self.concat, ("<", ">", "<=", ">="))

    def concat(self):
        return self._bin(self.add, (".",))

    def add(self):
        return self._bin(self.mul, ("+", "-"))

    def mul(self):
        return self._bin(self.unary, ("*", "/", "%"))

    def unary(self):
        t = self.peek()
        if t == ("op", "!"):
            self.next()
            return ("un", "not ", self.unary())
        if t == ("op", "-"):
            self.next()
            return ("un", "-", self.unary())
        if t[0] == "cast":
            self.next()
            return ("cast", t[1][1:-1], self.unary())
        return self.postfix()

    def args(self):
        out = []
        self.expect("(")
        while not self.accept(")"):
            out.append(self.ternary())
            self.accept(",")
        return out

    def postfix(self):
        e = self.primary()
        while True:
            t = self.peek()
            if t[0] == "op" and t[1] in ("->", "?->"):
                self.next()
                name = self.next()[1]
                if self.peek() == ("op", "("):
                    e = ("mcall", e, name, self.args())
                else:
                    e = ("attr", e, name)
            elif t == ("op", "["):
                self.next()
                idx = self.ternary()
                self.expect("]")
                e = ("index", e, idx)
            else:
                return e

    def primary(self):
        t = self.next()
        k, v = t
        if k == "num":
            return ("lit", v)
        if k == "str":
            return ("str", v)
        if k == "var":
            return ("var", v[1:])
        if k == "ident":
            low = v.lower()
            if low in ("true", "false", "null"):
                return ("lit", {"true": "true", "false": "false", "null": "none"}[low])
            if self.peek() == ("op", "("):
                return ("call", v, self.args())
            return ("lit", v)
        if k == "op" and v == "(":
            e = self.ternary()
            self.expect(")")
            return ("paren", e)
        if k == "op" and v == "[":
            items, is_dict = [], False
            while not self.accept("]"):
                a = self.ternary()
                if self.accept("=>"):
                    b = self.ternary()
                    items.append((a, b))
                    is_dict = True
                else:
                    items.append(a)
                self.accept(",")
            return ("dict" if is_dict else "list", items)
        raise ValueError(f"unexpected token {t} in {self.src!r}")


def jstr(raw):
    """PHP string literal -> jinja single-quoted literal."""
    q = raw[0]
    body = raw[1:-1]
    if q == '"':
        body = body.replace("\\\"", '"').replace("'", "\\'")
    return "'" + body + "'"


def emit(n):
    t = n[0]
    if t == "lit":
        return n[1]
    if t == "str":
        return jstr(n[1])
    if t == "var":
        return n[1]
    if t == "paren":
        return "(" + emit(n[1]) + ")"
    if t == "bin":
        op = {"&&": "and", "||": "or", "===": "==", "!==": "!=", ".": "~"}.get(n[1], n[1])
        return f"({emit(n[2])} {op} {emit(n[3])})"
    if t == "un":
        return f"({n[1]}{emit(n[2])})"
    if t == "cast":
        f = {"int": "int", "float": "float", "string": "string", "bool": "bool", "array": "list"}[n[1]]
        return f"({emit(n[2])}|{f})"
    if t == "tern":
        return f"({emit(n[2])} if {emit(n[1])} else {emit(n[3])})"
    if t == "elvis":
        return f"({emit(n[1])} or {emit(n[2])})"
    if t == "coal":
        a = emit(n[1])
        return f"({a} if ({a} is defined and {a} is not none) else {emit(n[2])})"
    if t == "attr":
        return f"{emit(n[1])}.{n[2]}"
    if t == "index":
        return f"{emit(n[1])}[{emit(n[2])}]"
    if t == "list":
        return "[" + ", ".join(emit(x) for x in n[1]) + "]"
    if t == "dict":
        return "{" + ", ".join(f"{emit(a)}: {emit(b)}" for a, b in n[1]) + "}"
    if t == "call":
        return emit_call(n)
    if t == "mcall":
        return emit_mcall(n)
    raise ValueError(n)


def emit_call(n):
    name, args = n[1].lstrip("\\"), n[2]
    a = [emit(x) for x in args]
    if name == "count":
        return f"({a[0]}|length)"
    if name == "in_array":
        return f"({a[0]} in {a[1]})"
    if name == "isset":
        return f"({a[0]} is defined and {a[0]} is not none)"
    if name == "Auth::user" or name == "auth":
        return "current_user"
    if name == "Auth::check":
        return "current_user.is_authenticated"
    if name == "request":
        return "req_input(" + ", ".join(a) + ")" if a else "request"
    if name == "session":
        return "flash_msg(" + ", ".join(a) + ")"
    if name == "Illuminate\\Support\\Str::limit":
        return "str_limit(" + ", ".join(a) + ")"
    if name == "url" and not a:
        return "url"
    if name == "optional":
        return a[0]
    if name == "urlencode":
        return "urlencode(" + ", ".join(a) + ")"
    return f"{name}(" + ", ".join(a) + ")"


def emit_mcall(n):
    obj, name, args = n[1], n[2], n[3]
    a = [emit(x) for x in args]
    # optional($x)->format(F)
    if name == "format":
        inner = obj[2][0] if obj[0] == "call" and obj[1] == "optional" else obj
        return f"date_fmt({emit(inner)}, {a[0]})"
    # request()->is(...) / url()->current()
    if obj[0] == "call" and obj[1] == "request" and not obj[2] and name == "is":
        return f"request_is({a[0]})"
    if obj[0] == "call" and obj[1] == "url" and not obj[2]:
        return {"current": "url_current()", "full": "url_full()"}[name]
    if obj[0] == "call" and obj[1] == "auth" and name == "user":
        return "current_user"
    o = emit(obj)
    if name == "count" and not a:
        return f"({o}|length)"
    if name == "first" and not a:
        return f"({o}|first)"
    if name == "take":
        return f"({o}|take({a[0]}))"
    if name == "links":
        return f"({o}.links()|safe)"
    if name == "isNotEmpty":
        return f"({o}|length > 0)"
    if name == "isEmpty":
        return f"({o}|length == 0)"
    if name == "contains":
        return f"({a[0]} in {o})"
    if name == "implode":
        return f"({o}|join({a[0]}))"
    return f"{o}.{name}(" + ", ".join(a) + ")"


def strip_parens(s):
    # remove one redundant outer pair when it wraps the whole expression
    if s.startswith("(") and s.endswith(")"):
        depth = 0
        for i, ch in enumerate(s):
            if ch == "(":
                depth += 1
            elif ch == ")":
                depth -= 1
                if depth == 0 and i != len(s) - 1:
                    return s
        return s[1:-1]
    return s


PRE = [
    (re.compile(r"collect\(\[\$a->street, \$a->commune, \$a->district, \$a->province, \$a->country\]\)->filter\(\)->implode\(', '\)"),
     "$a->formatted()"),
    (re.compile(r"\$fmt\("), "fmt_dt("),
    (re.compile(r"Auth::user\(\)\?->"), "Auth::user()->"),
    (re.compile(r"auth\(\)->user\(\)"), "Auth::user()"),
]


def php2j(expr):
    expr = expr.strip()
    for pat, rep in PRE:
        expr = pat.sub(rep, expr)
    ast = Parser(expr).parse()
    return strip_parens(emit(ast))


# ----------------------------------------------------------------------------
# Blade -> Jinja
# ----------------------------------------------------------------------------
MANUAL = []


def balanced(text, start):
    """text[start] == '(' -> index after matching ')', string aware."""
    depth, i, n = 0, start, len(text)
    while i < n:
        ch = text[i]
        if ch in "'\"":
            q = ch
            i += 1
            while i < n and text[i] != q:
                i += 2 if text[i] == "\\" else 1
        elif ch == "(":
            depth += 1
        elif ch == ")":
            depth -= 1
            if depth == 0:
                return i + 1
        i += 1
    raise ValueError("unbalanced parens near: " + text[start:start + 60])


def split_top(s, sep=","):
    parts, depth, cur, i = [], 0, "", 0
    while i < len(s):
        ch = s[i]
        if ch in "'\"":
            q = ch
            j = i + 1
            while j < len(s) and s[j] != q:
                j += 2 if s[j] == "\\" else 1
            cur += s[i:j + 1]
            i = j + 1
            continue
        if ch in "([{":
            depth += 1
        elif ch in ")]}":
            depth -= 1
        if ch == sep and depth == 0:
            parts.append(cur)
            cur = ""
        else:
            cur += ch
        i += 1
    if cur.strip() != "":
        parts.append(cur)
    return parts


def view_path(name):
    return name.replace(".", "/") + ".html"


def unq(s):
    s = s.strip()
    if len(s) >= 2 and s[0] == s[-1] and s[0] in "'\"":
        return s[1:-1]
    raise ValueError("expected literal " + s)


ARG_DIRS = {"if", "elseif", "foreach", "forelse", "for", "section", "yield", "extends", "include",
            "push", "stack", "hasSection", "unless", "isset", "selected", "checked", "json", "props",
            "empty"}
BARE_DIRS = {"else", "endif", "endforeach", "endforelse", "endfor", "endsection", "endpush",
             "endauth", "endguest", "auth", "guest", "endunless", "endisset", "csrf", "show", "stop"}


def conv_foreach(arg):
    m = re.match(r"^(.*)\s+as\s+(\$\w+)(?:\s*=>\s*(\$\w+))?\s*$", arg.strip(), re.S)
    if not m:
        raise ValueError("foreach: " + arg)
    src = php2j(m.group(1))
    if m.group(3):
        return f"for {m.group(2)[1:]}, {m.group(3)[1:]} in ({src})|kv"
    return f"for {m.group(2)[1:]} in ({src})"


def conv_for(arg):
    m = re.match(r"^\s*\$(\w+)\s*=\s*(-?\d+)\s*;\s*\$\1\s*(<=|>=|<|>)\s*(-?\d+)\s*;\s*\$\1(\+\+|--)\s*$", arg)
    if not m:
        raise ValueError("for: " + arg)
    var, a, op, b, step = m.groups()
    a, b = int(a), int(b)
    if step == "++":
        end = b + 1 if op == "<=" else b
        return f"for {var} in range({a}, {end})"
    end = b - 1 if op == ">=" else b
    return f"for {var} in range({a}, {end}, -1)"


def conv_php_block(code, fname):
    stmts = [s.strip() for s in split_top(code, ";") if s.strip()]
    out = []
    try:
        for s in stmts:
            m = re.match(r"^\$(\w+)\s*=\s*(.*)$", s, re.S)
            if not m or "match" in s or "fn (" in s or "foreach" in s or "if (" in s:
                raise ValueError("complex")
            out.append("{% set " + m.group(1) + " = " + php2j(m.group(2)) + " %}")
        return "".join(out)
    except Exception:
        MANUAL.append((fname, code.strip()[:80]))
        return "{# MANUAL-PHP\n" + code.replace("#}", "# }") + "\n#}"


def conv_component(name, attr_src):
    attrs = []
    for m in re.finditer(r'([:\w.-]+)(?:=(?:"([^"]*)"|\'([^\']*)\'))?', attr_src):
        key, v1, v2 = m.group(1), m.group(2), m.group(3)
        val = v1 if v1 is not None else v2
        if key.startswith(":"):
            attrs.append(f"{key[1:].replace('-', '_')}=({php2j(val)})")
        elif val is not None:
            attrs.append(f"{key.replace('-', '_')}='{val}'")
        else:
            attrs.append(f"{key.replace('-', '_')}=true")
    path = "components/" + name.replace(".", "/").replace("-", "_") + ".html"
    if attrs:
        return "{% with " + ", ".join(attrs) + " %}{% include '" + path + "' %}{% endwith %}"
    return "{% include '" + path + "' %}"


PAT = re.compile(
    r"\{\{--.*?--\}\}"
    r"|\{!!(?P<raw>.*?)!!\}"
    r"|\{\{(?P<esc>.*?)\}\}"
    r"|(?<![\w@])@(?P<dir>\w+)"
    r"|<x-(?P<comp>[\w.-]+)(?P<cattrs>(?:\s+[:\w.-]+(?:=(?:\"[^\"]*\"|'[^']*'))?)*)\s*/?>",
    re.S,
)


def convert(text, fname):
    out, pos = [], 0
    while True:
        m = PAT.search(text, pos)
        if not m:
            out.append(text[pos:])
            break
        out.append(text[pos:m.start()])
        pos = m.end()
        whole = m.group(0)
        if whole.startswith("{{--"):
            out.append("{#" + whole[4:-4].replace("#}", "# }") + "#}")
        elif m.group("raw") is not None:
            out.append("{{ " + php2j(m.group("raw")) + "|safe }}")
        elif m.group("esc") is not None:
            out.append("{{ " + php2j(m.group("esc")) + " }}")
        elif m.group("comp") is not None:
            out.append(conv_component(m.group("comp"), m.group("cattrs") or ""))
        else:
            d = m.group("dir")
            if d == "php":
                end = text.index("@endphp", pos)
                out.append(conv_php_block(text[pos:end], fname))
                pos = end + len("@endphp")
                continue
            arg = None
            if d in ARG_DIRS:
                mm = re.compile(r"[ \t]*\(").match(text, pos)
                if mm:
                    e = balanced(text, mm.end() - 1)
                    arg = text[mm.end():e - 1]
                    pos = e
            elif d not in BARE_DIRS:
                out.append(whole)  # unknown (e.g. @media in CSS)
                continue
            if d == "if":
                out.append("{% if " + php2j(arg) + " %}")
            elif d == "elseif":
                out.append("{% elif " + php2j(arg) + " %}")
            elif d == "else":
                out.append("{% else %}")
            elif d in ("endif", "endauth", "endguest", "endunless", "endisset"):
                out.append("{% endif %}")
            elif d == "unless":
                out.append("{% if not (" + php2j(arg) + ") %}")
            elif d == "isset":
                out.append("{% if " + php2j(arg) + " is defined %}")
            elif d == "auth":
                out.append("{% if current_user.is_authenticated %}")
            elif d == "guest":
                out.append("{% if not current_user.is_authenticated %}")
            elif d == "foreach":
                out.append("{% " + conv_foreach(arg) + " %}")
            elif d == "forelse":
                out.append("{% " + conv_foreach(arg) + " %}")
            elif d == "empty":
                out.append("{% else %}")
            elif d in ("endforeach", "endforelse", "endfor"):
                out.append("{% endfor %}")
            elif d == "for":
                out.append("{% " + conv_for(arg) + " %}")
            elif d == "extends":
                out.append("{% extends '" + view_path(unq(arg)) + "' %}")
            elif d == "section":
                parts = split_top(arg)
                if len(parts) == 1:
                    out.append("{% block " + unq(parts[0]) + " %}")
                else:
                    val = ",".join(parts[1:]).strip()
                    ast = Parser(val).parse()
                    if ast[0] == "str":
                        body = ast[1][1:-1].replace("{", "&#123;")
                    else:
                        body = "{{ " + php2j(val) + " }}"
                    out.append("{% block " + unq(parts[0]) + " %}" + body + "{% endblock %}")
            elif d in ("endsection", "endpush", "show", "stop"):
                out.append("{% endblock %}")
            elif d == "push":
                out.append("{% block " + unq(arg) + " %}")
            elif d == "stack":
                out.append("{% block " + unq(arg) + " %}{% endblock %}")
            elif d == "yield":
                parts = split_top(arg)
                body = unq(parts[1]) if len(parts) > 1 else ""
                out.append("{% block " + unq(parts[0]) + " %}" + body + "{% endblock %}")
            elif d == "hasSection":
                out.append("{% if false %}")
                MANUAL.append((fname, "hasSection " + arg))
            elif d == "include":
                parts = split_top(arg)
                path = view_path(unq(parts[0]))
                if len(parts) > 1:
                    ast = Parser(parts[1].strip()).parse()
                    assert ast[0] == "dict", parts[1]
                    kv = ", ".join(f"{unq(k[1])}={emit(v)}" for k, v in ast[1])
                    out.append("{% with " + kv + " %}{% include '" + path + "' %}{% endwith %}")
                else:
                    out.append("{% include '" + path + "' %}")
            elif d == "selected":
                out.append("{{ 'selected' if " + php2j(arg) + " }}")
            elif d == "checked":
                out.append("{{ 'checked' if " + php2j(arg) + " }}")
            elif d == "json":
                out.append("{{ " + php2j(arg) + "|tojson }}")
            elif d == "csrf":
                out.append('<input type="hidden" name="csrf_token" value="{{ csrf_token() }}">')
            elif d == "props":
                ast = Parser(arg.strip()).parse()
                for item in ast[1]:
                    if isinstance(item, tuple) and item[0] not in ("str", "lit", "var", "call"):
                        k, v = item
                        key = unq(k[1])
                        out.append("{% set " + key + " = " + key + " if " + key + " is defined else " + emit(v) + " %}")
            else:
                out.append(whole)
    return "".join(out)


BLOCK_TAG = re.compile(r"\{%\s*(block\s+\w+|endblock)\s*%\}")


def hoist_nested_blocks(text):
    """Blade @push inside @section -> Jinja needs the block at top level (else it renders twice)."""
    while True:
        tags = [(m.start(), m.end(), m.group(1)) for m in BLOCK_TAG.finditer(text)]
        stack, found = [], None
        for idx, (s, e, kind) in enumerate(tags):
            if kind.startswith("block"):
                if stack and found is None:
                    found = (idx, stack[-1])
                stack.append(idx)
            else:
                stack.pop()
        if not found:
            return text
        inner_idx, outer_idx = found
        # matching endblock for inner and outer
        def match_end(start_idx):
            depth = 0
            for j in range(start_idx, len(tags)):
                if tags[j][2].startswith("block"):
                    depth += 1
                else:
                    depth -= 1
                    if depth == 0:
                        return j
        inner_end = match_end(inner_idx)
        outer_end = match_end(outer_idx)
        seg = text[tags[inner_idx][0]:tags[inner_end][1]]
        text = text[:tags[inner_idx][0]] + text[tags[inner_end][1]:]
        removed = len(seg)
        ins = tags[outer_end][1] - removed
        text = text[:ins] + "\n" + seg + "\n" + text[ins:]


def main():
    count = 0
    for root, _, files in os.walk(SRC):
        for f in files:
            if not f.endswith(".blade.php"):
                continue
            src = os.path.join(root, f)
            rel = os.path.relpath(src, SRC).replace("\\", "/")
            if rel.startswith("_"):
                continue
            name = rel[:-len(".blade.php")]
            name = name.replace("product-card", "product_card").replace("product-tile", "product_tile") \
                .replace("status-pill", "status_pill")
            dst = os.path.join(DST, name + ".html")
            # keep dash names for partials/layouts; components use underscore (see conv_component)
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            raw = open(src, "rb").read()
            try:
                text = raw.decode("utf-8")
            except UnicodeDecodeError:
                print("cp1252 fallback:", rel)
                text = raw.decode("cp1252")
            try:
                result = hoist_nested_blocks(convert(text, rel))
            except Exception as exc:
                print("FAILED", rel, exc)
                raise
            with open(dst, "w", encoding="utf-8", newline="\n") as fh:
                fh.write(result)
            count += 1
    print("converted", count, "templates")
    for fname, snip in MANUAL:
        print("MANUAL:", fname, "::", snip.replace("\n", " ")[:70])


if __name__ == "__main__":
    main()

