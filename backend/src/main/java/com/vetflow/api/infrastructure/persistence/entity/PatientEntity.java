package com.vetflow.api.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * JPA entity mapped to table 'patients'.
 * Enforces FK to owners and keeps species normalized in lowercase to match DB CHECK.
 */
@Entity
@Table(name = "patients",
       indexes = {
           @Index(name = "idx_patients_owner_id", columnList = "owner_id"),
           @Index(name = "idx_patients_species", columnList = "species"),
           @Index(name = "idx_patients_name", columnList = "name"),
           @Index(name = "idx_patients_created_at", columnList = "created_at")
       }
)
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    // Must be 'dog' or 'cat' (DB check constraint). Store lowercase.
    @Column(name = "species", length = 50, nullable = false)
    private String species;

    @Column(name = "breed", length = 100)
    private String breed;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "allergies")
    private String allergies;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_patient_owner"))
    private OwnerEntity owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        final LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        normalize();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        normalize();
    }

    private void normalize() {
        if (this.species != null) this.species = this.species.trim().toLowerCase();
        if (this.name != null) this.name = this.name.trim();
        if (this.breed != null) this.breed = this.breed.trim();
    }

    // Getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = (name != null ? name.trim() : null); }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = (species != null ? species.trim().toLowerCase() : null); }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = (breed != null ? breed.trim() : null); }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public OwnerEntity getOwner() { return owner; }
    public void setOwner(OwnerEntity owner) { this.owner = owner; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
