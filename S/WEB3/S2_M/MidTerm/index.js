function getLocalData() {
    var stored = localStorage.getItem("object");
    return stored ? JSON.parse(stored) : [];
}
function setLocalData(data) {
    localStorage.setItem("object", JSON.stringify(data));
}
function readAll() {
    var table = document.querySelector(".data_table");
    var data = getLocalData().sort(function (a, b) { return a.id - b.id; });
    table.innerHTML = data
        .map(function (item) { return "\n    <tr>\n      <td>".concat(item.id, "</td>\n      <td>").concat(item.name, "</td>\n      <td>").concat(item.gender, "</td>\n      <td>").concat(item.age, "</td>\n      <td>").concat(item.address, "</td>\n      <td>\n        <button onclick=\"edit(").concat(item.id, ")\">Edit</button>\n        <button onclick=\"delet(").concat(item.id, ")\">Delete</button>\n      </td>\n    </tr>\n  "); })
        .join("");
}
function add() {
    var modal = document.getElementById("customModal");
    document.getElementById("modalTitle").innerText = "Add Record";
    clearModalInputs();
    delete modal.dataset.editId;
    modal.style.display = "flex";
}
function edit(id) {
    var data = getLocalData();
    // Manual find
    var record = undefined;
    for (var _i = 0, data_1 = data; _i < data_1.length; _i++) {
        var d = data_1[_i];
        if (d.id === id) {
            record = d;
            break;
        }
    }
    if (!record)
        return;
    document.getElementById("modalName").value = record.name;
    document.getElementById("modalGender").value = record.gender;
    document.getElementById("modalAge").value = record.age.toString();
    document.getElementById("modalAddress").value = record.address;
    var modal = document.getElementById("customModal");
    modal.dataset.editId = id.toString();
    document.getElementById("modalTitle").innerText = "Edit Record";
    modal.style.display = "flex";
}
function delet(id) {
    if (!confirm("Are you sure you want to delete this record?"))
        return;
    var data = getLocalData();
    var newData = data.filter(function (d) { return d.id !== id; });
    setLocalData(newData);
    readAll();
}
function saveModal() {
    var name = document.getElementById("modalName").value.trim();
    var gender = document.getElementById("modalGender").value;
    var ageStr = document.getElementById("modalAge").value;
    var address = document.getElementById("modalAddress").value.trim();
    if (!name || !gender || !ageStr || !address) {
        alert("Please fill in all fields.");
        return;
    }
    var age = parseInt(ageStr, 10);
    if (isNaN(age) || age <= 0) {
        alert("Please enter a valid age.");
        return;
    }
    var modal = document.getElementById("customModal");
    var editId = modal.dataset.editId;
    var data = getLocalData();
    if (editId) {
        var id = parseInt(editId);
        // Manual findIndex
        var index = -1;
        for (var i = 0; i < data.length; i++) {
            if (data[i].id === id) {
                index = i;
                break;
            }
        }
        if (index !== -1) {
            data[index] = { id: id, name: name, gender: gender, age: age, address: address };
        }
    }
    else {
        var newId = data.length > 0 ? Math.max.apply(Math, data.map(function (d) { return d.id; })) + 1 : 1;
        data.push({ id: newId, name: name, gender: gender, age: age, address: address });
    }
    setLocalData(data);
    closeModal();
    readAll();
}
function closeModal() {
    var modal = document.getElementById("customModal");
    modal.style.display = "none";
    clearModalInputs();
}
function clearModalInputs() {
    document.getElementById("modalName").value = "";
    document.getElementById("modalGender").value = "";
    document.getElementById("modalAge").value = "";
    document.getElementById("modalAddress").value = "";
}
window.readAll = readAll;
window.add = add;
window.edit = edit;
window.delet = delet;
window.saveModal = saveModal;
window.closeModal = closeModal;
