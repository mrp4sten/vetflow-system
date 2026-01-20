import type { SystemUser } from '@domain/models/SystemUser'

export interface LoginCredentials {
  username: string
  password: string
  rememberMe?: boolean
}

export interface AuthToken {
  accessToken: string
  refreshToken?: string
  expiresIn: number
  tokenType: string
}

export interface LoginResult {
  user: SystemUser
  token: AuthToken
}

export interface LoginUseCase {
  execute(credentials: LoginCredentials): Promise<LoginResult>
}