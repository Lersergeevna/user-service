package userservice.exception;

/**
 * Исключение уровня доступа к данным.
 */
public class DataAccessException extends RuntimeException {
    /**
     * Создаёт исключение с сообщением и исходной причиной.
     *
     * @param message безопасное для пользователя сообщение
     * @param cause   исходная причина ошибки
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}