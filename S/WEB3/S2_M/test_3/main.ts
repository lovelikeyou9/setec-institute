function printReceipt(): void {
  const product_name: string | null = prompt("Enter product name:");
  if (!product_name || product_name.trim() === "") {
    alert("Error: product_name cannot be empty.");
    return;
  }

  const product_qty_input: string | null = prompt("Enter product quantity (start from 1):");
  const product_qty: number = parseInt(product_qty_input || "0", 10);

  const product_price_input: string | null = prompt("Enter product price (start from 0):");
  const product_price: number = parseFloat(product_price_input || "0");

  const discount_percent_input: string | null = prompt("Enter discount (0-100):");
  const discount_percent: number = parseFloat(discount_percent_input || "0");

  if (
    isNaN(product_qty) || product_qty < 1 ||
    isNaN(product_price) || product_price < 0 ||
    isNaN(discount_percent) || discount_percent < 0 || discount_percent > 100
  ) {
    alert("Invalid input values!");
    return;
  }

  const subTotal: number = product_qty * product_price;
  const discountPrice: number = subTotal * (discount_percent / 100);
  const grandTotalUSD: number = subTotal - discountPrice;
  const exchangeRate: number = 4200;
  const grandTotalKHR: number = grandTotalUSD * exchangeRate;

  const output: string = `
    <h3>Receipt</h3>
    <p>product_name (can not be empty): ${product_name}</p>
    <p>product_qty (start from 1): ${product_qty}</p>
    <p>product_price (start from 0): ${product_price}$</p>
    <p>Discount (0-100): ${discount_percent}%</p>
    <hr>
    <p>SubTotal: ${subTotal.toFixed(2)}$</p>
    <p>DiscountPrice: ${discountPrice.toFixed(2)}$</p>
    <p>GrandTotal(USD): ${grandTotalUSD.toFixed(2)}$</p>
    <p>GrandTotal(KHR): ${grandTotalKHR.toLocaleString()}៛</p>
    <hr>
  `;

  const receiptContainer: HTMLElement | null = document.getElementById("receipt");
  if (receiptContainer) {
    receiptContainer.innerHTML = output;
  } else {
    // fallback for demo purposes
    document.write(output);
  }
}

// Ensure there's a container to render the receipt in
const container = document.createElement("div");
container.id = "receipt";
document.body.appendChild(container);

// Run the function
printReceipt();
