package userservice.dto;

/**
 * Неизменяемый запрос на создание пользователя.
 *
 * @param name  имя пользователя
 * @param email email пользователя
 * @param age   возраст пользователя
 */
public record UserCreateRequest(String name, String email, int age) {
}