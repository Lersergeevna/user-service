package userservice.service;

import userservice.dto.UserCreateRequest;
import userservice.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(long id, UserCreateRequest request);

    void deleteUser(long id);
}
