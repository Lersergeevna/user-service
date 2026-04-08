package userservice.dao.impl;

import org.junit.jupiter.api.*;
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

        Assertions.assertAll(
                () -> Assertions.assertNotNull(id),
                () -> Assertions.assertTrue(found.isPresent()),
                () -> Assertions.assertEquals("Alice", found.orElseThrow().getName()),
                () -> Assertions.assertEquals("alice@example.com", found.orElseThrow().getEmail()),
                () -> Assertions.assertEquals(25, found.orElseThrow().getAge()),
                () -> Assertions.assertNotNull(found.orElseThrow().getCreatedAt())
        );
    }

    @Test
    void saveShouldThrowDataAccessExceptionForDuplicateEmail() {
        userDao.save(new UserEntity("Alice", "alice@example.com", 25));

        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> userDao.save(new UserEntity("Bob", "alice@example.com", 30))
        );

        Assertions.assertEquals(Messages.DB_CONSTRAINT_FAILED, exception.getMessage());
    }

    @Test
    void findByIdShouldReturnEmptyWhenUserMissing() {
        Optional<UserEntity> found = userDao.findById(999L);

        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    void findByEmailShouldReturnUser() {
        userDao.save(new UserEntity("Bob", "bob@example.com", 30));

        Optional<UserEntity> found = userDao.findByEmail("bob@example.com");

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Bob", found.orElseThrow().getName());
    }

    @Test
    void findByEmailShouldReturnEmptyWhenUserMissing() {
        Optional<UserEntity> found = userDao.findByEmail("missing@example.com");

        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    void findAllShouldReturnUsersOrderedById() {
        Long firstId = userDao.save(new UserEntity("Alice", "alice@example.com", 25));
        Long secondId = userDao.save(new UserEntity("Bob", "bob@example.com", 30));

        List<UserEntity> users = userDao.findAll();

        Assertions.assertEquals(2, users.size());
        Assertions.assertEquals(List.of(firstId, secondId), users.stream().map(UserEntity::getId).toList());
    }

    @Test
    void countShouldReturnZeroForEmptyDatabase() {
        Assertions.assertEquals(0L, userDao.count());
    }

    @Test
    void countAndExistsByIdShouldReflectDatabaseState() {
        Long id = userDao.save(new UserEntity("Alice", "alice@example.com", 25));

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, userDao.count()),
                () -> Assertions.assertTrue(userDao.existsById(id)),
                () -> Assertions.assertFalse(userDao.existsById(id + 1))
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

        Assertions.assertAll(
                () -> Assertions.assertEquals("Alice Updated", updated.getName()),
                () -> Assertions.assertEquals("updated@example.com", updated.getEmail()),
                () -> Assertions.assertEquals(26, updated.getAge()),
                () -> Assertions.assertEquals("updated@example.com", userDao.findById(id).orElseThrow().getEmail())
        );
    }

    @Test
    void updateShouldThrowDataAccessExceptionForDuplicateEmail() {
        Long firstId = userDao.save(new UserEntity("Alice", "alice@example.com", 25));
        userDao.save(new UserEntity("Bob", "bob@example.com", 30));

        UserEntity firstUser = userDao.findById(firstId).orElseThrow();
        firstUser.setEmail("bob@example.com");

        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> userDao.update(firstUser)
        );

        Assertions.assertEquals(Messages.DB_CONSTRAINT_FAILED, exception.getMessage());
    }

    @Test
    void deleteByIdShouldRemoveUser() {
        Long id = userDao.save(new UserEntity("Alice", "alice@example.com", 25));

        boolean deleted = userDao.deleteById(id);

        Assertions.assertAll(
                () -> Assertions.assertTrue(deleted),
                () -> Assertions.assertTrue(userDao.findById(id).isEmpty()),
                () -> Assertions.assertEquals(0L, userDao.count())
        );
    }

    @Test
    void deleteByIdShouldReturnFalseWhenUserMissing() {
        Assertions.assertFalse(userDao.deleteById(999L));
    }
}