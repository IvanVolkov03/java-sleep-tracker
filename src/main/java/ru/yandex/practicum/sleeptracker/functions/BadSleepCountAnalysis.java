package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepQuality;
import ru.yandex.practicum.sleeptracker.SleepingSession;
import java.util.List;
import java.util.function.Function;

public class BadSleepCountAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long count = sessions.stream().filter(s -> s.getQuality() == SleepQuality.BAD).count();
        return new SleepAnalysisResult("Количество сессий с плохим качеством сна", count);
    }
}