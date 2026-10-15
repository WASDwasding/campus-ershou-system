import React, { useCallback, useState } from 'react';
import { Alert, StyleSheet, Text, View } from 'react-native';
import { router, useFocusEffect, useLocalSearchParams } from 'expo-router';
import { Image } from 'expo-image';
import { Button } from '@/components/Button';
import { api } from '@/services/api';
import { useAuth } from '@/context/AuthContext';
import { Colors, Spacing, Typography } from '@/constants/theme';
import type { Order, OrderStatus, Product } from '@/types';

const FLOW: OrderStatus[] = [
  'reserved',
  'meetup_scheduled',
  'completed',
  'rated',
];

const LABEL: Record<OrderStatus, string> = {
  pending_reserve: 'Pending reserve',
  reserved: 'Reserved',
  meetup_scheduled: 'Meetup scheduled',
  completed: 'Completed',
  cancelled: 'Cancelled',
  rated: 'Mutual rating done',
};

export default function OrderDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const [order, setOrder] = useState<Order | null>(null);
  const [product, setProduct] = useState<Product | null>(null);
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    const o = await api.getOrder(id);
    if (!o) return;
    setOrder(o);
    setProduct((await api.getProduct(o.productId)) ?? null);
  }, [id]);

  useFocusEffect(
    useCallback(() => {
      load();
    }, [load]),
  );

  if (!order || !user) return <View style={{ flex: 1, backgroundColor: Colors.background }} />;

  const advance = async (status: OrderStatus) => {
    setBusy(true);
    try {
      const updated = await api.updateOrderStatus(
        order.id,
        status,
        status === 'meetup_scheduled' ? 'Campus lake plaza · tomorrow 6pm' : undefined,
      );
      setOrder(updated);
    } finally {
      setBusy(false);
    }
  };

  const cancel = () => {
    Alert.alert('Cancel order?', 'The listing will become available again.', [
      { text: 'Keep', style: 'cancel' },
      {
        text: 'Cancel order',
        style: 'destructive',
        onPress: () => advance('cancelled'),
      },
    ]);
  };

  const stepIndex = FLOW.indexOf(order.status === 'cancelled' ? 'reserved' : order.status);

  return (
    <View style={styles.root}>
      <Image source={{ uri: product?.images[0] }} style={styles.image} contentFit="cover" />
      <Text style={styles.title}>{product?.title}</Text>
      <Text style={styles.price}>¥{order.price}</Text>

      <Text style={styles.section}>Status tracking</Text>
      <View style={styles.timeline}>
        {FLOW.map((s, i) => {
          const done =
            order.status === 'cancelled'
              ? false
              : order.status === 'rated'
                ? true
                : i <= stepIndex;
          return (
            <View key={s} style={styles.step}>
              <View style={[styles.dot, done && styles.dotOn]} />
              <Text style={[styles.stepText, done && styles.stepOn]}>{LABEL[s]}</Text>
            </View>
          );
        })}
      </View>

      {order.meetupNote ? (
        <Text style={styles.note}>Meetup: {order.meetupNote}</Text>
      ) : null}
      {order.status === 'cancelled' ? (
        <Text style={styles.cancelled}>This order was cancelled.</Text>
      ) : null}

      <View style={styles.actions}>
        {order.status === 'reserved' ? (
          <Button
            title="Schedule meetup"
            loading={busy}
            onPress={() => advance('meetup_scheduled')}
          />
        ) : null}
        {order.status === 'meetup_scheduled' ? (
          <Button title="Mark completed" loading={busy} onPress={() => advance('completed')} />
        ) : null}
        {order.status === 'completed' ? (
          <Button title="Rate this trade" onPress={() => router.push(`/rate/${order.id}`)} />
        ) : null}
        {order.status === 'rated' ? (
          <Text style={styles.done}>Thanks — credit scores updated.</Text>
        ) : null}
        {order.status !== 'completed' &&
        order.status !== 'rated' &&
        order.status !== 'cancelled' ? (
          <Button title="Cancel order" variant="ghost" onPress={cancel} style={{ marginTop: 12 }} />
        ) : null}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: Colors.background, padding: Spacing.xl },
  image: { width: '100%', height: 180, borderRadius: 12, backgroundColor: Colors.primarySoft },
  title: { ...Typography.title3, color: Colors.text, marginTop: Spacing.lg },
  price: { ...Typography.title2, color: Colors.primaryDark, marginTop: 4 },
  section: { ...Typography.headline, color: Colors.text, marginTop: Spacing.xxl, marginBottom: Spacing.md },
  timeline: { gap: Spacing.md },
  step: { flexDirection: 'row', alignItems: 'center', gap: Spacing.md },
  dot: {
    width: 12,
    height: 12,
    borderRadius: 6,
    backgroundColor: Colors.border,
  },
  dotOn: { backgroundColor: Colors.primary },
  stepText: { ...Typography.subhead, color: Colors.textTertiary },
  stepOn: { color: Colors.text, fontWeight: '600' },
  note: { ...Typography.callout, color: Colors.textSecondary, marginTop: Spacing.lg },
  cancelled: { ...Typography.callout, color: Colors.danger, marginTop: Spacing.md },
  actions: { marginTop: Spacing.xxxl },
  done: { ...Typography.callout, color: Colors.success, textAlign: 'center' },
});
