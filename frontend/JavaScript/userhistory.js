"use strict";
// Frontend-only account preview
const HISTORY_ACCOUNT_KEY = "hallucination-demo-active-user";
const HISTORY_DATA_PREFIX = "hallucination-demo-history:";

function getCurrentDemoUser() {
    return sessionStorage.getItem(HISTORY_ACCOUNT_KEY) || "demo-user";
}

function getHistoryStorageKey(username) {
    return HISTORY_DATA_PREFIX + username.toLowerCase();
}

function getStarterHistory(username) {
    return [
        {
            id: "sample-1",
            question: "What is the capital of France?",
            answer: "Paris is the capital of France.",
            date: "2026-10-20T14:30:00",
            verdict: "Supported",
            confidence: 98,
            explanation: "The answer agrees with widely established geographical information."
        },
        {
            id: "sample-2",
            question: "Can humans breathe on Mars without equipment?",
            answer: "Humans cannot safely breathe the Martian atmosphere without life-support equipment.",
            date: "2026-10-19T10:15:00",
            verdict: "Supported",
            confidence: 96,
            explanation: "Mars has a very thin atmosphere made mostly of carbon dioxide."
        }
    ];
}

function loadHistory(username) {
    const key = getHistoryStorageKey(username);
    const saved = localStorage.getItem(key);

    if (saved !== null) {
        try {
            const records = JSON.parse(saved);
            return Array.isArray(records) ? records : [];
        } catch (error) {
            console.warn("Unable to read demonstration history", error);
            return [];
        }
    }

    const starter = getStarterHistory(username);
    saveHistory(username, starter);
    return starter;
}

function saveHistory(username, records) {
    localStorage.setItem(getHistoryStorageKey(username), JSON.stringify(records));
}

function makeTextElement(tagName, className, value) {
    const element = document.createElement(tagName);
    element.className = className;
    element.textContent = value;
    return element;
}

function formatHistoryDate(value) {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? "Date unavailable" : date.toLocaleDateString();
}

function makeHistoryCard(record) {
    const article = document.createElement("article");
    article.className = "userhistory-card";

    const heading = document.createElement("div");
    heading.className = "userhistory-card-header";
    heading.appendChild(makeTextElement("h2", "", record.question));
    heading.appendChild(makeTextElement("span", "userhistory-date", formatHistoryDate(record.date)));
    article.appendChild(heading);

    article.appendChild(makeTextElement("p", "userhistory-label", "AI ANSWER"));
    article.appendChild(makeTextElement("p", "userhistory-answer", record.answer));

    const verification = document.createElement("section");
    verification.className = "userhistory-verification";
    verification.appendChild(makeTextElement("h3", "", "Verification Result"));

    const verdict = String(record.verdict || "Uncertain");
    const verdictClass = ["Supported", "Unsupported", "Uncertain"].includes(verdict)
        ? verdict.toLowerCase() : "uncertain";
    verification.appendChild(makeTextElement("span", "userhistory-verdict " + verdictClass, verdict));
    verification.appendChild(makeTextElement("p", "userhistory-answer", record.explanation || "No verification details available."));

    const score = Number(record.confidence);
    if (Number.isFinite(score) && score >= 0 && score <= 100) {
        const scoreRow = document.createElement("div");
        scoreRow.className = "userhistory-score-row";
        scoreRow.appendChild(makeTextElement("span", "userhistory-label", "Confidence Score"));
        scoreRow.appendChild(makeTextElement("strong", "", score + "%"));
        verification.appendChild(scoreRow);

        const track = document.createElement("div");
        track.className = "userhistory-score-track";
        track.setAttribute("role", "progressbar");
        track.setAttribute("aria-label", "Confidence Score");
        track.setAttribute("aria-valuemin", "0");
        track.setAttribute("aria-valuemax", "100");
        track.setAttribute("aria-valuenow", String(score));
        const fill = document.createElement("div");
        fill.className = "userhistory-score-fill";
        fill.style.width = score + "%";
        track.appendChild(fill);
        verification.appendChild(track);
    }

    article.appendChild(verification);
    const actions = document.createElement("div");
    actions.className = "userhistory-card-actions";
    const deleteButton = makeTextElement("button", "userhistory-delete", "Delete Question");
    deleteButton.type = "button";
    deleteButton.dataset.recordId = String(record.id);
    deleteButton.addEventListener("click", deleteHistoryRecord);
    actions.appendChild(deleteButton);
    article.appendChild(actions);
    return article;
}

function renderHistory() {
    const username = getCurrentDemoUser();
    const records = loadHistory(username);
    const list = document.getElementById("userhistory-list");
    const current = document.getElementById("userhistory-current-account");
    const input = document.getElementById("userhistory-account-name");

    current.textContent = "Viewing demo history for: " + username;
    input.value = username;
    list.replaceChildren();

    if (records.length === 0) {
        list.appendChild(makeTextElement("p", "userhistory-empty", "No saved questions for this demo account."));
        return;
    }

    records.sort((a, b) => new Date(b.date) - new Date(a.date));
    records.forEach(function(record) {
        list.appendChild(makeHistoryCard(record));
    });
}

function deleteHistoryRecord(event) {
    const recordId = event.currentTarget.dataset.recordId;
    const username = getCurrentDemoUser();
    const records = loadHistory(username);
    const record = records.find(function(item) {
        return String(item.id) === recordId;
    });

    if (!record || !window.confirm("Delete this question and its verification result?")) {
        return;
    }

    saveHistory(username, records.filter(function(item) {
        return String(item.id) !== recordId;
    }));
    renderHistory();
    document.getElementById("userhistory-feedback").textContent = "Question deleted from this demo account.";
}

function switchDemoAccount() {
    const input = document.getElementById("userhistory-account-name");
    const username = input.value.trim();
    const feedback = document.getElementById("userhistory-feedback");

    if (!/^[a-zA-Z0-9_-]{1,40}$/.test(username)) {
        feedback.textContent = "Use 1–40 letters, numbers, underscores, or hyphens.";
        return;
    }

    sessionStorage.setItem(HISTORY_ACCOUNT_KEY, username);
    feedback.textContent = "Switched demo account.";
    renderHistory();
}

function initializeUserHistory() {
    document.getElementById("userhistory-switch-account").addEventListener("click", switchDemoAccount);
    document.getElementById("userhistory-account-name").addEventListener("keydown", function(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            switchDemoAccount();
        }
    });
    renderHistory();
}

document.addEventListener("DOMContentLoaded", initializeUserHistory);
