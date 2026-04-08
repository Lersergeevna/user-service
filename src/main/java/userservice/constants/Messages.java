package userservice.constants;

/**
 * Содержит все пользовательские сообщения приложения в едином стиле.
 */
public final class Messages {
    public static final String APP_TITLE = "Сервис управления пользователями";
    public static final String CHOOSE_ACTION = "Выберите действие:";
    public static final String INVALID_MENU_ITEM = "Некорректный пункт меню. Повторите ввод.";
    public static final String EXIT = "Работа приложения завершена.";
    public static final String UNEXPECTED_ERROR = "Произошла непредвиденная ошибка.";

    public static final String SUCCESS_PREFIX = "[УСПЕХ] ";
    public static final String INFO_PREFIX = "[ИНФО] ";
    public static final String ERROR_PREFIX = "[ОШИБКА] ";

    public static final String EMPTY_VALUE = "Значение не должно быть пустым.";
    public static final String INVALID_INT = "Введите целое число.";
    public static final String INVALID_ID = "Введите корректный положительный идентификатор.";
    public static final String INVALID_CONFIRMATION = "Введите Y или N.";

    public static final String USER_NOT_FOUND_BY_ID = "Пользователь с id=%d не найден.";
    public static final String USER_CREATED = "Пользователь создан. ID = %d.";
    public static final String USER_UPDATED = "Пользователь успешно обновлён.";
    public static final String USER_DELETED = "Пользователь успешно удалён.";
    public static final String USER_LIST_EMPTY = "Список пользователей пуст.";
    public static final String DELETE_CANCELLED = "Удаление отменено пользователем.";
    public static final String NO_USERS_FOR_COMMAND = "Команда \"%s\" недоступна: в базе данных нет пользователей. Сначала создайте хотя бы одного пользователя.";

    public static final String DUPLICATE_EMAIL = "Пользователь с таким e-mail уже существует.";
    public static final String INVALID_NAME = "Имя не должно быть пустым и длиннее 100 символов.";
    public static final String INVALID_EMAIL = "Неверный формат e-mail.";
    public static final String INVALID_EMAIL_LENGTH = "E-mail не должен быть длиннее 150 символов.";
    public static final String INVALID_AGE = "Возраст должен быть в диапазоне от 1 до 130 лет.";
    public static final String INVALID_ENTITY_ID = "ID должен быть положительным числом.";
    public static final String NULL_REQUEST = "Данные пользователя не должны быть пустыми.";

    public static final String CREATE_FAILED = "Не удалось создать пользователя.";
    public static final String READ_FAILED = "Не удалось получить пользователя.";
    public static final String READ_ALL_FAILED = "Не удалось получить список пользователей.";
    public static final String UPDATE_FAILED = "Не удалось обновить пользователя.";
    public static final String DELETE_FAILED = "Не удалось удалить пользователя.";
    public static final String USERS_EXISTENCE_CHECK_FAILED = "Не удалось проверить наличие пользователей.";
    public static final String EMAIL_AVAILABILITY_CHECK_FAILED = "Не удалось проверить уникальность e-mail.";

    public static final String SAVE_DB_FAILED = "Ошибка при сохранении пользователя.";
    public static final String FIND_BY_ID_DB_FAILED = "Ошибка при поиске пользователя по ID.";
    public static final String FIND_BY_EMAIL_DB_FAILED = "Ошибка при поиске пользователя по e-mail.";
    public static final String FIND_ALL_DB_FAILED = "Ошибка при получении списка пользователей.";
    public static final String COUNT_DB_FAILED = "Ошибка при подсчёте пользователей.";
    public static final String UPDATE_DB_FAILED = "Ошибка при обновлении пользователя.";
    public static final String DELETE_DB_FAILED = "Ошибка при удалении пользователя.";
    public static final String DB_CONSTRAINT_FAILED = "Нарушено ограничение базы данных.";

    public static final String CRITICAL_STARTUP_ERROR = "Критическая ошибка запуска приложения. Проверьте настройки базы данных и логи.";

    private Messages() {
    }

    /**
     * Добавляет к сообщению префикс ошибки.
     *
     * @param message текст сообщения
     * @return сообщение с префиксом ошибки
     */
    public static String formatError(String message) {
        return ERROR_PREFIX + message;
    }

    /**
     * Добавляет к сообщению префикс успешного выполнения.
     *
     * @param message текст сообщения
     * @return сообщение с префиксом успеха
     */
    public static String formatSuccess(String message) {
        return SUCCESS_PREFIX + message;
    }

    /**
     * Добавляет к сообщению информационный префикс.
     *
     * @param message текст сообщения
     * @return сообщение с информационным префиксом
     */
    public static String formatInfo(String message) {
        return INFO_PREFIX + message;
    }
}