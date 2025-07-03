package cl.rporras.desafio2025.usuariosapi.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(String password) {
        return encoder.encode(password); // genera salt y hash
    }

    public boolean matches(String password, String hashedPassword) {
        return encoder.matches(password, hashedPassword);
    }
}

