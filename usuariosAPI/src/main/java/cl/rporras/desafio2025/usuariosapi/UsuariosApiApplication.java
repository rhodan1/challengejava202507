package cl.rporras.desafio2025.usuariosapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class UsuariosApiApplication {

    public static void main(String[] args) {
        log.info("Aplicación iniciada");
        SpringApplication.run(UsuariosApiApplication.class, args);
    }

}
