package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.functions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private static final List<Function<List<SleepingSession>, SleepAnalysisResult>> analyzers = List.of(
            new TotalSessionsAnalysis(),
            new MinDurationAnalysis(),
            new MaxDurationAnalysis(),
            new AverageDurationAnalysis(),
            new BadSleepCountAnalysis(),
            new SleeplessNightsAnalysis(),
            new ChronotypeAnalysis()
    );

    public static void main(String[] args) {
        String filePath = "src/main/resources/sleep_log.txt";

        try {
            if (!Files.exists(Paths.get(filePath))) {
                System.out.println("Ошибка: Файл не найден по пути: " + Paths.get(filePath).toAbsolutePath());
                return;
            }

            // Читаем файл и преобразуем строки в объекты сессий
            List<SleepingSession> sessions = Files.lines(Paths.get(filePath))
                    .filter(line -> !line.isBlank())
                    .map(SleepTrackerApp::parseLine)
                    .collect(Collectors.toList());

            System.out.println("\nАнализ данных сна (из файла " + filePath + ")\n");

            // Запускаем каждый анализатор из списка и выводим результат
            analyzers.stream()
                    .map(analyzer -> analyzer.apply(sessions))
                    .forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка при обработке данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static SleepingSession parseLine(String line) {
        String[] parts = line.split(";");
        // Используем trim(), чтобы убрать случайные пробелы вокруг значений
        return new SleepingSession(
                LocalDateTime.parse(parts[0].trim(), FORMATTER),
                LocalDateTime.parse(parts[1].trim(), FORMATTER),
                SleepQuality.valueOf(parts[2].trim())
        );
    }
}
