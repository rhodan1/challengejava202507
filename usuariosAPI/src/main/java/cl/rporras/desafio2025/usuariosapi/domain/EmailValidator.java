package cl.rporras.desafio2025.usuariosapi.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    @Value("${email.pattern}")
    private String pattern;

    public boolean isValid(String email) {
        return email != null && email.matches(pattern);
    }
}
