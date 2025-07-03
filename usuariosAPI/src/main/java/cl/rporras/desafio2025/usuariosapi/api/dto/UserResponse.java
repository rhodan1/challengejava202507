package cl.rporras.desafio2025.usuariosapi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;

    @JsonProperty("nombre")
    private String name;

    @JsonProperty("correo")
    private String email;

    @JsonProperty("telefonos")
    private List<PhoneResponse> phones;

    @JsonProperty("creado")
    private LocalDateTime created;

    @JsonProperty("modificado")
    private LocalDateTime modified;

    @JsonProperty("ultimoLogin")
    private LocalDateTime lastLogin;

    @JsonProperty("activo")
    private boolean active;

    private String token;
}