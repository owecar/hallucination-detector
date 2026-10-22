
"use strict";

const adminPanels = {
    gemini: {
        title: "Gemini API Configuration",
        description: "Review demonstration AI model settings.",
        items: [
            "Provider: Google Gemini",
            "Model: Configurable by backend",
            "API key: Managed securely on server"
        ]
    },
    wikipedia: {
        title: "Wikipedia API Configuration",
        description: "Review evidence retrieval settings.",
        items: [
            "Evidence source: Wikipedia",
            "Retrieval status: Demo only",
            "Source verification: Planned"
        ]
    },
    fever: {
        title: "FEVER Dataset Management",
        description: "Review benchmark dataset configuration.",
        items: [
            "Dataset: FEVER",
            "Supported claims",
            "Refuted claims",
            "Not Enough Information claims"
        ]
    },
    evaluation: {
        title: "Evaluation Metrics",
        description: "Metrics available in the planned system.",
        items: [
            "Precision and Recall: Not evaluated",
            "Hallucination frequency: Not evaluated",
            "Faithfulness: Not evaluated",
            "Response latency: Not evaluated"
        ]
    },
    users: {
        title: "Manage Users",
        description: "Demonstration of supported user roles.",
        items: [
            "USER: Student or Researcher",
            "ADMIN: System configuration",
            "DB_ADMIN: Database management"
        ]
    }
};

function adminElement(id) {
    return document.getElementById(id);
}

function createAdminItem(text) {
    const item = document.createElement("li");
    item.textContent = text;
    return item;
}

function renderAdminPanel(panelName) {
    const data = adminPanels[panelName];
    if (!data) return;

    adminElement("admin-panel-title").textContent = data.title;
    adminElement("admin-panel-description").textContent =
        data.description;

    const content = adminElement("admin-panel-content");
    const list = document.createElement("ul");

    data.items.forEach(function(item) {
        list.appendChild(createAdminItem(item));
    });

    content.replaceChildren(list);

    const panel = adminElement("admin-panel");
    panel.hidden = false;
    panel.scrollIntoView({
        behavior: "smooth",
        block: "nearest"
    });
}

function handleAdminCardClick(event) {
    const panelName = event.currentTarget.dataset.panel;
    renderAdminPanel(panelName);
}

function closeAdminPanel() {
    adminElement("admin-panel").hidden = true;
}

function setupAdminDashboard() {
    const cards = document.querySelectorAll(
        ".admin-card[data-panel]"
    );

    cards.forEach(function(card) {
        card.addEventListener("click", handleAdminCardClick);
    });

    adminElement("admin-close").addEventListener(
        "click", closeAdminPanel
    );
}

document.addEventListener(
    "DOMContentLoaded", setupAdminDashboard
);
