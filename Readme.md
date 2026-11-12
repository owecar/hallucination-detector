# Hallucination Detection System

A Java Spring Boot web app that uses Gemini to answer questions and checks claims against Wikipedia excerpts. PostgreSQL stores accounts, question history, and claim results.

## Run locally

1. Install **JDK 17** and **PostgreSQL**, then create an empty database named `hallucination_db`.
2. Unzip the project and open the extracted folder in VS Code.
3. Run the commands below from the project root using your local PostgreSQL password and Gemini API key.

**Windows PowerShell**

```powershell
$env:DB_PASSWORD = "your-postgres-password"
$env:GEMINI_API_KEY = "your-gemini-api-key"
cd backend
.\mvnw.cmd spring-boot:run
```

4. Open [http://localhost:8080](http://localhost:8080).

The app defaults to `localhost:5432/hallucination_db` and the PostgreSQL user `postgres`. Set `DB_URL` or `DB_USERNAME` if your setup differs. 

## Main features

- **User** — Create an account, submit a question, view claim verdicts and evidence, and manage your own question history.
- **Admin** — View verification statistics, Gemini configuration status, and FEVER sample counts.
- **Database Admin** — Browse and manage saved questions and user records.

To try verification, ask **“Why does binary search require a sorted list?”** and click **Show Evidence**. Coverage shows how many claims received a definite verdict. The hallucination score shows the percentage of those claims that were contradicted.
