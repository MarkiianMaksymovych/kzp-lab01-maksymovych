package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Консольна програма аналізу музичної фонотеки (варіант 13).
 *
 * <p>Формат запису: {@code artist;title;album;durationSec;genre}.
 * Порядок роботи: прочитати → створити об'єкти {@link Track} → обчислити → вивести.
 */
public final class Main {

    private static final String USAGE =
        "Використання: java -jar lab01-1.0.0.jar [--help] [--input <файл>] [--output <файл>]";

    /* Забороняє створення екземплярів службового класу. */
    private Main() {
    }

    /**
     * Точка входу до програми.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        if (args.length > 0 && "--help".equals(args[0])) {
            System.out.printf("%s%n", USAGE);
            return;
        }
        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");
        for (int i = 0; i < args.length - 1; i++) {
            if ("--input".equals(args[i])) {
                input = Path.of(args[i + 1]);
            } else if ("--output".equals(args[i])) {
                output = Path.of(args[i + 1]);
            }
        }
        try {
            List<String> lines = FileReport.readLines(input);
            // Один текст звіту виводимо і в консоль, і у файл.
            String report = format(analyze(lines));
            System.out.print(report);
            FileReport.writeReport(output, report);
        } catch (IOException exception) {
            System.err.printf("Помилка роботи з файлом: %s%n", exception.getMessage());
        }
    }

    /**
     * Перетворює рядки на об'єкти {@link Track} і обчислює показники.
     * Помилка одного рядка не зупиняє обробку: її номер і причина потрапляють у звіт.
     *
     * @param lines рядки вхідного файла
     * @return показники та повідомлення про помилки
     */
    static TrackSummary analyze(List<String> lines) {
        List<Track> tracks = new ArrayList<>();
        StringBuilder errors = new StringBuilder();
        int errorCount = 0;

        for (int index = 0; index < lines.size(); index++) {
            try {
                // Некоректний рядок не потрапляє до списку, тому не впливає на показники.
                tracks.add(Track.fromCsv(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                errorCount++;
                errors.append("Рядок ").append(index + 1).append(": ")
                    .append(exception.getMessage()).append(System.lineSeparator());
            }
        }

        long total = 0;
        Track longest = null;
        for (Track track : tracks) {
            total += track.getDurationSec();
            if (longest == null || track.getDurationSec() > longest.getDurationSec()) {
                longest = track;
            }
        }

        int count = tracks.size();
        // Порожній набір обробляємо окремо, щоб не ділити на нуль.
        double average = count == 0 ? 0.0 : (double) total / count;
        int longestSec = longest == null ? 0 : longest.getDurationSec();
        String longestTitle = longest == null ? "" : longest.displayName();
        return new TrackSummary(count, total, average, longestSec, longestTitle,
            errorCount, errors.toString());
    }

    /**
     * Формує текст звіту з фіксованою кількістю знаків після крапки.
     *
     * @param result підсумкові показники
     * @return готовий текст звіту
     */
    static String format(TrackSummary result) {
        String longestInfo = result.count() == 0 ? "немає даних" : result.longestTitle();
        StringBuilder report = new StringBuilder();
        report.append(String.format(Locale.ROOT, "%-30s %10s%n", "Показник", "Значення"));
        report.append(String.format(Locale.ROOT, "%-30s %10d%n",
            "Коректних записів", result.count()));
        report.append(String.format(Locale.ROOT, "%-30s %10d%n",
            "Сумарна тривалість, с", result.totalSec()));
        report.append(String.format(Locale.ROOT, "%-30s %10.2f%n",
            "Середня тривалість, с", result.avgSec()));
        report.append(String.format(Locale.ROOT, "%-30s %10d%n",
            "Найдовший трек, с", result.longestSec()));
        report.append(String.format(Locale.ROOT, "Найдовший трек: %s%n", longestInfo));
        report.append(String.format(Locale.ROOT, "Пропущено рядків: %d%n", result.errorCount()));
        report.append(result.errorText());
        return report.toString();
    }
}
