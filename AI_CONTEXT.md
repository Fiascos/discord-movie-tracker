#### **1. Project Overview**
**Kinota** is a Discord bot developed in **Java 21**, designed to automate movie tracking for a community. The bot processes Discord **Slash Commands** and synchronizes data (additions, deletions, statistics, and history) with a **Google Sheet** in real-time.

#### **2. Technology Stack**
*   **Language:** Java 21 (OpenJDK 21.0.x).
*   **Build System:** Gradle 8.14.4.
*   **Structure:** Multi-module project; the core logic resides in the **app** module.
*   **Dependencies:**
    *   `net.dv8tion:JDA`: Discord API interaction (Gateway, Events, Slash Commands).
    *   `com.google.api-client`: Integration with Google Sheets & Drive API v3.
    *   `io.github.cdimascio:dotenv-java`: Configuration management via `.env` files.

#### **3. Source Code Structure**
All Java classes are located in the `com.kinota` package under `app/src/main/java/com/kinota/`:
1.  **App.java:** Entry point; initializes Dotenv, services, and the JDA instance.
2.  **CommandListener.java:** Event listener that intercepts `onSlashCommandInteraction` and triggers business logic.
3.  **GoogleSheetsService.java:** Service class handling read/write/delete operations via Google Sheets API.

#### **4. Database Schema (Google Sheets)**
The bot uses a Google Sheet named **"Аркуш1"** (referenced as `Аркуш1!A:E` in range queries).
*   **Column A:** Movie Title (String).
*   **Column B:** Release Year (Integer/String).
*   **Column C:** Rating (Double/String).
*   **Column D:** Watch Date (Date/String).
*   **Column E:** Watch Count (Integer).

#### **5. Command List**
*   **/add:** Adds a new movie to the end of the sheet.
*   **/stats:** Displays community viewing analytics and average ratings.
*   **/history:** Lists the last 5 watched movies in a Discord Embed.
*   **/delete:** Removes the last entry or a specific movie from the sheet.
*   **/help:** Provides instructions and a list of available commands.

#### **6. Production Infrastructure (Oracle Cloud)**
The bot is deployed on an **Ubuntu VM** within **Oracle Cloud Infrastructure (OCI)**.
*   **User:** `ubuntu`.
*   **Working Directory:** `/home/ubuntu/kinota-bot/`.
*   **Executable:** `/home/ubuntu/kinota-bot/app/build/libs/app.jar`.
*   **Secrets:** `.env` and `credentials.json` are stored in the root directory and excluded via `.gitignore`.

#### **7. AI Development Rules**
1.  **Security:** Never hardcode tokens; always use `Dotenv`.
2.  **Asynchronicity:** Always use `event.deferReply()` for Google Sheets operations to prevent Discord timeouts.
3.  **Fault Tolerance:** Use `try-catch` blocks for all API calls to ensure the bot informs the user of errors instead of crashing.
4.  **Java 21 Style:** Use modern constructs like Records and Pattern Matching where appropriate.