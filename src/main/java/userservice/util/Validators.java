package userservice.util;

import userservice.constants.Messages;
import userservice.exception.InvalidInputException;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Содержит централизованные методы валидации пользовательских данных.
 */
public final class Validators {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validators() {
    }

    /**
     * Проверяет корректность положительного идентификатора.
     *
     * @param id идентификатор для проверки
     * @return корректный идентификатор
     */
    public static long requireValidId(long id) {
        if (id <= 0) {
            throw new InvalidInputException(Messages.INVALID_ENTITY_ID);
        }
        return id;
    }

    /**
     * Проверяет корректность имени пользователя.
     *
     * @param name имя пользователя
     * @return нормализованное имя
     */
    public static String requireValidName(String name) {
        String normalized = requireNonBlank(name);
        if (normalized.length() > 100) {
            throw new InvalidInputException(Messages.INVALID_NAME);
        }
        return normalized;
    }

    /**
     * Проверяет корректность e-mail.
     *
     * @param email e-mail пользователя
     * @return нормализованный e-mail
     */
    public static String requireValidEmail(String email) {
        String normalized = requireNonBlank(email).toLowerCase(Locale.ROOT);
        if (normalized.length() > 150) {
            throw new InvalidInputException(Messages.INVALID_EMAIL_LENGTH);
        }
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidInputException(Messages.INVALID_EMAIL);
        }
        return normalized;
    }

    /**
     * Проверяет корректность возраста пользователя.
     *
     * @param age возраст пользователя
     * @return корректный возраст
     */
    public static int requireValidAge(int age) {
        if (age < 1 || age > 130) {
            throw new InvalidInputException(Messages.INVALID_AGE);
        }
        return age;
    }

    /**
     * Проверяет, что строка не пустая.
     *
     * @param value исходное значение
     * @return обрезанная строка
     */
    private static String requireNonBlank(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(Messages.EMPTY_VALUE);
        }
        return value.trim();
    }
}