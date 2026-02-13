package ru.yandex.practicum.sleeptracker;

public enum Chronotype {
    SOVA("Сова"),
    JAVORONOK("Жаворонок"),
    GOLUB("Голубь");

    private final String title;
    Chronotype(String title) { this.title = title; }
    @Override
    public String toString() { return title; }
}
