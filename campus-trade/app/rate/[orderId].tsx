import React, { useState } from 'react';
import { Alert, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { router, useLocalSearchParams } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { Button } from '@/components/Button';
import { api } from '@/services/api';
import { useAuth } from '@/context/AuthContext';
import { Colors, Spacing, Typography } from '@/constants/theme';

export default function RateScreen() {
  const { orderId } = useLocalSearchParams<{ orderId: string }>();
  const { user, refreshUser } = useAuth();
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async () => {
    if (!user) return;
    setLoading(true);
    try {
      await api.rateOrder(orderId, user.id, rating, comment.trim());
      await refreshUser();
      Alert.alert('Thanks', 'Rating saved. Credit scores updated.', [
        { text: 'OK', onPress: () => router.back() },
      ]);
    } catch (e) {
      Alert.alert('Failed', e instanceof Error ? e.message : 'Try again');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.root}>
      <Text style={styles.title}>How was this trade?</Text>
      <Text style={styles.sub}>Mutual ratings keep the campus marketplace trustworthy.</Text>
      <View style={styles.stars}>
        {[1, 2, 3, 4, 5].map((n) => (
          <Pressable key={n} onPress={() => setRating(n)} hitSlop={6}>
            <Ionicons
              name={n <= rating ? 'star' : 'star-outline'}
              size={36}
              color={Colors.accent}
            />
          </Pressable>
        ))}
      </View>
      <TextInput
        style={styles.input}
        placeholder="Optional comment"
        placeholderTextColor={Colors.textTertiary}
        value={comment}
        onChangeText={setComment}
        multiline
      />
      <Button title="Submit rating" loading={loading} onPress={submit} />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: Colors.background, padding: Spacing.xl },
  title: { ...Typography.title2, color: Colors.text },
  sub: { ...Typography.callout, color: Colors.textSecondary, marginTop: Spacing.sm },
  stars: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: Spacing.md,
    marginVertical: Spacing.xxxl,
  },
  input: {
    ...Typography.body,
    minHeight: 100,
    backgroundColor: Colors.surface,
    borderRadius: 12,
    padding: Spacing.lg,
    color: Colors.text,
    textAlignVertical: 'top',
    marginBottom: Spacing.xl,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: Colors.border,
  },
});
