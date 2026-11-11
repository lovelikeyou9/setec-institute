interface RecordItem {
  id: number;
  name: string;
  gender: string;
  age: number;
  address: string;
}

function getLocalData(): RecordItem[] {
  const stored = localStorage.getItem("object");
  return stored ? JSON.parse(stored) : [];
}

function setLocalData(data: RecordItem[]): void {
  localStorage.setItem("object", JSON.stringify(data));
}

function readAll(): void {
  const table = document.querySelector(".data_table") as HTMLTableSectionElement;
  const data = getLocalData().sort((a, b) => a.id - b.id);

  table.innerHTML = data
    .map(
      (item) => `
    <tr>
      <td>${item.id}</td>
      <td>${item.name}</td>
      <td>${item.gender}</td>
      <td>${item.age}</td>
      <td>${item.address}</td>
      <td>
        <button onclick="edit(${item.id})">Edit</button>
        <button onclick="delet(${item.id})">Delete</button>
      </td>
    </tr>
  `
    )
    .join("");
}

function add(): void {
  const modal = document.getElementById("customModal") as HTMLDivElement;
  (document.getElementById("modalTitle") as HTMLElement).innerText = "Add Record";

  clearModalInputs();

  delete modal.dataset.editId;
  modal.style.display = "flex";
}

function edit(id: number): void {
  const data = getLocalData();

  // Manual find
  let record: RecordItem | undefined = undefined;
  for (const d of data) {
    if (d.id === id) {
      record = d;
      break;
    }
  }
  if (!record) return;

  (document.getElementById("modalName") as HTMLInputElement).value = record.name;
  (document.getElementById("modalGender") as HTMLSelectElement).value = record.gender;
  (document.getElementById("modalAge") as HTMLInputElement).value = record.age.toString();
  (document.getElementById("modalAddress") as HTMLInputElement).value = record.address;

  const modal = document.getElementById("customModal") as HTMLDivElement;
  modal.dataset.editId = id.toString();
  (document.getElementById("modalTitle") as HTMLElement).innerText = "Edit Record";
  modal.style.display = "flex";
}

function delet(id: number): void {
  if (!confirm("Are you sure you want to delete this record?")) return;

  const data = getLocalData();
  const newData = data.filter((d) => d.id !== id);
  setLocalData(newData);
  readAll();
}

function saveModal(): void {
  const name = (document.getElementById("modalName") as HTMLInputElement).value.trim();
  const gender = (document.getElementById("modalGender") as HTMLSelectElement).value;
  const ageStr = (document.getElementById("modalAge") as HTMLInputElement).value;
  const address = (document.getElementById("modalAddress") as HTMLInputElement).value.trim();

  if (!name || !gender || !ageStr || !address) {
    alert("Please fill in all fields.");
    return;
  }

  const age = parseInt(ageStr, 10);
  if (isNaN(age) || age <= 0) {
    alert("Please enter a valid age.");
    return;
  }

  const modal = document.getElementById("customModal") as HTMLDivElement;
  const editId = modal.dataset.editId;
  const data = getLocalData();

  if (editId) {
    const id = parseInt(editId);

    // Manual findIndex
    let index = -1;
    for (let i = 0; i < data.length; i++) {
      if (data[i].id === id) {
        index = i;
        break;
      }
    }
    if (index !== -1) {
      data[index] = { id, name, gender, age, address };
    }
  } else {
    const newId = data.length > 0 ? Math.max(...data.map((d) => d.id)) + 1 : 1;
    data.push({ id: newId, name, gender, age, address });
  }

  setLocalData(data);
  closeModal();
  readAll();
}

function closeModal(): void {
  const modal = document.getElementById("customModal") as HTMLDivElement;
  modal.style.display = "none";
  clearModalInputs();
}

function clearModalInputs(): void {
  (document.getElementById("modalName") as HTMLInputElement).value = "";
  (document.getElementById("modalGender") as HTMLSelectElement).value = "";
  (document.getElementById("modalAge") as HTMLInputElement).value = "";
  (document.getElementById("modalAddress") as HTMLInputElement).value = "";
}

(window as any).readAll = readAll;
(window as any).add = add;
(window as any).edit = edit;
(window as any).delet = delet;
(window as any).saveModal = saveModal;
(window as any).closeModal = closeModal;
