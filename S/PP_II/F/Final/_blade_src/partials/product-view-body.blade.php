{{-- Shared Product Details modal body (seller + admin) --}}
<div class="product-view">
  <div class="product-view-media">
    <img id="viewProductImage" src="" alt=""/>
  </div>
  <div class="product-view-info">
    <p class="product-view-meta">
      <span id="viewProductId"></span>
      @if(!empty($showSeller))
        · <span id="viewProductSeller"></span>
      @else
        · Sold <span id="viewProductSold">0</span>
      @endif
    </p>
    <h3 id="viewProductName">Product</h3>
    <div class="product-view-tags">
      <span class="status-pill gray" id="viewProductCategory">Category</span>
      <span class="status-pill gray" id="viewProductBrand">Brand</span>
      <span class="status-pill" id="viewProductActive">Active</span>
    </div>
    <div class="product-view-price">
      <strong id="viewProductPrice">$0.00</strong>
      <span id="viewProductOriginal"></span>
    </div>
    <dl class="product-view-dl">
      <div><dt>Stock</dt><dd id="viewProductStock">0</dd></div>
      <div><dt>SKU</dt><dd id="viewProductSku">—</dd></div>
      <div><dt>Warranty</dt><dd id="viewProductWarranty">—</dd></div>
    </dl>

    <div class="product-view-options" id="viewProductOptions" hidden></div>

    <div class="product-view-block">
      <h4>Description</h4>
      <p id="viewProductDescription">—</p>
    </div>
    <div class="product-view-block">
      <h4>Specifications</h4>
      <div id="viewProductSpecsWrap">
        <table class="spec-table" id="viewProductSpecsTable" hidden>
          <tbody id="viewProductSpecsBody"></tbody>
        </table>
        <p class="product-view-empty" id="viewProductSpecsEmpty">No specifications listed.</p>
      </div>
    </div>
  </div>
</div>
