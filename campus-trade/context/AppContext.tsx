import React, { createContext, useCallback, useContext, useMemo, useState } from 'react';
import type { FlawBox, PostDraft, RecognitionResult } from '@/types';

type AppContextValue = {
  recognition: RecognitionResult | null;
  setRecognition: (r: RecognitionResult | null) => void;
  draft: PostDraft | null;
  setDraft: (d: PostDraft | null) => void;
  applyRecognitionToDraft: (r: RecognitionResult) => void;
  favoritesVersion: number;
  bumpFavorites: () => void;
};

const emptyDraft = (): PostDraft => ({
  title: '',
  description: '',
  price: '',
  category: 'other',
  condition: '90_new',
  images: [],
  flaws: [] as FlawBox[],
  fromSmart: false,
});

const AppContext = createContext<AppContextValue | null>(null);

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [recognition, setRecognition] = useState<RecognitionResult | null>(null);
  const [draft, setDraft] = useState<PostDraft | null>(null);
  const [favoritesVersion, setFavoritesVersion] = useState(0);

  const applyRecognitionToDraft = useCallback((r: RecognitionResult) => {
    setRecognition(r);
    setDraft({
      title: r.suggestedTitle,
      description: r.suggestedDescription,
      price: '',
      category: r.category,
      condition: r.condition,
      images: [r.enhancedUri],
      flaws: r.flaws,
      fromSmart: true,
    });
  }, []);

  const bumpFavorites = useCallback(() => {
    setFavoritesVersion((v) => v + 1);
  }, []);

  const value = useMemo(
    () => ({
      recognition,
      setRecognition,
      draft,
      setDraft,
      applyRecognitionToDraft,
      favoritesVersion,
      bumpFavorites,
    }),
    [recognition, draft, applyRecognitionToDraft, favoritesVersion, bumpFavorites],
  );

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useAppState() {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error('useAppState must be used within AppProvider');
  return ctx;
}

export { emptyDraft };
