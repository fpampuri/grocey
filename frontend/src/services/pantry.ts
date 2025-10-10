import Api from './api';
import { User } from './user';

// Types based on Swagger documentation
export interface PantryMetadata {
  color?: string;
  [key: string]: any;
}

export interface Pantry {
  id?: number;
  name: string;
  metadata?: PantryMetadata;
  createdAt?: string;
  updatedAt?: string;
  owner?: User;
  sharedWith?: User[];
}

export interface PantryCreate {
  name: string;
  metadata?: PantryMetadata;
}

export interface PantryUpdate {
  name?: string;
  metadata?: PantryMetadata;
}

export class PantryApi {
  private static getUrl(slug?: string | number): string {
    return `/pantries${slug ? `/${slug}` : ""}`;
  }

  static async add(
    pantry: PantryCreate, 
    controller?: AbortController
  ): Promise<Pantry> {
    return await Api.post<Pantry>(
      PantryApi.getUrl(), 
      true, 
      pantry, 
      controller
    );
  }

  static async modify(
    pantryId: number,
    pantry: PantryUpdate, 
    controller?: AbortController
  ): Promise<Pantry> {
    return await Api.put<Pantry>(
      PantryApi.getUrl(pantryId), 
      true, 
      pantry, 
      controller
    );
  }

  static async remove(
    id: number, 
    controller?: AbortController
  ): Promise<void> {
    return await Api.delete<void>(
      PantryApi.getUrl(id), 
      true, 
      controller
    );
  }

  static async get(
    id: number, 
    controller?: AbortController
  ): Promise<Pantry> {
    return await Api.get<Pantry>(
      PantryApi.getUrl(id), 
      true, 
      controller
    );
  }

  static async getAll(
    controller?: AbortController
  ): Promise<Pantry[]> {
    return await Api.get<Pantry[]>(
      PantryApi.getUrl(), 
      true, 
      controller
    );
  }

  // Sharing methods
  static async share(
    pantryId: number,
    userEmail: string,
    controller?: AbortController
  ): Promise<void> {
    return await Api.post<void>(
      PantryApi.getUrl(`${pantryId}/share`),
      true,
      { email: userEmail },
      controller
    );
  }

  static async unshare(
    pantryId: number,
    userId: number,
    controller?: AbortController
  ): Promise<void> {
    return await Api.delete<void>(
      PantryApi.getUrl(`${pantryId}/share/${userId}`),
      true,
      controller
    );
  }
}

// Pantry model class
export class PantryModel {
  id?: number;
  name: string;
  metadata: PantryMetadata;
  createdAt?: string;
  updatedAt?: string;
  owner?: User;
  sharedWith?: User[];

  constructor(
    name: string,
    metadata?: PantryMetadata,
    id?: number,
    createdAt?: string,
    updatedAt?: string,
    owner?: User,
    sharedWith?: User[]
  ) {
    if (id) {
      this.id = id;
    }
    this.name = name;
    this.metadata = metadata || {};
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.owner = owner;
    this.sharedWith = sharedWith || [];
  }

  toString(): string {
    return JSON.stringify(this, null, 2);
  }
}

export default PantryApi;