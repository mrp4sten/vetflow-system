package com.vetflow.api.infrastructure.persistence.mapper;

import java.util.Locale;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.vetflow.api.domain.model.Patient;
import com.vetflow.api.infrastructure.persistence.entity.PatientEntity;

@Mapper(componentModel = "spring", uses = { OwnerMapper.class })
public interface PatientMapper {

  // domain -> entity
  @Mapping(target = "owner", source = "owner")
  @Mapping(target = "species", source = "species", qualifiedByName = "toDbSpecies")
  @Mapping(target = "allergies", ignore = true)
  PatientEntity toEntity(Patient domain);

  // entity -> domain
  @InheritInverseConfiguration
  // ojo: aquí vamos a convertir al enum, no solo a string
  @Mapping(target = "species", source = "species", qualifiedByName = "toDomainSpeciesEnum")
  @Mapping(target = "isActive", source = "active")
  Patient toDomain(PatientEntity entity);

  // ===== helpers =====

  @Named("toDbSpecies")
  default String toDbSpecies(Object species) {
    if (species == null) return null;
    // si te llega el enum Patient.Species
    if (species instanceof Patient.Species s) {
      return s.name().toLowerCase(Locale.ROOT);
    }
    // fallback por si algún día es String
    return species.toString().toLowerCase(Locale.ROOT);
  }

  @Named("toDomainSpeciesEnum")
  default Patient.Species toDomainSpeciesEnum(String dbSpecies) {
    if (dbSpecies == null) return null;
    // lo subimos a MAYÚSCULAS para que calce con el enum
    return Patient.Species.valueOf(dbSpecies.toUpperCase(Locale.ROOT));
  }
}
