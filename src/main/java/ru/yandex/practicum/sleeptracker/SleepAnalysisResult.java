package ru.yandex.practicum.sleeptracker;

// Обёртка для результата анализа
public class SleepAnalysisResult {
    private final String description;
    private final Object value;

    public SleepAnalysisResult(String description, Object value) {
        this.description = description;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return description + ": " + value;
    }
}