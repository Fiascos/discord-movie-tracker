# 🎬 Kinota: Discord Movie Tracker

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21">
  <img src="https://img.shields.io/badge/Discord-JDA-5865F2?style=for-the-badge&logo=discord" alt="JDA">
  <img src="https://img.shields.io/badge/Google%20Sheets-API-34A853?style=for-the-badge&logo=googlesheets" alt="Google Sheets API">
  <img src="https://img.shields.io/badge/Oracle%20Cloud-Always%20Free-F80000?style=for-the-badge&logo=oracle" alt="Oracle Cloud">
</p>

---

## 🌟 Про проєкт
**Kinota** — це інтелектуальний помічник для кіноманів у Discord. Бот автоматизує процес ведення списку переглянутих фільмів прямо у Google Таблиці.

### ✨ Функції:
* **Smart Counting:** Автоматичне оновлення лічильника переглядів ($n+1$).
* **Embed Reports:** Гарні звіти про історію переглядів у чаті.
* **Statistics:** Середній рейтинг та загальна кількість фільмів.

---

## ⌨️ Команди бота
* `/add [title] [year] [rating] [date]` — Додати фільм.
* `/history` — Список останніх 5 переглядів.
* `/stats` — Ваша статистика.
* `/delete` — Видалити останній запис.

---

## 🚀 Швидкий старт
1. Додайте `credentials.json` від Google Cloud у корінь.
2. Налаштуйте токен у файлі `.env`.
3. Зберіть проєкт: `./gradlew build`.
4. Запустіть: `java -jar build/libs/kinota-all.jar`.

---

## 🛡️ Безпека
**Ніколи не публікуйте ключі!** Переконайтеся, що `.gitignore` містить:
`credentials.json`, `.env`, `build/`.