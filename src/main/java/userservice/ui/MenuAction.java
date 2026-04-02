package userservice.ui;

import java.util.Arrays;

/**
 * Перечисление команд консольного меню.
 */
public enum MenuAction {
    CREATE("1", "Создать пользователя", false),
    GET_BY_ID("2", "Найти пользователя по id", true),
    GET_ALL("3", "Показать всех пользователей", true),
    UPDATE("4", "Обновить пользователя", true),
    DELETE("5", "Удалить пользователя", true),
    EXIT("0", "Выход", false);

    private final String code;
    private final String description;
    private final boolean requiresExistingUsers;

    MenuAction(String code, String description, boolean requiresExistingUsers) {
        this.code = code;
        this.description = description;
        this.requiresExistingUsers = requiresExistingUsers;
    }

    /**
     * Возвращает код команды.
     *
     * @return код команды
     */
    public String code() {
        return code;
    }

    /**
     * Возвращает описание команды.
     *
     * @return описание команды
     */
    public String description() {
        return description;
    }

    /**
     * Показывает, требует ли команда наличия пользователей в базе.
     *
     * @return true, если команда доступна только при наличии пользователей
     */
    public boolean requiresExistingUsers() {
        return requiresExistingUsers;
    }

    /**
     * Находит команду по коду.
     *
     * @param code код команды
     * @return найденная команда или null
     */
    public static MenuAction fromCode(String code) {
        return Arrays.stream(values())
                .filter(action -> action.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}