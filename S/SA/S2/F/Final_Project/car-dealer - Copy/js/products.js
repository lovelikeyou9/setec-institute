let allProducts = [];

fetch("http://localhost:3000/products")

.then(res => res.json())

.then(data => {

    allProducts = data;

    showProducts(data);

});

function showProducts(products){

    let html = "";

    products.forEach(product => {

        html += `
        
        <div class="col-lg-4 col-md-6">

            <div class="car-card">

                <img src="${product.image}"
                    class="img-fluid">

                <div class="car-info">

                    <h3>
                        ${product.name}
                    </h3>

                    <p>
                        ${product.brand}
                    </p>

                    <h4>
                        $${product.price}
                    </h4>

                    <div class="product-buttons">

                        <a href="product-detail.html?id=${product._id}">

                            <button class="view-btn">

                                View Detail

                            </button>

                        </a>

                        <button class="cart-btn"
                            onclick="addToCart('${product._id}')">

                            Add To Cart

                        </button>

                    </div>

                </div>

            </div>

        </div>
        
        `;

    });

    document.getElementById(
        "productList"
    ).innerHTML = html;

}

function filterProducts(){

    const search =
    document.getElementById(
        "searchInput"
    ).value.toLowerCase();

    const brand =
    document.getElementById(
        "brandFilter"
    ).value;

    const filtered =
    allProducts.filter(product => {

        const matchSearch =
        product.name.toLowerCase()
        .includes(search);

        const matchBrand =
        brand === "" ||
        product.brand === brand;

        return matchSearch &&
        matchBrand;

    });

    showProducts(filtered);

}

function addToCart(productId){

    const user =
    JSON.parse(localStorage.getItem("user"));

    if(!user){

        alert("Please Login First");

        window.location =
        "login.html";

        return;

    }

    fetch("http://localhost:3000/cart", {

        method:"POST",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({

            userId:user._id,

            items:[
                {
                    productId:productId,
                    quantity:1
                }
            ]

        })

    })

    .then(res => res.json())

    .then(data => {

        alert(data.message);

    });

}
