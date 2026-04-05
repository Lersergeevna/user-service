package userservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import userservice.constants.Messages;
import userservice.dao.UserDao;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;
import userservice.exception.DataAccessException;
import userservice.exception.EntityNotFoundException;
import userservice.exception.InvalidInputException;
import userservice.exception.ServiceException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao);
    }

    @Test
    void createUserShouldSaveValidatedUser() {
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userDao.save(any(UserEntity.class))).thenReturn(10L);

        Long id = userService.createUser(new UserCreateRequest("  Alice  ", "TEST@EXAMPLE.COM", 25));

        assertEquals(10L, id);
        verify(userDao).findByEmail("test@example.com");
        verify(userDao).save(argThat(user ->
                user.getName().equals("Alice")
                        && user.getEmail().equals("test@example.com")
                        && user.getAge().equals(25)));
    }

    @Test
    void createUserShouldThrowWhenRequestIsNull() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> userService.createUser(null));

        assertEquals(Messages.NULL_REQUEST, exception.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    void createUserShouldThrowWhenEmailAlreadyExists() {
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user("Bob", "test@example.com", 30, 1L)));

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> userService.createUser(new UserCreateRequest("Alice", "test@example.com", 25)));

        assertEquals(Messages.DUPLICATE_EMAIL, exception.getMessage());
        verify(userDao, never()).save(any());
    }

    @Test
    void createUserShouldWrapDaoError() {
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userDao.save(any(UserEntity.class))).thenThrow(new DataAccessException(Messages.SAVE_DB_FAILED, new RuntimeException()));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.createUser(new UserCreateRequest("Alice", "test@example.com", 25)));

        assertEquals(Messages.CREATE_FAILED, exception.getMessage());
    }

    @Test
    void getUserByIdShouldReturnUser() {
        UserEntity expected = user("Alice", "alice@example.com", 25, 5L);
        when(userDao.findById(5L)).thenReturn(Optional.of(expected));

        UserEntity actual = userService.getUserById(5L);

        assertSame(expected, actual);
    }

    @Test
    void getUserByIdShouldThrowWhenNotFound() {
        when(userDao.findById(7L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(7L));

        assertEquals(Messages.USER_NOT_FOUND_BY_ID.formatted(7L), exception.getMessage());
    }

    @Test
    void getAllUsersShouldReturnUsers() {
        List<UserEntity> expected = List.of(
                user("Alice", "alice@example.com", 25, 1L),
                user("Bob", "bob@example.com", 30, 2L)
        );
        when(userDao.findAll()).thenReturn(expected);

        List<UserEntity> actual = userService.getAllUsers();

        assertEquals(expected, actual);
    }

    @Test
    void updateUserShouldApplyChangesAndReturnUpdatedUser() {
        UserEntity existing = user("Alice", "alice@example.com", 25, 3L);
        when(userDao.findById(3L)).thenReturn(Optional.of(existing));
        when(userDao.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userDao.update(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity updated = userService.updateUser(new UserUpdateRequest(3L, "  New Name ", "NEW@example.com", 31));

        assertEquals("New Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals(31, updated.getAge());
        verify(userDao).update(existing);
    }

    @Test
    void updateUserShouldAllowSameEmailForCurrentUser() {
        UserEntity existing = user("Alice", "alice@example.com", 25, 3L);
        when(userDao.findById(3L)).thenReturn(Optional.of(existing));
        when(userDao.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));
        when(userDao.update(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> userService.updateUser(new UserUpdateRequest(3L, "Alice", "alice@example.com", 26)));
    }

    @Test
    void deleteUserShouldThrowWhenNothingDeleted() {
        when(userDao.deleteById(9L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser(9L));

        assertEquals(Messages.USER_NOT_FOUND_BY_ID.formatted(9L), exception.getMessage());
    }

    @Test
    void deleteUserShouldWrapDaoError() {
        when(userDao.deleteById(2L)).thenThrow(new DataAccessException(Messages.DELETE_DB_FAILED, new RuntimeException()));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.deleteUser(2L));

        assertEquals(Messages.DELETE_FAILED, exception.getMessage());
    }

    @Test
    void hasUsersShouldReturnTrueWhenCountGreaterThanZero() {
        when(userDao.count()).thenReturn(1L);
        assertTrue(userService.hasUsers());
    }

    @Test
    void ensureUserExistsShouldThrowWhenMissing() {
        when(userDao.existsById(11L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.ensureUserExists(11L));

        assertEquals(Messages.USER_NOT_FOUND_BY_ID.formatted(11L), exception.getMessage());
    }

    @Test
    void ensureEmailAvailableShouldWrapDaoError() {
        when(userDao.findByEmail("alice@example.com"))
                .thenThrow(new DataAccessException(Messages.FIND_BY_EMAIL_DB_FAILED, new RuntimeException()));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.ensureEmailAvailable("alice@example.com", null));

        assertEquals(Messages.EMAIL_AVAILABILITY_CHECK_FAILED, exception.getMessage());
    }

    private static UserEntity user(String name, String email, int age, Long id) {
        UserEntity user = new UserEntity(name, email, age);
        setField(user, "id", id);
        setField(user, "createdAt", LocalDateTime.now());
        return user;
    }

    private static void setField(Object target, String fieldName, Object value) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                type = type.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Field not found: " + fieldName);
    }
}
