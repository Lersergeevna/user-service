package userservice.dto;

/**
 * Неизменяемый запрос на обновление пользователя.
 *
 * @param id    идентификатор пользователя
 * @param name  имя пользователя
 * @param email email пользователя
 * @param age   возраст пользователя
 */
public record UserUpdateRequest(long id, String name, String email, int age) {
}