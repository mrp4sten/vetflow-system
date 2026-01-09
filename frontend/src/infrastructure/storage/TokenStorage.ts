const TOKEN_KEY = 'vetflow_access_token'
const REFRESH_TOKEN_KEY = 'vetflow_refresh_token'
const TOKEN_EXPIRY_KEY = 'vetflow_token_expiry'

export class TokenStorage {
  static getAccessToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  }

  static getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY)
  }

  static setTokens(accessToken: string, refreshToken?: string, expiresIn?: number): void {
    localStorage.setItem(TOKEN_KEY, accessToken)
    
    if (refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
    }
    
    if (expiresIn) {
      const expiryTime = Date.now() + expiresIn * 1000
      localStorage.setItem(TOKEN_EXPIRY_KEY, expiryTime.toString())
    }
  }

  static clearTokens(): void {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(TOKEN_EXPIRY_KEY)
  }

  static isTokenExpired(): boolean {
    const expiryTime = localStorage.getItem(TOKEN_EXPIRY_KEY)
    if (!expiryTime) return true
    
    return Date.now() > parseInt(expiryTime)
  }

  static getTokenExpiry(): Date | null {
    const expiryTime = localStorage.getItem(TOKEN_EXPIRY_KEY)
    if (!expiryTime) return null
    
    return new Date(parseInt(expiryTime))
  }
}