package userservice.exception;

/**
 * Исключение, сигнализирующее о некорректном пользовательском вводе.
 */
public class InvalidInputException extends ServiceException {
    /**
     * Создаёт исключение с сообщением.
     *
     * @param message безопасное для пользователя сообщение
     */
    public InvalidInputException(String message) {
        super(message);
    }
}