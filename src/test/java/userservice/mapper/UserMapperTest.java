package userservice.mapper;

import org.junit.jupiter.api.Test;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserUpdateRequest;
import userservice.entity.UserEntity;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toEntityShouldCreateNewUserEntity() {
        UserEntity user = UserMapper.toEntity(new UserCreateRequest("Alice", "alice@example.com", 25));

        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void applyUpdateShouldMutateExistingEntity() throws Exception {
        UserEntity existing = new UserEntity("Alice", "alice@example.com", 25);
        Field idField = existing.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(existing, 7L);

        UserEntity updated = UserMapper.applyUpdate(existing,
                new UserUpdateRequest(7L, "Bob", "bob@example.com", 30));

        assertSame(existing, updated);
        assertEquals("Bob", updated.getName());
        assertEquals("bob@example.com", updated.getEmail());
        assertEquals(30, updated.getAge());
        assertEquals(7L, updated.getId());
    }
}
