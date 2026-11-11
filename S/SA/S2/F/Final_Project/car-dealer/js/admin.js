loadProducts();

function addProduct(){

    fetch("http://localhost:3000/add-product", {

        method:"POST",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({

            name:
            document.getElementById("name").value,

            brand:
            document.getElementById("brand").value,

            price:
            document.getElementById("price").value,

            stock:
            document.getElementById("stock").value,

            image:
            document.getElementById("image").value,

            description:
            document.getElementById("description").value

        })

    })

    .then(res => res.json())

    .then(data => {

        alert(data.message);

        loadProducts();

        clearForm();

    });

}

function loadProducts(){

    fetch("http://localhost:3000/products")

    .then(res => res.json())

    .then(products => {

        let html = "";

        products.forEach(product => {

            html += `
            
            <tr>

                <td>

                    <img src="${product.image}"
                        width="120">

                </td>

                <td>
                    ${product.name}
                </td>

                <td>
                    ${product.brand}
                </td>

                <td>
                    $${product.price}
                </td>

                <td>
                    ${product.stock}
                </td>

                <td>

                    <button class="btn btn-warning"
                        onclick="editProduct('${product._id}')">

                        Edit

                    </button>

                    <button class="btn btn-danger"
                        onclick="deleteProduct('${product._id}')">

                        Delete

                    </button>

                </td>

            </tr>
            
            `;

        });

        document.getElementById(
            "adminProducts"
        ).innerHTML = html;

    });

}

function deleteProduct(id){

    if(confirm("Delete Product?")){

        fetch(
            "http://localhost:3000/delete-product/" + id,
            {
                method:"DELETE"
            }
        )

        .then(res => res.json())

        .then(data => {

            alert(data.message);

            loadProducts();

        });

    }

}

function editProduct(id){

    fetch("http://localhost:3000/products")

    .then(res => res.json())

    .then(products => {

        const product =
        products.find(
            p => p._id === id
        );

        document.getElementById("name")
        .value = product.name;

        document.getElementById("brand")
        .value = product.brand;

        document.getElementById("price")
        .value = product.price;

        document.getElementById("stock")
        .value = product.stock;

        document.getElementById("image")
        .value = product.image;

        document.getElementById("description")
        .value = product.description;

        const button =
        document.querySelector(
            ".btn-dark"
        );

        button.innerHTML =
        "Update Product";

        button.onclick = function(){

            updateProduct(id);

        };

    });

}

function updateProduct(id){

    fetch(
        "http://localhost:3000/edit-product/" + id,
        {

            method:"PUT",

            headers:{
                "Content-Type":"application/json"
            },

            body:JSON.stringify({

                name:
                document.getElementById("name").value,

                brand:
                document.getElementById("brand").value,

                price:
                document.getElementById("price").value,

                stock:
                document.getElementById("stock").value,

                image:
                document.getElementById("image").value,

                description:
                document.getElementById("description").value

            })

        }
    )

    .then(res => res.json())

    .then(data => {

        alert(data.message);

        loadProducts();

        clearForm();

        resetButton();

    });

}

function clearForm(){

    document.getElementById("name").value = "";

    document.getElementById("brand").value = "";

    document.getElementById("price").value = "";

    document.getElementById("stock").value = "";

    document.getElementById("image").value = "";

    document.getElementById("description").value = "";

}

function resetButton(){

    const button =
    document.querySelector(".btn-dark");

    button.innerHTML =
    "Add Product";

    button.onclick = addProduct;

}