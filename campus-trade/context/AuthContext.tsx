import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { api } from '@/services/api';
import type { User } from '@/types';
import { CURRENT_USER_ID, mockUsers } from '@/data/mock';

const STORAGE_KEY = 'campus_trade_session';

type AuthContextValue = {
  user: User | null;
  loading: boolean;
  login: (studentId: string, name: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const raw = await AsyncStorage.getItem(STORAGE_KEY);
        if (raw) {
          setUser(JSON.parse(raw) as User);
        }
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const login = useCallback(async (studentId: string, name: string) => {
    const u = await api.login(studentId, name);
    setUser(u);
    await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(u));
  }, []);

  const logout = useCallback(async () => {
    setUser(null);
    await AsyncStorage.removeItem(STORAGE_KEY);
  }, []);

  const refreshUser = useCallback(async () => {
    if (!user) return;
    const fresh = await api.getUser(user.id);
    if (fresh) {
      setUser(fresh);
      await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(fresh));
    }
  }, [user]);

  const value = useMemo(
    () => ({ user, loading, login, logout, refreshUser }),
    [user, loading, login, logout, refreshUser],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}

/** Dev helper: quick demo login as seeded user */
export async function demoLoginAsDefault(): Promise<User> {
  return mockUsers.find((u) => u.id === CURRENT_USER_ID)!;
}
