document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".qty-control").forEach((wrap) => {
    const input = wrap.querySelector('input[type="number"]');
    const minus = wrap.querySelector("[data-qty-minus]");
    const plus = wrap.querySelector("[data-qty-plus]");
    if (!input) return;

    const clamp = () => {
      const min = Number(input.min || 1);
      const max = Number(input.max || 999);
      let value = Number(input.value || min);
      if (Number.isNaN(value)) value = min;
      input.value = Math.min(max, Math.max(min, value));
    };

    if (minus) {
      minus.addEventListener("click", () => {
        input.value = Number(input.value || 1) - 1;
        clamp();
        if (input.form && input.form.classList.contains("cart-qty-form")) {
          input.form.submit();
        }
      });
    }
    if (plus) {
      plus.addEventListener("click", () => {
        input.value = Number(input.value || 1) + 1;
        clamp();
        if (input.form && input.form.classList.contains("cart-qty-form")) {
          input.form.submit();
        }
      });
    }
  });

  document.querySelectorAll(".alert").forEach((alert) => {
    setTimeout(() => {
      alert.classList.remove("show");
      setTimeout(() => alert.remove(), 300);
    }, 5000);
  });
});
