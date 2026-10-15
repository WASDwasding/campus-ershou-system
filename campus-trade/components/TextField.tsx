import React from 'react';
import {
  StyleSheet,
  Text,
  TextInput,
  View,
  type TextInputProps,
} from 'react-native';
import { Colors, Radius, Spacing, Typography } from '@/constants/theme';

type Props = TextInputProps & {
  label?: string;
  error?: string;
};

export function TextField({ label, error, style, ...rest }: Props) {
  return (
    <View style={styles.wrap}>
      {label ? <Text style={styles.label}>{label}</Text> : null}
      <TextInput
        placeholderTextColor={Colors.textTertiary}
        style={[styles.input, error ? styles.inputError : null, style]}
        {...rest}
      />
      {error ? <Text style={styles.error}>{error}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { marginBottom: Spacing.lg },
  label: {
    ...Typography.footnote,
    color: Colors.textSecondary,
    marginBottom: Spacing.sm,
    fontWeight: '700',
  },
  input: {
    ...Typography.body,
    backgroundColor: Colors.paper,
    borderWidth: 1.5,
    borderColor: Colors.border,
    borderStyle: 'dashed',
    borderRadius: Radius.md,
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.md,
    color: Colors.ink,
    minHeight: 48,
  },
  inputError: { borderColor: Colors.danger },
  error: {
    ...Typography.caption1,
    color: Colors.danger,
    marginTop: Spacing.xs,
  },
});
