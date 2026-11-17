package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileReportTest {

    /* Звіт з українським текстом записується у вкладений каталог і читається назад. */
    @Test
    void writesAndReadsUkrainianTextInUtf8(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out").resolve("report.txt");
        FileReport.writeReport(file, "Фонотека");
        assertEquals(List.of("Фонотека"), FileReport.readLines(file));
    }
}
