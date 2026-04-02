package userservice.dao;

import userservice.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Описывает операции доступа к данным пользователя.
 */
public interface UserDao {
    /**
     * Сохраняет пользователя в базе данных.
     *
     * @param userEntity пользователь для сохранения
     * @return идентификатор сохранённого пользователя
     */
    Long save(UserEntity userEntity);

    /**
     * Ищет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь или пустой результат
     */
    Optional<UserEntity> findById(Long id);

    /**
     * Ищет пользователя по e-mail.
     *
     * @param email e-mail пользователя
     * @return найденный пользователь или пустой результат
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    List<UserEntity> findAll();

    /**
     * Возвращает количество пользователей в базе данных.
     *
     * @return количество пользователей
     */
    long count();

    /**
     * Проверяет существование пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь существует, иначе false
     */
    boolean existsById(Long id);

    /**
     * Обновляет данные пользователя.
     *
     * @param userEntity пользователь с новыми данными
     * @return обновлённый пользователь
     */
    UserEntity update(UserEntity userEntity);

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь удалён, иначе false
     */
    boolean deleteById(Long id);
}