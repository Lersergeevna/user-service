package userservice.service.impl;

import userservice.constants.Messages;
import userservice.dao.UserDao;
import userservice.dao.impl.UserDaoImpl;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;
import userservice.exception.DataAccessException;
import userservice.exception.EntityNotFoundException;
import userservice.exception.InvalidInputException;
import userservice.exception.ServiceException;
import userservice.mapper.UserMapper;
import userservice.service.UserService;
import userservice.util.Validators;

import java.util.List;

/**
 * Реализация сервисного слоя для работы с пользователями.
 */
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    /**
     * Создаёт сервис с реализацией DAO по умолчанию.
     */
    public UserServiceImpl() {
        this(new UserDaoImpl());
    }

    /**
     * Создаёт сервис с переданным DAO.
     *
     * @param userDao DAO для работы с пользователями
     */
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createUser(UserCreateRequest request) {
        UserCreateRequest validRequest = validateCreateRequest(request);
        ensureEmailAvailable(validRequest.email(), null);
        try {
            UserEntity user = UserMapper.toEntity(validRequest);
            return userDao.save(user);
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.CREATE_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserEntity getUserById(long id) {
        long validId = Validators.requireValidId(id);
        try {
            return userDao.findById(validId)
                    .orElseThrow(() ->
                            new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(validId)));
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.READ_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEntity> getAllUsers() {
        try {
            return userDao.findAll();
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.READ_ALL_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserEntity updateUser(UserUpdateRequest request) {
        UserUpdateRequest validRequest = validateUpdateRequest(request);
        UserEntity existing = getUserById(validRequest.id());
        ensureEmailAvailable(validRequest.email(), validRequest.id());
        try {
            UserEntity updated = UserMapper.applyUpdate(existing, validRequest);
            return userDao.update(updated);
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.UPDATE_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(long id) {
        long validId = Validators.requireValidId(id);
        try {
            boolean deleted = userDao.deleteById(validId);
            if (!deleted) {
                throw new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(validId));
            }
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.DELETE_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUsers() {
        try {
            return userDao.count() > 0;
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.USERS_EXISTENCE_CHECK_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ensureUserExists(long id) {
        long validId = Validators.requireValidId(id);
        try {
            if (!userDao.existsById(validId)) {
                throw new EntityNotFoundException(Messages.USER_NOT_FOUND_BY_ID.formatted(validId));
            }
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.READ_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ensureEmailAvailable(String email, Long currentUserId) {
        String validEmail = Validators.requireValidEmail(email);
        try {
            userDao.findByEmail(validEmail).ifPresent(userEntity -> {
                if (currentUserId == null || !userEntity.getId().equals(currentUserId)) {
                    throw new InvalidInputException(Messages.DUPLICATE_EMAIL);
                }
            });
        } catch (DataAccessException e) {
            throw new ServiceException(Messages.EMAIL_AVAILABILITY_CHECK_FAILED, e);
        }
    }

    /**
     * Проверяет корректность данных для создания пользователя.
     *
     * @param request данные для создания
     * @return валидные данные
     */
    private UserCreateRequest validateCreateRequest(UserCreateRequest request) {
        if (request == null) {
            throw new InvalidInputException(Messages.NULL_REQUEST);
        }
        return new UserCreateRequest(
                Validators.requireValidName(request.name()),
                Validators.requireValidEmail(request.email()),
                Validators.requireValidAge(request.age())
        );
    }

    /**
     * Проверяет корректность данных для обновления пользователя.
     *
     * @param request данные для обновления
     * @return валидные данные
     */
    private UserUpdateRequest validateUpdateRequest(UserUpdateRequest request) {
        if (request == null) {
            throw new InvalidInputException(Messages.NULL_REQUEST);
        }
        return new UserUpdateRequest(
                Validators.requireValidId(request.id()),
                Validators.requireValidName(request.name()),
                Validators.requireValidEmail(request.email()),
                Validators.requireValidAge(request.age())
        );
    }
}