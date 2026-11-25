package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TrackTest {

    /* Коректний конструктор зберігає всі поля. */
    @Test
    void constructorStoresAllFields() {
        Track track = new Track("Скрябін", "Кохана", "Тихий океан", 215, "рок");
        assertEquals("Скрябін", track.getArtist());
        assertEquals("Кохана", track.getTitle());
        assertEquals("Тихий океан", track.getAlbum());
        assertEquals(215, track.getDurationSec());
        assertEquals("рок", track.getGenre());
    }

    /* Граничне коректне значення: 1 секунда. */
    @Test
    void constructorAcceptsOneSecond() {
        assertEquals(1, new Track("a", "b", "c", 1, "d").getDurationSec());
    }

    /* Нульова тривалість відхиляється (так само, як у лабораторній роботі № 1). */
    @Test
    void constructorRejectsZeroDuration() {
        assertThrows(IllegalArgumentException.class,
            () -> new Track("a", "b", "c", 0, "d"));
    }

    /* Від'ємна тривалість відхиляється. */
    @Test
    void constructorRejectsNegativeDuration() {
        assertThrows(IllegalArgumentException.class,
            () -> new Track("a", "b", "c", -200, "d"));
    }

    /* Порожнє або з пробілів текстове поле відхиляється для кожного поля. */
    @Test
    void constructorRejectsBlankTextFields() {
        assertThrows(IllegalArgumentException.class, () -> new Track("", "b", "c", 10, "d"));
        assertThrows(IllegalArgumentException.class, () -> new Track("a", " ", "c", 10, "d"));
        assertThrows(IllegalArgumentException.class, () -> new Track("a", "b", "", 10, "d"));
        assertThrows(IllegalArgumentException.class, () -> new Track("a", "b", "c", 10, "  "));
    }

    /* null відрізняється від порожнього тексту. */
    @Test
    void constructorRejectsNull() {
        assertThrows(NullPointerException.class, () -> new Track(null, "b", "c", 10, "d"));
    }

    /* fromCsv створює об'єкт із коректного рядка з українським текстом. */
    @Test
    void fromCsvCreatesTrack() {
        Track track = Track.fromCsv("Океан Ельзи;Відпусти;Земля;243;рок");
        assertEquals("Океан Ельзи", track.getArtist());
        assertEquals(243, track.getDurationSec());
        assertEquals("Океан Ельзи – Відпусти", track.displayName());
    }

    /* fromCsv обрізає зайві пробіли навколо полів. */
    @Test
    void fromCsvTrimsFields() {
        Track track = Track.fromCsv(" a ; b ; c ; 10 ; d ");
        assertEquals("a", track.getArtist());
        assertEquals(10, track.getDurationSec());
    }

    /* Неправильна кількість полів. */
    @Test
    void fromCsvRejectsWrongFieldCount() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> Track.fromCsv("Без поля;12"));
        assertEquals("очікується 5 полів, знайдено 2", e.getMessage());
    }

    /* Нечислова тривалість. */
    @Test
    void fromCsvRejectsInvalidNumber() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> Track.fromCsv("a;b;c;хвилина;d"));
        assertEquals("тривалість не є цілим числом", e.getMessage());
    }

    /* Порушення інваріанта через фабрику. */
    @Test
    void fromCsvRejectsNegativeDuration() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> Track.fromCsv("a;b;c;-200;d"));
        assertEquals("тривалість має бути додатною", e.getMessage());
    }

    /* Порожнє поле в рядку та порожній рядок. */
    @Test
    void fromCsvRejectsEmptyFieldAndBlankLine() {
        assertEquals("порожнє обов'язкове поле", assertThrows(IllegalArgumentException.class,
            () -> Track.fromCsv("a;b;;190;d")).getMessage());
        assertEquals("порожній рядок", assertThrows(IllegalArgumentException.class,
            () -> Track.fromCsv("")).getMessage());
    }

    /* Порожнє текстове поле має пріоритет над нечисловою тривалістю, як у лабораторній № 1. */
    @Test
    void fromCsvReportsEmptyFieldBeforeBadNumber() {
        assertEquals("порожнє обов'язкове поле", assertThrows(IllegalArgumentException.class,
            () -> Track.fromCsv("a;b;;abc;d")).getMessage());
    }

    /* toString дає стабільний формат з українським текстом. */
    @Test
    void toStringHasStableFormat() {
        assertEquals("Скрябін – Кохана (Тихий океан), 215 с, рок",
            new Track("Скрябін", "Кохана", "Тихий океан", 215, "рок").toString());
    }

    /* record має значеннєву рівність і доступ до компонентів. */
    @Test
    void summaryUsesValueEquality() {
        TrackSummary first = new TrackSummary(3, 676, 225.33, 243, "x", 1, "");
        TrackSummary second = new TrackSummary(3, 676, 225.33, 243, "x", 1, "");
        assertEquals(first, second);
        assertEquals(3, first.count());
        assertEquals(243, first.longestSec());
    }

    /* record відхиляє від'ємні показники. */
    @Test
    void summaryRejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class,
            () -> new TrackSummary(-1, 0, 0.0, 0, "", 0, ""));
        assertThrows(IllegalArgumentException.class,
            () -> new TrackSummary(0, 0, Double.NaN, 0, "", 0, ""));
        assertTrue(new TrackSummary(0, 0, 0.0, 0, "", 0, "").errorText().isEmpty());
    }
}
