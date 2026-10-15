import React, { useCallback, useState } from 'react';
import { FlatList, StyleSheet, Text, View } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { ProductCard } from '@/components/ProductCard';
import { api } from '@/services/api';
import { useAuth } from '@/context/AuthContext';
import { Colors, Spacing, Typography } from '@/constants/theme';
import type { Product } from '@/types';
import { Dimensions } from 'react-native';

const W = Dimensions.get('window').width - Spacing.lg * 2;

export default function MyPostsScreen() {
  const { user } = useAuth();
  const [items, setItems] = useState<Product[]>([]);

  useFocusEffect(
    useCallback(() => {
      (async () => {
        if (!user) return;
        setItems(await api.getUserProducts(user.id));
      })();
    }, [user]),
  );

  return (
    <FlatList
      style={{ flex: 1, backgroundColor: Colors.background }}
      data={items}
      keyExtractor={(p) => p.id}
      contentContainerStyle={{ padding: Spacing.lg }}
      ListEmptyComponent={
        <Text style={styles.empty}>You have not posted anything yet.</Text>
      }
      renderItem={({ item }) => (
        <View style={{ marginBottom: Spacing.md }}>
          <ProductCard
            product={item}
            width={W}
            onPress={() => router.push(`/product/${item.id}`)}
          />
        </View>
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
