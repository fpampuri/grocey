import { defineStore } from 'pinia'

export interface User {
  firstName: string
  lastName: string
  email: string
  memberSince: string
  listsCreated: number
}

export const useUserStore = defineStore('user', {
  state: () => ({
    user: {
      firstName: 'Juan',
      lastName: 'Pérez',
      email: 'juan@example.com',
      memberSince: 'January 2024',
      listsCreated: 3
    } as User,
    isLoggedIn: true,
  }),
  
  actions: {
    updateProfile(userData: Partial<User>) {
      this.user = { ...this.user, ...userData }
    },
    
    logout() {
      this.isLoggedIn = false
      // Here you would typically clear user data and redirect to login
    },
    
    saveChanges() {
      // Here you would typically make an API call to save the user data
      console.log('Saving user changes:', this.user)
    }
  },
})