package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class MainTest {

    /* Коректний рядок враховується в усіх показниках. */
    @Test
    void validLineIsCounted() {
        Main.Result r = Main.analyze(List.of("Скрябін;Кохана;Тихий океан;215;рок"));
        assertEquals(1, r.count());
        assertEquals(215, r.totalSec());
        assertEquals(215, r.longestSec());
        assertEquals(0, r.errorCount());
    }

    /* Порожній рядок пропускається з повідомленням про номер рядка. */
    @Test
    void blankLineIsSkipped() {
        Main.Result r = Main.analyze(List.of(""));
        assertEquals(0, r.count());
        assertEquals(1, r.errorCount());
        assertTrue(r.errorText().contains("Рядок 1"));
    }

    /* Неправильна кількість полів. */
    @Test
    void wrongFieldCountIsSkipped() {
        Main.Result r = Main.analyze(List.of("Без поля;12"));
        assertEquals(0, r.count());
        assertTrue(r.errorText().contains("5 полів"));
    }

    /* Порожнє обов'язкове поле (альбом). */
    @Test
    void emptyAlbumIsSkipped() {
        Main.Result r = Main.analyze(List.of("Тінь Сонця;Вогонь;;190;фолк"));
        assertEquals(0, r.count());
        assertTrue(r.errorText().contains("порожнє"));
    }

    /* Нечислове значення тривалості. */
    @Test
    void nonNumericDurationIsSkipped() {
        Main.Result r = Main.analyze(List.of("Антитіла;Фортеця;Фортеця;хвилина;поп"));
        assertEquals(0, r.count());
        assertTrue(r.errorText().contains("цілим числом"));
    }

    /* Від'ємна тривалість. */
    @Test
    void negativeDurationIsSkipped() {
        Main.Result r = Main.analyze(List.of("Go_A;Шум;Шум;-200;електроніка"));
        assertEquals(0, r.count());
        assertTrue(r.errorText().contains("додатною"));
    }

    /* Файл без коректних записів: середнє 0, без ділення на нуль. */
    @Test
    void noValidRecordsGivesZeroAverage() {
        Main.Result r = Main.analyze(List.of("", "a;b"));
        assertEquals(0, r.count());
        assertEquals(0.0, r.avgSec(), 0.0001);
        assertEquals(2, r.errorCount());
    }

    /* Середнє, сума та найдовший трек для кількох записів; хибний рядок не впливає. */
    @Test
    void averageAndLongestForSeveralRecords() {
        Main.Result r = Main.analyze(List.of(
            "Океан Ельзи;Відпусти;Земля;243;рок",
            "Скрябін;Кохана;Тихий океан;215;рок",
            "ONUKA;Вселенная;ONUKA;218;електроніка",
            "Антитіла;Фортеця;Фортеця;хвилина;поп"));
        assertEquals(3, r.count());
        assertEquals(676, r.totalSec());
        assertEquals(225.3333, r.avgSec(), 0.0001);
        assertEquals(243, r.longestSec());
        assertEquals("Океан Ельзи – Відпусти", r.longestTitle());
        assertEquals(1, r.errorCount());
    }

    /* Українські символи зберігаються у звіті, числа форматуються з крапкою. */
    @Test
    void reportKeepsUkrainianTextAndDotDecimal() {
        String report = Main.format(Main.analyze(List.of(
            "Океан Ельзи;Відпусти;Земля;243;рок",
            "Скрябін;Кохана;Тихий океан;215;рок")));
        assertTrue(report.contains("Океан Ельзи – Відпусти"));
        assertTrue(report.contains("229.00"));
    }
}
