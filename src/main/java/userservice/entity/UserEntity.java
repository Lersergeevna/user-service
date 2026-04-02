package userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Сущность пользователя.
 */
@Entity(name = "UserEntity")
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email")
)
public class UserEntity extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private Integer age;

    /**
     * Конструктор без аргументов, необходимый Hibernate.
     */
    protected UserEntity() {
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param name  имя пользователя
     * @param email e-mail пользователя
     * @param age   возраст пользователя
     */
    public UserEntity(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name новое имя пользователя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает e-mail пользователя.
     *
     * @return e-mail пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает e-mail пользователя.
     *
     * @param email новый e-mail пользователя
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает возраст пользователя.
     *
     * @return возраст пользователя
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Устанавливает возраст пользователя.
     *
     * @param age новый возраст пользователя
     */
    public void setAge(Integer age) {
        this.age = age;
    }
}