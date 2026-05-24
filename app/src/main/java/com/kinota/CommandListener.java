package com.kinota;

import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;
import javax.annotation.Nonnull;

public class CommandListener extends ListenerAdapter {
// У класі CommandListener додаємо конструктор і виклик сервісу
private final GoogleSheetsService sheetsService;

public CommandListener(GoogleSheetsService sheetsService) {
    this.sheetsService = sheetsService;
}

@Override
public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

    String command = event.getName();

    if (event.getName().equals("add")) {
        event.deferReply().queue();

        try {
            var titleOption = event.getOption("title");
            var yearOption = event.getOption("year");
            var ratingOption = event.getOption("rating");
            
            if (titleOption == null || yearOption == null || ratingOption == null) {
                event.getHook().sendMessage("❌ Помилка: не всі параметри передані!").queue();
                return;
            }
            
            String title = titleOption.getAsString();
            int year = yearOption.getAsInt();
            double rating = ratingOption.getAsDouble();
            String date = java.time.LocalDate.now().toString();

            // 1. Отримуємо, скільки разів цей фільм уже є в базі
            int existingCount = sheetsService.getWatchCount(title);
            
            // 2. Нове значення - це n + 1
            int newCount = existingCount + 1;

            // 3. Формуємо рядок для запису
            List<Object> row = List.of(title, year, rating, date, newCount);
            sheetsService.appendMovie(row);

            // Відповідь користувачу
            String message = "✅ Фільм **" + title + "** записано!";
            if (newCount > 1) {
                message += " (Це твій перегляд № " + newCount + " 🍿)";
            }
            
            event.getHook().sendMessage(message).queue();

        } catch (Exception e) {
            event.getHook().sendMessage("❌ Помилка: " + e.getMessage()).queue();
            e.printStackTrace();
        }
        //handleLogMovie(event); didn't work, so I moved the code here
    } else if (command.equals("stats")) {
        event.deferReply().queue();
        try {
            String stats = sheetsService.getStatistics();
            if (stats != null) {
                event.getHook().sendMessage(stats).queue();
            }
        } catch (Exception e) {
            event.getHook().sendMessage("❌ Не вдалося отримати статистику: " + e.getMessage()).queue();
        }
    // У метод onSlashCommandInteraction додай обробку /history:
    } else if (command.equals("history")) {
    event.deferReply().queue();
    try {
        List<List<Object>> lastMovies = sheetsService.getLastMovies(5);

        if (lastMovies.isEmpty()) {
            event.getHook().sendMessage("Історія порожня. Додай свій перший фільм!").queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("🎬 Останні переглянуті фільми");
        embed.setColor(Color.CYAN);
        embed.setTimestamp(java.time.Instant.now());

        // Перебираємо фільми (у зворотному порядку, щоб найновіші були зверху)
        for (int i = lastMovies.size() - 1; i >= 0; i--) {
            List<Object> row = lastMovies.get(i);
            String title = row.get(0).toString();
            String year = row.size() > 1 ? row.get(1).toString() : "???";
            String rating = row.size() > 2 ? row.get(2).toString() : "0";
            String watchCount = row.size() > 4 ? row.get(4).toString() : "1";

            embed.addField(
                title + " (" + year + ")",
                "⭐ Оцінка: " + rating + " | 🔁 Переглядів: " + watchCount,
                false
            );
        }

        event.getHook().sendMessageEmbeds(embed.build()).queue();
        
    } catch (Exception e) {
        event.getHook().sendMessage("❌ Помилка історії: " + e.getMessage()).queue();
    }
}  else if (command.equals("delete")) {
    event.deferReply().queue();
    try {
        String result = sheetsService.deleteLastEntry();
        if (result != null) {
            event.getHook().sendMessage(result).queue();
        }
    } catch (Exception e) {
        event.getHook().sendMessage("❌ Не вдалося видалити запис: " + e.getMessage()).queue();
    }
} else if (command.equals("help")) {
    EmbedBuilder embed = new EmbedBuilder();
    embed.setTitle("Помічник Kinota: Як це працює?");
    embed.setDescription("Я допомагаю вести облік твоїх переглянутих фільмів. Ось список доступних команд:");
    embed.setColor(Color.YELLOW);

    embed.addField("➕ /add", "Додає новий фільм. Якщо ти вже дивився його раніше, я автоматично оновлю кількість переглядів у таблиці.\n*Параметри: назва, рік, оцінка (0-10).*", false);
    
    embed.addField("📊 /stats", "Твоя статистика: скільки всього фільмів у списку, загальна кількість переглядів та середній бал.", false);
    
    embed.addField("📜 /history", "Показує 5 останніх доданих тобою фільмів у вигляді зручних карток.", false);
    
    embed.addField("🗑️ /delete", "Видаляє останній доданий рядок з таблиці (корисно, якщо помилився при введенні).", false);
    
    embed.addField("❓ /help", "Викликає це меню з інструкціями.", false);

    embed.setFooter("Kinota Bot • Зроблено в Україні 🇺🇦");
    embed.setTimestamp(java.time.Instant.now());

    event.replyEmbeds(embed.build()).queue();
}
}

}