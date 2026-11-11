const form = document.getElementById("productForm");

form.addEventListener("submit", async (e) => {

    e.preventDefault();

    const formData = new FormData();

    formData.append(
        "name",
        document.getElementById("name").value
    );

    formData.append(
        "brand",
        document.getElementById("brand").value
    );

    formData.append(
        "price",
        document.getElementById("price").value
    );

    formData.append(
        "description",
        document.getElementById("description").value
    );

    formData.append(
        "stock",
        document.getElementById("stock").value
    );

    formData.append(
        "image",
        document.getElementById("image").files[0]
    );

    const response = await fetch(
        "http://localhost:5000/api/products",
        {
            method: "POST",
            body: formData
        }
    );

    const data = await response.json();

    console.log(data);

    alert("Product Added");
});