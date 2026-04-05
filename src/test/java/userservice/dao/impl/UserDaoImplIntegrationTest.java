package userservice.dao.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import userservice.config.DatabaseMigrator;
import userservice.config.HibernateUtil;
import userservice.constants.Messages;
import userservice.entity.UserEntity;
import userservice.exception.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("user_service_test")
            .withUsername("postgres")
            .withPassword("postgres");

    private final UserDaoImpl userDao = new UserDaoImpl();

    @BeforeAll
    static void beforeAll() {
        System.setProperty("db.url", POSTGRES.getJdbcUrl());
        System.setProperty("db.username", POSTGRES.getUsername());
        System.setProperty("db.password", POSTGRES.getPassword());

        HibernateUtil.shutdown();
        DatabaseMigrator.migrate();
    }

    @BeforeEach
    void cleanDatabase() {
        HibernateUtil.executeInTransaction(session -> {
            session.createMutationQuery("delete from UserEntity").executeUpdate();
            return null;
        });
    }

    @AfterAll
    static void afterAll() {
        HibernateUtil.shutdown();
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
    }

    @Test
    void saveAndFindByIdShouldPersistUser() {
        UserEntity user = new UserEntity("Alice", "alice@example.com", 25);

        Long id = userDao.save(user);
        Optional<UserEntity> found = userDao.findById(id);

        assertAll(
                () -> assertNotNull(id),
                () -> assertTrue(found.isPresent()),
                () -> assertEquals("Alice", found.orElseThrow().getName()),
                () -> assertEquals("alice@example.com", found.orElseThrow().getEmail()),
                () -> assertEquals(25, found.orElseThrow().getAge()),
                () -> assertNotNull(found.orElseThrow().getCreatedAt())
        );
    }

    @Test
    void saveShouldThrowDataAccessExceptionForDuplicateEmail() {
        userDao.save(new UserEntity("Alice", "alice@example.com", 25));

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> userDao.save(new UserEntity("Bob", "alice@example.com", 30))
        );

        assertEquals(Messages.DB_CONSTRAINT_FAILED, exception.getMessage());
    }

    @Test
    void findByIdShouldReturnEmptyWhenUserMissing() {
        Optional<UserEntity> found = userDao.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findByEmailShouldReturnUser() {
        userDao.save(new UserEntity("Bob", "bob@example.com", 30));

        Optional<UserEntity> found = userDao.findByEmail("bob@example.com");

        assertTrue(found.isPresent());
        assertEquals("Bob", found.orElseThrow().getName());
    }

    @Test
    void findByEmailShouldReturnEmptyWhenUserMissing() {
        Optional<UserEntity> found = userDao.findByEmail("missing@example.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void findAllShouldReturnUsersOrderedById() {
        Long firstId = userDao.save(new UserEntity("Alice", "alice@example.com", 25));
        Long secondId = userDao.save(new UserEntity("Bob", "bob@example.com", 30));

        List<UserEntity> users = userDao.findAll();

        assertEquals(2, users.size());
        assertEquals(List.of(firstId, secondId), users.stream().map(UserEntity::getId).toList());
    }

    @Test
    void countShouldReturnZeroForEmptyDatabase() {
        assertEquals(0L, userDao.count());
    }

    @Test
    void countAndExistsByIdShouldReflectDatabaseState() {
        Long id = userDao.save(new UserEntity("Alice", "alice@example.com", 25));

        assertAll(
                () -> assertEquals(1L, userDao.count()),
                () -> assertTrue(userDao.existsById(id)),
                () -> assertFalse(userDao.existsById(id + 1))
        );
    }

    @Test
    void updateShouldMergeChanges() {
        Long id = userDao.save(new UserEntity("Alice", "alice@example.com", 25));
        UserEntity persisted = userDao.findById(id).orElseThrow();

        persisted.setName("Alice Updated");
        persisted.setEmail("updated@example.com");
        persisted.setAge(26);

        UserEntity updated = userDao.update(persisted);

        assertAll(
                () -> assertEquals("Alice Updated", updated.getName()),
                () -> assertEquals("updated@example.com", updated.getEmail()),
                () -> assertEquals(26, updated.getAge()),
                () -> assertEquals("updated@example.com", userDao.findById(id).orElseThrow().getEmail())
        );
    }

    @Test
    void updateShouldThrowDataAccessExceptionForDuplicateEmail() {
        Long firstId = userDao.save(new UserEntity("Alice", "alice@example.com", 25));
        userDao.save(new UserEntity("Bob", "bob@example.com", 30));

        UserEntity firstUser = userDao.findById(firstId).orElseThrow();
        firstUser.setEmail("bob@example.com");

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> userDao.update(firstUser)
        );

        assertEquals(Messages.DB_CONSTRAINT_FAILED, exception.getMessage());
    }

    @Test
    void deleteByIdShouldRemoveUser() {
        Long id = userDao.save(new UserEntity("Alice", "alice@example.com", 25));

        boolean deleted = userDao.deleteById(id);

        assertAll(
                () -> assertTrue(deleted),
                () -> assertTrue(userDao.findById(id).isEmpty()),
                () -> assertEquals(0L, userDao.count())
        );
    }

    @Test
    void deleteByIdShouldReturnFalseWhenUserMissing() {
        assertFalse(userDao.deleteById(999L));
    }
}