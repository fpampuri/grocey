import type { Category } from './category'
import Api from './api'

// Types based on Swagger documentation
export interface ProductMetadata {
  [key: string]: any
}

export interface Product {
  id?: number
  name: string
  metadata?: ProductMetadata
  createdAt?: string
  updatedAt?: string
  category?: Category
}

export interface ProductCreationData {
  name: string
  category?: { id: number }
  metadata?: ProductMetadata
}

export interface ProductRegistrationData {
  name: string
  category?: { id: number }
  metadata?: ProductMetadata
}

export class ProductApi {
  private static getUrl (slug?: string | number, query?: string): string {
    return `/products${slug ? `/${slug}` : ''}${query ? `?${query}` : ''}`
  }

  static async add (
    product: ProductCreationData,
    controller?: AbortController,
  ): Promise<Product> {
    return await Api.post<Product>(
      ProductApi.getUrl(),
      true,
      product,
      controller,
    )
  }

  static async modify (
    productId: number,
    product: Partial<ProductCreationData>,
    controller?: AbortController,
  ): Promise<Product> {
    return await Api.put<Product>(
      ProductApi.getUrl(productId),
      true,
      product,
      controller,
    )
  }

  static async remove (
    id: number,
    controller?: AbortController,
  ): Promise<void> {
    return await Api.delete<void>(
      ProductApi.getUrl(id),
      true,
      controller,
    )
  }

  static async get (
    id: number,
    controller?: AbortController,
  ): Promise<Product> {
    return await Api.get<Product>(
      ProductApi.getUrl(id),
      true,
      controller,
    )
  }

  static async getAll (
    controller?: AbortController,
  ): Promise<Product[]> {
    return await Api.get<Product[]>(
      ProductApi.getUrl(),
      true,
      controller,
    )
  }

  static async getByName (
    name: string,
    controller?: AbortController,
  ): Promise<Product[]> {
    return await Api.get<Product[]>(
      ProductApi.getUrl(undefined, `name=${encodeURIComponent(name)}`),
      true,
      controller,
    )
  }

  static async getByCategoryId (
    categoryId: number,
    controller?: AbortController,
  ): Promise<Product[]> {
    return await Api.get<Product[]>(
      ProductApi.getUrl(undefined, `categoryId=${categoryId}`),
      true,
      controller,
    )
  }
}

// Product model class
export class ProductModel {
  id?: number
  name: string
  metadata: ProductMetadata
  createdAt?: string
  updatedAt?: string
  category?: Category

  constructor (
    name: string,
    metadata?: ProductMetadata,
    id?: number,
    createdAt?: string,
    updatedAt?: string,
    category?: Category,
  ) {
    if (id) {
      this.id = id
    }
    this.name = name
    this.metadata = metadata || {}
    this.createdAt = createdAt
    this.updatedAt = updatedAt
    this.category = category
  }

  toString (): string {
    return JSON.stringify(this, null, 2)
  }
}

export default ProductApi
