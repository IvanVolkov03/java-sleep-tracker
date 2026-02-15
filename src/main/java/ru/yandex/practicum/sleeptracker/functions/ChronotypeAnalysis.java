package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.Chronotype;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Функция: Хронотип
public class ChronotypeAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        // Группируем сессии по "Ночи" (к какому 00:00-06:00 они относятся)
        Map<LocalDate, List<SleepingSession>> nightGroups = sessions.stream()
                .flatMap(s -> getAffectedNights(s).stream().map(d -> Map.entry(d, s)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        Map<Chronotype, Long> counts = nightGroups.values().stream()
                .map(this::determineNightType)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long owls = counts.getOrDefault(Chronotype.SOVA, 0L);
        long larks = counts.getOrDefault(Chronotype.JAVORONOK, 0L);
        long pigeons = counts.getOrDefault(Chronotype.GOLUB, 0L);

        Chronotype result = Chronotype.GOLUB;
        if (owls > larks && owls > pigeons) result = Chronotype.SOVA;
        else if (larks > owls && larks > pigeons) result = Chronotype.JAVORONOK;

        return new SleepAnalysisResult("Хронотип пользователя", result);
    }

    private List<LocalDate> getAffectedNights(SleepingSession s) {
        // сессия может затронуть только ночь текущего или следующего дня
        return Stream.of(s.getStart().toLocalDate(), s.getStart().toLocalDate().plusDays(1))
                .filter(s::crossesNightWindow)
                .collect(Collectors.toList());
    }

    private Chronotype determineNightType(List<SleepingSession> nightSessions) {
        // Берем самое раннее начало и самое позднее окончание среди сессий этой ночи
        LocalTime sleepTime = nightSessions.stream().map(s -> s.getStart().toLocalTime()).min(LocalTime::compareTo).get();
        LocalTime wakeTime = nightSessions.stream().map(s -> s.getEnd().toLocalTime()).max(LocalTime::compareTo).get();

        if (sleepTime.isAfter(LocalTime.of(23, 0)) && wakeTime.isAfter(LocalTime.of(9, 0))) {
            return Chronotype.SOVA;
        } else if (sleepTime.isBefore(LocalTime.of(22, 0)) && wakeTime.isBefore(LocalTime.of(7, 0))) {
            return Chronotype.JAVORONOK;
        } else {
            return Chronotype.GOLUB;
        }
    }
}