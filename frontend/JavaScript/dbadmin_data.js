
const STORAGE_KEY = "hallucination_dbadmin_demo_v1";

// Sample database
const sampleData = {
    app_user: [
        { id: 1, username: "demo_user", role: "USER", created_at: "2026-10-18" },
        { id: 2, username: "demo_admin", role: "ADMIN", created_at: "2026-10-19" },
        { id: 3, username: "demo_dbadmin", role: "DB_ADMIN", created_at: "2026-10-20" }
    ],

    saved_query: [
        {
            id: 101,
            question: "What is the difference between TCP and UDP?",
            answer: "TCP provides reliable, ordered delivery. UDP is connectionless and does not guarantee delivery or order.",
            created_at: "2026-10-20"
        }
    ],

    ai_claim: [
        {
            ai_claim_id: 201,
            ai_claim_text: "TCP is a connection-oriented protocol.",
            created_at: "2026-10-20"
        }
    ],

    result: [
        {
            result_id: 301,
            ai_claim_id: 201,
            verdict: "SUPPORTED",
            score: 0.99,
            created_at: "2026-10-20"
        }
    ]
};

const primaryKeys = {
    app_user: "id",
    saved_query: "id",
    ai_claim: "ai_claim_id",
    result: "result_id"
};

let database = {};

function loadData() {
    try {
        const saved = JSON.parse(localStorage.getItem(STORAGE_KEY));

        if (saved && Object.keys(sampleData).every(table =>
            Array.isArray(saved[table]))) {
            return saved;
        }
    } catch (error) {
        console.log("Using sample data.");
    }

    return JSON.parse(JSON.stringify(sampleData));
}

function saveData() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(database));
}

function selectedTable() {
    return document.getElementById("dbadmin-table-select").value;
}

function showRecords() {
    const table = selectedTable();
    const container = document.getElementById("dbadmin-table-container");

    const records = database[table];

    container.innerHTML = "";

    if (records.length === 0) {
        container.textContent = "No records found.";
        return;
    }

    const tableElement = document.createElement("table");
    tableElement.className = "dbadmin-table";

    const columns = Object.keys(database[table][0] || sampleData[table][0]);

    const header = tableElement.insertRow();

    columns.forEach(column => {
        const cell = document.createElement("th");
        cell.textContent = column;
        header.appendChild(cell);
    });

    records.forEach(record => {
        const row = tableElement.insertRow();

        columns.forEach(column => {
            const cell = row.insertCell();
            cell.textContent = record[column];
        });
    });

    container.appendChild(tableElement);

    const count = document.getElementById("dbadmin-row-count");

    if (count) {
        count.textContent = `Showing ${records.length} records`;
    }
}

function loadRecords() {
    const table = selectedTable();
    const select = document.getElementById("dbadmin-record-select");

    select.innerHTML = "";

    database[table].forEach(record => {
        const id = record[primaryKeys[table]];
        const option = document.createElement("option");

        option.value = id;
        option.textContent = id + " - " +
            (record.username || record.question ||
             record.ai_claim_text || record.verdict);

        select.appendChild(option);
    });

    showEditForm();
}

function selectedRecord() {
    const table = selectedTable();
    const id = document.getElementById("dbadmin-record-select").value;

    return database[table].find(record =>
        String(record[primaryKeys[table]]) === id
    );
}

function showEditForm() {
    const table = selectedTable();
    const record = selectedRecord();
    const container = document.getElementById("dbadmin-form-fields");

    container.innerHTML = "";

    if (!record) {
        container.textContent = "No records available.";
        return;
    }

    Object.keys(record).forEach(column => {
        const label = document.createElement("label");
        const title = document.createElement("span");

        title.className = "dbadmin-label";
        title.textContent = column;

        const large = ["question", "answer", "ai_claim_text"].includes(column);
        const field = document.createElement(large ? "textarea" : "input");

        field.className = large ? "dbadmin-textarea" : "dbadmin-input";
        field.name = column;
        field.value = record[column];

        if (large) {
            label.className = "dbadmin-field-full";
            field.rows = 3;
        }

        if (column === primaryKeys[table] || column === "created_at") {
            field.disabled = true;
        } else {
            field.required = true;
        }

        label.append(title, field);
        container.appendChild(label);
    });

    const feedback = document.getElementById("dbadmin-feedback");

    if (feedback) {
        feedback.textContent = "";
    }
}

function updateRecord(event) {
    event.preventDefault();

    const record = selectedRecord();

    if (!record) {
        return;
    }

    const form = document.getElementById("dbadmin-edit-form");

    if (!form.reportValidity()) {
        return;
    }

    const data = new FormData(form);

    for (const [key, value] of data.entries()) {
        const cleaned = String(value).trim();

        if (!cleaned) {
            alert("Fields cannot be empty.");
            return;
        }

        if (key === "score" && (Number(cleaned) < 0 || Number(cleaned) > 1)) {
            alert("Score must be between 0 and 1.");
            return;
        }

        if (key === "ai_claim_id") {
            const exists = database.ai_claim.some(claim =>
                claim.ai_claim_id === Number(cleaned)
            );

            if (!exists) {
                alert("Claim ID does not exist.");
                return;
            }
        }
    }

    for (const [key, value] of data.entries()) {
        record[key] = ["score", "ai_claim_id"].includes(key)
            ? Number(value)
            : value.trim();
    }

    saveData();
    loadRecords();

    document.getElementById("dbadmin-feedback").textContent =
        "Record updated successfully.";
}

function deleteRecord() {
    const table = selectedTable();
    const record = selectedRecord();

    if (!record) {
        return;
    }

    const id = record[primaryKeys[table]];

    if (table === "ai_claim" && database.result.some(result =>
        String(result.ai_claim_id) === String(id))) {
        alert("Cannot delete a claim referenced by a result.");
        return;
    }

    if (!confirm("Delete record " + id + "?")) {
        return;
    }

    database[table] = database[table].filter(item =>
        item !== record
    );

    saveData();
    loadRecords();

    document.getElementById("dbadmin-feedback").textContent =
        "Record deleted successfully.";
}

function startDatabaseAdmin() {
    database = loadData();

    const page = document.body.dataset.dbadminPage;
    const tableSelect = document.getElementById("dbadmin-table-select");

   if (page === "view") {
    tableSelect.addEventListener("change", showRecords);
    showRecords();
}

    if (page === "update") {
        tableSelect.addEventListener("change", loadRecords);

        document.getElementById("dbadmin-record-select")
            .addEventListener("change", showEditForm);

        document.getElementById("dbadmin-edit-form")
            .addEventListener("submit", updateRecord);

        document.getElementById("dbadmin-delete")
            .addEventListener("click", deleteRecord);

        document.getElementById("dbadmin-cancel")
            ?.addEventListener("click", showEditForm);

        loadRecords();
    }
}

document.addEventListener("DOMContentLoaded", startDatabaseAdmin);
