package userservice.ui;

import userservice.constants.Messages;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;
import userservice.exception.EntityNotFoundException;
import userservice.exception.InvalidInputException;
import userservice.exception.ServiceException;
import userservice.service.UserService;
import userservice.service.impl.UserServiceImpl;
import userservice.util.InputReader;

import java.util.List;

/**
 * Реализует консольный интерфейс приложения.
 */
public final class ConsoleApp {
    private final UserService userService;
    private final ConsolePrinter printer;

    /**
     * Создаёт консольное приложение с зависимостями по умолчанию.
     */
    public ConsoleApp() {
        this.userService = new UserServiceImpl();
        this.printer = new ConsolePrinter();
    }

    /**
     * Запускает интерактивное консольное меню.
     */
    public void run() {
        printer.printHeader();
        try (InputReader inputReader = new InputReader()) {
            boolean running = true;
            while (running) {
                try {
                    printer.printMenu();
                    MenuAction action = MenuAction.fromCode(inputReader.readNonBlank("Введите команду: "));
                    if (action == null) {
                        printer.printError(Messages.INVALID_MENU_ITEM);
                        continue;
                    }
                    ensureActionAvailable(action);
                    running = handleAction(action, inputReader);
                } catch (ServiceException e) {
                    printer.printError(e.getMessage());
                } catch (RuntimeException e) {
                    printer.printError(Messages.UNEXPECTED_ERROR);
                }
            }
        }
    }

    /**
     * Выполняет выбранную команду.
     *
     * @param action команда меню
     * @param inputReader объект для чтения пользовательского ввода
     * @return true, если цикл нужно продолжить
     */
    private boolean handleAction(MenuAction action, InputReader inputReader) {
        switch (action) {
            case CREATE -> createUser(inputReader);
            case GET_BY_ID -> showUserById(inputReader);
            case GET_ALL -> showAllUsers();
            case UPDATE -> updateUser(inputReader);
            case DELETE -> deleteUser(inputReader);
            case EXIT -> {
                printer.printInfo(Messages.EXIT);
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет доступность команды в текущем состоянии базы данных.
     *
     * @param action команда меню
     */
    private void ensureActionAvailable(MenuAction action) {
        if (action.requiresExistingUsers() && !userService.hasUsers()) {
            throw new ServiceException(Messages.NO_USERS_FOR_COMMAND.formatted(action.description()));
        }
    }

    /**
     * Создаёт пользователя.
     *
     * @param inputReader объект для чтения пользовательского ввода
     */
    private void createUser(InputReader inputReader) {
        String name = inputReader.readNonBlank("Введите имя: ");
        String email = readAvailableEmail(inputReader, "Введите e-mail: ", null);
        int age = inputReader.readAge("Введите возраст: ");
        UserCreateRequest request = new UserCreateRequest(name, email, age);
        Long id = userService.createUser(request);
        printer.printSuccess(Messages.USER_CREATED.formatted(id));
    }

    /**
     * Показывает пользователя по идентификатору.
     *
     * @param inputReader объект для чтения пользовательского ввода
     */
    private void showUserById(InputReader inputReader) {
        long id = readExistingUserId(inputReader, "Введите id пользователя: ");
        UserEntity userEntity = userService.getUserById(id);
        printer.printUser(userEntity);
    }

    /**
     * Показывает всех пользователей.
     */
    private void showAllUsers() {
        List<UserEntity> userEntities = userService.getAllUsers();
        printer.printUsers(userEntities);
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param inputReader объект для чтения пользовательского ввода
     */
    private void updateUser(InputReader inputReader) {
        long id = readExistingUserId(inputReader, "Введите id пользователя для обновления: ");
        String name = inputReader.readNonBlank("Введите новое имя: ");
        String email = readAvailableEmail(inputReader, "Введите новый e-mail: ", id);
        int age = inputReader.readAge("Введите новый возраст: ");
        UserUpdateRequest request = new UserUpdateRequest(id, name, email, age);
        UserEntity updated = userService.updateUser(request);
        printer.printSuccess(Messages.USER_UPDATED);
        printer.printUser(updated);
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param inputReader объект для чтения пользовательского ввода
     */
    private void deleteUser(InputReader inputReader) {
        long id = readExistingUserId(inputReader, "Введите id пользователя для удаления: ");
        boolean confirmed = inputReader.readConfirmation("Подтвердите удаление (Y/N): ");
        if (!confirmed) {
            printer.printInfo(Messages.DELETE_CANCELLED);
            return;
        }
        userService.deleteUser(id);
        printer.printSuccess(Messages.USER_DELETED);
    }

    /**
     * Считывает e-mail и сразу проверяет его уникальность.
     *
     * @param inputReader объект для чтения пользовательского ввода
     * @param prompt текст приглашения ко вводу
     * @param currentUserId идентификатор текущего пользователя или null
     * @return корректный и свободный e-mail
     */
    private String readAvailableEmail(InputReader inputReader, String prompt, Long currentUserId) {
        while (true) {
            String email = inputReader.readEmail(prompt);
            try {
                userService.ensureEmailAvailable(email, currentUserId);
                return email;
            } catch (InvalidInputException e) {
                printer.printError(e.getMessage());
            }
        }
    }

    /**
     * Считывает идентификатор пользователя и проверяет, что пользователь существует.
     *
     * @param inputReader объект для чтения пользовательского ввода
     * @param prompt текст приглашения ко вводу
     * @return существующий идентификатор пользователя
     */
    private long readExistingUserId(InputReader inputReader, String prompt) {
        while (true) {
            long id = inputReader.readPositiveId(prompt);
            try {
                userService.ensureUserExists(id);
                return id;
            } catch (EntityNotFoundException e) {
                printer.printError(e.getMessage());
            }
        }
    }
}