package userservice.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import userservice.constants.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class InputReaderTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void readNonBlank_shouldReturnTrimmedValue() {
        setInput("   Alice   \n");

        try (InputReader reader = new InputReader()) {
            assertEquals("Alice", reader.readNonBlank("Введите имя: "));
        }
    }

    @Test
    void readNonBlank_shouldRepeatUntilNonBlank() {
        setInput("\n   \n Bob \n");
        ByteArrayOutputStream out = captureOutput();

        try (InputReader reader = new InputReader()) {
            assertEquals("Bob", reader.readNonBlank("Введите имя: "));
        }

        assertTrue(output(out).contains(Messages.formatError(Messages.EMPTY_VALUE)));
    }

    @Test
    void readEmail_shouldReturnValidEmail() {
        setInput("test@example.com\n");

        try (InputReader reader = new InputReader()) {
            assertEquals("test@example.com", reader.readEmail("Введите e-mail: "));
        }
    }

    @Test
    void readEmail_shouldRepeatUntilValidEmail() {
        setInput("wrong-email\nuser@mail.com\n");
        ByteArrayOutputStream out = captureOutput();

        try (InputReader reader = new InputReader()) {
            assertEquals("user@mail.com", reader.readEmail("Введите e-mail: "));
        }

        assertTrue(output(out).contains(Messages.formatError(Messages.INVALID_EMAIL)));
    }

    @Test
    void readAge_shouldReturnValidAge() {
        setInput("25\n");

        try (InputReader reader = new InputReader()) {
            assertEquals(25, reader.readAge("Введите возраст: "));
        }
    }

    @Test
    void readAge_shouldRepeatAfterNonNumericInput() {
        setInput("abc\n30\n");
        ByteArrayOutputStream out = captureOutput();

        try (InputReader reader = new InputReader()) {
            assertEquals(30, reader.readAge("Введите возраст: "));
        }

        assertTrue(output(out).contains(Messages.formatError(Messages.INVALID_INT)));
    }

    @Test
    void readAge_shouldRepeatAfterInvalidRange() {
        setInput("0\n18\n");
        ByteArrayOutputStream out = captureOutput();

        try (InputReader reader = new InputReader()) {
            assertEquals(18, reader.readAge("Введите возраст: "));
        }

        assertTrue(output(out).contains(Messages.formatError(Messages.INVALID_AGE)));
    }

    @Test
    void readPositiveId_shouldReturnValidId() {
        setInput("15\n");

        try (InputReader reader = new InputReader()) {
            assertEquals(15L, reader.readPositiveId("Введите id: "));
        }
    }

    @Test
    void readPositiveId_shouldRepeatAfterInvalidInput() {
        setInput("abc\n-5\n10\n");
        ByteArrayOutputStream out = captureOutput();

        try (InputReader reader = new InputReader()) {
            assertEquals(10L, reader.readPositiveId("Введите id: "));
        }

        String output = output(out);
        assertTrue(output.contains(Messages.formatError(Messages.INVALID_ID)));
        assertTrue(output.contains(Messages.formatError(Messages.INVALID_ENTITY_ID)));
    }

    @Test
    void readConfirmation_shouldReturnTrueForYIgnoringCase() {
        setInput("y\n");

        try (InputReader reader = new InputReader()) {
            assertTrue(reader.readConfirmation("Подтвердите: "));
        }
    }

    @Test
    void readConfirmation_shouldReturnFalseForNIgnoringCase() {
        setInput("n\n");

        try (InputReader reader = new InputReader()) {
            assertFalse(reader.readConfirmation("Подтвердите: "));
        }
    }

    @Test
    void readConfirmation_shouldRepeatUntilValidValue() {
        setInput("maybe\nN\n");
        ByteArrayOutputStream out = captureOutput();

        try (InputReader reader = new InputReader()) {
            assertFalse(reader.readConfirmation("Подтвердите: "));
        }

        assertTrue(output(out).contains(Messages.formatError(Messages.INVALID_CONFIRMATION)));
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    private ByteArrayOutputStream captureOutput() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        return out;
    }

    private String output(ByteArrayOutputStream out) {
        return out.toString(StandardCharsets.UTF_8);
    }
}