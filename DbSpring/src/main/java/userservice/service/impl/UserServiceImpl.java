package userservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.dto.UserCreateRequest;
import userservice.dto.UserResponse;
import userservice.entity.UserEntity;
import userservice.exception.EntityNotFoundException;
import userservice.exception.InvalidInputException;
import userservice.mapper.UserMapper;
import userservice.repository.UserRepository;
import userservice.service.MessageService;
import userservice.service.UserService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MessageService messageService;

    public UserServiceImpl(UserRepository userRepository, MessageService messageService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        ensureEmailAvailable(normalizedEmail, null);

        UserEntity saved = userRepository.save(UserMapper.toEntity(request, normalizedEmail));
        return UserMapper.toResponse(saved);
    }

    @Override
    public UserResponse getUserById(long id) {
        long validId = requireValidId(id);
        return userRepository.findById(validId)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageService.getMessage("app.message.user-not-found-by-id", validId)
                ));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(long id, UserCreateRequest request) {
        long validId = requireValidId(id);
        UserEntity existing = userRepository.findById(validId)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageService.getMessage("app.message.user-not-found-by-id", validId)
                ));

        String normalizedEmail = normalizeEmail(request.email());
        ensureEmailAvailable(normalizedEmail, validId);

        UserMapper.applyUpdate(existing, request, normalizedEmail);
        return UserMapper.toResponse(existing);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        long validId = requireValidId(id);
        if (!userRepository.existsById(validId)) {
            throw new EntityNotFoundException(
                    messageService.getMessage("app.message.user-not-found-by-id", validId)
            );
        }

        userRepository.deleteById(validId);
    }

    private long requireValidId(long id) {
        if (id <= 0) {
            throw new InvalidInputException(
                    messageService.getMessage("app.message.invalid-entity-id", id)
            );
        }
        return id;
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
        boolean exists = currentUserId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, currentUserId);

        if (exists) {
            throw new InvalidInputException(
                    messageService.getMessage("app.message.duplicate-email")
            );
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}