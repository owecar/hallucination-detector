
const DBADMIN_KEY = "hallucination_dbadmin_demo_v1";

// Sample database for Code Version 2
const DBADMIN_SCHEMA = {
    app_user: {
        key: "id",
        columns: ["id", "username", "role", "created_at"],
        readonly: ["id", "created_at"]
    },

    saved_query: {
        key: "id",
        columns: ["id", "question", "answer", "created_at"],
        readonly: ["id", "created_at"]
    },

    ai_claim: {
        key: "ai_claim_id",
        columns: ["ai_claim_id", "ai_claim_text", "created_at"],
        readonly: ["ai_claim_id", "created_at"]
    },

    result: {
        key: "result_id",
        columns: ["result_id", "ai_claim_id", "verdict", "score", "created_at"],
        readonly: ["result_id", "created_at"]
    }
};

const DBADMIN_SAMPLE = {
    app_user: [
        {
            id: 1,
            username: "demo_user",
            role: "USER",
            created_at: "2026-10-18 14:25"
        },
        {
            id: 2,
            username: "demo_admin",
            role: "ADMIN",
            created_at: "2026-10-19 09:10"
        },
        {
            id: 3,
            username: "demo_dbadmin",
            role: "DB_ADMIN",
            created_at: "2026-10-20 11:45"
        }
    ],

    saved_query: [
        {
            id: 101,
            question: "What is the difference between TCP and UDP?",
            answer: "TCP is a connection-oriented protocol that ensures reliable, ordered delivery. UDP is connectionless and does not guarantee delivery or order. TCP is commonly used for web browsing and file transfers, while UDP is used for gaming, streaming, and voice calls.",
            created_at: "2026-10-20 13:15"
        }
    ],

    ai_claim: [
        {
            ai_claim_id: 201,
            ai_claim_text: "TCP is a connection-oriented protocol.",
            created_at: "2026-10-20 13:15"
        }
    ],

    result: [
        {
            result_id: 301,
            ai_claim_id: 201,
            verdict: "SUPPORTED",
            score: 0.99,
            created_at: "2026-10-20 13:16"
        }
    ]
};

let dbadminData = {};

// Load and save sample data
function dbadminLoad() {
    try {
        const saved = JSON.parse(localStorage.getItem(DBADMIN_KEY));

        if (saved && Object.keys(DBADMIN_SCHEMA).every(function (table) {
            return Array.isArray(saved[table]);
        })) {
            return saved;
        }
    } catch (error) {
        console.log("Could not load sample data.");
    }

    return JSON.parse(JSON.stringify(DBADMIN_SAMPLE));
}

function dbadminPersist() {
    try {
        localStorage.setItem(DBADMIN_KEY, JSON.stringify(dbadminData));
        return true;
    } catch (error) {
        console.log("Could not save sample data.");
        return false;
    }
}

function dbadminElement(id) {
    return document.getElementById(id);
}

function dbadminMake(tag, content, className) {
    const element = document.createElement(tag);

    if (content !== undefined) {
        element.textContent = String(content);
    }

    if (className) {
        element.className = className;
    }

    return element;
}

function dbadminCurrentTable() {
    const select = dbadminElement("dbadmin-table-select");

    return select ? select.value : "saved_query";
}

function dbadminSelectedRecord() {
    const table = dbadminCurrentTable();
    const select = dbadminElement("dbadmin-record-select");

    if (!select) {
        return null;
    }

    const key = DBADMIN_SCHEMA[table].key;

    return dbadminData[table].find(function (record) {
        return String(record[key]) === select.value;
    });
}

// Display and search database records
function dbadminRenderTable(table, records) {
    const container = dbadminElement("dbadmin-table-container");

    if (!container) {
        return;
    }

    container.replaceChildren();

    if (records.length === 0) {
        container.appendChild(
            dbadminMake("p", "No records found.", "dbadmin-empty")
        );
        return;
    }

    const tableElement = dbadminMake("table", undefined, "dbadmin-table");
    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");

    DBADMIN_SCHEMA[table].columns.forEach(function (column) {
        headerRow.appendChild(dbadminMake("th", column));
    });

    thead.appendChild(headerRow);

    const tbody = document.createElement("tbody");

    records.forEach(function (record) {
        const row = document.createElement("tr");

        DBADMIN_SCHEMA[table].columns.forEach(function (column) {
            const cell = document.createElement("td");
            const value = record[column] ?? "";

            if (column === "role" || column === "verdict") {
                cell.appendChild(
                    dbadminMake("span", value, "dbadmin-pill")
                );
            } else {
                cell.textContent = String(value);
            }

            row.appendChild(cell);
        });

        tbody.appendChild(row);
    });

    tableElement.appendChild(thead);
    tableElement.appendChild(tbody);
    container.appendChild(tableElement);
}

function dbadminRenderBrowse() {
    const table = dbadminCurrentTable();
    const search = dbadminElement("dbadmin-search");

    const query = search
        ? search.value.trim().toLowerCase()
        : "";

    const records = dbadminData[table].filter(function (record) {
        return Object.values(record).some(function (value) {
            return String(value).toLowerCase().includes(query);
        });
    });

    dbadminRenderTable(table, records);

    const count = dbadminElement("dbadmin-row-count");

    if (count) {
        count.textContent =
            "Showing " + records.length + " of " +
            dbadminData[table].length + " records";
    }
}

// Select a record to update or delete
function dbadminPopulateRecords() {
    const table = dbadminCurrentTable();
    const select = dbadminElement("dbadmin-record-select");

    if (!select) {
        return;
    }

    select.replaceChildren();

    dbadminData[table].forEach(function (record) {
        const key = DBADMIN_SCHEMA[table].key;

        const description =
            record.username ||
            record.question ||
            record.ai_claim_text ||
            record.verdict ||
            "Record";

        const option = document.createElement("option");

        option.value = String(record[key]);
        option.textContent = record[key] + " - " + description;

        select.appendChild(option);
    });

    dbadminRenderForm();
}

// Build the update form
function dbadminRenderForm() {
    const container = dbadminElement("dbadmin-form-fields");

    if (!container) {
        return;
    }

    container.replaceChildren();

    const table = dbadminCurrentTable();
    const record = dbadminSelectedRecord();

    if (!record) {
        container.appendChild(
            dbadminMake("p", "No records available.")
        );

        dbadminRenderTable(table, []);
        return;
    }

    DBADMIN_SCHEMA[table].columns.forEach(function (column) {
        const largeField =
            column === "question" ||
            column === "answer" ||
            column === "ai_claim_text";

        const label = dbadminMake(
            "label",
            undefined,
            largeField ? "dbadmin-field-full" : ""
        );

        label.appendChild(
            dbadminMake("span", column, "dbadmin-label")
        );

        let field;

        if (largeField) {
            field = dbadminMake("textarea", undefined, "dbadmin-textarea");
            field.rows = 3;
        } else if (column === "role" || column === "verdict") {
            field = dbadminMake("select", undefined, "dbadmin-select");

            const options = column === "role"
                ? ["USER", "ADMIN", "DB_ADMIN"]
                : ["SUPPORTED", "UNSUPPORTED", "UNCERTAIN"];

            options.forEach(function (value) {
                const option = dbadminMake("option", value);
                option.value = value;
                field.appendChild(option);
            });
        } else {
            field = dbadminMake("input", undefined, "dbadmin-input");

            field.type = column === "score" ? "number" : "text";

            if (column === "score") {
                field.min = "0";
                field.max = "1";
                field.step = "0.01";
            }
        }

        field.name = column;
        field.value = String(record[column] ?? "");
        field.disabled = DBADMIN_SCHEMA[table].readonly.includes(column);

        if (!field.disabled) {
            field.required = true;
        }

        label.appendChild(field);
        container.appendChild(label);
    });

    dbadminRenderTable(table, dbadminData[table]);

    const feedback = dbadminElement("dbadmin-feedback");

    if (feedback) {
        feedback.textContent = "";
    }
}

// Save changes to a sample record
function dbadminSave(event) {
    event.preventDefault();

    const table = dbadminCurrentTable();
    const record = dbadminSelectedRecord();
    const form = dbadminElement("dbadmin-edit-form");
    const feedback = dbadminElement("dbadmin-feedback");

    if (!record || !form) {
        return;
    }

    if (!form.reportValidity()) {
        return;
    }

    const formData = new FormData(form);

    for (const column of DBADMIN_SCHEMA[table].columns) {
        if (DBADMIN_SCHEMA[table].readonly.includes(column)) {
            continue;
        }

        const value = String(formData.get(column) || "").trim();

        if (!value) {
            feedback.textContent = column + " cannot be empty.";
            feedback.className = "dbadmin-error";
            return;
        }

        if (column === "score") {
            const score = Number(value);

            if (!Number.isFinite(score) || score < 0 || score > 1) {
                feedback.textContent = "Score must be between 0 and 1.";
                feedback.className = "dbadmin-error";
                return;
            }

            record[column] = score;
        } else if (column === "ai_claim_id") {
            const claimId = Number(value);

            const exists = dbadminData.ai_claim.some(function (claim) {
                return claim.ai_claim_id === claimId;
            });

            if (!Number.isInteger(claimId) || !exists) {
                feedback.textContent = "Enter an existing Claim ID.";
                feedback.className = "dbadmin-error";
                return;
            }

            record[column] = claimId;
        } else {
            record[column] = value;
        }
    }

    const saved = dbadminPersist();

    dbadminPopulateRecords();

    feedback.textContent = saved
        ? "Sample record updated successfully."
        : "Could not save changes to browser storage.";

    feedback.className = saved ? "dbadmin-success" : "dbadmin-error";
}

// Delete a sample record
function dbadminDeleteRecord() {
    const table = dbadminCurrentTable();
    const record = dbadminSelectedRecord();
    const feedback = dbadminElement("dbadmin-feedback");

    if (!record) {
        return;
    }

    const key = DBADMIN_SCHEMA[table].key;
    const id = record[key];

    if (table === "ai_claim") {
        const referenced = dbadminData.result.some(function (result) {
            return String(result.ai_claim_id) === String(id);
        });

        if (referenced) {
            feedback.textContent =
                "Cannot delete a claim referenced by a result.";
            feedback.className = "dbadmin-error";
            return;
        }
    }

    const confirmed = window.confirm(
        "Are you sure you want to delete record " + id + "?"
    );

    if (!confirmed) {
        return;
    }

    dbadminData[table] = dbadminData[table].filter(function (item) {
        return String(item[key]) !== String(id);
    });

    const saved = dbadminPersist();

    dbadminPopulateRecords();

    feedback.textContent = saved
        ? "Sample record deleted successfully."
        : "Record deleted in memory, but could not be saved.";

    feedback.className = saved ? "dbadmin-success" : "dbadmin-error";
}

function dbadminSetupBrowse() {
    const tableSelect = dbadminElement("dbadmin-table-select");
    const search = dbadminElement("dbadmin-search");

    if (tableSelect) {
        tableSelect.addEventListener("change", dbadminRenderBrowse);
    }

    if (search) {
        search.addEventListener("input", dbadminRenderBrowse);
    }

    dbadminRenderBrowse();
}

function dbadminSetupEditor() {
    const tableSelect = dbadminElement("dbadmin-table-select");
    const recordSelect = dbadminElement("dbadmin-record-select");
    const form = dbadminElement("dbadmin-edit-form");
    const cancelButton = dbadminElement("dbadmin-cancel");
    const deleteButton = dbadminElement("dbadmin-delete");

    if (tableSelect) {
        tableSelect.addEventListener("change", dbadminPopulateRecords);
    }

    if (recordSelect) {
        recordSelect.addEventListener("change", dbadminRenderForm);
    }

    if (form) {
        form.addEventListener("submit", dbadminSave);
    }

    if (cancelButton) {
        cancelButton.addEventListener("click", dbadminRenderForm);
    }

    if (deleteButton) {
        deleteButton.addEventListener("click", dbadminDeleteRecord);
    }

    dbadminPopulateRecords();
}

function dbadminInitialize() {
    dbadminData = dbadminLoad();

    const page = document.body.dataset.dbadminPage;

    if (page === "view") {
        dbadminSetupBrowse();
    } else if (page === "update") {
        dbadminSetupEditor();
    }
}

document.addEventListener("DOMContentLoaded", dbadminInitialize);
