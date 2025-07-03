package cl.rporras.desafio2025.usuariosapi.exceptions;

public class EmailAlreadyExistException extends RuntimeException {

    public EmailAlreadyExistException(String email) {
        super("El correo ya está registrado: " + email);
    }
}
