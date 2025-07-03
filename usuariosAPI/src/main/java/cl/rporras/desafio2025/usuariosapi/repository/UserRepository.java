package cl.rporras.desafio2025.usuariosapi.repository;

import cl.rporras.desafio2025.usuariosapi.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    List<UserEntity> findAllByActiveIsTrue();
    Optional<UserEntity> findByIdAndActiveIsTrue(UUID id);
    Optional<UserEntity> findByEmailAndActiveIsTrue (String email);
    boolean existsByEmailAndActiveIsTrue(String email);
}