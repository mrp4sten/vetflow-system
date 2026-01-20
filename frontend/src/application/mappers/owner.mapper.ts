import type { Owner } from '@domain/models/Owner'

export class OwnerMapper {
  static toDomain(dto: any): Owner {
    return {
      ...dto,
      get fullName() {
        return `${this.firstName} ${this.lastName}`
      }
    }
  }

  static toDomainList(dtos: any[]): Owner[] {
    return dtos.map(dto => OwnerMapper.toDomain(dto))
  }
}