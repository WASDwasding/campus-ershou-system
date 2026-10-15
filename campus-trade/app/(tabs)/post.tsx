import React from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { router } from 'expo-router';
import { LinearGradient } from 'expo-linear-gradient';
import { SafeAreaView } from 'react-native-safe-area-context';
import { PaperCard, Sticker, WashiTape } from '@/components/Scrapbook';
import { emptyDraft, useAppState } from '@/context/AppContext';
import { Colors, Spacing, Typography } from '@/constants/theme';

export default function PostHubScreen() {
  const { setDraft, setRecognition } = useAppState();

  const startSmart = () => {
    setRecognition(null);
    setDraft(null);
    router.push('/post/smart');
  };

  const startManual = () => {
    setRecognition(null);
    setDraft(emptyDraft());
    router.push('/post/manual');
  };

  return (
    <LinearGradient colors={['#D4E8F4', '#E6F1F7', '#FFFCF8']} style={{ flex: 1 }}>
      <SafeAreaView style={styles.safe}>
        <Text style={styles.title}>贴一张闲置</Text>
        <Text style={styles.sub}>低光也能拍清楚 · 划痕自动圈出来 · 你只需填个价</Text>
        <WashiTape label="smart · camera" color={Colors.tapeBlue} style={{ marginVertical: 16 }} />

        <Pressable onPress={startSmart} style={({ pressed }) => [pressed && { opacity: 0.92 }]}>
          <PaperCard style={styles.primaryCard} rotate={-1}>
            <Sticker text="推荐" emoji="✨" bg={Colors.accentYellow} />
            <Text style={styles.cardEmoji}>📸</Text>
            <Text style={styles.cardTitle}>智能低光发帖</Text>
            <Text style={styles.cardBody}>
              拍照 → 增强去噪 → 识别分类/成色 → 红框标瑕疵 → 自动填表，只输价格就能发布
            </Text>
            <Text style={styles.cta}>打开相机贴贴 →</Text>
          </PaperCard>
        </Pressable>

        <Pressable onPress={startManual} style={({ pressed }) => [pressed && { opacity: 0.92 }]}>
          <PaperCard style={styles.altCard} rotate={1.2} tone="alt">
            <Text style={styles.cardEmoji}>🖍️</Text>
            <Text style={[styles.cardTitle, { color: Colors.ink }]}>手写手动发帖</Text>
            <Text style={[styles.cardBody, { color: Colors.textSecondary }]}>
              从相册挑图，自己填写标题分类成色，适合已经拍好的宝贝
            </Text>
          </PaperCard>
        </Pressable>
      </SafeAreaView>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, padding: Spacing.xl },
  title: { ...Typography.largeTitle, color: Colors.primaryDark, marginTop: Spacing.lg },
  sub: {
    ...Typography.callout,
    color: Colors.textSecondary,
    marginTop: Spacing.sm,
    lineHeight: 22,
  },
  primaryCard: {
    backgroundColor: Colors.primarySoft,
    marginBottom: Spacing.lg,
  },
  altCard: { marginBottom: Spacing.lg },
  cardEmoji: { fontSize: 36, marginTop: Spacing.md },
  cardTitle: { ...Typography.title3, color: Colors.primaryDark, marginTop: Spacing.sm },
  cardBody: {
    ...Typography.subhead,
    color: Colors.textSecondary,
    marginTop: Spacing.sm,
    lineHeight: 20,
  },
  cta: { ...Typography.headline, color: Colors.primaryDark, marginTop: Spacing.lg },
});
