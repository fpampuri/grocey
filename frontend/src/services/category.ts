import Api from './api';

// Types based on Swagger documentation
export interface CategoryMetadata {
  color?: string;
  [key: string]: any;
}

export interface Category {
  id?: number;
  name: string;
  metadata?: CategoryMetadata;
  createdAt?: string;
  updatedAt?: string;
}

export interface CategoryRegistrationData {
  name: string;
  metadata?: CategoryMetadata;
}

export interface UpdateCategoryProfile {
  name?: string;
  metadata?: CategoryMetadata;
}

export class CategoryApi {
  private static getUrl(slug?: string | number, query?: string): string {
    return `/categories${slug ? `/${slug}` : ""}${query ? `?${query}` : ""}`;
  }

  static async add(
    category: CategoryRegistrationData, 
    controller?: AbortController
  ): Promise<Category> {
    return await Api.post<Category>(
      CategoryApi.getUrl(), 
      true, 
      category, 
      controller
    );
  }

  static async modify(
    categoryId: number,
    category: UpdateCategoryProfile, 
    controller?: AbortController
  ): Promise<Category> {
    return await Api.put<Category>(
      CategoryApi.getUrl(categoryId), 
      true, 
      category, 
      controller
    );
  }

  static async remove(
    id: number, 
    controller?: AbortController
  ): Promise<void> {
    return await Api.delete<void>(
      CategoryApi.getUrl(id), 
      true, 
      controller
    );
  }

  static async get(
    id: number, 
    controller?: AbortController
  ): Promise<Category> {
    return await Api.get<Category>(
      CategoryApi.getUrl(id), 
      true, 
      controller
    );
  }

  static async getAll(
    controller?: AbortController
  ): Promise<Category[]> {
    return await Api.get<Category[]>(
      CategoryApi.getUrl(), 
      true, 
      controller
    );
  }

  static async getByName(
    name: string, 
    controller?: AbortController
  ): Promise<Category[]> {
    return await Api.get<Category[]>(
      CategoryApi.getUrl(null, `name=${encodeURIComponent(name)}`), 
      true, 
      controller
    );
  }
}

// Category class for creating category instances
export class CategoryModel {
  id?: number;
  name: string;
  metadata: CategoryMetadata;
  createdAt?: string;
  updatedAt?: string;

  constructor(
    name: string,
    metadata?: CategoryMetadata,
    id?: number,
    createdAt?: string,
    updatedAt?: string
  ) {
    if (id) {
      this.id = id;
    }
    this.name = name;
    this.metadata = metadata || {};
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  toString(): string {
    return JSON.stringify(this, null, 2);
  }
}

// Metadata class for category metadata
export class CategoryMetadataModel {
  color?: string;
  [key: string]: any;

  constructor(color?: string) {
    if (color) {
      this.color = color;
    }
  }
}

export default CategoryApi;