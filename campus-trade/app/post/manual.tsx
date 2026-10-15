import React, { useState } from 'react';
import {
  Alert,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { Image } from 'expo-image';
import { router } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { Button } from '@/components/Button';
import { TextField } from '@/components/TextField';
import { emptyDraft, useAppState } from '@/context/AppContext';
import { Categories, Colors, ConditionLabels, Spacing, Typography } from '@/constants/theme';
import type { ConditionGrade, ProductCategory } from '@/types';

export default function ManualPostScreen() {
  const { draft, setDraft } = useAppState();
  const [local, setLocal] = useState(draft ?? emptyDraft());

  const pick = async () => {
    const res = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsMultipleSelection: true,
      quality: 0.85,
      selectionLimit: 6,
    });
    if (!res.canceled) {
      setLocal((d) => ({
        ...d,
        images: [...d.images, ...res.assets.map((a) => a.uri)].slice(0, 6),
      }));
    }
  };

  const continueReview = () => {
    if (!local.images.length) {
      Alert.alert('Photos required', 'Add at least one photo.');
      return;
    }
    if (!local.title.trim()) {
      Alert.alert('Title required', 'Enter a product title.');
      return;
    }
    setDraft({ ...local, fromSmart: false });
    router.push('/post/review');
  };

  return (
    <ScrollView contentContainerStyle={styles.container} keyboardShouldPersistTaps="handled">
      <Pressable style={styles.addPhoto} onPress={pick}>
        <Ionicons name="images-outline" size={28} color={Colors.primary} />
        <Text style={styles.addPhotoText}>Add photos</Text>
      </Pressable>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={{ marginBottom: Spacing.lg }}>
        {local.images.map((uri) => (
          <Image key={uri} source={{ uri }} style={styles.thumb} contentFit="cover" />
        ))}
      </ScrollView>

      <TextField
        label="Title"
        value={local.title}
        onChangeText={(title) => setLocal((d) => ({ ...d, title }))}
        placeholder="What are you selling?"
      />
      <TextField
        label="Description"
        value={local.description}
        onChangeText={(description) => setLocal((d) => ({ ...d, description }))}
        placeholder="Condition notes, accessories…"
        multiline
        style={{ minHeight: 100, textAlignVertical: 'top' }}
      />

      <Text style={styles.label}>Category</Text>
      <View style={styles.wrap}>
        {Categories.filter((c) => c.id !== 'all').map((c) => {
          const active = local.category === c.id;
          return (
            <Pressable
              key={c.id}
              onPress={() => setLocal((d) => ({ ...d, category: c.id as ProductCategory }))}
              style={[styles.chip, active && styles.chipOn]}
            >
              <Text style={[styles.chipText, active && styles.chipTextOn]}>{c.name}</Text>
            </Pressable>
          );
        })}
      </View>

      <Text style={styles.label}>Condition</Text>
      <View style={styles.wrap}>
        {(Object.keys(ConditionLabels) as ConditionGrade[]).map((key) => {
          const active = local.condition === key;
          return (
            <Pressable
              key={key}
              onPress={() => setLocal((d) => ({ ...d, condition: key }))}
              style={[styles.chip, active && styles.chipOn]}
            >
              <Text style={[styles.chipText, active && styles.chipTextOn]}>
                {ConditionLabels[key]}
              </Text>
            </Pressable>
          );
        })}
      </View>

      <Button title="Continue" onPress={continueReview} style={{ marginTop: Spacing.xl }} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { padding: Spacing.xl, paddingBottom: 48 },
  addPhoto: {
    height: 100,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: Colors.border,
    borderStyle: 'dashed',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: Colors.surface,
    marginBottom: Spacing.md,
    gap: 6,
  },
  addPhotoText: { ...Typography.subhead, color: Colors.primary },
  thumb: { width: 88, height: 88, borderRadius: 10, marginRight: Spacing.sm },
  label: {
    ...Typography.footnote,
    color: Colors.textSecondary,
    fontWeight: '600',
    marginBottom: Spacing.sm,
  },
  wrap: { flexDirection: 'row', flexWrap: 'wrap', gap: Spacing.sm, marginBottom: Spacing.lg },
  chip: {
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    borderRadius: 20,
    backgroundColor: Colors.primarySoft,
  },
  chipOn: { backgroundColor: Colors.primary },
  chipText: { ...Typography.footnote, color: Colors.primaryDark, fontWeight: '600' },
  chipTextOn: { color: '#fff' },
});
