{{-- Custom-named option groups + specs builder --}}
@php
  $prefix = $prefix ?? 'add';
  $specsVal = $specs ?? '';
  $groupsJson = $optionGroups ?? '[]';
  if (is_array($groupsJson)) {
      $groupsJson = json_encode($groupsJson, JSON_UNESCAPED_UNICODE);
  }
@endphp

<div class="variant-field-card">
  <div class="spec-builder-head">
    <label style="margin:0;font-weight:700">Configurable Options</label>
    <button type="button" class="btn btn-outline btn-sm js-group-add">+ Add option group</button>
  </div>
  <p class="variant-card-hint">
    Name each group yourself (Color, GB, TB, Size…) then add choices customers can tap as pills.
  </p>
  <div class="js-option-groups" data-option-groups data-initial-groups="{{ $groupsJson }}"></div>
  <textarea name="optionGroupsJson" class="js-groups-hidden" hidden>{{ $groupsJson }}</textarea>
  {{-- Kept for older edit fillers; synced from groups in JS --}}
  <input type="hidden" name="colorOptions" class="js-legacy-color" data-variant-field="color" value=""/>
  <input type="hidden" name="storageOptions" class="js-legacy-storage" data-variant-field="storage" value=""/>
  <input type="hidden" name="ramOptions" class="js-legacy-ram" data-variant-field="ram" value=""/>
</div>

<div class="variant-field-card">
  <div class="spec-builder-head">
    <label>Specifications</label>
    <button type="button" class="btn btn-outline btn-sm js-spec-add" data-spec-root="{{ $prefix }}Specs">+ Add row</button>
  </div>
  <p class="variant-card-hint">
    Add Key / Value rows (e.g. CPU → Intel Core Ultra 7). Shown as a specs table on the product page.
  </p>
  <div class="js-spec-rows" id="{{ $prefix }}Specs" data-spec-rows data-initial-specs="{{ $specsVal }}"></div>
  <textarea name="specifications" class="js-spec-hidden" hidden>{{ $specsVal }}</textarea>
</div>
