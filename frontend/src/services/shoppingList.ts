import type { User } from './user'
import Api from './api'

// Types based on Swagger documentation
export interface ShoppingListMetadata {
  icon: string
  isFavorite: boolean
  itemsCount: number
  [key: string]: any
}

export interface ShoppingListCreate {
  name: string
  description?: string
  recurring?: boolean
  metadata?: ShoppingListMetadata
}

export interface ShoppingListUpdate {
  name?: string
  description?: string
  recurring?: boolean
  metadata?: ShoppingListMetadata
}

export interface ShoppingList {
  id?: number
  name: string
  description?: string
  recurring?: boolean
  metadata?: ShoppingListMetadata
  owner?: User
  sharedWith?: User[]
  lastPurchasedAt?: string | null
  createdAt?: string
  updatedAt?: string
}

export class ShoppingListApi {
  private static getUrl (slug?: string | number): string {
    return `/shopping-lists${slug ? `/${slug}` : ''}`
  }

  static async add (
    shoppingList: ShoppingListCreate,
    controller?: AbortController,
  ): Promise<ShoppingList> {
    return await Api.post<ShoppingList>(
      ShoppingListApi.getUrl(),
      true,
      shoppingList,
      controller,
    )
  }

  static async modify (
    listId: number,
    shoppingList: ShoppingListUpdate,
    controller?: AbortController,
  ): Promise<ShoppingList> {
    return await Api.put<ShoppingList>(
      ShoppingListApi.getUrl(listId),
      true,
      shoppingList,
      controller,
    )
  }

  static async remove (
    id: number,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.delete<void>(
      ShoppingListApi.getUrl(id),
      true,
      controller,
    )
  }

  static async get (
    id: number,
    controller?: AbortController,
  ): Promise<ShoppingList> {
    return await Api.get<ShoppingList>(
      ShoppingListApi.getUrl(id),
      true,
      controller,
    )
  }

  static async getAll (
    controller?: AbortController,
  ): Promise<ShoppingList[]> {
    return await Api.get<ShoppingList[]>(
      ShoppingListApi.getUrl(),
      true,
      controller,
    )
  }

  // Additional methods for sharing (if supported by your backend)
  static async share (
    listId: number,
    userEmail: string,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.post<void>(
      ShoppingListApi.getUrl(`${listId}/share`),
      true,
      { email: userEmail },
      controller,
    )
  }

  static async unshare (
    listId: number,
    userId: number,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.delete<void>(
      ShoppingListApi.getUrl(`${listId}/share/${userId}`),
      true,
      controller,
    )
  }

  // Purchase method (if supported)
  static async purchase (
    listId: number,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.post<void>(
      ShoppingListApi.getUrl(`${listId}/purchase`),
      true,
      {},
      controller,
    )
  }
}

// Shopping List model class
export class ShoppingListModel {
  id?: number
  name: string
  description: string
  recurring: boolean
  metadata: ShoppingListMetadata
  owner?: User
  sharedWith?: User[]
  lastPurchasedAt?: string | null
  createdAt?: string
  updatedAt?: string

  constructor (
    name: string,
    description?: string,
    recurring?: boolean,
    metadata?: ShoppingListMetadata,
    id?: number,
    createdAt?: string,
    updatedAt?: string,
  ) {
    if (id) {
      this.id = id
    }
    this.name = name
    this.description = description || ''
    this.recurring = recurring || false
    this.metadata = metadata || { icon: 'mdi-apple', isFavorite: false, itemsCount: 0 }
    this.createdAt = createdAt
    this.updatedAt = updatedAt
  }

  toString (): string {
    return JSON.stringify(this, null, 2)
  }
}

export default ShoppingListApi
