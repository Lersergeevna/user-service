package userservice.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import userservice.constants.Messages;
import userservice.entity.BaseEntity;
import userservice.entity.UserEntity;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsolePrinterTest {

    private final PrintStream originalOut = System.out;

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void printHeader_shouldPrintTitle() {
        String output = captureOutput(() -> new ConsolePrinter().printHeader());

        assertTrue(output.contains(Messages.APP_TITLE));
    }

    @Test
    void printMenu_shouldPrintAllMenuItems() {
        String output = captureOutput(() -> new ConsolePrinter().printMenu());

        assertTrue(output.contains(Messages.CHOOSE_ACTION));
        for (MenuAction action : MenuAction.values()) {
            assertTrue(output.contains(action.code()));
            assertTrue(output.contains(action.description()));
        }
    }

    @Test
    void printSuccess_shouldPrintFormattedSuccessMessage() {
        String output = captureOutput(() -> new ConsolePrinter().printSuccess("готово"));

        assertTrue(output.contains(Messages.formatSuccess("готово")));
    }

    @Test
    void printError_shouldPrintFormattedErrorMessage() {
        String output = captureOutput(() -> new ConsolePrinter().printError("ошибка"));

        assertTrue(output.contains(Messages.formatError("ошибка")));
    }

    @Test
    void printInfo_shouldPrintFormattedInfoMessage() {
        String output = captureOutput(() -> new ConsolePrinter().printInfo("инфо"));

        assertTrue(output.contains(Messages.formatInfo("инфо")));
    }

    @Test
    void printUsers_shouldPrintEmptyMessageForEmptyList() {
        String output = captureOutput(() -> new ConsolePrinter().printUsers(List.of()));

        assertTrue(output.contains(Messages.formatInfo(Messages.USER_LIST_EMPTY)));
    }

    @Test
    void printUsers_shouldPrintTableForNonEmptyList() {
        UserEntity user = buildUser(
                1L,
                "ОченьДлинноеИмяПользователяКотороеДолжноСократиться",
                "very.long.email.address.for.testing@example.com",
                25,
                LocalDateTime.of(2026, 4, 3, 12, 0)
        );

        String output = captureOutput(() -> new ConsolePrinter().printUsers(List.of(user)));

        assertTrue(output.contains("ID"));
        assertTrue(output.contains("Имя"));
        assertTrue(output.contains("E-mail"));
        assertTrue(output.contains("Возраст"));
        assertTrue(output.contains("Создан"));
        assertTrue(output.contains("1"));
        assertTrue(output.contains("25"));
        assertTrue(output.contains("..."));
    }

    @Test
    void printUser_shouldPrintSingleUserDetails() {
        UserEntity user = buildUser(
                10L,
                "Alice",
                "alice@example.com",
                30,
                LocalDateTime.of(2026, 4, 3, 15, 30)
        );

        String output = captureOutput(() -> new ConsolePrinter().printUser(user));

        assertTrue(output.contains("Пользователь"));
        assertTrue(output.contains("10"));
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("alice@example.com"));
        assertTrue(output.contains("30"));
        assertTrue(output.contains("2026-04-03T15:30"));
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        action.run();
        return out.toString(StandardCharsets.UTF_8);
    }

    private UserEntity buildUser(Long id, String name, String email, Integer age, LocalDateTime createdAt) {
        UserEntity user = new UserEntity(name, email, age);

        try {
            Field idField = BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);

            Field createdAtField = BaseEntity.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(user, createdAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return user;
    }
}