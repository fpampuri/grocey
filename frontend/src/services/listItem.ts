import Api from './api';
import { Product } from './product';

// Types based on Swagger documentation
export interface ListItemMetadata {
  [key: string]: any;
}

export interface ListItem {
  id?: number;
  quantity: number;
  unit: string;
  metadata?: ListItemMetadata;
  purchased?: boolean;
  lastPurchasedAt?: string | null;
  createdAt?: string;
  updatedAt?: string;
  product?: Product;
}

export interface ListItemCreate {
  product: { id: number };
  quantity: number;
  unit: string;
  metadata?: ListItemMetadata;
}

export interface ListItemUpdate {
  quantity?: number;
  unit?: string;
  metadata?: ListItemMetadata;
}

export class ListItemApi {
  private static getUrl(listId: number, itemId?: number): string {
    return `/lists/${listId}/items${itemId ? `/${itemId}` : ""}`;
  }

  static async add(
    listId: number,
    listItem: ListItemCreate, 
    controller?: AbortController
  ): Promise<ListItem> {
    return await Api.post<ListItem>(
      ListItemApi.getUrl(listId), 
      true, 
      listItem, 
      controller
    );
  }

  static async modify(
    listId: number,
    itemId: number,
    listItem: ListItemUpdate, 
    controller?: AbortController
  ): Promise<ListItem> {
    return await Api.put<ListItem>(
      ListItemApi.getUrl(listId, itemId), 
      true, 
      listItem, 
      controller
    );
  }

  static async remove(
    listId: number,
    itemId: number, 
    controller?: AbortController
  ): Promise<void> {
    return await Api.delete<void>(
      ListItemApi.getUrl(listId, itemId), 
      true, 
      controller
    );
  }

  static async get(
    listId: number,
    itemId: number, 
    controller?: AbortController
  ): Promise<ListItem> {
    return await Api.get<ListItem>(
      ListItemApi.getUrl(listId, itemId), 
      true, 
      controller
    );
  }

  static async getAll(
    listId: number,
    controller?: AbortController
  ): Promise<ListItem[]> {
    return await Api.get<ListItem[]>(
      ListItemApi.getUrl(listId), 
      true, 
      controller
    );
  }

  // Mark item as purchased
  static async markAsPurchased(
    listId: number,
    itemId: number,
    controller?: AbortController
  ): Promise<ListItem> {
    return await Api.patch<ListItem>(
      ListItemApi.getUrl(listId, itemId),
      true,
      { purchased: true },
      controller
    );
  }

  // Mark item as not purchased
  static async markAsNotPurchased(
    listId: number,
    itemId: number,
    controller?: AbortController
  ): Promise<ListItem> {
    return await Api.patch<ListItem>(
      ListItemApi.getUrl(listId, itemId),
      true,
      { purchased: false },
      controller
    );
  }
}

// List Item model class
export class ListItemModel {
  id?: number;
  quantity: number;
  unit: string;
  metadata: ListItemMetadata;
  purchased: boolean;
  lastPurchasedAt?: string | null;
  createdAt?: string;
  updatedAt?: string;
  product?: Product;

  constructor(
    quantity: number,
    unit: string,
    product?: Product,
    metadata?: ListItemMetadata,
    purchased?: boolean,
    id?: number,
    lastPurchasedAt?: string | null,
    createdAt?: string,
    updatedAt?: string
  ) {
    if (id) {
      this.id = id;
    }
    this.quantity = quantity;
    this.unit = unit;
    this.metadata = metadata || {};
    this.purchased = purchased || false;
    this.lastPurchasedAt = lastPurchasedAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.product = product;
  }

  toString(): string {
    return JSON.stringify(this, null, 2);
  }
}

export default ListItemApi;