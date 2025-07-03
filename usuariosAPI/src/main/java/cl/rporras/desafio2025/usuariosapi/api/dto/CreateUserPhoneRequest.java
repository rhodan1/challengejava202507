package cl.rporras.desafio2025.usuariosapi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateUserPhoneRequest {
    @NotBlank(message = "El número es obligatorio")
    @JsonProperty("numero")
    private String number;

    @NotBlank(message = "El código de ciudad es obligatorio")
    @JsonProperty("codigoCiudad")
    private String cityCode;

    @NotBlank(message = "El código de país es obligatorio")
    @JsonProperty("codigoPais")
    private String countryCode;
}