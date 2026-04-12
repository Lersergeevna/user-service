package userservice.mapper;

import userservice.dto.UserCreateRequest;
import userservice.dto.UserResponse;
import userservice.entity.UserEntity;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserEntity toEntity(UserCreateRequest request, String normalizedEmail) {
        return new UserEntity(request.name().trim(), normalizedEmail, request.age());
    }

    public static void applyUpdate(UserEntity userEntity, UserCreateRequest request, String normalizedEmail) {
        userEntity.setName(request.name().trim());
        userEntity.setEmail(normalizedEmail);
        userEntity.setAge(request.age());
    }

    public static UserResponse toResponse(UserEntity userEntity) {
        return new UserResponse(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getAge(),
                userEntity.getCreatedAt()
        );
    }
}
