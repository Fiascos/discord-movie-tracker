# 🎬 Kinota: Discord Movie Tracker

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21">
  <img src="https://img.shields.io/badge/Discord-JDA-5865F2?style=for-the-badge&logo=discord" alt="JDA">
  <img src="https://img.shields.io/badge/Google%20Sheets-API-34A853?style=for-the-badge&logo=googlesheets" alt="Google Sheets API">
  <img src="https://img.shields.io/badge/Oracle%20Cloud-Always%20Free-F80000?style=for-the-badge&logo=oracle" alt="Oracle Cloud">
</p>

---

## 🌟 About the Project
**Kinota** is an intelligent assistant for movie buffs on Discord. The bot automates the process of keeping track of watched movies directly within Google Sheets.

### ✨ Features:
* **Smart Counting:** Automatic update of the view counter ($n+1$).
* **Embed Reports:** Beautiful reports of viewing history right in the chat.
* **Statistics:** Average rating and total number of movies.

---

## ⌨️ Bot Commands
* `/add [title] [year] [rating] [date]` — Add a movie.
* `/history` — List of the last 5 watched movies.
* `/stats` — Your statistics.
* `/delete` — Delete the most recent entry.

---

## 🚀 Quick Start
1. Add your Google Cloud `credentials.json` to the root directory.
2. Set up your token in the `.env` file.
3. Build the project: `./gradlew build`.
4. Run it: `java -jar build/libs/kinota-all.jar`.

---

## 🛡️ Security
**Never publish your keys!** Make sure your `.gitignore` contains:
`credentials.json`, `.env`, `build/`.