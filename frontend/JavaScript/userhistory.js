
const HISTORY_KEY = "hallucination_demo_history";

// Sample question history
const sampleHistory = [
    {
        id: 1,
        question: "What is the difference between TCP and UDP, and when would you use each protocol?",
        answer: "TCP provides reliable, ordered delivery of data. UDP is connectionless and does not guarantee delivery or order.",
        date: "2026-10-20",
        verdict: "Supported",
        confidence: 99,
        explanation: "The answer correctly describes the main differences between TCP and UDP."
    },
];

let historyData = [];

// Load saved questions
function loadHistory() {
    try {
        const saved = JSON.parse(localStorage.getItem(HISTORY_KEY));

        if (Array.isArray(saved)) {
            return saved;
        }
    } catch (error) {
        console.log("Using sample question history.");
    }

    return sampleHistory;
}

// Save question history
function saveHistory() {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(historyData));
}

// Display saved questions
function showHistory() {
    const container = document.getElementById("userhistory-list");

    container.innerHTML = "";

    if (historyData.length === 0) {
        container.textContent = "No saved questions found.";
        return;
    }

    historyData.forEach(function (record) {
        const card = document.createElement("article");
        card.className = "userhistory-card";

        const header = document.createElement("div");
        header.className = "userhistory-card-header";

        const question = document.createElement("h2");
        question.textContent = record.question;

        const date = document.createElement("span");
        date.className = "userhistory-date";
        date.textContent = record.date;

        header.append(question, date);

        const answerLabel = document.createElement("p");
        answerLabel.className = "userhistory-label";
        answerLabel.textContent = "AI ANSWER";

        const answer = document.createElement("p");
        answer.className = "userhistory-answer";
        answer.textContent = record.answer;

        const verification = document.createElement("section");
        verification.className = "userhistory-verification";

        const title = document.createElement("h3");
        title.textContent = "Verification Result";

        const verdict = document.createElement("span");
        verdict.className = "userhistory-verdict " +
            record.verdict.toLowerCase();
        verdict.textContent = record.verdict;

        const explanation = document.createElement("p");
        explanation.className = "userhistory-answer";
        explanation.textContent = record.explanation;

        const scoreRow = document.createElement("div");
        scoreRow.className = "userhistory-score-row";

        const scoreLabel = document.createElement("span");
        scoreLabel.className = "userhistory-label";
        scoreLabel.textContent = "Confidence Score";

        const scoreValue = document.createElement("strong");
        scoreValue.textContent = record.confidence + "%";

        scoreRow.append(scoreLabel, scoreValue);

        const scoreTrack = document.createElement("div");
        scoreTrack.className = "userhistory-score-track";

        const scoreFill = document.createElement("div");
        scoreFill.className = "userhistory-score-fill";
        scoreFill.style.width = record.confidence + "%";

        scoreTrack.appendChild(scoreFill);

        verification.append(title, verdict, explanation, scoreRow, scoreTrack);

        const actions = document.createElement("div");
        actions.className = "userhistory-card-actions";

        const deleteButton = document.createElement("button");
        deleteButton.className = "userhistory-delete";
        deleteButton.textContent = "Delete Question";

        deleteButton.addEventListener("click", function () {
            deleteQuestion(record.id);
        });

        actions.appendChild(deleteButton);

        card.append(header, answerLabel, answer, verification, actions);
        container.appendChild(card);
    });
}

// Delete a saved question
function deleteQuestion(id) {
    if (!confirm("Are you sure you want to delete this question?")) {
        return;
    }

    historyData = historyData.filter(function (record) {
        return record.id !== id;
    });

    saveHistory();
    showHistory();

    const feedback = document.getElementById("userhistory-feedback");

    if (feedback) {
        feedback.textContent = "Question deleted successfully.";
    }
}

function startHistory() {
    historyData = loadHistory();
    showHistory();
}

document.addEventListener("DOMContentLoaded", startHistory);
