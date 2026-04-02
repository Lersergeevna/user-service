package userservice.util;

import org.hibernate.exception.ConstraintViolationException;

/**
 * Содержит вспомогательные методы для анализа цепочки исключений.
 */
public final class ExceptionUtils {
    private ExceptionUtils() {
    }

    /**
     * Проверяет, содержит ли цепочка исключений ошибку нарушения ограничения базы данных.
     *
     * @param throwable исключение для анализа
     * @return {@code true}, если найдено нарушение ограничения базы данных
     */
    public static boolean isConstraintViolation(Throwable throwable) {
        Throwable current = throwable;

        while (current != null) {
            if (current instanceof ConstraintViolationException) {
                return true;
            }
            current = current.getCause();
        }

        return false;
    }
}