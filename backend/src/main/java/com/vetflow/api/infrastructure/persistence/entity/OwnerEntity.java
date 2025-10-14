package com.vetflow.api.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * JPA entity mapped to table 'owners'.
 * Mirrors DB constraints: unique email, phone regex (enforced at DB), length limits.
 * No domain logic here—pure persistence model.
 */
@Entity
@Table(name = "owners",
       indexes = {
           @Index(name = "idx_owners_email", columnList = "email"),
           @Index(name = "idx_owners_phone", columnList = "phone"),
           @Index(name = "idx_owners_created_at", columnList = "created_at")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_owners_email", columnNames = "email")
       }
)
public class OwnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "phone", length = 15, nullable = false)
    private String phone;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        final LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters/setters (o usa Lombok si quieres en infra):
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = (name != null ? name.trim() : null); }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = (phone != null ? phone.trim() : null); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = (email != null ? email.trim().toLowerCase() : null); }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
