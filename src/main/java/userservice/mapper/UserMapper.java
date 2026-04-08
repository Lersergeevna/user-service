package userservice.mapper;

import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;

/**
 * Преобразует DTO-запросы в сущности {@link UserEntity}.
 */
public final class UserMapper {
    private UserMapper() {
    }

    /**
     * Преобразует запрос на создание в новую сущность пользователя.
     *
     * @param request валидированный запрос на создание
     * @return новая сущность пользователя
     */
    public static UserEntity toEntity(UserCreateRequest request) {
        return new UserEntity(request.name(), request.email(), request.age());
    }

    /**
     * Применяет запрос на обновление к существующей сущности.
     *
     * @param userEntity существующий пользователь
     * @param request  валидированный запрос на обновление
     * @return обновлённая сущность пользователя
     */
    public static UserEntity applyUpdate(UserEntity userEntity, UserUpdateRequest request) {
        userEntity.setName(request.name());
        userEntity.setEmail(request.email());
        userEntity.setAge(request.age());
        return userEntity;
    }
}