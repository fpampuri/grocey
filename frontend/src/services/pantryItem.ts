import Api from './api';
import type { Product } from './product';

// Types based on Swagger documentation
export interface PantryItemMetadata {
  expirationDate?: string;
  [key: string]: any;
}

export interface PantryItem {
  id?: number;
  quantity: number;
  unit: string;
  metadata?: PantryItemMetadata;
  product?: Product;
  createdAt?: string;
  updatedAt?: string;
}

export interface PantryItemCreate {
  product: { id: number };
  quantity: number;
  unit: string;
  metadata?: PantryItemMetadata;
}

export interface PantryItemUpdate {
  quantity?: number;
  unit?: string;
  metadata?: PantryItemMetadata;
}

export class PantryItemApi {
  private static getUrl(pantryId: number, itemId?: number): string {
    return `/pantries/${pantryId}/items${itemId ? `/${itemId}` : ""}`;
  }

  static async add(
    pantryId: number,
    pantryItem: PantryItemCreate, 
    controller?: AbortController
  ): Promise<PantryItem> {
    return await Api.post<PantryItem>(
      PantryItemApi.getUrl(pantryId), 
      true, 
      pantryItem, 
      controller
    );
  }

  static async modify(
    pantryId: number,
    itemId: number,
    pantryItem: PantryItemUpdate, 
    controller?: AbortController
  ): Promise<PantryItem> {
    return await Api.put<PantryItem>(
      PantryItemApi.getUrl(pantryId, itemId), 
      true, 
      pantryItem, 
      controller
    );
  }

  static async remove(
    pantryId: number,
    itemId: number, 
    controller?: AbortController
  ): Promise<void> {
    return await Api.delete<void>(
      PantryItemApi.getUrl(pantryId, itemId), 
      true, 
      controller
    );
  }

  static async get(
    pantryId: number,
    itemId: number, 
    controller?: AbortController
  ): Promise<PantryItem> {
    return await Api.get<PantryItem>(
      PantryItemApi.getUrl(pantryId, itemId), 
      true, 
      controller
    );
  }

  static async getAll(
    pantryId: number,
    controller?: AbortController
  ): Promise<PantryItem[]> {
    return await Api.get<PantryItem[]>(
      PantryItemApi.getUrl(pantryId), 
      true, 
      controller
    );
  }

  // Update quantity (useful for consuming items)
  static async updateQuantity(
    pantryId: number,
    itemId: number,
    newQuantity: number,
    controller?: AbortController
  ): Promise<PantryItem> {
    return await Api.patch<PantryItem>(
      PantryItemApi.getUrl(pantryId, itemId),
      true,
      { quantity: newQuantity },
      controller
    );
  }
}

// Pantry Item model class
export class PantryItemModel {
  id?: number;
  quantity: number;
  unit: string;
  metadata: PantryItemMetadata;
  product?: Product;
  createdAt?: string;
  updatedAt?: string;

  constructor(
    quantity: number,
    unit: string,
    product?: Product,
    metadata?: PantryItemMetadata,
    id?: number,
    createdAt?: string,
    updatedAt?: string
  ) {
    if (id) {
      this.id = id;
    }
    this.quantity = quantity;
    this.unit = unit;
    this.metadata = metadata || {};
    this.product = product;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  toString(): string {
    return JSON.stringify(this, null, 2);
  }
}

export default PantryItemApi;