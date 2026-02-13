package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;
import java.util.List;
import java.util.function.Function;

public class AverageDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        double avg = sessions.stream().mapToLong(SleepingSession::getDurationMinutes).average().orElse(0);
        return new SleepAnalysisResult("Средняя продолжительность сессии (мин)", String.format("%.2f", avg));
    }
}
