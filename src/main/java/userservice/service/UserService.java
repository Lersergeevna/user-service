package userservice.service;

import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;

import java.util.List;

/**
 * Описывает бизнес-операции для работы с пользователями.
 */
public interface UserService {
    /**
     * Создаёт нового пользователя.
     *
     * @param request данные для создания
     * @return идентификатор созданного пользователя
     */
    Long createUser(UserCreateRequest request);

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     */
    UserEntity getUserById(long id);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    List<UserEntity> getAllUsers();

    /**
     * Обновляет данные пользователя.
     *
     * @param request данные для обновления
     * @return обновлённый пользователь
     */
    UserEntity updateUser(UserUpdateRequest request);

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     */
    void deleteUser(long id);

    /**
     * Проверяет, есть ли в базе хотя бы один пользователь.
     *
     * @return true, если пользователи существуют, иначе false
     */
    boolean hasUsers();

    /**
     * Проверяет существование пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     */
    void ensureUserExists(long id);

    /**
     * Проверяет, свободен ли e-mail для создания или обновления пользователя.
     *
     * @param email e-mail для проверки
     * @param currentUserId идентификатор текущего пользователя или null
     */
    void ensureEmailAvailable(String email, Long currentUserId);
}