package cl.rporras.desafio2025.usuariosapi.service;

import cl.rporras.desafio2025.usuariosapi.api.dto.*;
import cl.rporras.desafio2025.usuariosapi.domain.*;
import cl.rporras.desafio2025.usuariosapi.entities.*;
import cl.rporras.desafio2025.usuariosapi.exceptions.*;
import cl.rporras.desafio2025.usuariosapi.repository.UserRepository;
import cl.rporras.desafio2025.usuariosapi.security.JwtProvider;
import cl.rporras.desafio2025.usuariosapi.security.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordValidator passwordValidator;
    private final EmailValidator emailValidator;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtProvider jwtProvider, PasswordValidator passwordValidator, EmailValidator emailValidator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.passwordValidator = passwordValidator;
        this.emailValidator = emailValidator;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Busca todos los usuarios
     * @return List<UserResponse>
     */
    public List<UserResponse> findAllUsers() {
        var users = userRepository.findAllByActiveIsTrue();
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca usuario por id unico
     * @param id
     * @return UserResponse
     */
    public UserResponse findUserById(UUID id) {
        var user = userRepository.findByIdAndActiveIsTrue(id);
        if (user.isPresent()) {
            return toResponse(user.get());
        }
        else {
            throw new UserNotFoundException(id.toString());
        }
    }

    /**
     * Eliminacion Logica del usuario (Active = False)
     * @param id
     */
    public void deleteUserById(UUID id) {
        log.debug("Eliminando Usuario ID : {}", id);

        var user = userRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        user.setActive(false);
        user.setModified(LocalDateTime.now());

        userRepository.save(user);

        log.debug("Usuario Eliminado ID : {}", user.getId());
    }

    /**
     * Crea un nuevo usuario en el sistema, con email unico
     * @param request
     * @return UserResponse (usuario creado)
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("Creando nuevo usuario con correo {}", request.getEmail());

        validatePasswordFormat(request.getPassword());
        validateEmailFormat(request.getEmail());
        validateEmailUniqueness(request.getEmail());

        var userId = UUID.randomUUID();
        var now = LocalDateTime.now();
        String token = jwtProvider.generateToken(request.getEmail());

        var user = new UserEntity();
        user.setId(userId);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Usa hash+salt para el password
        user.setActive(true);
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);
        user.setToken(token);

        var phones = request.getPhones()
                .stream()
                .map(dto -> {
                    var p = new PhoneEntity();
                    p.setNumber(dto.getNumber());
                    p.setCityCode(dto.getCityCode());
                    p.setCountryCode(dto.getCountryCode());
                    return p;
                })
                .collect(Collectors.toList());

        user.setPhones(phones);
        userRepository.save(user);

        log.debug("Usuario creado ID: {}", user.getId());

        return toResponse(user);
    }

    /**
     * Actualiza un usuario por id. Reemplaza toda la informacion del usuario
     * Aplica las mismas reglas de creacion
     * @param id
     * @param request
     * @return User actualizado
     */
    @Transactional
    public UserResponse updateUser(UUID id, CreateUserRequest request) {
        log.debug("Actualizando usuario ID: {}", id);

        var user = userRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        validatePasswordFormat(request.getPassword());
        validateEmailFormat(request.getEmail());

        // Validar correo si cambió y actualizar la entidad si no está registrado
        if (!user.getEmail().equals(request.getEmail())) {
            validateEmailUniqueness(request.getEmail());
            user.setEmail(request.getEmail());
        }

        var now = LocalDateTime.now();

        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Usa hash+salt para el password
        user.setModified(now);

        var newPhones = request.getPhones()
                .stream()
                .map(dto -> {
                    var p = new PhoneEntity();
                    p.setNumber(dto.getNumber());
                    p.setCityCode(dto.getCityCode());
                    p.setCountryCode(dto.getCountryCode());
                    return p;
                })
                .collect(Collectors.toList());

        user.getPhones().clear();
        user.getPhones().addAll(newPhones);

        userRepository.save(user);

        log.debug("Usuario Actualizado ID : {}", user.getId());
        return toResponse(user);
    }

    /**
     * Modifica campos especificos de un usuario si es que se envian en request
     * @param id
     * @param request
     * @return User modificado
     */
    public UserResponse patchUser(UUID id, PatchUserRequest request) {
        log.debug("Patching usuario ID: {}", id);

        var user = userRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        if (request.getName() != null)
            user.setName(request.getName());

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            validateEmailFormat(request.getEmail());
            validateEmailUniqueness(request.getEmail());

            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            validatePasswordFormat(request.getPassword());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getPhones() != null) {
            var newPhones = request.getPhones().stream()
                    .map(dto -> {
                        var phone = new PhoneEntity();
                        phone.setNumber(dto.getNumber());
                        phone.setCityCode(dto.getCityCode());
                        phone.setCountryCode(dto.getCountryCode());
                        return phone;
                    }).collect(Collectors.toList());

            user.getPhones().clear();
            user.getPhones().addAll(newPhones);
        }

        user.setModified(LocalDateTime.now());

        userRepository.save(user);

        log.debug("Usuario Modificado ID : {}", user.getId());

        return toResponse(user);
    }


    private void validateEmailFormat(String email) {
        if (!emailValidator.isValid(email)) {
            throw new IllegalArgumentException("El correo no cumple con el formato requerido.");
        }
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmailAndActiveIsTrue(email)) {
            throw new EmailAlreadyExistException(email);
        }
    }

    private void validatePasswordFormat(String password) {
        if (!passwordValidator.isValid(password)) {
            throw new InvalidPasswordFormatException(password);
        }
    }

    private UserResponse toResponse(UserEntity entity) {
        var resp = new UserResponse();
        resp.setId(entity.getId());
        resp.setName(entity.getName());
        resp.setEmail(entity.getEmail());
        resp.setCreated(entity.getCreated());
        resp.setModified(entity.getModified());
        resp.setLastLogin(entity.getLastLogin());
        resp.setToken(entity.getToken());
        resp.setActive(entity.isActive());

        var phones = entity.getPhones().stream().map(p -> {
            var phoneResponse = new PhoneResponse();
            phoneResponse.setNumber(p.getNumber());
            phoneResponse.setCityCode(p.getCityCode());
            phoneResponse.setCountryCode(p.getCountryCode());
            return phoneResponse;
        }).collect(Collectors.toList());

        resp.setPhones(phones);
        return resp;
    }
}