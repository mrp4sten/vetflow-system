import type { Patient } from '@domain/models/Patient'
import { calculateAge } from '@infrastructure/utils/date-utils'
import { convertKgToLbs } from '@infrastructure/utils/validation-utils'
import { OwnerMapper } from './owner.mapper'

export class PatientMapper {
  static toDomain(dto: any): Patient {
    return {
      ...dto,
      owner: dto.owner ? OwnerMapper.toDomain(dto.owner) : undefined,
      get age() {
        if (!this.birthDate) return undefined
        const ageInfo = calculateAge(this.birthDate)
        return ageInfo ? ageInfo.years : undefined
      },
      get weightInLbs() {
        return this.weight ? convertKgToLbs(this.weight) : undefined
      }
    }
  }

  static toDomainList(dtos: any[]): Patient[] {
    return dtos.map(dto => PatientMapper.toDomain(dto))
  }
}