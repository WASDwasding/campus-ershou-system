import React from 'react';
import { Image } from 'expo-image';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Colors, Radius, Spacing, Typography, ConditionLabels } from '@/constants/theme';
import { Sticker } from '@/components/Scrapbook';
import type { Product } from '@/types';

type Props = {
  product: Product;
  width: number;
  onPress: () => void;
  badge?: string;
  tilt?: number;
};

/** Polaroid / scrapbook listing card */
export function ProductCard({ product, width, onPress, badge, tilt = 0 }: Props) {
  return (
    <Pressable
      onPress={onPress}
      style={({ pressed }) => [
        styles.wrap,
        { width, transform: [{ rotate: `${tilt}deg` }] },
        pressed && { opacity: 0.92, transform: [{ rotate: `${tilt}deg` }, { scale: 0.98 }] },
      ]}
    >
      <View style={styles.tape} />
      <View style={styles.card}>
        <View style={styles.photoFrame}>
          <Image
            source={{ uri: product.images[0] }}
            style={{ width: width - 20, height: (width - 20) * 1.05, borderRadius: 8 }}
            contentFit="cover"
          />
          {badge ? (
            <View style={styles.badgeWrap}>
              <Sticker text={badge} emoji="📍" bg={Colors.accentYellow} />
            </View>
          ) : null}
          {product.status === 'reserved' ? (
            <View style={styles.reserved}>
              <Text style={styles.reservedText}>已预定 ✿</Text>
            </View>
          ) : null}
        </View>
        <Text numberOfLines={2} style={styles.title}>
          {product.title}
        </Text>
        <View style={styles.row}>
          <Text style={styles.price}>¥{product.price}</Text>
          <Text style={styles.condition}>{ConditionLabels[product.condition]}</Text>
        </View>
        <Text style={styles.dorm} numberOfLines={1}>
          🏠 {product.dormBuilding}
        </Text>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  wrap: {
    marginBottom: Spacing.md,
  },
  tape: {
    alignSelf: 'center',
    width: 54,
    height: 14,
    backgroundColor: Colors.tapeBlue,
    marginBottom: -7,
    zIndex: 2,
    transform: [{ rotate: '2deg' }],
  },
  card: {
    backgroundColor: Colors.paper,
    borderRadius: Radius.md,
    borderWidth: 2,
    borderColor: Colors.border,
    padding: 10,
    paddingTop: 12,
    shadowColor: Colors.primaryDark,
    shadowOpacity: 0.12,
    shadowRadius: 8,
    shadowOffset: { width: 1, height: 3 },
  },
  photoFrame: {
    borderRadius: 8,
    overflow: 'hidden',
    backgroundColor: Colors.primarySoft,
    borderWidth: 1,
    borderColor: Colors.separator,
  },
  badgeWrap: {
    position: 'absolute',
    top: 8,
    left: 6,
  },
  reserved: {
    ...StyleSheet.absoluteFill,
    backgroundColor: 'rgba(61,79,95,0.4)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  reservedText: {
    color: '#fff',
    ...Typography.headline,
    backgroundColor: Colors.accent,
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 12,
    overflow: 'hidden',
    transform: [{ rotate: '-6deg' }],
  },
  title: {
    ...Typography.subhead,
    color: Colors.ink,
    fontWeight: '700',
    marginTop: 8,
    minHeight: 36,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: 4,
  },
  price: {
    ...Typography.headline,
    color: Colors.primaryDark,
  },
  condition: {
    ...Typography.caption2,
    color: Colors.secondary,
    backgroundColor: 'rgba(125,211,192,0.25)',
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 8,
    overflow: 'hidden',
  },
  dorm: {
    ...Typography.caption2,
    color: Colors.textTertiary,
    marginTop: 4,
  },
});
