package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.functions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class SleepTrackerAppTest {
    // Тесты для Бессонных ночей
    @Test
    void testSleeplessNights_NormalSleep() {
        // Спал с 23 до 07 (пересекает 00-06) - 0 бессонных ночей
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2023,1,1,23,0), LocalDateTime.of(2023,1,2,7,0), SleepQuality.GOOD)
        );
        assertEquals(0L, new SleeplessNightsAnalysis().apply(sessions).getValue());
    }

    @Test
    void testSleeplessNights_DaytimeOnly() {
        // Сон только днем (07:00 - 11:00) - 1 бессонная ночь
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2023,1,1,7,0), LocalDateTime.of(2023,1,1,11,0), SleepQuality.GOOD)
        );
        assertEquals(1L, new SleeplessNightsAnalysis().apply(sessions).getValue());
    }

    @Test
    void testSleeplessNights_GapBetweenDays() {
        // Сон 1-го числа и 3-го числа. 2-е число пропущено.
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2023,1,1,22,0), LocalDateTime.of(2023,1,2,6,0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2023,1,3,22,0), LocalDateTime.of(2023,1,4,6,0), SleepQuality.GOOD)
        );
        assertEquals(1L, new SleeplessNightsAnalysis().apply(sessions).getValue());
    }

    @Test
    void testSleeplessNights_ComplexEdgeCase() {
        // Первая сессия до 12 дня - потенциальная ночь вчерашняя.
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2023,1,1,10,0), LocalDateTime.of(2023,1,1,11,0), SleepQuality.GOOD)
        );
        // Анализ видит ночь 1-го числа. Сон в 10 утра ее не покрывает.
        assertEquals(1L, new SleeplessNightsAnalysis().apply(sessions).getValue());
    }

    // Тесты для Хронотипа
    @Test
    void testChronotype_Owl() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2023,1,1,23,30), LocalDateTime.of(2023,1,2,10,0), SleepQuality.GOOD)
        );
        assertEquals(Chronotype.SOVA, new ChronotypeAnalysis().apply(sessions).getValue());
    }

    @Test
    void testChronotype_Lark() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2023,1,1,21,0), LocalDateTime.of(2023,1,2,6,30), SleepQuality.GOOD)
        );
        assertEquals(Chronotype.JAVORONOK, new ChronotypeAnalysis().apply(sessions).getValue());
    }

    @Test
    void testChronotype_Pigeon() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2023,1,1,22,0), LocalDateTime.of(2023,1,2,8,30), SleepQuality.GOOD)
        );
        assertEquals(Chronotype.GOLUB, new ChronotypeAnalysis().apply(sessions).getValue());
    }

    private SleepingSession createSession(int startHour, int endHour, SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.of(2025, 10, 1, startHour, 0),
                LocalDateTime.of(2025, 10, 1, endHour, 0),
                quality
        );
    }

    @Test
    void testTotalSessionsAnalysis() {
        TotalSessionsAnalysis analysis = new TotalSessionsAnalysis();

        // Обычный список
        List<SleepingSession> sessions = List.of(
                createSession(22, 6, SleepQuality.GOOD),
                createSession(23, 7, SleepQuality.NORMAL)
        );
        assertEquals("Общее количество сессий сна: 2", analysis.apply(sessions).toString());

        // Пустой список
        assertEquals("Общее количество сессий сна: 0", analysis.apply(new ArrayList<>()).toString());
    }

    @Test
    void testMinDurationAnalysis() {
        MinDurationAnalysis analysis = new MinDurationAnalysis();

        // Разная длительность (8 часов и 2 часа)
        List<SleepingSession> sessions = List.of(
                createSession(0, 8, SleepQuality.GOOD), // 480 мин
                createSession(14, 16, SleepQuality.NORMAL) // 120 мин
        );
        assertEquals("Минимальная продолжительность сессии (мин): 120", analysis.apply(sessions).toString());

        // Одна сессия
        List<SleepingSession> oneSession = List.of(createSession(10, 11, SleepQuality.BAD));
        assertEquals("Минимальная продолжительность сессии (мин): 60", analysis.apply(oneSession).toString());
    }

    @Test
    void testMaxDurationAnalysis() {
        MaxDurationAnalysis analysis = new MaxDurationAnalysis();

        // Поиск максимума
        List<SleepingSession> sessions = List.of(
                createSession(0, 5, SleepQuality.GOOD), // 300 мин
                createSession(20, 23, SleepQuality.GOOD) // 180 мин
        );
        assertEquals("Максимальная продолжительность сессии (мин): 300", analysis.apply(sessions).toString());

        // Пустой список
        assertEquals("Максимальная продолжительность сессии (мин): 0", analysis.apply(new ArrayList<>()).toString());
    }

    @Test
    void testAverageDurationAnalysis() {
        AverageDurationAnalysis analysis = new AverageDurationAnalysis();

        // Среднее (100 мин + 200 мин) / 2 = 150
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.now(), LocalDateTime.now().plusMinutes(100), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.now(), LocalDateTime.now().plusMinutes(200), SleepQuality.GOOD)
        );
        String result = analysis.apply(sessions).toString().replace(",", ".");
        assertTrue(result.contains("150.00"));

        // Пустой список
        assertEquals("Средняя продолжительность сессии (мин): 0.00", analysis.apply(new ArrayList<>()).toString().replace(",", "."));
    }

    @Test
    void testBadSleepCountAnalysis() {
        BadSleepCountAnalysis analysis = new BadSleepCountAnalysis();

        // Есть BAD сессии
        List<SleepingSession> sessions = List.of(
                createSession(22, 6, SleepQuality.BAD),
                createSession(23, 7, SleepQuality.GOOD),
                createSession(1, 4, SleepQuality.BAD)
        );
        assertEquals("Количество сессий с плохим качеством сна: 2", analysis.apply(sessions).toString());

        // Нет BAD сессий
        List<SleepingSession> goodSessions = List.of(createSession(22, 6, SleepQuality.NORMAL));
        assertEquals("Количество сессий с плохим качеством сна: 0", analysis.apply(goodSessions).toString());
    }
}