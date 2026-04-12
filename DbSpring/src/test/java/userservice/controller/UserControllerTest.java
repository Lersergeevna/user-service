package userservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import userservice.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/users creates user and returns dto")
    void createUser_shouldReturnCreatedUserDto() throws Exception {
        String requestBody = """
                {
                  "name": "Lera",
                  "email": "Lera@Example.com",
                  "age": 25
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Lera"))
                .andExpect(jsonPath("$.email").value("lera@example.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("GET /api/users/{id} returns existing user")
    void getUserById_shouldReturnUser() throws Exception {
        long userId = createUser("Mila", "mila@example.com", 30);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Mila"))
                .andExpect(jsonPath("$.email").value("mila@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    @DisplayName("GET /api/users/{id} returns 404 for missing user")
    void getUserById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с id=9999 не найден."));
    }

    @Test
    @DisplayName("GET /api/users returns list of users")
    void getAllUsers_shouldReturnUsersList() throws Exception {
        createUser("Anna", "anna@example.com", 21);
        createUser("Kate", "kate@example.com", 22);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("PUT /api/users/{id} updates user")
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        long userId = createUser("Old Name", "old@example.com", 20);
        String requestBody = """
                {
                  "name": "New Name",
                  "email": "NEW@example.com",
                  "age": 35
                }
                """;

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.age").value(35));
    }

    @Test
    @DisplayName("PUT /api/users/{id} returns bad request for duplicate email")
    void updateUser_shouldFailOnDuplicateEmail() throws Exception {
        createUser("First", "first@example.com", 20);
        long secondUserId = createUser("Second", "second@example.com", 21);

        String requestBody = """
                {
                  "name": "Second Updated",
                  "email": "first@example.com",
                  "age": 22
                }
                """;

        mockMvc.perform(put("/api/users/{id}", secondUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с таким e-mail уже существует."));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} deletes user")
    void deleteUser_shouldReturnNoContent() throws Exception {
        long userId = createUser("Delete Me", "delete@example.com", 28);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/{id} returns bad request for non-positive id")
    void getUserById_shouldReturnBadRequestForInvalidId() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 0))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID должен быть положительным числом."));
    }

    @Test
    @DisplayName("POST /api/users returns bad request for duplicate email")
    void createUser_shouldFailOnDuplicateEmail() throws Exception {
        createUser("First", "duplicate@example.com", 22);
        String requestBody = """
                {
                  "name": "Second",
                  "email": "duplicate@example.com",
                  "age": 23
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с таким e-mail уже существует."));
    }

    @Test
    @DisplayName("POST /api/users returns bad request for invalid body")
    void createUser_shouldFailOnInvalidBody() throws Exception {
        String requestBody = """
                {
                  "name": "",
                  "email": "wrong-email",
                  "age": 200
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.details").isArray());
    }

    private long createUser(String name, String email, int age) throws Exception {
        String requestBody = """
                {
                  "name": "%s",
                  "email": "%s",
                  "age": %d
                }
                """.formatted(name, email, age);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("id").asLong();
    }
}
