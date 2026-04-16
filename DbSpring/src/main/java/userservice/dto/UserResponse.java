package userservice.dto;

import java.time.LocalDateTime;

/**
 * DTO ответа с данными пользователя.
 *
 * @param id идентификатор пользователя
 * @param name имя пользователя
 * @param email email пользователя
 * @param age возраст пользователя
 * @param createdAt дата и время создания пользователя
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {
}
