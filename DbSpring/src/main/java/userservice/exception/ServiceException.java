package userservice.exception;

/**
 * Базовое исключение сервисного слоя.
 */
public class ServiceException extends RuntimeException {
    /**
     * Создаёт исключение с сообщением.
     *
     * @param message безопасное для пользователя сообщение
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и исходной причиной.
     *
     * @param message безопасное для пользователя сообщение
     * @param cause   исходная причина ошибки
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}