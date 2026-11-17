package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Консольна програма аналізу музичної фонотеки (варіант 13).
 *
 * <p>Формат запису: {@code artist;title;album;durationSec;genre}.
 * Порядок роботи: прочитати → перевірити → обчислити → вивести.
 */
public final class Main {

    private static final String USAGE =
        "Використання: java -jar lab01-1.0.0.jar [--help] [--input <файл>] [--output <файл>]";

    /* Забороняє створення екземплярів службового класу. */
    private Main() {
    }

    /**
     * Результат обробки: чотири показники та повідомлення про пропущені рядки.
     *
     * @param count кількість коректних записів
     * @param totalSec сумарна тривалість у секундах
     * @param avgSec середня тривалість треку в секундах
     * @param longestSec тривалість найдовшого треку в секундах
     * @param longestTitle виконавець і назва найдовшого треку
     * @param errorCount кількість пропущених рядків
     * @param errorText повідомлення про помилки, по одному на рядок
     */
    record Result(int count, long totalSec, double avgSec, int longestSec,
                  String longestTitle, int errorCount, String errorText) {
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
     * Перевіряє кожен рядок і обчислює показники за коректними записами.
     * Хибний рядок не зупиняє обробку: його номер і причина потрапляють у звіт.
     *
     * @param lines рядки вхідного файла
     * @return показники та повідомлення про помилки
     */
    static Result analyze(List<String> lines) {
        StringBuilder errors = new StringBuilder();
        int errorCount = 0;
        int count = 0;
        long total = 0;
        int longest = 0;
        String longestTitle = "";

        for (int index = 0; index < lines.size(); index++) {
            int number = index + 1;
            String line = lines.get(index);
            String problem = null;

            if (line.isBlank()) {
                problem = "порожній рядок";
            } else {
                // -1 зберігає порожнє останнє поле.
                String[] fields = line.split(";", -1);
                if (fields.length != 5) {
                    problem = "очікується 5 полів, знайдено " + fields.length;
                } else if (fields[0].isBlank() || fields[1].isBlank()
                        || fields[2].isBlank() || fields[4].isBlank()) {
                    problem = "порожнє обов'язкове поле";
                } else {
                    try {
                        int seconds = Integer.parseInt(fields[3].trim());
                        if (seconds <= 0) {
                            problem = "тривалість має бути додатною";
                        } else {
                            // До статистики потрапляють лише записи, що пройшли всі перевірки.
                            count++;
                            total += seconds;
                            if (seconds > longest) {
                                longest = seconds;
                                longestTitle = fields[0].trim() + " – " + fields[1].trim();
                            }
                        }
                    } catch (NumberFormatException exception) {
                        problem = "тривалість не є цілим числом";
                    }
                }
            }

            if (problem != null) {
                errorCount++;
                errors.append("Рядок ").append(number).append(": ").append(problem)
                    .append(System.lineSeparator());
            }
        }

        // Порожній набір обробляємо окремо, щоб не ділити на нуль.
        double average = count == 0 ? 0.0 : (double) total / count;
        return new Result(count, total, average, longest, longestTitle,
            errorCount, errors.toString());
    }

    /**
     * Формує текст звіту з фіксованою кількістю знаків після крапки.
     *
     * @param result результат обробки
     * @return готовий текст звіту
     */
    static String format(Result result) {
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
