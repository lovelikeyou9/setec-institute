let data = [
    { id: 1, name: "sami", gender: "F", age: 20, address: "KBS" },
    { id: 2, name: "dara", gender: "M", age: 22, address: "PP" }
];

function readAll() {
    localStorage.setItem("object", JSON.stringify(data));
    const tabledata = document.querySelector(".data_table");

    const object = localStorage.getItem('object');
    const objectdata = JSON.parse(object);
    let elements = "";

    objectdata.forEach(record => {
        elements += `
            <tr>
                <td>${record.name}</td>
                <td>${record.gender}</td>
                <td>${record.age}</td>
                <td>${record.address}</td>
                <td>
                    <button class="edit" onclick="edit(${record.id})">Edit</button>
                    <button class="delete" onclick="delet(${record.id})">Delete</button>
                </td>
            </tr>`;
    });

    tabledata.innerHTML = elements;
}

function delet(id) {
    if(confirm("Are you sure you want to delete this record?")) {
        data = data.filter(rec => rec.id !== id);
        readAll();
    }
}

function add() {
    editingId = null;
    document.getElementById("modalTitle").textContent = "Add Record";
    document.getElementById("modalName").value = "";
    document.getElementById("modalGender").value = "";
    document.getElementById("modalAge").value = "";
    document.getElementById("modalAddress").value = "";
    document.getElementById("customModal").style.display = "flex";
}



function edit(id) {
    const obj = data.find(rec => rec.id === id);
    if (!obj) return alert("Record not found");

    editingId = id;
    document.getElementById("modalTitle").textContent = "Edit Record";
    document.getElementById("modalName").value = obj.name;
    document.getElementById("modalGender").value = obj.gender;
    document.getElementById("modalAge").value = obj.age;
    document.getElementById("modalAddress").value = obj.address;
    document.getElementById("customModal").style.display = "flex";
}


function saveModal() {
    const name = document.getElementById("modalName").value.trim();
    const gender = document.getElementById("modalGender").value;

    const age = parseInt(document.getElementById("modalAge").value);
    const address = document.getElementById("modalAddress").value.trim();

    if (!name || !gender || isNaN(age) || !address) {
        alert("Please fill in all fields correctly and No Missing.");
        return;
    }

    if (editingId === null) {
        const newId = data.length ? Math.max(...data.map(d => d.id)) + 1 : 1;
        data.push({ id: newId, name, gender, age, address });
        alert(`Added:\nName: ${name}\nGender: ${gender}\nAge: ${age}\nAddress: ${address}`);
    } else {
        const index = data.findIndex(rec => rec.id === editingId);
        data[index] = { id: editingId, name, gender, age, address };
        alert(`Updated:\nName: ${name}\nGender: ${gender}\nAge: ${age}\nAddress: ${address}`);
    }

    closeModal();
    readAll();
}


// On page load
window.onload = function() {
    readAll();
    document.querySelector(".add_div button").onclick = add;

    // Hide unused forms
    document.querySelector(".create_form").style.display = "none";
    document.querySelector(".update_form").style.display = "none";
};

function closeModal() {
    document.getElementById("customModal").style.display = "none";
}
