package cl.rporras.desafio2025.usuariosapi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CreateUserRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @JsonProperty("nombre")
    private String name;

    @NotBlank(message = "El correo es obligatorio")
    @JsonProperty("correo")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty("contraseña")
    private String password;

    @JsonProperty("telefonos")
    private List<CreateUserPhoneRequest> phones;
}


