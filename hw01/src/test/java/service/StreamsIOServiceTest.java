package service;

import org.junit.jupiter.api.Test;
import ru.otus.hw.service.StreamsIOService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreamsIOServiceTest {
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private StreamsIOService streamsIOService = new StreamsIOService(new PrintStream(outputStream));
    private String TEXT_TO_PRINT = "some text";
    private String TEXT_TO_PRINT_WITH_FORMAT_SPECIFIER = "some text%s";

    @Test
    public void printLineTest() {
        streamsIOService.printLine(TEXT_TO_PRINT);

        String expectedOutput  = TEXT_TO_PRINT + "\n";
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void printFormattedLineTest() {
        streamsIOService.printFormattedLine(TEXT_TO_PRINT_WITH_FORMAT_SPECIFIER, TEXT_TO_PRINT);

        String expectedOutput  = TEXT_TO_PRINT + TEXT_TO_PRINT + "\n";
        assertEquals(expectedOutput, outputStream.toString());
    }
}
