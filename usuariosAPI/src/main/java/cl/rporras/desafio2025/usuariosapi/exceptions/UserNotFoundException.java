package cl.rporras.desafio2025.usuariosapi.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super(String.format("Usuario con id: %s no encontrado", id));
    }
}
