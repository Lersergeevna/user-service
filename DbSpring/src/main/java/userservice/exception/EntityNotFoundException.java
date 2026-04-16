package userservice.exception;

/**
 * Исключение, сигнализирующее об отсутствии запрошенной сущности.
 */
public class EntityNotFoundException extends ServiceException {
    /**
     * Создаёт исключение с сообщением.
     *
     * @param message безопасное для пользователя сообщение
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}