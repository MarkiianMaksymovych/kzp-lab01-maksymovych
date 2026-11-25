package ua.lpnu.kzp;

import java.util.Objects;

/**
 * Незмінний набір підсумкових показників фонотеки та повідомлень про пропущені рядки.
 *
 * @param count кількість коректних треків
 * @param totalSec сумарна тривалість, с
 * @param avgSec середня тривалість, с
 * @param longestSec тривалість найдовшого треку, с
 * @param longestTitle виконавець і назва найдовшого треку
 * @param errorCount кількість пропущених рядків
 * @param errorText повідомлення про помилки, по одному на рядок
 */
public record TrackSummary(int count, long totalSec, double avgSec, int longestSec,
                           String longestTitle, int errorCount, String errorText) {

    /** Перевіряє допустимість показників. */
    public TrackSummary {
        if (count < 0 || totalSec < 0 || longestSec < 0 || errorCount < 0
                || avgSec < 0 || !Double.isFinite(avgSec)) {
            throw new IllegalArgumentException("Показники не можуть бути від'ємними");
        }
        Objects.requireNonNull(longestTitle, "Назва не може бути null");
        Objects.requireNonNull(errorText, "Текст помилок не може бути null");
    }
}
