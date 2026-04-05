package userservice.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import userservice.constants.Messages;
import userservice.dto.UserCreateRequest;
import userservice.entity.UserEntity;
import userservice.exception.EntityNotFoundException;
import userservice.exception.InvalidInputException;
import userservice.service.impl.UserServiceImpl;
import userservice.util.InputReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ConsoleAppTest {

    private final PrintStream originalErr = System.err;

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    @Test
    void run_shouldExitWhenExitCommandChosen() {
        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class);
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: ")).thenReturn("0");
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            ConsolePrinter printer = printerConstruction.constructed().get(0);
            InputReader inputReader = inputReaderConstruction.constructed().get(0);

            verify(printer).printHeader();
            verify(printer, atLeastOnce()).printMenu();
            verify(printer).printInfo(Messages.EXIT);
            verify(inputReader).close();
        }
    }

    @Test
    void run_shouldPrintErrorForInvalidMenuItemThenExit() {
        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class);
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: "))
                                 .thenReturn("999", "0");
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            ConsolePrinter printer = printerConstruction.constructed().get(0);

            verify(printer).printError(Messages.INVALID_MENU_ITEM);
            verify(printer).printInfo(Messages.EXIT);
        }
    }

    @Test
    void run_shouldBlockActionWhenUsersDoNotExist() {
        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class, (mock, context) -> {
                         when(mock.hasUsers()).thenReturn(false);
                     });
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: "))
                                 .thenReturn("3", "0");
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            ConsolePrinter printer = printerConstruction.constructed().get(0);

            verify(printer).printError(
                    Messages.NO_USERS_FOR_COMMAND.formatted(MenuAction.GET_ALL.description())
            );
            verify(printer).printInfo(Messages.EXIT);
        }
    }

    @Test
    void run_shouldCreateUserSuccessfully() {
        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class, (mock, context) -> {
                         when(mock.createUser(any(UserCreateRequest.class))).thenReturn(5L);
                     });
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: "))
                                 .thenReturn("1", "0");
                         when(mock.readNonBlank("Введите имя: "))
                                 .thenReturn("Alice");
                         when(mock.readEmail("Введите e-mail: "))
                                 .thenReturn("alice@example.com");
                         when(mock.readAge("Введите возраст: "))
                                 .thenReturn(25);
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            UserServiceImpl userService = userServiceConstruction.constructed().get(0);
            ConsolePrinter printer = printerConstruction.constructed().get(0);

            verify(userService).ensureEmailAvailable("alice@example.com", null);
            verify(userService).createUser(any(UserCreateRequest.class));
            verify(printer).printSuccess(Messages.USER_CREATED.formatted(5L));
            verify(printer).printInfo(Messages.EXIT);
        }
    }

    @Test
    void run_shouldRepeatUntilEmailBecomesAvailable() {
        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class, (mock, context) -> {
                         doThrow(new InvalidInputException(Messages.DUPLICATE_EMAIL))
                                 .doNothing()
                                 .when(mock).ensureEmailAvailable(anyString(), isNull());
                         when(mock.createUser(any(UserCreateRequest.class))).thenReturn(7L);
                     });
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: "))
                                 .thenReturn("1", "0");
                         when(mock.readNonBlank("Введите имя: "))
                                 .thenReturn("Bob");
                         when(mock.readEmail("Введите e-mail: "))
                                 .thenReturn("dup@mail.com", "ok@mail.com");
                         when(mock.readAge("Введите возраст: "))
                                 .thenReturn(30);
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            UserServiceImpl userService = userServiceConstruction.constructed().get(0);
            ConsolePrinter printer = printerConstruction.constructed().get(0);

            verify(userService, times(2)).ensureEmailAvailable(anyString(), isNull());
            verify(printer).printError(Messages.DUPLICATE_EMAIL);
            verify(printer).printSuccess(Messages.USER_CREATED.formatted(7L));
        }
    }

    @Test
    void run_shouldShowUserByIdAfterRetryingUntilUserExists() {
        UserEntity user = buildUser(2L, "Kate", "kate@mail.com", 22,
                LocalDateTime.of(2026, 4, 3, 16, 0));

        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class, (mock, context) -> {
                         when(mock.hasUsers()).thenReturn(true);
                         doThrow(new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(1L)))
                                 .doNothing()
                                 .when(mock).ensureUserExists(anyLong());
                         when(mock.getUserById(2L)).thenReturn(user);
                     });
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: "))
                                 .thenReturn("2", "0");
                         when(mock.readPositiveId("Введите id пользователя: "))
                                 .thenReturn(1L, 2L);
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            UserServiceImpl userService = userServiceConstruction.constructed().get(0);
            ConsolePrinter printer = printerConstruction.constructed().get(0);

            verify(userService, times(2)).ensureUserExists(anyLong());
            verify(printer).printError(Messages.USER_NOT_FOUND_BY_ID.formatted(1L));
            verify(printer).printUser(user);
        }
    }

    @Test
    void run_shouldShowAllUsers() {
        UserEntity user = buildUser(1L, "Ann", "ann@mail.com", 19,
                LocalDateTime.of(2026, 4, 3, 17, 0));

        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class, (mock, context) -> {
                         when(mock.hasUsers()).thenReturn(true);
                         when(mock.getAllUsers()).thenReturn(java.util.List.of(user));
                     });
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: "))
                                 .thenReturn("3", "0");
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            ConsolePrinter printer = printerConstruction.constructed().get(0);
            verify(printer).printUsers(java.util.List.of(user));
        }
    }

    @Test
    void run_shouldCancelDeleteWhenUserDoesNotConfirm() {
        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class, (mock, context) -> {
                         when(mock.hasUsers()).thenReturn(true);
                     });
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: "))
                                 .thenReturn("5", "0");
                         when(mock.readPositiveId("Введите id пользователя для удаления: "))
                                 .thenReturn(10L);
                         when(mock.readConfirmation("Подтвердите удаление (Y/N): "))
                                 .thenReturn(false);
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            UserServiceImpl userService = userServiceConstruction.constructed().get(0);
            ConsolePrinter printer = printerConstruction.constructed().get(0);

            verify(userService).ensureUserExists(10L);
            verify(userService, never()).deleteUser(anyLong());
            verify(printer).printInfo(Messages.DELETE_CANCELLED);
        }
    }

    @Test
    void run_shouldDeleteUserWhenConfirmed() {
        try (MockedConstruction<UserServiceImpl> userServiceConstruction =
                     mockConstruction(UserServiceImpl.class, (mock, context) -> {
                         when(mock.hasUsers()).thenReturn(true);
                     });
             MockedConstruction<ConsolePrinter> printerConstruction =
                     mockConstruction(ConsolePrinter.class);
             MockedConstruction<InputReader> inputReaderConstruction =
                     mockConstruction(InputReader.class, (mock, context) -> {
                         when(mock.readNonBlank("Введите команду: "))
                                 .thenReturn("5", "0");
                         when(mock.readPositiveId("Введите id пользователя для удаления: "))
                                 .thenReturn(11L);
                         when(mock.readConfirmation("Подтвердите удаление (Y/N): "))
                                 .thenReturn(true);
                     })) {

            ConsoleApp app = new ConsoleApp();
            app.run();

            UserServiceImpl userService = userServiceConstruction.constructed().get(0);
            ConsolePrinter printer = printerConstruction.constructed().get(0);

            verify(userService).deleteUser(11L);
            verify(printer).printSuccess(Messages.USER_DELETED);
        }
    }

    private UserEntity buildUser(Long id, String name, String email, Integer age, LocalDateTime createdAt) {
        UserEntity user = new UserEntity(name, email, age);

        try {
            Field idField = userservice.entity.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);

            Field createdAtField = userservice.entity.BaseEntity.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(user, createdAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return user;
    }
}