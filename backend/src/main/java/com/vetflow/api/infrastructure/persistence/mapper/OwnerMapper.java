package com.vetflow.api.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;

import com.vetflow.api.domain.model.Owner;
import com.vetflow.api.infrastructure.persistence.entity.OwnerEntity;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
  @org.mapstruct.Named("ownerToEntity")
  OwnerEntity toEntity(Owner domain);

  @org.mapstruct.Named("entityToOwner")
  Owner toDomain(OwnerEntity entity);
}
