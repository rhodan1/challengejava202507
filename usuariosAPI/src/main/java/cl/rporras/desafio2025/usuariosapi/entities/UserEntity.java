package cl.rporras.desafio2025.usuariosapi.entities;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")  // UUID binario, mas eficiente
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<PhoneEntity> phones;

    @Column(nullable = false)
    private LocalDateTime created;

    private LocalDateTime modified;

    private LocalDateTime lastLogin;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String token;
}