package com.kinota;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

public class GoogleSheetsService {
    private final Sheets sheetsService;
    private final String spreadsheetId;

    public GoogleSheetsService(String spreadsheetId) throws IOException, GeneralSecurityException {
        this.spreadsheetId = spreadsheetId;

        Dotenv dotenv = Dotenv.load();
        String credentialsPath = dotenv.get("GOOGLE_CREDENTIALS_PATH");
        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IOException("Помилка: GOOGLE_CREDENTIALS_PATH не знайдено в .env файлі!");
        }

        FileInputStream in = new FileInputStream(credentialsPath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        this.sheetsService = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Kinota-Tracker")
                .build();
    }

    public void appendMovie(List<Object> rowData) throws IOException {
        ValueRange body = new ValueRange().setValues(Collections.singletonList(rowData));
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, "Аркуш1!A1", body) // Переконайся, що лист називається Sheet1
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    public int getWatchCount(String title) throws IOException {
    // Читаємо весь перший стовпець (Назви)
    ValueRange response = sheetsService.spreadsheets().values()
            .get(spreadsheetId, "Аркуш1!A:A") 
            .execute();

    List<List<Object>> values = response.getValues();
    int count = 0;

    if (values != null) {
        for (List<Object> row : values) {
            if (!row.isEmpty() && row.get(0).toString().equalsIgnoreCase(title)) {
                count++;
            }
        }
    }
    return count;
}

    public String getStatistics() throws IOException {
    // Читаємо назви та оцінки (стовпці A та C)
    ValueRange response = sheetsService.spreadsheets().values()
            .get(spreadsheetId, "Аркуш1!A2:C") // Починаємо з A2, щоб пропустити шапку
            .execute();

    List<List<Object>> values = response.getValues();
    if (values == null || values.isEmpty()) {
        return "Твоя база фільмів поки що порожня! 🎬";
    }

    int totalMovies = values.size();
    double sumRatings = 0;
    int ratedMovies = 0;

    for (List<Object> row : values) {
        // Оцінка в нас у третьому стовпці (індекс 2)
        if (row.size() >= 3 && row.get(2) != null) {
            try {
                double rating = Double.parseDouble(row.get(2).toString());
                sumRatings += rating;
                ratedMovies++;
            } catch (NumberFormatException e) {
                // Ігноруємо, якщо оцінка не є числом
            }
        }
    }

    double average = ratedMovies > 0 ? sumRatings / ratedMovies : 0;

    return String.format(
        "📊 **Твоя кіно-статистика:**\n" +
        "• Переглянуто фільмів: **%d**\n" +
        "• Середній бал: **%.2f**", 
        totalMovies, average
    );
}

public List<List<Object>> getLastMovies(int limit) throws IOException {
    // Читаємо всю таблицю (від A2 до E, щоб охопити всі стовпці)
    ValueRange response = sheetsService.spreadsheets().values()
            .get(spreadsheetId, "Аркуш1!A2:E")
            .execute();

    List<List<Object>> values = response.getValues();
    if (values == null || values.isEmpty()) {
        return Collections.emptyList();
    }

    // Беремо останні записів (limit)
    int size = values.size();
    int fromIndex = Math.max(0, size - limit);
    return values.subList(fromIndex, size);
}

public String deleteLastEntry() throws IOException {
    // 1. Отримуємо всі дані, щоб знайти номер останнього рядка
    ValueRange response = sheetsService.spreadsheets().values()
            .get(spreadsheetId, "Аркуш1!A:A")
            .execute();

    List<List<Object>> values = response.getValues();
    
    // Якщо таблиця порожня або лише з шапкою
    if (values == null || values.size() <= 1) {
        return "Твоя таблиця вже порожня, нічого видаляти! 🤷‍♂️";
    }

    int lastRowIndex = values.size(); // Номер останнього рядка (1-based)
    String lastMovieName = values.get(lastRowIndex - 1).get(0).toString();

    // 2. Готуємо запит на видалення рядка
    com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest content = new com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest();
    com.google.api.services.sheets.v4.model.Request request = new com.google.api.services.sheets.v4.model.Request()
        .setDeleteDimension(new com.google.api.services.sheets.v4.model.DeleteDimensionRequest()
            .setRange(new com.google.api.services.sheets.v4.model.DimensionRange()
                .setSheetId(0) // Зазвичай перший аркуш має ID 0
                .setDimension("ROWS")
                .setStartIndex(lastRowIndex - 1)
                .setEndIndex(lastRowIndex)
            )
        );

    content.setRequests(java.util.Collections.singletonList(request));
    sheetsService.spreadsheets().batchUpdate(spreadsheetId, content).execute();

    return "🗑️ Видалено останній запис: **" + lastMovieName + "**";
}
}