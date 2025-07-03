package cl.rporras.desafio2025.usuariosapi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PatchUserRequest {
    @JsonProperty("nombre")
    private String name;

    @JsonProperty("correo")
    private String email;

    @JsonProperty("contraseña")
    private String password;

    @JsonProperty("telefonos")
    private List<CreateUserPhoneRequest> phones;
}