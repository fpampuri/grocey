// Main API client
export { default as Api } from './api'
export { setAuthToken } from './api'

// Re-export the main API client as default
export { default } from './api'
// Category service
export { default as CategoryApi } from './category'
export type {
  Category,
  CategoryMetadata,
  CategoryRegistrationData,
  UpdateCategoryProfile,
} from './category'

export { CategoryMetadataModel, CategoryModel } from './category'
// List Item service
export { default as ListItemApi } from './listItem'
export type {
  ListItem,
  ListItemCreate,
  ListItemMetadata,
  ListItemUpdate,
} from './listItem'

export { ListItemModel } from './listItem'
// Pantry service
export { default as PantryApi } from './pantry'
export type {
  Pantry,
  PantryCreate,
  PantryMetadata,
  PantryUpdate,
} from './pantry'

export { PantryModel } from './pantry'
// Pantry Item service
export { default as PantryItemApi } from './pantryItem'
export type {
  PantryItem,
  PantryItemCreate,
  PantryItemMetadata,
  PantryItemUpdate,
} from './pantryItem'

export { PantryItemModel } from './pantryItem'
// Product service
export { default as ProductApi } from './product'
export type {
  Product,
  ProductCreationData,
  ProductMetadata,
  ProductRegistrationData,
} from './product'

export { ProductModel } from './product'
// Shopping List service
export { default as ShoppingListApi } from './shoppingList'
export type {
  ShoppingList,
  ShoppingListCreate,
  ShoppingListMetadata,
  ShoppingListUpdate,
} from './shoppingList'

export { ShoppingListModel } from './shoppingList'
// User service
export { default as UserApi } from './user'
export type {
  AuthenticationToken,
  Credentials,
  GetUser,
  NewUser,
  PasswordChange,
  PasswordRecovery,
  PasswordReset,
  RegisteredUser,
  RegistrationData,
  UpdateUserProfile,
  User,
  UserMetadata,
  VerificationCode,
} from './user'

export { CredentialsModel, UserModel } from './user'
