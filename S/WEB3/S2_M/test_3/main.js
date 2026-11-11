function printReceipt() {
    var product_name = prompt("Enter product name:");
    if (!product_name || product_name.trim() === "") {
        alert("Error: product_name cannot be empty.");
        return;
    }
    var product_qty_input = prompt("Enter product quantity (start from 1):");
    var product_qty = parseInt(product_qty_input || "0", 10);
    var product_price_input = prompt("Enter product price (start from 0):");
    var product_price = parseFloat(product_price_input || "0");
    var discount_percent_input = prompt("Enter discount (0-100):");
    var discount_percent = parseFloat(discount_percent_input || "0");
    if (isNaN(product_qty) || product_qty < 1 ||
        isNaN(product_price) || product_price < 0 ||
        isNaN(discount_percent) || discount_percent < 0 || discount_percent > 100) {
        alert("Invalid input values!");
        return;
    }
    var subTotal = product_qty * product_price;
    var discountPrice = subTotal * (discount_percent / 100);
    var grandTotalUSD = subTotal - discountPrice;
    var exchangeRate = 4200;
    var grandTotalKHR = grandTotalUSD * exchangeRate;
    var output = "\n    <h3>Receipt</h3>\n    <p>product_name (can not be empty): ".concat(product_name, "</p>\n    <p>product_qty (start from 1): ").concat(product_qty, "</p>\n    <p>product_price (start from 0): ").concat(product_price, "$</p>\n    <p>Discount (0-100): ").concat(discount_percent, "%</p>\n    <hr>\n    <p>SubTotal: ").concat(subTotal.toFixed(2), "$</p>\n    <p>DiscountPrice: ").concat(discountPrice.toFixed(2), "$</p>\n    <p>GrandTotal(USD): ").concat(grandTotalUSD.toFixed(2), "$</p>\n    <p>GrandTotal(KHR): ").concat(grandTotalKHR.toLocaleString(), "\u17DB</p>\n    <hr>\n  ");
    var receiptContainer = document.getElementById("receipt");
    if (receiptContainer) {
        receiptContainer.innerHTML = output;
    }
    else {
        // fallback for demo purposes
        document.write(output);
    }
}
// Ensure there's a container to render the receipt in
var container = document.createElement("div");
container.id = "receipt";
document.body.appendChild(container);
// Run the function
printReceipt();
