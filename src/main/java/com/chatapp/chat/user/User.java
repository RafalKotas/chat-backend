package com.chatapp.chat.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uc_user_username", columnNames = "username")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 30)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(unique = true, length = 30)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public String getDisplayName() {
        if (username != null && !username.isBlank()) return username;
        if (firstName != null && !firstName.isBlank()) {
            return lastName == null || lastName.isBlank()
                    ? firstName
                    : firstName + " " + lastName;
        }
        return "User-" + id.toString().substring(0, 8);
    }
}
