package cl.rporras.desafio2025.usuariosapi.service;

import cl.rporras.desafio2025.usuariosapi.api.dto.*;
import cl.rporras.desafio2025.usuariosapi.repository.UserRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    private CreateUserRequest buildCreateUserRequest(String email) {
        var phone = new CreateUserPhoneRequest();
        phone.setNumber("123456");
        phone.setCityCode("2");
        phone.setCountryCode("56");

        var req = new CreateUserRequest();
        req.setName("TestUser");
        req.setEmail(email);
        req.setPassword("ValidPassword123");
        req.setPhones(List.of(phone));
        return req;
    }

    @Test
    void createAndFindUser_success() {
        CreateUserRequest req = buildCreateUserRequest("test@integration.com");
        UserResponse created = userService.createUser(req);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo(req.getEmail());
        assertThat(created.getName()).isEqualTo(req.getName());
        assertThat(created.getPhones()).hasSize(1);

        UserResponse fetched = userService.findUserById(created.getId());
        assertThat(fetched.getEmail()).isEqualTo(created.getEmail());
    }

    @Test
    void updateUser_success() {
        CreateUserRequest req = buildCreateUserRequest("update@integration.com");
        UserResponse created = userService.createUser(req);

        req.setName("UpdatedName");
        req.setPassword("NewPassword1");

        UserResponse updated = userService.updateUser(created.getId(), req);

        assertThat(updated.getName()).isEqualTo("UpdatedName");
        assertThat(updated.getEmail()).isEqualTo(req.getEmail());
    }

    @Test
    void patchUser_success() {
        CreateUserRequest req = buildCreateUserRequest("patch@integration.com");
        UserResponse created = userService.createUser(req);

        var patch = new PatchUserRequest();
        patch.setName("PatchedName");
        patch.setPassword("AnotherPassword1");

        UserResponse patched = userService.patchUser(created.getId(), patch);

        assertThat(patched.getName()).isEqualTo("PatchedName");
        assertThat(patched.getEmail()).isEqualTo(created.getEmail());
    }

    @Test
    void deleteUser_logicallyDisablesUser() {
        CreateUserRequest req = buildCreateUserRequest("delete@integration.com");
        UserResponse created = userService.createUser(req);

        userService.deleteUserById(created.getId());

        assertThatThrownBy(() -> userService.findUserById(created.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void findAllUsers_onlyActive() {
        CreateUserRequest req1 = buildCreateUserRequest("one@integration.com");
        CreateUserRequest req2 = buildCreateUserRequest("two@integration.com");

        UserResponse u1 = userService.createUser(req1);
        UserResponse u2 = userService.createUser(req2);

        userService.deleteUserById(u1.getId());

        List<UserResponse> activeUsers = userService.findAllUsers();
        assertThat(activeUsers).extracting(UserResponse::getEmail)
                .contains("two@integration.com")
                .doesNotContain("one@integration.com");
    }
}
