// Product share helpers (used by fragments/social)
window.chShare = function (network) {
  const box = document.querySelector('.product-share');
  const title = (box && box.getAttribute('data-share-title')) || document.title;
  const url = window.location.href;
  let shareUrl = '';
  switch (network) {
    case 'facebook':
      shareUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`;
      break;
    case 'x':
      shareUrl = `https://twitter.com/intent/tweet?url=${encodeURIComponent(url)}&text=${encodeURIComponent(title)}`;
      break;
    case 'telegram':
      shareUrl = `https://t.me/share/url?url=${encodeURIComponent(url)}&text=${encodeURIComponent(title)}`;
      break;
    case 'instagram':
      if (navigator.clipboard) navigator.clipboard.writeText(url).catch(() => {});
      window.open('https://www.instagram.com/', '_blank', 'noopener');
      return false;
    case 'youtube':
      window.open('https://www.youtube.com/', '_blank', 'noopener');
      return false;
    default:
      return false;
  }
  window.open(shareUrl, '_blank', 'noopener,width=640,height=480');
  return false;
};

document.addEventListener('DOMContentLoaded', () => {
  // Products page filter drawer (tablet/mobile)
  const filterPanel = document.getElementById('productsFilterPanel');
  const filterToggle = document.getElementById('productsFilterToggle');
  const filterClose = document.getElementById('productsFilterClose');
  const filterBackdrop = document.getElementById('productsFilterBackdrop');
  const setFiltersOpen = (open) => {
    if (!filterPanel) return;
    filterPanel.classList.toggle('is-open', open);
    if (filterBackdrop) {
      filterBackdrop.hidden = !open;
      filterBackdrop.classList.toggle('is-open', open);
    }
    document.body.classList.toggle('filters-open', open);
    if (filterToggle) filterToggle.setAttribute('aria-expanded', String(open));
  };
  if (filterToggle) filterToggle.addEventListener('click', () => setFiltersOpen(true));
  if (filterClose) filterClose.addEventListener('click', () => setFiltersOpen(false));
  if (filterBackdrop) filterBackdrop.addEventListener('click', () => setFiltersOpen(false));

  // Header Browse Technology — one delegated click handler (avoids open/close race)
  const closeMegaMenus = () => {
    document.querySelectorAll('.mega-nav.open').forEach((n) => {
      n.classList.remove('open');
      const b = n.querySelector('.mega-nav-btn');
      if (b) b.setAttribute('aria-expanded', 'false');
    });
  };
  document.addEventListener('click', (e) => {
    const toggle = e.target.closest('#browseTechBtn, .mega-nav-btn');
    if (toggle && toggle.closest('.mega-nav')) {
      e.preventDefault();
      const root = toggle.closest('.mega-nav');
      const panel = root.querySelector('.mega-panel');
      const willOpen = !root.classList.contains('open');
      closeMegaMenus();
      if (willOpen) {
        if (panel) {
          const header = document.querySelector('.site-header');
          const top = header ? Math.ceil(header.getBoundingClientRect().bottom) + 8 : 120;
          panel.style.top = `${top}px`;
        }
        root.classList.add('open');
        toggle.setAttribute('aria-expanded', 'true');
      }
      return;
    }
    if (!e.target.closest('.mega-nav')) closeMegaMenus();
  });
  let lastScrollY = window.scrollY;
  window.addEventListener('scroll', () => {
    if (Math.abs(window.scrollY - lastScrollY) < 12) return;
    lastScrollY = window.scrollY;
    closeMegaMenus();
  }, { passive: true });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeMegaMenus();
  });

  // Promo peek carousel — seamless infinite loop
  const promoCarousel = document.getElementById('promoCarousel');
  const promoTrack = document.getElementById('promoTrack');
  if (promoCarousel && promoTrack) {
    const originals = [...promoTrack.querySelectorAll('.promo-slide')];
    if (originals.length) {
      const prepClone = (node) => {
        const c = node.cloneNode(true);
        c.classList.remove('active');
        c.setAttribute('aria-hidden', 'true');
        c.querySelectorAll('a').forEach((a) => a.setAttribute('tabindex', '-1'));
        return c;
      };
      promoTrack.appendChild(prepClone(originals[0]));
      promoTrack.insertBefore(prepClone(originals[originals.length - 1]), originals[0]);

      const slides = [...promoTrack.querySelectorAll('.promo-slide')];
      // 0 = clone of last, 1..n = real, n+1 = clone of first
      let index = 1;
      let autoTimer;
      let locked = false;
      let unlockTimer;

      const metrics = () => {
        const viewport = promoCarousel.clientWidth;
        // offsetWidth = layout width (ignores visual scale/filters)
        const slideWidth = slides[0].offsetWidth;
        const styles = getComputedStyle(promoTrack);
        const gap = parseFloat(styles.columnGap || styles.gap) || 16;
        const step = slideWidth + gap;
        const centerPad = (viewport - slideWidth) / 2;
        return { step, centerPad, slideWidth, viewport };
      };

      const update = (animate = true) => {
        const { step, centerPad } = metrics();
        const offset = centerPad - index * step;
        promoTrack.classList.toggle('is-jumping', !animate);
        promoTrack.style.transform = `translate3d(${offset}px, 0, 0)`;
        slides.forEach((s, i) => s.classList.toggle('active', i === index));
        if (!animate) {
          void promoTrack.offsetWidth;
          promoTrack.classList.remove('is-jumping');
        }
      };

      const normalize = () => {
        if (index === 0) {
          index = slides.length - 2;
          update(false);
        } else if (index === slides.length - 1) {
          index = 1;
          update(false);
        }
        locked = false;
      };

      const go = (dir) => {
        if (locked || originals.length < 2) return;
        locked = true;
        index += dir;
        update(true);
        clearTimeout(unlockTimer);
        unlockTimer = setTimeout(normalize, 650);
        restartAuto();
      };

      promoTrack.addEventListener('transitionend', (e) => {
        if (e.target !== promoTrack || e.propertyName !== 'transform') return;
        clearTimeout(unlockTimer);
        normalize();
      });

      const restartAuto = () => {
        clearInterval(autoTimer);
        if (originals.length > 1) {
          autoTimer = setInterval(() => go(1), 5500);
        }
      };

      const prev = document.getElementById('promoPrev');
      const next = document.getElementById('promoNext');
      if (prev) prev.addEventListener('click', (e) => { e.preventDefault(); go(-1); });
      if (next) next.addEventListener('click', (e) => { e.preventDefault(); go(1); });

      let startX = 0;
      let dragging = false;
      promoCarousel.addEventListener('pointerdown', (e) => {
        if (e.target.closest('a, button')) return;
        startX = e.clientX;
        dragging = true;
      });
      promoCarousel.addEventListener('pointerup', (e) => {
        if (!dragging) return;
        dragging = false;
        const dx = e.clientX - startX;
        if (Math.abs(dx) > 40) go(dx < 0 ? 1 : -1);
      });
      promoCarousel.addEventListener('pointercancel', () => { dragging = false; });

      promoCarousel.addEventListener('mouseenter', () => clearInterval(autoTimer));
      promoCarousel.addEventListener('mouseleave', restartAuto);

      window.addEventListener('resize', () => update(false));
      requestAnimationFrame(() => {
        update(false);
        restartAuto();
      });
    }
  }

  // Back to top
  const backToTop = document.getElementById('backToTop');
  if (backToTop) {
    const toggleTop = () => {
      backToTop.classList.toggle('visible', window.scrollY > 420);
    };
    toggleTop();
    window.addEventListener('scroll', toggleTop, { passive: true });
    backToTop.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));
  }

  // User dropdown
  const userBtn = document.getElementById('userMenuBtn');
  const userDrop = document.getElementById('userDropdown');
  if (userBtn && userDrop) {
    userBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      userDrop.classList.toggle('open');
    });
    document.addEventListener('click', () => userDrop.classList.remove('open'));
  }

  // Dashboard sidebar submenus (Products → List / Category / Brand)
  document.querySelectorAll('.dash-nav-toggle').forEach((btn) => {
    btn.addEventListener('click', () => {
      const group = btn.closest('.dash-nav-group');
      if (!group) return;
      const open = group.classList.toggle('open');
      btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    });
  });

  // Search suggestions
  const searchInput = document.getElementById('searchInput');
  const searchDropdown = document.getElementById('searchDropdown');
  const suggestList = document.getElementById('suggestList');
  let debounce;

  if (searchInput && searchDropdown) {
    searchInput.addEventListener('focus', () => searchDropdown.classList.add('open'));
    searchInput.addEventListener('input', () => {
      clearTimeout(debounce);
      const q = searchInput.value.trim();
      if (q.length < 2) {
        if (suggestList) suggestList.innerHTML = '';
        return;
      }
      debounce = setTimeout(async () => {
        try {
          const res = await fetch('/search/suggest?q=' + encodeURIComponent(q));
          const items = await res.json();
          if (suggestList) {
            suggestList.innerHTML = items.map(s =>
              `<a href="/products?q=${encodeURIComponent(s)}">${s}</a>`
            ).join('');
          }
          searchDropdown.classList.add('open');
        } catch (_) {}
      }, 200);
    });
    document.addEventListener('click', (e) => {
      if (!e.target.closest('.search-wrap')) searchDropdown.classList.remove('open');
    });
  }

  // Quantity selectors
  document.querySelectorAll('.qty-selector').forEach(el => {
    const input = el.querySelector('input');
    const minus = el.querySelector('[data-qty="-"]');
    const plus = el.querySelector('[data-qty="+"]');
    if (minus) minus.addEventListener('click', () => {
      input.value = Math.max(1, parseInt(input.value || 1) - 1);
      input.dispatchEvent(new Event('change'));
    });
    if (plus) plus.addEventListener('click', () => {
      input.value = parseInt(input.value || 1) + 1;
      input.dispatchEvent(new Event('change'));
    });
  });

  // Tabs
  document.querySelectorAll('.tab-nav').forEach(nav => {
    nav.querySelectorAll('button').forEach(btn => {
      btn.addEventListener('click', () => {
        const target = btn.dataset.tab;
        nav.querySelectorAll('button').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        const parent = nav.closest('.tabs') || document;
        parent.querySelectorAll('.tab-panel').forEach(p => {
          p.classList.toggle('active', p.id === target);
        });
      });
    });
  });

  // Option pills
  document.querySelectorAll('.option-pills').forEach(group => {
    group.querySelectorAll('button').forEach(btn => {
      btn.addEventListener('click', () => {
        group.querySelectorAll('button').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
      });
    });
  });

  // Gallery thumbs
  document.querySelectorAll('.gallery-thumbs button').forEach(btn => {
    btn.addEventListener('click', () => {
      const main = document.querySelector('.gallery-main img');
      if (main) main.src = btn.dataset.src || btn.querySelector('img').src;
      btn.parentElement.querySelectorAll('button').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
    });
  });

  // Countdown timers (supports days/hours/mins/secs)
  document.querySelectorAll('[data-countdown]').forEach(el => {
    let end = new Date(el.dataset.countdown).getTime();
    const tick = () => {
      const diff = Math.max(0, end - Date.now());
      const d = Math.floor(diff / 86400000);
      const h = Math.floor((diff % 86400000) / 3600000);
      const m = Math.floor((diff % 3600000) / 60000);
      const s = Math.floor((diff % 60000) / 1000);
      const days = el.querySelector('[data-d]');
      const hours = el.querySelector('[data-h]');
      const mins = el.querySelector('[data-m]');
      const secs = el.querySelector('[data-s]');
      if (days) days.textContent = String(d).padStart(2, '0');
      if (hours) hours.textContent = String(h).padStart(2, '0');
      if (mins) mins.textContent = String(m).padStart(2, '0');
      if (secs) secs.textContent = String(s).padStart(2, '0');
    };
    el._setCountdownEnd = (iso) => {
      end = new Date(iso).getTime();
      tick();
    };
    tick();
    setInterval(tick, 1000);
  });

  // Sale Off featured deal switcher
  const saleOff = document.getElementById('saleOff');
  if (saleOff) {
    const thumbs = [...saleOff.querySelectorAll('.sale-off-thumb')];
    const title = document.getElementById('saleOffTitle');
    const stars = document.getElementById('saleOffStars');
    const price = document.getElementById('saleOffPrice');
    const original = document.getElementById('saleOffOriginal');
    const badge = document.getElementById('saleOffBadge');
    const image = document.getElementById('saleOffImage');
    const view = document.getElementById('saleOffView');
    const wishId = document.getElementById('saleOffWishId');
    const cartId = document.getElementById('saleOffCartId');
    const countdown = document.getElementById('saleOffCountdown');
    let index = Math.max(0, thumbs.findIndex((t) => t.classList.contains('active')));

    const renderStars = (rating) => {
      if (!stars) return;
      const full = Math.round(Number(rating) || 0);
      stars.innerHTML = '';
      for (let i = 1; i <= 5; i++) {
        const span = document.createElement('span');
        if (i <= full) span.className = 'on';
        span.textContent = i <= full ? '★' : '☆';
        stars.appendChild(span);
      }
    };

    const select = (i) => {
      if (!thumbs.length) return;
      index = (i + thumbs.length) % thumbs.length;
      const t = thumbs[index];
      thumbs.forEach((thumb, idx) => {
        thumb.classList.toggle('active', idx === index);
        thumb.setAttribute('aria-selected', idx === index ? 'true' : 'false');
      });
      if (title) title.textContent = t.dataset.name || '';
      if (price) price.textContent = t.dataset.price ? `$${t.dataset.price}` : '';
      if (original) {
        if (t.dataset.original && Number(t.dataset.discount) > 0) {
          original.textContent = `$${t.dataset.original}`;
          original.hidden = false;
          original.style.display = '';
        } else {
          original.textContent = '';
          original.hidden = true;
          original.style.display = 'none';
        }
      }
      if (badge) {
        const d = Number(t.dataset.discount) || 0;
        if (d > 0) {
          badge.textContent = `-${d}%`;
          badge.hidden = false;
          badge.style.display = '';
        } else {
          badge.hidden = true;
          badge.style.display = 'none';
        }
      }
      if (image) {
        image.style.opacity = '0';
        image.style.transform = 'scale(.97)';
        setTimeout(() => {
          image.src = t.dataset.image || '';
          image.alt = t.dataset.name || '';
          image.style.opacity = '1';
          image.style.transform = 'scale(1)';
        }, 120);
      }
      if (view && t.dataset.slug) view.href = `/product/${t.dataset.slug}`;
      if (wishId) wishId.value = t.dataset.id || '';
      if (cartId) cartId.value = t.dataset.id || '';
      renderStars(t.dataset.rating);
      if (countdown && t.dataset.ends && typeof countdown._setCountdownEnd === 'function') {
        countdown.dataset.countdown = t.dataset.ends;
        countdown._setCountdownEnd(t.dataset.ends);
      }
    };

    thumbs.forEach((thumb) => {
      thumb.addEventListener('click', () => select(Number(thumb.dataset.index) || 0));
    });
    const prev = document.getElementById('saleOffPrev');
    const next = document.getElementById('saleOffNext');
    if (prev) prev.addEventListener('click', () => select(index - 1));
    if (next) next.addEventListener('click', () => select(index + 1));
  }

  // Toast host for flash messages
  let toastHost = document.querySelector('.toast-host');
  if (!toastHost) {
    toastHost = document.createElement('div');
    toastHost.className = 'toast-host';
    document.body.appendChild(toastHost);
  }

  function showToast(message, type = 'success') {
    if (!message) return;
    const el = document.createElement('div');
    el.className = `toast toast-${type}`;
    el.innerHTML = `<span></span><button type="button" class="toast-close" aria-label="Close">×</button>`;
    el.querySelector('span').textContent = message;
    const close = () => {
      el.style.opacity = '0';
      el.style.transform = 'translateY(-6px)';
      el.style.transition = 'all .25s';
      setTimeout(() => el.remove(), 250);
    };
    el.querySelector('.toast-close').addEventListener('click', close);
    toastHost.appendChild(el);
    setTimeout(close, 4000);
  }

  // Convert flash alerts into toast popups (keep auth-page alerts visible too)
  document.querySelectorAll('.alert-success, .alert-error').forEach((a) => {
    if (a.hasAttribute('data-keep-alert')) return;
    const type = a.classList.contains('alert-error') ? 'error' : 'success';
    const text = a.textContent.trim();
    if (text) showToast(text, type);
    a.remove();
  });

  // Modal open/close
  const openModal = (id) => {
    const modal = document.getElementById(id);
    if (!modal) return;
    modal.classList.add('open');
    document.body.classList.add('modal-open');
  };
  const closeModal = (modal) => {
    modal.classList.remove('open');
    if (!document.querySelector('.modal-backdrop.open')) {
      document.body.classList.remove('modal-open');
    }
  };

  document.querySelectorAll('[data-modal-open]').forEach((btn) => {
    btn.addEventListener('click', () => openModal(btn.getAttribute('data-modal-open')));
  });
  document.querySelectorAll('[data-modal-close]').forEach((btn) => {
    btn.addEventListener('click', () => {
      const modal = btn.closest('.modal-backdrop');
      if (modal) closeModal(modal);
    });
  });
  document.querySelectorAll('.modal-backdrop').forEach((backdrop) => {
    backdrop.addEventListener('click', (e) => {
      if (e.target === backdrop) closeModal(backdrop);
    });
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      document.querySelectorAll('.modal-backdrop.open').forEach(closeModal);
    }
  });

  // Fill edit forms from data-* on trigger buttons
  // (browsers lowercase data-* names, so match form fields case-insensitively)
  document.querySelectorAll('[data-edit]').forEach((btn) => {
    btn.addEventListener('click', () => {
      const formId = btn.getAttribute('data-edit');
      const form = document.getElementById(formId);
      if (!form) return;
      [...btn.attributes].forEach((attr) => {
        if (!attr.name.startsWith('data-field-')) return;
        const key = attr.name.replace('data-field-', '').toLowerCase();
        const field = [...form.querySelectorAll('[name]')].find(
          (f) => f.name.toLowerCase() === key
        );
        if (!field) return;
        if (field.type === 'checkbox') {
          field.checked = attr.value === 'true';
        } else if (field.type === 'file') {
          field.value = '';
        } else {
          field.value = attr.value ?? '';
        }
      });
      // Product image: show current preview; require file choose if none
      const imageUrl = btn.getAttribute('data-field-imageUrl') || '';
      const fileInput = form.querySelector('input[name="imageFile"]');
      const preview = form.querySelector('#editProductImagePreview');
      if (fileInput) {
        fileInput.value = '';
        fileInput.required = !imageUrl;
      }
      if (preview) {
        if (imageUrl) {
          preview.src = imageUrl;
          preview.style.display = 'block';
        } else {
          preview.removeAttribute('src');
          preview.style.display = 'none';
        }
      }
      const action = btn.getAttribute('data-action');
      if (action) form.setAttribute('action', withCsrfQuery(action, form));
      const modalId = btn.getAttribute('data-modal-open');
      if (modalId) openModal(modalId);
    });
  });

  /** Append CSRF token to action URL (needed for multipart POSTs). */
  function withCsrfQuery(action, form) {
    if (!action || action === '#') return action;
    const csrfInput = form && form.querySelector('input[name="_csrf"]');
    const token = csrfInput ? csrfInput.value : null;
    const param = csrfInput ? csrfInput.name : '_csrf';
    if (!token) return action;
    try {
      const url = new URL(action, window.location.origin);
      url.searchParams.set(param, token);
      return url.pathname + url.search;
    } catch (e) {
      const sep = action.includes('?') ? '&' : '?';
      return action + sep + encodeURIComponent(param) + '=' + encodeURIComponent(token);
    }
  }

  // Delete confirm modal
  document.querySelectorAll('[data-delete]').forEach((btn) => {
    btn.addEventListener('click', () => {
      const form = document.getElementById('deleteForm');
      const label = document.getElementById('deleteLabel');
      if (!form) return;
      form.setAttribute('action', withCsrfQuery(btn.getAttribute('data-delete'), form));
      if (label) label.textContent = btn.getAttribute('data-label') || 'this item';
      openModal('modalDelete');
    });
  });

  // Product view modal (admin / seller Products List)
  let viewProductSource = null;
  const fillProductView = (src) => {
    if (!src || !document.getElementById('modalViewProduct')) return;
    viewProductSource = src;
    const setText = (id, value, fallback = '—') => {
      const el = document.getElementById(id);
      if (el) el.textContent = value && String(value).trim() ? value : fallback;
    };
    const img = document.getElementById('viewProductImage');
    if (img) {
      img.src = src.getAttribute('data-image') || '';
      img.alt = src.getAttribute('data-name') || 'Product';
      img.style.display = src.getAttribute('data-image') ? '' : 'none';
    }
    setText('viewProductId', src.getAttribute('data-id') ? `#${src.getAttribute('data-id')}` : '');
    setText('viewProductSeller', src.getAttribute('data-seller'));
    setText('viewProductName', src.getAttribute('data-name'));
    setText('viewProductCategory', src.getAttribute('data-category'));
    setText('viewProductBrand', src.getAttribute('data-brand'));
    setText('viewProductStock', src.getAttribute('data-stock'));
    setText('viewProductSku', src.getAttribute('data-sku'));
    setText('viewProductWarranty', src.getAttribute('data-warranty'));
    setText('viewProductDescription', src.getAttribute('data-description'));
    setText('viewProductSpecs', src.getAttribute('data-specs'));
    setText('viewProductSold', src.getAttribute('data-sold') || '0');
    const price = document.getElementById('viewProductPrice');
    if (price) price.textContent = src.getAttribute('data-price') ? `$${src.getAttribute('data-price')}` : '—';
    const original = document.getElementById('viewProductOriginal');
    if (original) {
      const o = src.getAttribute('data-original');
      original.textContent = o ? `$${o}` : '';
      original.style.display = o ? '' : 'none';
    }
    const active = document.getElementById('viewProductActive');
    if (active) {
      const on = src.getAttribute('data-active') === 'true';
      active.textContent = on ? 'Active' : 'Inactive';
      active.classList.toggle('green', on);
      active.classList.toggle('gray', !on);
    }
    const storefront = document.getElementById('viewProductStorefront');
    if (storefront) {
      const slug = src.getAttribute('data-slug');
      storefront.href = slug ? `/product/${slug}` : '#';
      storefront.style.display = slug ? '' : 'none';
    }
    openModal('modalViewProduct');
  };

  document.querySelectorAll('tr.profile-row-click[data-href]').forEach((row) => {
    row.addEventListener('click', (e) => {
      if (e.target.closest('.table-actions, a, button, form, input, select, label')) return;
      const href = row.getAttribute('data-href');
      if (href) window.location.href = href;
    });
  });

  document.querySelectorAll('[data-view-product]').forEach((el) => {
    el.addEventListener('click', (e) => {
      if (e.target.closest('.table-actions')) return;
      const src = el.closest('[data-view-product].product-row-click') || el.closest('tr[data-view-product]') || el;
      // Prefer the row that holds full product data
      const row = el.closest('tr[data-view-product]') || src;
      e.preventDefault();
      e.stopPropagation();
      fillProductView(row);
    });
  });

  const viewEditBtn = document.getElementById('viewProductEditBtn');
  if (viewEditBtn) {
    viewEditBtn.addEventListener('click', () => {
      if (!viewProductSource) return;
      const form = document.getElementById('editProductForm');
      if (!form) return;
      [...viewProductSource.attributes].forEach((attr) => {
        if (!attr.name.startsWith('data-field-')) return;
        const key = attr.name.replace('data-field-', '').toLowerCase();
        const field = [...form.querySelectorAll('[name]')].find(
          (f) => f.name.toLowerCase() === key
        );
        if (!field) return;
        if (field.type === 'checkbox') {
          field.checked = attr.value === 'true';
        } else {
          field.value = attr.value ?? '';
        }
      });
      const action = viewProductSource.getAttribute('data-edit-action');
      if (action) form.setAttribute('action', action);
      const viewModal = document.getElementById('modalViewProduct');
      if (viewModal) closeModal(viewModal);
      openModal('modalEditProduct');
    });
  }

  // Image preview for avatar / logo / banner upload fields
  document.querySelectorAll('[data-logo-field]').forEach((root) => {
    const img = root.querySelector('.logo-preview-img');
    const empty = root.querySelector('.logo-preview-empty');
    const urlInput = root.querySelector('.logo-url-input');
    const fileInput = root.querySelector('.logo-file-input');
    if (!img || !empty) return;

    const show = (src) => {
      if (src && String(src).trim()) {
        img.src = src;
        img.hidden = false;
        empty.hidden = true;
      } else {
        img.removeAttribute('src');
        img.hidden = true;
        empty.hidden = false;
      }
    };

    if (urlInput && urlInput.value) show(urlInput.value.trim());

    if (urlInput && urlInput.type !== 'hidden') {
      urlInput.addEventListener('input', () => {
        if (fileInput) fileInput.value = '';
        show(urlInput.value.trim());
      });
    }
    if (fileInput) {
      fileInput.addEventListener('change', () => {
        const file = fileInput.files && fileInput.files[0];
        if (!file) {
          show(urlInput ? urlInput.value.trim() : '');
          return;
        }
        const reader = new FileReader();
        reader.onload = () => show(reader.result);
        reader.readAsDataURL(file);
      });
    }
  });
});
