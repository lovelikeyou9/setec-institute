<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('pageTitle', 'KhmerStore')</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700&family=IBM+Plex+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="{{ asset('css/storefront.css') }}">
    @yield('headerBlock')
</head>
<body class="@yield('bodyClass')">
    <header class="sf-header">
        <div class="sf-wrap sf-header__inner">
            <a href="{{ route('store.home') }}" class="sf-logo">KhmerStore</a>
            <nav class="sf-nav" id="sfNav">
                <a href="{{ route('store.home') }}" class="{{ request()->routeIs('store.home') ? 'is-active' : '' }}">Home</a>
                <a href="{{ route('store.products') }}" class="{{ request()->routeIs('store.products', 'store.show') ? 'is-active' : '' }}">Products</a>
                @auth
                    <a href="{{ route('home') }}">Admin</a>
                @else
                    <a href="{{ route('login') }}">Sign in</a>
                @endauth
            </nav>
            <button type="button" class="sf-menu-btn" id="sfMenuBtn" aria-label="Menu" aria-expanded="false">
                <span></span><span></span><span></span>
            </button>
        </div>
    </header>

    <main>
        @yield('content')
    </main>

    <footer class="sf-footer">
        <div class="sf-wrap sf-footer__inner">
            <div>
                <strong class="sf-logo">KhmerStore</strong>
                <p>Computer parts, phones, and accessories. Phnom Penh.</p>
            </div>
            <div class="sf-footer__links">
                <a href="{{ route('store.home') }}">Home</a>
                <a href="{{ route('store.products') }}">Products</a>
                @auth
                    <a href="{{ route('home') }}">Dashboard</a>
                @endauth
            </div>
        </div>
        <div class="sf-wrap sf-footer__copy">
            &copy; {{ date('Y') }} KhmerStore
        </div>
    </footer>

    <script>
        (function () {
            var btn = document.getElementById('sfMenuBtn');
            var nav = document.getElementById('sfNav');
            if (!btn || !nav) return;
            btn.addEventListener('click', function () {
                var open = nav.classList.toggle('is-open');
                btn.setAttribute('aria-expanded', open ? 'true' : 'false');
            });
        })();
    </script>
    @yield('footerBlock')
</body>
</html>
