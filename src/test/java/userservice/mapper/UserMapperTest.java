package userservice.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;

import java.lang.reflect.Field;

class UserMapperTest {

    @Test
    void toEntityShouldCreateNewUserEntity() {
        UserEntity user = UserMapper.toEntity(new UserCreateRequest("Alice", "alice@example.com", 25));

        Assertions.assertEquals("Alice", user.getName());
        Assertions.assertEquals("alice@example.com", user.getEmail());
        Assertions.assertEquals(25, user.getAge());
    }

    @Test
    void applyUpdateShouldMutateExistingEntity() throws Exception {
        UserEntity existing = new UserEntity("Alice", "alice@example.com", 25);
        Field idField = existing.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(existing, 7L);

        UserEntity updated = UserMapper.applyUpdate(existing,
                new UserUpdateRequest(7L, "Bob", "bob@example.com", 30));

        Assertions.assertSame(existing, updated);
        Assertions.assertEquals("Bob", updated.getName());
        Assertions.assertEquals("bob@example.com", updated.getEmail());
        Assertions.assertEquals(30, updated.getAge());
        Assertions.assertEquals(7L, updated.getId());
    }
}
