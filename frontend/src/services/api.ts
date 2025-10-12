class Api {
  static token: string | null = null

  // Use /api with proxy instead of http://localhost:8080/api
  // (Se puede modificar en el archivo vite.config.mts)
  static get baseUrl (): string {
    return '/api'
  }

  static get timeout (): number {
    return 60 * 1000
  }

  static async fetch<T = any>(
    url: string,
    secure: boolean,
    init: RequestInit = {},
    controller?: AbortController,
  ): Promise<T> {
    if (secure && Api.token) {
      if (!init.headers) {
        init.headers = {}
      }
      (init.headers as Record<string, string>)[
        'Authorization'
      ] = `Bearer ${Api.token}`
    }

    controller = controller || new AbortController()
    init.signal = controller.signal
    const timer = setTimeout(() => controller.abort(), Api.timeout)

    try {
      const response = await fetch(url, init)
      const result = await response.json()
      if (result.message && !response.ok) {
        throw result
      }
      return result
    } catch (error: any) {
      const error_ = error.name === 'AbortError' || error.name === 'TypeError' ? { message: error.message } : error
      throw error_
    } finally {
      clearTimeout(timer)
    }
  }

  static async get<T = any>(
    endpoint: string,
    secure = true,
    controller?: AbortController,
  ): Promise<T> {
    const url = `${Api.baseUrl}${endpoint}`
    return await Api.fetch<T>(url, secure, {}, controller)
  }

  static async post<T = any>(
    endpoint: string,
    secure = true,
    data?: any,
    controller?: AbortController,
  ): Promise<T> {
    const url = `${Api.baseUrl}${endpoint}`
    return await Api.fetch<T>(
      url,
      secure,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json; charset=utf-8',
        },
        body: JSON.stringify(data),
      },
      controller,
    )
  }

  static async put<T = any>(
    endpoint: string,
    secure = true,
    data?: any,
    controller?: AbortController,
  ): Promise<T> {
    const url = `${Api.baseUrl}${endpoint}`
    return await Api.fetch<T>(
      url,
      secure,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json; charset=utf-8',
        },
        body: JSON.stringify(data),
      },
      controller,
    )
  }

  static async patch<T = any>(
    endpoint: string,
    secure = true,
    data?: any,
    controller?: AbortController,
  ): Promise<T> {
    const url = `${Api.baseUrl}${endpoint}`
    return await Api.fetch<T>(
      url,
      secure,
      {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json; charset=utf-8',
        },
        body: JSON.stringify(data),
      },
      controller,
    )
  }

  static async delete<T = any>(
    endpoint: string,
    secure = true,
    controller?: AbortController,
  ): Promise<T> {
    const url = `${Api.baseUrl}${endpoint}`
    return await Api.fetch<T>(
      url,
      secure,
      {
        method: 'DELETE',
      },
      controller,
    )
  }

  static setAuthToken (token: string | null): void {
    Api.token = token
  }
}

// Named export for convenience
export const setAuthToken = Api.setAuthToken

export default Api
