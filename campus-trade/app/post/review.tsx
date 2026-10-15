import React, { useState } from 'react';
import {
  Alert,
  Dimensions,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { Image } from 'expo-image';
import { router } from 'expo-router';
import { Button } from '@/components/Button';
import { FlawOverlay } from '@/components/FlawOverlay';
import { TextField } from '@/components/TextField';
import { useAppState } from '@/context/AppContext';
import { useAuth } from '@/context/AuthContext';
import { api } from '@/services/api';
import { Colors, ConditionLabels, Spacing, Typography } from '@/constants/theme';

const W = Dimensions.get('window').width - Spacing.xl * 2;
const H = W * 0.9;

export default function PostReviewScreen() {
  const { draft, setDraft, setRecognition } = useAppState();
  const { user } = useAuth();
  const [price, setPrice] = useState(draft?.price ?? '');
  const [title, setTitle] = useState(draft?.title ?? '');
  const [description, setDescription] = useState(draft?.description ?? '');
  const [loading, setLoading] = useState(false);

  if (!draft || !user) {
    return (
      <View style={styles.empty}>
        <Text style={styles.emptyText}>还没有草稿哦，先去发帖页贴一张～</Text>
        <Button title="去发帖" onPress={() => router.replace('/(tabs)/post')} />
      </View>
    );
  }

  const publish = async () => {
    const num = Number(price);
    if (!num || num <= 0) {
      Alert.alert('先写个价格', '填上人民币价格才能贴到墙上～');
      return;
    }
    setLoading(true);
    try {
      const product = await api.createProduct({
        title: title.trim() || draft.title,
        description: description.trim() || draft.description,
        price: num,
        category: draft.category,
        condition: draft.condition,
        images: draft.images,
        flaws: draft.flaws,
        sellerId: user.id,
        dormBuilding: user.dormBuilding,
      });
      setDraft(null);
      setRecognition(null);
      Alert.alert('贴上去啦 ✿', '你的闲置已经出现在校园墙上', [
        { text: '去看看', onPress: () => router.replace(`/product/${product.id}`) },
      ]);
    } catch (e) {
      Alert.alert('失败了', e instanceof Error ? e.message : '再试一次');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container} keyboardShouldPersistTaps="handled">
      {draft.fromSmart ? (
        <View style={styles.banner}>
          <Text style={styles.bannerText}>
            智能识别已经帮你填好啦～确认一下，再写下价格就能发布。
          </Text>
        </View>
      ) : null}

      {draft.images[0] ? (
        <View style={{ width: W, height: H, marginBottom: Spacing.lg }}>
          <Image
            source={{ uri: draft.images[0] }}
            style={{ width: W, height: H, borderRadius: 12 }}
            contentFit="cover"
          />
          <FlawOverlay flaws={draft.flaws} width={W} height={H} />
        </View>
      ) : null}

      <View style={styles.summary}>
        <Text style={styles.summaryLine}>
          分类 · <Text style={styles.strong}>{draft.category}</Text>
        </Text>
        <Text style={styles.summaryLine}>
          成色 · <Text style={styles.strong}>{ConditionLabels[draft.condition]}</Text>
        </Text>
        <Text style={styles.summaryLine}>
          瑕疵 · <Text style={styles.strong}>{draft.flaws.length || '未检出'}</Text>
        </Text>
      </View>

      <TextField label="标题" value={title} onChangeText={setTitle} />
      <TextField
        label="描述"
        value={description}
        onChangeText={setDescription}
        multiline
        style={{ minHeight: 90, textAlignVertical: 'top' }}
      />
      <TextField
        label="价格（¥）— 必填"
        value={price}
        onChangeText={setPrice}
        keyboardType="decimal-pad"
        placeholder="写下价格再发布"
      />

      <Button title="贴到校园墙上 ✿" loading={loading} onPress={publish} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { padding: Spacing.xl, paddingBottom: 48 },
  empty: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: Spacing.xl, gap: Spacing.lg },
  emptyText: { ...Typography.callout, color: Colors.textSecondary },
  banner: {
    backgroundColor: Colors.primarySoft,
    padding: Spacing.md,
    borderRadius: 10,
    marginBottom: Spacing.lg,
  },
  bannerText: { ...Typography.footnote, color: Colors.primaryDark },
  summary: {
    backgroundColor: Colors.surface,
    borderRadius: 12,
    padding: Spacing.lg,
    marginBottom: Spacing.lg,
    gap: 6,
  },
  summaryLine: { ...Typography.subhead, color: Colors.textSecondary },
  strong: { color: Colors.text, fontWeight: '600' },
});
