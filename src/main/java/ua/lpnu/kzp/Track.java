package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Незмінний запис про музичний трек фонотеки (варіант 13).
 *
 * <p>Інваріанти: виконавець, назва, альбом і жанр не порожні;
 * тривалість у секундах більша за нуль.
 */
public final class Track {

    private final String artist;
    private final String title;
    private final String album;
    private final int durationSec;
    private final String genre;

    /**
     * Створює коректний трек після перевірки всіх полів.
     *
     * @param artist виконавець
     * @param title назва треку
     * @param album назва альбому
     * @param durationSec тривалість у секундах
     * @param genre жанр
     * @throws NullPointerException якщо одне з текстових полів дорівнює null
     * @throws IllegalArgumentException якщо текстове поле порожнє або тривалість не додатна
     */
    public Track(String artist, String title, String album, int durationSec, String genre) {
        // Усі перевірки виконуються до присвоєння, тому некоректний об'єкт не створюється.
        this.artist = requireText(artist);
        this.title = requireText(title);
        this.album = requireText(album);
        this.genre = requireText(genre);
        if (durationSec <= 0) {
            throw new IllegalArgumentException("тривалість має бути додатною");
        }
        this.durationSec = durationSec;
    }

    /**
     * Створює трек із CSV-рядка формату {@code artist;title;album;durationSec;genre}.
     *
     * <p>Фабрика перевіряє структуру рядка та тип числа, а інваріанти
     * залишаються в конструкторі. Тексти повідомлень збігаються з лабораторною роботою № 1.
     *
     * @param line рядок вхідного файла
     * @return створений трек
     * @throws IllegalArgumentException якщо структура, число або значення некоректні
     */
    public static Track fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        if (line.isBlank()) {
            throw new IllegalArgumentException("порожній рядок");
        }
        // -1 зберігає порожнє останнє поле.
        String[] fields = line.split(";", -1);
        if (fields.length != 5) {
            throw new IllegalArgumentException(
                "очікується 5 полів, знайдено " + fields.length);
        }
        // Текстові поля перевіряються до числа, як у лабораторній роботі № 1.
        requireText(fields[0]);
        requireText(fields[1]);
        requireText(fields[2]);
        requireText(fields[4]);
        try {
            return new Track(fields[0].trim(), fields[1].trim(), fields[2].trim(),
                Integer.parseInt(fields[3].trim()), fields[4].trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("тривалість не є цілим числом", exception);
        }
    }

    /* Єдине місце правила «текстове поле не null і не порожнє». */
    private static String requireText(String value) {
        Objects.requireNonNull(value, "Текстове поле не може бути null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("порожнє обов'язкове поле");
        }
        return value;
    }

    /** @return виконавець */
    public String getArtist() {
        return artist;
    }

    /** @return назва треку */
    public String getTitle() {
        return title;
    }

    /** @return назва альбому */
    public String getAlbum() {
        return album;
    }

    /** @return тривалість у секундах */
    public int getDurationSec() {
        return durationSec;
    }

    /** @return жанр */
    public String getGenre() {
        return genre;
    }

    /**
     * Повертає коротку назву для звіту у форматі «виконавець – назва».
     *
     * @return виконавець і назва треку
     */
    public String displayName() {
        return artist + " – " + title;
    }

    /** Повертає текстове подання треку. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s – %s (%s), %d с, %s",
            artist, title, album, durationSec, genre);
    }
}
