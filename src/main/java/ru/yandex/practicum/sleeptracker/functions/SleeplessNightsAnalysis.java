package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import java.util.stream.LongStream;

// Функция: Бессонные ночи
public class SleeplessNightsAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) return new SleepAnalysisResult("Количество бессонных ночей", 0);

        // Определяем границы анализа
        LocalDateTime first = sessions.get(0).getStart();
        LocalDate startNight = first.getHour() < 12 ? first.toLocalDate() : first.toLocalDate().plusDays(1);

        LocalDateTime last = sessions.get(sessions.size() - 1).getEnd();
        LocalDate endNight = last.getHour() < 12 ? last.toLocalDate() : last.toLocalDate().plusDays(1);

        long totalNights = ChronoUnit.DAYS.between(startNight, endNight) + 1;

        long nightsWithSleep = LongStream.range(0, totalNights)
                .mapToObj(startNight::plusDays)
                .filter(nightDate -> sessions.stream().anyMatch(s -> s.crossesNightWindow(nightDate)))
                .count();

        return new SleepAnalysisResult("Количество бессонных ночей", totalNights - nightsWithSleep);
    }
}
