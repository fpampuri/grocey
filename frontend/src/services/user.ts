import Api from './api'

// Types based on Swagger documentation
export interface UserMetadata {
  [key: string]: any
}

export interface User {
  id?: number
  name: string
  surname: string
  email: string
  password?: string
  metadata?: UserMetadata
  createdAt?: string
  updatedAt?: string
}

export interface NewUser {
  id?: number
  email: string
  name: string
  surname: string
  metadata?: UserMetadata
  updatedAt?: string
  createdAt?: string
}

export interface GetUser {
  id?: number
  name: string
  surname: string
  email: string
  metadata?: UserMetadata
  createdAt?: string
  updatedAt?: string
}

export interface Credentials {
  email: string
  password: string
}

export interface RegistrationData {
  email: string
  name: string
  surname: string
  password: string
  metadata?: UserMetadata
}

export interface RegisteredUser {
  user: NewUser
  verificationToken: string
}

export interface AuthenticationToken {
  token: string
}

export interface VerificationCode {
  code: string
}

export interface PasswordRecovery {
  email: string
}

export interface PasswordReset {
  code: string
  password: string
}

export interface UpdateUserProfile {
  name: string
  surname: string
  metadata?: UserMetadata
}

export interface PasswordChange {
  currentPassword: string
  newPassword: string
}

export class UserApi {
  private static getUrl (slug?: string): string {
    return `/users${slug ? `/${slug}` : ''}`
  }

  // Authentication endpoints
  static async login (
    credentials: Credentials,
    controller?: AbortController,
  ): Promise<AuthenticationToken> {
    return await Api.post<AuthenticationToken>(
      UserApi.getUrl('login'),
      false, // Login doesn't require auth token
      credentials,
      controller,
    )
  }

  static async register (
    registrationData: RegistrationData,
    controller?: AbortController,
  ): Promise<RegisteredUser> {
    return await Api.post<RegisteredUser>(
      UserApi.getUrl('register'),
      false, // Registration doesn't require auth token
      registrationData,
      controller,
    )
  }

  static async verify (
    verificationCode: VerificationCode,
    controller?: AbortController,
  ): Promise<AuthenticationToken> {
    return await Api.post<AuthenticationToken>(
      UserApi.getUrl('verify-account'),
      false, // Verification doesn't require auth token
      verificationCode,
      controller,
    )
  }

  static async logout (
    controller?: AbortController,
  ): Promise<void> {
    return await Api.post<void>(
      UserApi.getUrl('logout'),
      true,
      undefined,
      controller,
    )
  }

  // Password recovery
  static async requestPasswordRecovery (
    passwordRecovery: PasswordRecovery,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.post<void>(
      `${UserApi.getUrl('forgot-password')}?email=${encodeURIComponent(passwordRecovery.email)}`,
      false, // Password recovery doesn't require auth token
      undefined,
      controller,
    )
  }

  static async resetPassword (
    passwordReset: PasswordReset,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.post<void>(
      UserApi.getUrl('reset-password'),
      false, // Password reset doesn't require auth token
      passwordReset,
      controller,
    )
  }

  // Profile management
  static async getProfile (
    controller?: AbortController,
  ): Promise<GetUser> {
    return await Api.get<GetUser>(
      UserApi.getUrl('profile'),
      true,
      controller,
    )
  }

  static async updateProfile (
    profileData: UpdateUserProfile,
    controller?: AbortController,
  ): Promise<GetUser> {
    return await Api.put<GetUser>(
      UserApi.getUrl('profile'),
      true,
      profileData,
      controller,
    )
  }

  static async changePassword (
    passwordChange: PasswordChange,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.post<void>(
      UserApi.getUrl('change-password'),
      true,
      passwordChange,
      controller,
    )
  }

  static async resendVerification (
    email: string,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.post<void>(
      UserApi.getUrl('send-verification'),
      false, // Resend verification doesn't require auth token
      { email },
      controller,
    )
  }

  static async deleteAccount (
    controller?: AbortController,
  ): Promise<void> {
    return await Api.delete<void>(
      UserApi.getUrl('profile'),
      true,
      controller,
    )
  }
}

// User model classes
export class CredentialsModel {
  email: string
  password: string

  constructor (email: string, password: string) {
    this.email = email
    this.password = password
  }
}

export class UserModel {
  id?: number
  name: string
  surname: string
  email: string
  metadata: UserMetadata
  createdAt?: string
  updatedAt?: string

  constructor (
    name: string,
    surname: string,
    email: string,
    metadata?: UserMetadata,
    id?: number,
    createdAt?: string,
    updatedAt?: string,
  ) {
    if (id) {
      this.id = id
    }
    this.name = name
    this.surname = surname
    this.email = email
    this.metadata = metadata || {}
    this.createdAt = createdAt
    this.updatedAt = updatedAt
  }

  toString (): string {
    return JSON.stringify(this, null, 2)
  }
}

export default UserApi
