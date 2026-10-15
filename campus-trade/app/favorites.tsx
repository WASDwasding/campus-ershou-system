import React, { useCallback, useState } from 'react';
import { Dimensions, FlatList, StyleSheet, Text, View } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { ProductCard } from '@/components/ProductCard';
import { api } from '@/services/api';
import { useAppState } from '@/context/AppContext';
import { Colors, Spacing, Typography } from '@/constants/theme';
import type { Product } from '@/types';

const W = (Dimensions.get('window').width - Spacing.lg * 2 - 10) / 2;

export default function FavoritesScreen() {
  const { favoritesVersion } = useAppState();
  const [items, setItems] = useState<Product[]>([]);

  useFocusEffect(
    useCallback(() => {
      (async () => setItems(await api.getFavorites()))();
    }, [favoritesVersion]),
  );

  return (
    <FlatList
      style={{ flex: 1, backgroundColor: Colors.background }}
      data={items}
      keyExtractor={(p) => p.id}
      numColumns={2}
      columnWrapperStyle={{ gap: 10, paddingHorizontal: Spacing.lg }}
      contentContainerStyle={{ paddingTop: Spacing.lg, paddingBottom: 40 }}
      ListEmptyComponent={<Text style={styles.empty}>No favorites yet.</Text>}
      renderItem={({ item }) => (
        <ProductCard
          product={item}
          width={W}
          onPress={() => router.push(`/product/${item.id}`)}
        />
      )}
    />
  );
}

const styles = StyleSheet.create({
  empty: {
    ...Typography.callout,
    color: Colors.textTertiary,
    textAlign: 'center',
    marginTop: 48,
  },
});
