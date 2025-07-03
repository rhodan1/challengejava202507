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

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceEdgeCaseTest {

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
    void emailWithWhitespace_shouldFail() {
        CreateUserRequest req = buildCreateUserRequest("  test@correo.com  ");
        assertThatThrownBy(() -> userService.createUser(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void patchOnlyNombre_shouldNotAffectOthers() {
        var original = buildCreateUserRequest("patchname@correo.com");
        var created = userService.createUser(original);

        var patch = new PatchUserRequest();
        patch.setName("NuevoNombre");

        var patched = userService.patchUser(created.getId(), patch);
        assertThat(patched.getName()).isEqualTo("NuevoNombre");
        assertThat(patched.getEmail()).isEqualTo(created.getEmail());
    }

    @Test
    void deleteTwice_shouldThrowOnSecond() {
        var req = buildCreateUserRequest("doubledelete@correo.com");
        var created = userService.createUser(req);

        userService.deleteUserById(created.getId());

        assertThatThrownBy(() -> userService.deleteUserById(created.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void concurrentEmailCreation_shouldRejectDuplicate() {
        var req1 = buildCreateUserRequest("dup@correo.com");
        var req2 = buildCreateUserRequest("dup@correo.com");

        userService.createUser(req1);

        assertThatThrownBy(() -> userService.createUser(req2))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void invalidEmailFormat_shouldThrow() {
        var req = buildCreateUserRequest("noformato");
        assertThatThrownBy(() -> userService.createUser(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invalidPasswordFormat_shouldThrow() {
        var req = buildCreateUserRequest("valido@correo.com");
        req.setPassword("123");
        assertThatThrownBy(() -> userService.createUser(req))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void unhandledException_shouldBubbleUp() {
        assertThatThrownBy(() -> userService.findUserById(UUID.fromString("00000000-0000-0000-0000-000000000000")))
                .isInstanceOf(RuntimeException.class);
    }
}

