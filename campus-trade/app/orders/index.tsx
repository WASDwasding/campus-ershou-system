import React, { useCallback, useState } from 'react';
import { FlatList, Pressable, StyleSheet, Text, View } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Image } from 'expo-image';
import { DotPaper, PaperCard, Sticker } from '@/components/Scrapbook';
import { api } from '@/services/api';
import { useAuth } from '@/context/AuthContext';
import { Colors, Spacing, Typography } from '@/constants/theme';
import type { Order, Product } from '@/types';

const STATUS_LABEL: Record<Order['status'], string> = {
  pending_reserve: '待确认',
  reserved: '已预定',
  meetup_scheduled: '已约见面',
  completed: '已完成',
  cancelled: '已取消',
  rated: '已互评',
};

const STATUS_BG: Record<Order['status'], string> = {
  pending_reserve: Colors.kraft,
  reserved: Colors.primarySoft,
  meetup_scheduled: Colors.accentYellow,
  completed: '#E4F8F2',
  cancelled: '#FFE8E1',
  rated: Colors.accentLilac,
};

export default function OrdersScreen() {
  const { user } = useAuth();
  const [rows, setRows] = useState<(Order & { product?: Product })[]>([]);

  useFocusEffect(
    useCallback(() => {
      (async () => {
        if (!user) return;
        const orders = await api.getOrders(user.id);
        const withProducts = await Promise.all(
          orders.map(async (o) => ({
            ...o,
            product: await api.getProduct(o.productId),
          })),
        );
        setRows(withProducts);
      })();
    }, [user]),
  );

  return (
    <DotPaper>
      <FlatList
        data={rows}
        keyExtractor={(o) => o.id}
        contentContainerStyle={{ padding: Spacing.lg, gap: Spacing.md, paddingBottom: 40 }}
        ListEmptyComponent={
          <PaperCard style={styles.empty}>
            <Text style={{ fontSize: 36 }}>📦</Text>
            <Text style={styles.emptyTitle}>订单页还空着</Text>
            <Text style={styles.emptyBody}>预定一件好物后，进度会记在这里</Text>
          </PaperCard>
        }
        renderItem={({ item, index }) => (
          <Pressable onPress={() => router.push(`/orders/${item.id}`)}>
            <PaperCard rotate={index % 2 === 0 ? -0.5 : 0.5} style={styles.card}>
              <Image
                source={{ uri: item.product?.images[0] }}
                style={styles.thumb}
                contentFit="cover"
              />
              <View style={{ flex: 1 }}>
                <Text numberOfLines={1} style={styles.title}>
                  {item.product?.title ?? '商品'}
                </Text>
                <Text style={styles.price}>¥{item.price}</Text>
                <View style={{ marginTop: 6, alignSelf: 'flex-start' }}>
                  <Sticker
                    text={STATUS_LABEL[item.status]}
                    emoji="🔖"
                    bg={STATUS_BG[item.status]}
                  />
                </View>
              </View>
            </PaperCard>
          </Pressable>
        )}
      />
    </DotPaper>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    gap: Spacing.md,
    alignItems: 'center',
    paddingVertical: Spacing.md,
  },
  thumb: {
    width: 72,
    height: 72,
    borderRadius: 14,
    backgroundColor: Colors.primarySoft,
  },
  title: { ...Typography.headline, color: Colors.ink },
  price: { ...Typography.subhead, color: Colors.primaryDark, marginTop: 4, fontWeight: '800' },
  empty: { alignItems: 'center', marginTop: 48, paddingVertical: 36 },
  emptyTitle: { ...Typography.title3, color: Colors.ink, marginTop: 8 },
  emptyBody: { ...Typography.subhead, color: Colors.textSecondary, marginTop: 6 },
});
