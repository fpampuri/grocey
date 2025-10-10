import { defineStore } from 'pinia';
import { UserApi, Api } from '@/services';

export interface UserProfile {
  firstName: string;
  lastName: string;
  email: string;
  memberSince: string;
  listsCreated: number;
  metadata?: Record<string, any>;
}

type SaveChangesInput = Partial<Pick<UserProfile, 'firstName' | 'lastName'>>;

const TOKEN_STORAGE_KEY = 'grocey.authToken';

function formatDate(value?: string | null): string {
  if (!value) return '';
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? value : parsed.toLocaleDateString();
}

function mapServerUser(data: any): UserProfile {
  return {
    firstName: data?.name ?? '',
    lastName: data?.surname ?? '',
    email: data?.email ?? '',
    memberSince: formatDate(data?.createdAt),
    listsCreated:
      data?.metadata?.listsCreated ??
      data?.metadata?.lists ??
      data?.metadata?.lists_count ??
      0,
    metadata: data?.metadata ?? {},
  };
}

function defaultUser(): UserProfile {
  return {
    firstName: '',
    lastName: '',
    email: '',
    memberSince: '',
    listsCreated: 0,
    metadata: {},
  };
}

export const useUserStore = defineStore('user', {
  state: () => ({
    user: defaultUser() as UserProfile,
    token: null as string | null,
    loading: false,
    error: null as string | null,
    profileLoaded: false,
  }),

  actions: {
    init() {
      if (typeof window === 'undefined') return;
      const storedToken = window.localStorage.getItem(TOKEN_STORAGE_KEY);
      if (storedToken) {
        this.setToken(storedToken);
        this.fetchUserProfile();
      }
    },

    setToken(token: string | null) {
      this.token = token;
      Api.setAuthToken(token);
      if (typeof window !== 'undefined') {
        if (token) {
          window.localStorage.setItem(TOKEN_STORAGE_KEY, token);
        } else {
          window.localStorage.removeItem(TOKEN_STORAGE_KEY);
        }
      }
    },

    updateProfile(userData: Partial<UserProfile>) {
      this.user = { ...this.user, ...userData };
    },

    async fetchUserProfile() {
      if (!this.token) return;
      this.loading = true;
      this.error = null;
      try {
        const data = await UserApi.getProfile();
        this.user = mapServerUser(data);
        this.profileLoaded = true;
      } catch (err: any) {
        this.error =
          typeof err === 'string'
            ? err
            : err?.message ?? 'Unable to load profile';
      } finally {
        this.loading = false;
      }
    },

    async saveChanges(changes?: SaveChangesInput) {
      if (!this.token) return;
      this.loading = true;
      this.error = null;

      const payload = {
        name: changes?.firstName ?? this.user.firstName,
        surname: changes?.lastName ?? this.user.lastName,
        metadata: this.user.metadata ?? {},
      };

      try {
        const data = await UserApi.updateProfile(payload);
        this.user = mapServerUser(data);
        this.profileLoaded = true;
      } catch (err: any) {
        this.error =
          typeof err === 'string'
            ? err
            : err?.message ?? 'Unable to update profile';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async logout() {
      try {
        if (this.token) {
          await UserApi.logout();
        }
      } catch (err) {
        // Failing to call logout shouldn't block clearing the session
        console.warn('Logout request failed', err);
      } finally {
        this.setToken(null);
        this.user = defaultUser();
        this.profileLoaded = false;
        this.error = null;
      }
    },
  },
});
