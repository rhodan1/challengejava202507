package cl.rporras.desafio2025.usuariosapi.exceptions;

public class InvalidPasswordFormatException extends RuntimeException {

    public InvalidPasswordFormatException(String password) {
        super("La contraseña tiene formato inválido: " + password);
    }
}
