// Main API client
export { default as Api } from './api';
export { setAuthToken } from './api';

// User service
export { default as UserApi } from './user';
export type {
  User,
  NewUser,
  GetUser,
  Credentials,
  RegistrationData,
  RegisteredUser,
  AuthenticationToken,
  VerificationCode,
  PasswordRecovery,
  PasswordReset,
  UpdateUserProfile,
  PasswordChange,
  UserMetadata
} from './user';
export { CredentialsModel, UserModel } from './user';

// Category service
export { default as CategoryApi } from './category';
export type {
  Category,
  CategoryRegistrationData,
  UpdateCategoryProfile,
  CategoryMetadata
} from './category';
export { CategoryModel, CategoryMetadataModel } from './category';

// Product service
export { default as ProductApi } from './product';
export type {
  Product,
  ProductCreationData,
  ProductRegistrationData,
  ProductMetadata
} from './product';
export { ProductModel } from './product';

// Shopping List service
export { default as ShoppingListApi } from './shoppingList';
export type {
  ShoppingList,
  ShoppingListCreate,
  ShoppingListUpdate,
  ShoppingListMetadata
} from './shoppingList';
export { ShoppingListModel } from './shoppingList';

// List Item service
export { default as ListItemApi } from './listItem';
export type {
  ListItem,
  ListItemCreate,
  ListItemUpdate,
  ListItemMetadata
} from './listItem';
export { ListItemModel } from './listItem';

// Pantry service
export { default as PantryApi } from './pantry';
export type {
  Pantry,
  PantryCreate,
  PantryUpdate,
  PantryMetadata
} from './pantry';
export { PantryModel } from './pantry';

// Pantry Item service
export { default as PantryItemApi } from './pantryItem';
export type {
  PantryItem,
  PantryItemCreate,
  PantryItemUpdate,
  PantryItemMetadata
} from './pantryItem';
export { PantryItemModel } from './pantryItem';



// Re-export the main API client as default
export { default } from './api';