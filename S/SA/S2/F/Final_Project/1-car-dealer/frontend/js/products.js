const API_URL = "http://localhost:5000/api/products";

async function loadProducts() {

    const response = await fetch(API_URL);

    const products = await response.json();

    let output = "";

    products.forEach(product => {

        output += `
            <div class="product-card">

                <img src="${product.image}" />

                <h3>${product.name}</h3>

                <p>$${product.price}</p>

                <button onclick="viewProduct('${product._id}')">
                    View
                </button>

            </div>
        `;
    });

    document.getElementById("productList").innerHTML = output;
}

loadProducts();

function viewProduct(id) {

    localStorage.setItem("productId", id);

    window.location.href = "product-detail.html";
}