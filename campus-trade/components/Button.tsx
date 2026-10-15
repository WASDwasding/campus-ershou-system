import React from 'react';
import {
  ActivityIndicator,
  Pressable,
  StyleSheet,
  Text,
  type PressableProps,
  type ViewStyle,
} from 'react-native';
import { Colors, Radius, Spacing, Typography } from '@/constants/theme';

type Variant = 'primary' | 'secondary' | 'ghost' | 'danger' | 'sticker';

type Props = PressableProps & {
  title: string;
  variant?: Variant;
  loading?: boolean;
  style?: ViewStyle;
};

export function Button({
  title,
  variant = 'primary',
  loading,
  disabled,
  style,
  ...rest
}: Props) {
  const isDisabled = disabled || loading;
  return (
    <Pressable
      accessibilityRole="button"
      disabled={isDisabled}
      style={({ pressed }) => [
        styles.base,
        styles[variant],
        pressed && !isDisabled && styles.pressed,
        isDisabled && styles.disabled,
        style,
      ]}
      {...rest}
    >
      {loading ? (
        <ActivityIndicator color={variant === 'ghost' || variant === 'secondary' ? Colors.primaryDark : '#fff'} />
      ) : (
        <Text style={[styles.label, styles[`${variant}Label` as const]]}>{title}</Text>
      )}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    minHeight: 48,
    borderRadius: Radius.lg,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: Spacing.xl,
    borderWidth: 1.5,
  },
  primary: {
    backgroundColor: Colors.primary,
    borderColor: Colors.primaryDark,
  },
  secondary: {
    backgroundColor: Colors.primarySoft,
    borderColor: Colors.primaryLight,
  },
  ghost: {
    backgroundColor: Colors.paper,
    borderColor: Colors.border,
    borderStyle: 'dashed',
  },
  danger: {
    backgroundColor: Colors.danger,
    borderColor: '#D95A52',
  },
  sticker: {
    backgroundColor: Colors.accentYellow,
    borderColor: '#E6C96A',
    transform: [{ rotate: '-1deg' }],
  },
  pressed: { opacity: 0.88, transform: [{ scale: 0.98 }] },
  disabled: { opacity: 0.45 },
  label: { ...Typography.headline },
  primaryLabel: { color: '#fff' },
  secondaryLabel: { color: Colors.primaryDark },
  ghostLabel: { color: Colors.ink },
  dangerLabel: { color: '#fff' },
  stickerLabel: { color: Colors.ink },
});

export { ScrapTitle as SectionHeader } from '@/components/Scrapbook';
