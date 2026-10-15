import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { Colors, Radius, Spacing, Typography } from '@/constants/theme';

export function CreditBadge({ score, compact }: { score: number; compact?: boolean }) {
  const color =
    score >= 95 ? Colors.creditGold : score >= 85 ? Colors.primary : Colors.warning;
  return (
    <View style={[styles.badge, compact && styles.compact, { borderColor: color }]}>
      <Ionicons name="shield-checkmark" size={compact ? 12 : 14} color={color} />
      <Text style={[styles.score, { color }]}>{score}</Text>
      {!compact ? <Text style={styles.label}>信用</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  badge: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    paddingHorizontal: Spacing.sm,
    paddingVertical: 4,
    borderRadius: Radius.full,
    borderWidth: 1,
    backgroundColor: Colors.surface,
  },
  compact: {
    paddingHorizontal: 6,
    paddingVertical: 2,
  },
  score: {
    ...Typography.footnote,
    fontWeight: '700',
  },
  label: {
    ...Typography.caption2,
    color: Colors.textSecondary,
  },
});
