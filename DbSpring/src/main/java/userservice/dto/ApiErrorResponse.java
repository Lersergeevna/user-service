package userservice.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO ответа с информацией об ошибке.
 *
 * @param timestamp время формирования ответа
 * @param status HTTP-статус ответа
 * @param error текстовое описание HTTP-статуса
 * @param message основное сообщение об ошибке
 * @param path путь запроса, на котором произошла ошибка
 * @param details дополнительные детали ошибки
 */
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {
}
