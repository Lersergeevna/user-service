package userservice.service;

import userservice.dto.UserCreateRequest;
import userservice.dto.UserResponse;

import java.util.List;

/**
 * Сервис для работы с пользователями.
 */
public interface UserService {

    /**
     * Создаёт нового пользователя.
     *
     * @param request данные для создания пользователя
     * @return созданный пользователь
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     */
    UserResponse getUserById(long id);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    List<UserResponse> getAllUsers();

    /**
     * Обновляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @param request новые данные пользователя
     * @return обновлённый пользователь
     */
    UserResponse updateUser(long id, UserCreateRequest request);

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     */
    void deleteUser(long id);
}
