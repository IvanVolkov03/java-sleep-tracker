package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SleepingSession {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime start, LocalDateTime end, SleepQuality quality) {
        this.start = start;
        this.end = end;
        this.quality = quality;
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public SleepQuality getQuality() { return quality; }

    public long getDurationMinutes() {
        return Duration.between(start, end).toMinutes();
    }

    // Проверка, пересекает ли сессия интервал 00:00 - 06:00 конкретной даты
    public boolean crossesNightWindow(LocalDate nightDate) {
        LocalDateTime windowStart = nightDate.atStartOfDay();
        LocalDateTime windowEnd = nightDate.atTime(6, 0);
        return start.isBefore(windowEnd) && end.isAfter(windowStart);
    }
}