(function () {
  const VARIANT_BY_SLUG = {
    laptop: [{ label: 'Color', choices: ['Black', 'Silver'] }, { label: 'Storage', choices: ['512GB', '1TB', '2TB'] }, { label: 'RAM', choices: ['16GB', '32GB', '64GB'] }],
    'gaming-pc': [{ label: 'Color', choices: ['Black', 'Silver'] }, { label: 'Storage', choices: ['512GB', '1TB', '2TB'] }, { label: 'RAM', choices: ['16GB', '32GB', '64GB'] }],
    computers: [{ label: 'Color', choices: ['Black', 'Silver'] }, { label: 'Storage', choices: ['512GB', '1TB', '2TB'] }, { label: 'RAM', choices: ['16GB', '32GB', '64GB'] }],
    workstations: [{ label: 'Color', choices: ['Black', 'Silver'] }, { label: 'Storage', choices: ['512GB', '1TB', '2TB'] }, { label: 'RAM', choices: ['16GB', '32GB', '64GB'] }],
    ssd: [{ label: 'Storage', choices: ['512GB', '1TB', '2TB', '4TB'] }],
    hdd: [{ label: 'Storage', choices: ['512GB', '1TB', '2TB', '4TB'] }],
    ram: [{ label: 'Color', choices: ['Black'] }, { label: 'RAM', choices: ['8GB', '16GB', '32GB', '64GB'] }],
    gpu: [{ label: 'Color', choices: ['Black'] }],
    monitor: [{ label: 'Color', choices: ['Black'] }],
    'smart-tv': [{ label: 'Color', choices: ['Black'] }],
    mouse: [{ label: 'Color', choices: ['Black', 'White'] }],
    keyboard: [{ label: 'Color', choices: ['Black', 'White'] }],
    headset: [{ label: 'Color', choices: ['Black', 'White'] }],
    cooling: [{ label: 'Color', choices: ['Black', 'White'] }],
    'pc-case': [{ label: 'Color', choices: ['Black', 'White'] }],
  };
  const DEFAULT_GROUPS = [{ label: 'Color', choices: ['Black'] }];

  function slugFromSelect(select) {
    if (!select) return '';
    const opt = select.options[select.selectedIndex];
    return (opt && (opt.getAttribute('data-slug') || '')).toLowerCase().trim();
  }

  function defaultsForForm(form) {
    const select = form.querySelector('select[name="categoryId"], select[name="category_id"]');
    const slug = slugFromSelect(select);
    return VARIANT_BY_SLUG[slug] || DEFAULT_GROUPS;
  }

  function parseGroups(raw) {
    if (Array.isArray(raw)) return raw;
    try {
      const data = JSON.parse(raw || '[]');
      return Array.isArray(data) ? data : [];
    } catch (e) {
      return [];
    }
  }

  function syncGroupsHidden(form) {
    const root = form.querySelector('[data-option-groups]');
    const hidden = form.querySelector('.js-groups-hidden');
    if (!root || !hidden) return;
    const groups = [];
    root.querySelectorAll('[data-option-group]').forEach((block) => {
      const label = (block.querySelector('[data-group-label]')?.value || '').trim();
      const choices = [];
      const prices = {};
      const originalPrices = {};
      block.querySelectorAll('[data-option-row]').forEach((row) => {
        const v = (row.querySelector('[data-option-value]')?.value || '').trim();
        if (!v) return;
        choices.push(v);
        const p = (row.querySelector('[data-option-price]')?.value || '').trim();
        const o = (row.querySelector('[data-option-original]')?.value || '').trim();
        if (p !== '' && !Number.isNaN(Number(p))) prices[v] = Number(p);
        if (o !== '' && !Number.isNaN(Number(o))) originalPrices[v] = Number(o);
      });
      if (label && choices.length) {
        const g = { label, choices };
        if (Object.keys(prices).length) g.prices = prices;
        if (Object.keys(originalPrices).length) g.original_prices = originalPrices;
        groups.push(g);
      }
    });
    hidden.value = JSON.stringify(groups);

    // Legacy sync for Color / Storage / RAM named groups
    const find = (names) => {
      const g = groups.find((x) => names.includes(String(x.label).toLowerCase()));
      return g ? g.choices.join(',') : '';
    };
    const color = form.querySelector('.js-legacy-color');
    const storage = form.querySelector('.js-legacy-storage');
    const ram = form.querySelector('.js-legacy-ram');
    if (color) color.value = find(['color', 'colour']);
    if (storage) storage.value = find(['storage', 'ssd', 'hdd', 'tb', 'capacity']);
    if (ram) ram.value = find(['ram', 'memory', 'gb']);
  }

  function makeChoiceRow(block, value, price, original) {
    const list = block.querySelector('[data-group-choices]');
    if (!list) return;
    const row = document.createElement('div');
    row.className = 'option-choice-row';
    row.setAttribute('data-option-row', '1');
    row.innerHTML = `
      <input type="text" data-option-value placeholder="e.g. Black / 16GB / 1TB" />
      <input type="number" step="0.01" min="0" data-option-price placeholder="+ Price $" style="max-width:110px" title="Added to product price" />
      <input type="number" step="0.01" min="0" data-option-original placeholder="+ Was $" style="max-width:110px" title="Compare-at add-on (optional)" />
      <button type="button" class="btn btn-outline btn-sm" data-option-remove>Remove</button>
    `;
    const input = row.querySelector('[data-option-value]');
    const priceInput = row.querySelector('[data-option-price]');
    const originalInput = row.querySelector('[data-option-original]');
    input.value = value || '';
    if (price != null && price !== '') priceInput.value = price;
    if (original != null && original !== '') originalInput.value = original;
    const form = block.closest('form');
    const sync = () => syncGroupsHidden(form);
    input.addEventListener('input', sync);
    priceInput.addEventListener('input', sync);
    originalInput.addEventListener('input', sync);
    row.querySelector('[data-option-remove]').addEventListener('click', () => {
      row.remove();
      if (!list.querySelector('[data-option-row]')) makeChoiceRow(block, '');
      syncGroupsHidden(form);
    });
    list.appendChild(row);
  }

  function makeGroupBlock(form, root, group) {
    const block = document.createElement('div');
    block.className = 'option-builder option-group-block';
    block.setAttribute('data-option-group', '1');
    block.innerHTML = `
      <div class="spec-builder-head">
        <div class="form-group" style="margin:0;flex:1;min-width:140px">
          <label style="font-size:.72rem">Option name</label>
          <input type="text" data-group-label placeholder="e.g. Color, GB, TB, Size"/>
        </div>
        <button type="button" class="btn btn-outline btn-sm" data-group-remove>Remove group</button>
      </div>
      <div class="spec-builder-head" style="margin-top:.35rem">
        <label style="margin:0;font-size:.78rem;color:var(--text-muted)">Choices customers can select</label>
        <button type="button" class="btn btn-outline btn-sm" data-choice-add>+ Add choice</button>
      </div>
      <div data-group-choices></div>
    `;
    const labelInput = block.querySelector('[data-group-label]');
    labelInput.value = group?.label || '';
    labelInput.addEventListener('input', () => syncGroupsHidden(form));
    block.querySelector('[data-group-remove]').addEventListener('click', () => {
      block.remove();
      if (!root.querySelector('[data-option-group]')) {
        makeGroupBlock(form, root, { label: '', choices: [''] });
      }
      syncGroupsHidden(form);
    });
    block.querySelector('[data-choice-add]').addEventListener('click', () => {
      makeChoiceRow(block, '');
      const inputs = block.querySelectorAll('[data-option-value]');
      inputs[inputs.length - 1]?.focus();
      syncGroupsHidden(form);
    });
    root.appendChild(block);
    const choices = Array.isArray(group?.choices) && group.choices.length ? group.choices : [''];
    const prices = group?.prices || {};
    const originals = group?.original_prices || group?.originalPrices || {};
    choices.forEach((c) => makeChoiceRow(block, c, prices[c], originals[c]));
    syncGroupsHidden(form);
  }

  function loadGroups(form, groups) {
    const root = form.querySelector('[data-option-groups]');
    if (!root) return;
    root.innerHTML = '';
    const list = parseGroups(groups);
    if (!list.length) {
      makeGroupBlock(form, root, { label: 'Color', choices: ['Black'] });
    } else {
      list.forEach((g) => makeGroupBlock(form, root, g));
    }
    syncGroupsHidden(form);
  }

  function fillVariants(form, force) {
    const root = form.querySelector('[data-option-groups]');
    if (!root) return;
    const hidden = form.querySelector('.js-groups-hidden');
    const current = parseGroups(hidden?.value || '[]');
    if (force || !current.length) {
      loadGroups(form, defaultsForForm(form));
    }
  }

  function setVariantValues(form, color, storage, ram) {
    // Legacy edit buttons may only send color/storage/ram CSV
    const groups = [];
    if (color) groups.push({ label: 'Color', choices: String(color).split(',').map((s) => s.trim()).filter(Boolean) });
    if (storage) groups.push({ label: 'Storage', choices: String(storage).split(',').map((s) => s.trim()).filter(Boolean) });
    if (ram) groups.push({ label: 'RAM', choices: String(ram).split(',').map((s) => s.trim()).filter(Boolean) });
    loadGroups(form, groups.length ? groups : defaultsForForm(form));
  }

  function setOptionGroups(form, jsonOrArray) {
    loadGroups(form, jsonOrArray);
  }

  function parseSpecs(text) {
    const rows = [];
    String(text || '').split(/\r?\n/).forEach((line) => {
      const t = line.trim();
      if (!t) return;
      const idx = t.indexOf(':');
      if (idx > 0) rows.push({ key: t.slice(0, idx).trim(), value: t.slice(idx + 1).trim() });
      else rows.push({ key: 'Spec', value: t });
    });
    return rows.length ? rows : [{ key: '', value: '' }];
  }

  function syncHidden(root) {
    const form = root.closest('form');
    if (!form) return;
    const hidden = form.querySelector('.js-spec-hidden');
    if (!hidden) return;
    const lines = [];
    root.querySelectorAll('[data-spec-row]').forEach((row) => {
      const key = (row.querySelector('[data-spec-key]')?.value || '').trim();
      const value = (row.querySelector('[data-spec-value]')?.value || '').trim();
      if (!key && !value) return;
      lines.push(`${key || 'Spec'}: ${value}`);
    });
    hidden.value = lines.join('\n');
  }

  function makeRow(root, key, value) {
    const row = document.createElement('div');
    row.className = 'form-grid';
    row.style.cssText = 'grid-template-columns:1fr 1.4fr auto;gap:.5rem;margin-bottom:0;align-items:end';
    row.setAttribute('data-spec-row', '1');
    row.innerHTML = `
      <div class="form-group" style="margin:0">
        <label style="font-size:.72rem">Key</label>
        <input type="text" data-spec-key placeholder="e.g. CPU"/>
      </div>
      <div class="form-group" style="margin:0">
        <label style="font-size:.72rem">Value</label>
        <input type="text" data-spec-value placeholder="e.g. Intel Core Ultra 7"/>
      </div>
      <button type="button" class="btn btn-outline btn-sm" data-spec-remove>Remove</button>
    `;
    row.querySelector('[data-spec-key]').value = key || '';
    row.querySelector('[data-spec-value]').value = value || '';
    row.querySelector('[data-spec-key]').addEventListener('input', () => syncHidden(root));
    row.querySelector('[data-spec-value]').addEventListener('input', () => syncHidden(root));
    row.querySelector('[data-spec-remove]').addEventListener('click', () => {
      row.remove();
      if (!root.querySelector('[data-spec-row]')) makeRow(root, '', '');
      syncHidden(root);
    });
    root.appendChild(row);
    syncHidden(root);
  }

  function loadSpecs(form, text) {
    const root = form.querySelector('[data-spec-rows]');
    if (!root) return;
    root.innerHTML = '';
    parseSpecs(text).forEach((r) => makeRow(root, r.key, r.value));
    syncHidden(root);
  }

  function bindForm(form) {
    if (!form || form.dataset.variantBound === '1') return;
    form.dataset.variantBound = '1';

    const cat = form.querySelector('select[name="categoryId"], select[name="category_id"]');
    if (cat) cat.addEventListener('change', () => fillVariants(form, true));

    form.querySelectorAll('.js-group-add').forEach((btn) => {
      btn.addEventListener('click', () => {
        const root = form.querySelector('[data-option-groups]');
        if (root) {
          makeGroupBlock(form, root, { label: '', choices: [''] });
          root.querySelector('[data-option-group]:last-child [data-group-label]')?.focus();
        }
      });
    });

    form.querySelectorAll('.js-spec-add').forEach((btn) => {
      btn.addEventListener('click', () => {
        const root = form.querySelector('[data-spec-rows]');
        if (root) makeRow(root, '', '');
      });
    });

    form.addEventListener('submit', () => {
      try {
        syncGroupsHidden(form);
        const root = form.querySelector('[data-spec-rows]');
        if (root) syncHidden(root);
      } catch (err) {
        console.error('product-form sync failed', err);
      }
    });

    const groupsRoot = form.querySelector('[data-option-groups]');
    if (groupsRoot && !groupsRoot.querySelector('[data-option-group]')) {
      const initial = groupsRoot.getAttribute('data-initial-groups') || '[]';
      const parsed = parseGroups(initial);
      loadGroups(form, parsed.length ? parsed : defaultsForForm(form));
    }

    const specRoot = form.querySelector('[data-spec-rows]');
    if (specRoot && !specRoot.querySelector('[data-spec-row]')) {
      makeRow(specRoot, '', '');
    }
  }

  document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('form').forEach((form) => {
      if (!form.querySelector('[data-option-groups], [data-spec-rows]')) return;
      bindForm(form);
      const root = form.querySelector('[data-spec-rows]');
      if (root) loadSpecs(form, root.getAttribute('data-initial-specs') || '');
    });

    document.querySelectorAll('[data-edit="editProductForm"]').forEach((btn) => {
      btn.addEventListener('click', () => {
        requestAnimationFrame(() => {
          const form = document.getElementById('editProductForm');
          if (!form) return;
          bindForm(form);
          loadSpecs(form, btn.getAttribute('data-field-specifications') || '');
          const groups = btn.getAttribute('data-field-optionGroupsJson')
            || btn.getAttribute('data-field-option_groups')
            || '';
          if (groups && groups !== '[]' && groups !== '') {
            setOptionGroups(form, groups);
          } else {
            setVariantValues(
              form,
              btn.getAttribute('data-field-colorOptions') || '',
              btn.getAttribute('data-field-storageOptions') || '',
              btn.getAttribute('data-field-ramOptions') || ''
            );
          }
        });
      });
    });

    document.querySelectorAll('[data-modal-open="modalAddProduct"]').forEach((btn) => {
      btn.addEventListener('click', () => {
        const form = document.querySelector('#modalAddProduct form');
        if (!form) return;
        bindForm(form);
        loadSpecs(form, '');
        fillVariants(form, true);
      });
    });
  });

  function encodeMedia(path) {
    if (!path) return '/uploads/No_Image_Available.jpg';
    if (/^https?:\/\//i.test(path)) return path;
    const abs = path.startsWith('/');
    const parts = path.split('/').filter(Boolean).map(encodeURIComponent);
    return (abs ? '/' : '') + parts.join('/');
  }

  function parseCsv(raw) {
    return String(raw || '')
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean);
  }

  function syncGalleryKeep(form) {
    const keep = form.querySelector('[name="galleryKeep"]');
    const list = form.querySelector('[data-existing-gallery]');
    if (!keep || !list) return;
    const urls = [...list.querySelectorAll('[data-gallery-url]')].map((el) => el.getAttribute('data-gallery-url'));
    keep.value = urls.join(',');
  }

  function renderExistingGallery(form, csv) {
    const list = form.querySelector('[data-existing-gallery]');
    const keep = form.querySelector('[name="galleryKeep"]');
    if (!list) return;
    list.innerHTML = '';
    const urls = parseCsv(csv);
    urls.forEach((url) => {
      const wrap = document.createElement('div');
      wrap.className = 'multi-image-thumb';
      wrap.setAttribute('data-gallery-url', url);
      wrap.innerHTML = `
        <img src="${encodeMedia(url)}" alt=""/>
        <button type="button" class="multi-image-remove" aria-label="Remove">&times;</button>
      `;
      wrap.querySelector('.multi-image-remove').addEventListener('click', () => {
        wrap.remove();
        syncGalleryKeep(form);
      });
      list.appendChild(wrap);
    });
    if (keep) keep.value = urls.join(',');
  }

  function setInputFiles(input, files) {
    const dt = new DataTransfer();
    files.forEach((f) => dt.items.add(f));
    input.files = dt.files;
  }

  function listInputImages(input) {
    return [...(input.files || [])].filter((f) => {
      // Some Windows browsers leave type empty for webp — keep by extension too
      if (f.type && f.type.startsWith('image/')) return true;
      return /\.(jpe?g|png|gif|webp|avif|bmp)$/i.test(f.name || '');
    });
  }

  function renderNewFilePreviews(input, box) {
    if (!box) return;
    const files = listInputImages(input);
    box.innerHTML = '';
    files.forEach((file, index) => {
      const wrap = document.createElement('div');
      wrap.className = 'multi-image-thumb';
      wrap.innerHTML = `
        <img alt="${(file.name || 'image').replace(/"/g, '')}"/>
        <button type="button" class="multi-image-remove" aria-label="Remove image">&times;</button>
      `;
      const img = wrap.querySelector('img');
      const reader = new FileReader();
      reader.onload = () => { img.src = reader.result; };
      reader.readAsDataURL(file);
      wrap.querySelector('.multi-image-remove').addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        const next = listInputImages(input).filter((_, i) => i !== index);
        setInputFiles(input, next);
        renderNewFilePreviews(input, box);
      });
      box.appendChild(wrap);
    });
  }

  function fileKey(file) {
    return `${file.name}|${file.size}|${file.lastModified}`;
  }

  function getStaged(input) {
    if (!Array.isArray(input._mmtStaged)) input._mmtStaged = [];
    return input._mmtStaged;
  }

  function clearImageInput(input) {
    if (!input) return;
    input._mmtStaged = [];
    try { input.value = ''; } catch (_) { /* ignore */ }
    setInputFiles(input, []);
    const key = input.getAttribute('data-image-multi') || 'x';
    const form = input.closest('form');
    const box = form && form.querySelector(`[data-image-previews="${key}"]`);
    if (box) box.innerHTML = '';
  }

  function clearImageInputs(form) {
    (form || document).querySelectorAll('input[data-image-multi]').forEach(clearImageInput);
  }

  function bindMultiImageInput(input) {
    if (!input || input.dataset.multiBound === '1') return;
    input.dataset.multiBound = '1';
    const key = input.getAttribute('data-image-multi') || 'x';
    const form = input.closest('form');
    const box = form && form.querySelector(`[data-image-previews="${key}"]`);
    const maxFiles = Math.max(1, parseInt(input.getAttribute('data-image-max') || '12', 10) || 12);

    const sync = () => {
      input._mmtStaged = getStaged(input).slice(0, maxFiles);
      setInputFiles(input, input._mmtStaged);
      renderNewFilePreviews(input, box);
    };

    input.addEventListener('change', () => {
      const picked = listInputImages(input);
      if (!picked.length) {
        sync();
        return;
      }
      // Dialog replaces input.files — merge into staged, then write back.
      const fromDialog = picked.slice();
      try { input.value = ''; } catch (_) { /* ignore */ }
      const staged = getStaged(input);
      const seen = new Set(staged.map(fileKey));
      fromDialog.forEach((f) => {
        const k = fileKey(f);
        if (seen.has(k) || staged.length >= maxFiles) return;
        seen.add(k);
        staged.push(f);
      });
      sync();
    });

    if (box && box.dataset.removeBound !== '1') {
      box.dataset.removeBound = '1';
      box.addEventListener('click', (e) => {
        if (!e.target.closest('.multi-image-remove')) return;
        setTimeout(() => { input._mmtStaged = listInputImages(input); }, 0);
      });
    }
  }

  function bindImageWidgets(root) {
    (root || document).querySelectorAll('input[data-image-multi]').forEach(bindMultiImageInput);
  }

  document.addEventListener('DOMContentLoaded', () => {
    bindImageWidgets(document);
  });

  window.MMTProductForm = {
    loadSpecs, fillVariants, bindForm, setVariantValues, setOptionGroups, syncGroupsHidden,
    renderExistingGallery, syncGalleryKeep, bindImageWidgets, clearImageInput, clearImageInputs,
    refreshPreviews: (form) => syncGroupsHidden(form),
  };
})();
