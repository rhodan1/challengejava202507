package cl.rporras.desafio2025.usuariosapi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PhoneResponse {
    @JsonProperty("numero")
    private String number;

    @JsonProperty("codigoCiudad")
    private String cityCode;

    @JsonProperty("codigoPais")
    private String countryCode;
}
