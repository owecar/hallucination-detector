const tableSelect = document.getElementById("dbadmin-table-select");
const recordSelect = document.getElementById("dbadmin-record-select");
const editForm = document.getElementById("dbadmin-edit-form");
const deleteForm = document.getElementById("dbadmin-delete-form");
const fields = document.getElementById("dbadmin-form-fields");
const feedback = document.getElementById("dbadmin-feedback");
const saveButton = document.getElementById("dbadmin-save");
const deleteButton = document.getElementById("dbadmin-delete");

function addField(label, name, value, multiline = false) {
    const wrapper = document.createElement("label");
    const heading = document.createElement("span");
    heading.className = "dbadmin-label";
    heading.textContent = label;
    const control = document.createElement(multiline ? "textarea" : "input");
    control.className = "dbadmin-input";
    control.name = name;
    control.value = value || "";
    control.required = name !== "answer";
    if (multiline) control.rows = 5;
    wrapper.append(heading, control);
    fields.appendChild(wrapper);
}

function showSelectedRecord() {
    fields.replaceChildren();
    feedback.textContent = "";
    const selected = recordSelect.selectedOptions[0];
    const exists = Boolean(selected && selected.value);
    saveButton.disabled = !exists;
    deleteButton.disabled = !exists;
    if (!exists) {
        feedback.textContent = "No records available in this table.";
        return;
    }
    const table = tableSelect.value;
    const id = encodeURIComponent(selected.value);
    editForm.action = `/dbadmin/update/${table}/${id}`;
    deleteForm.action = `/dbadmin/delete/${table}/${id}`;
    if (table === "saved_query") {
        addField("Question", "question", selected.dataset.question);
        addField("Answer", "answer", selected.dataset.answer, true);
    } else {
        addField("Username", "username", selected.dataset.username);
        const wrapper = document.createElement("label");
        const label = document.createElement("span");
        label.className = "dbadmin-label";
        label.textContent = "Role";
        const roleSelect = document.createElement("select");
        roleSelect.className = "dbadmin-select";
        roleSelect.name = "role";
        for (const role of ["USER", "ADMIN", "DB_ADMIN"]) {
            const option = new Option(role, role);
            option.selected = selected.dataset.role === role;
            roleSelect.add(option);
        }
        wrapper.append(label, roleSelect);
        fields.appendChild(wrapper);
    }
}

function showTableRecords() {
    const source = document.getElementById(
        tableSelect.value === "saved_query" ? "dbadmin-source-queries" : "dbadmin-source-users"
    );
    recordSelect.replaceChildren();
    for (const record of source.options) {
        const option = record.cloneNode(true);
        recordSelect.appendChild(option);
    }
    showSelectedRecord();
}

if (tableSelect && recordSelect && editForm && deleteForm && fields && feedback && saveButton && deleteButton) {
tableSelect.addEventListener("change", showTableRecords);
recordSelect.addEventListener("change", showSelectedRecord);
document.getElementById("dbadmin-cancel").addEventListener("click", showSelectedRecord);
deleteButton.addEventListener("click", () => {
    if (!recordSelect.value) return;
    if (window.confirm(`Permanently delete ${tableSelect.value} record #${recordSelect.value}?`)) {
        deleteForm.requestSubmit();
    }
});
showTableRecords();
}
