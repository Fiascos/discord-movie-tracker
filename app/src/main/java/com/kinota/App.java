package com.kinota;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class App {
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        // Завантажуємо змінні оточення
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");

        // У main методі
        String sheetId = dotenv.get("SPREADSHEET_ID");
        GoogleSheetsService sheetsService = new GoogleSheetsService(sheetId);

        if (token == null || token.isEmpty()) {
            System.err.println("Помилка: DISCORD_TOKEN не знайдено в .env файлі!");
            return;
        }

        // Створюємо JDA бот-клієнт
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("фільми"))
                .addEventListeners(new CommandListener(sheetsService))
                .build();

        // Реєструємо слеш-команди (згідно з ТЗ)
        jda.updateCommands().addCommands(
            Commands.slash("help", "Показати інструкцію та список усіх команд"),
            Commands.slash("add", "Додати новий переглянутий фільм")
                .addOption(OptionType.STRING, "title", "Назва фільму", true)
                .addOption(OptionType.INTEGER, "year", "Рік випуску", true)
                .addOption(OptionType.NUMBER, "rating", "Твоя оцінка (напр. 8.5)", true),
            
            Commands.slash("stats", "Показати загальну статистику"),
            Commands.slash("history", "Останні 5 переглянутих фільмів"),
            Commands.slash("delete", "Видалити останній доданий запис")
        ).queue();

        System.out.println("Бот Kinota успішно запущений!");


    }
}