package cl.rporras.desafio2025.usuariosapi.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    @Value("${password.pattern}")
    private String pattern;

    public boolean isValid(String password) {
        return password != null && password.matches(pattern);
    }
}

