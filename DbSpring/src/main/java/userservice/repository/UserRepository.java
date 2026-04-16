package userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import userservice.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEmail(String email);
}
