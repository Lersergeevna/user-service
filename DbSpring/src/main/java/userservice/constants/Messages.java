package userservice.constants;

public final class Messages {
    public static final String USER_NOT_FOUND_BY_ID = "Пользователь с id=%d не найден.";
    public static final String DUPLICATE_EMAIL = "Пользователь с таким e-mail уже существует.";
    public static final String INVALID_ENTITY_ID = "ID должен быть положительным числом.";
    public static final String VALIDATION_FAILED = "Validation failed.";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error.";

    private Messages() {
    }
}
