import React from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import { CreditBadge } from '@/components/CreditBadge';
import { DotPaper, PaperCard, ScrapTitle, Sticker, WashiTape } from '@/components/Scrapbook';
import { useAuth } from '@/context/AuthContext';
import { Colors, Spacing, Typography } from '@/constants/theme';

const RULES = [
  { emoji: '⭐', title: '交易互评', body: '完成后双方各打 1–5 星，像贴一张信用贴纸。' },
  { emoji: '🌱', title: '好评加分', body: '4–5★ +2 分，同楼交易更让人放心。' },
  { emoji: '🌧️', title: '差评扣分', body: '1–2★ −3 分，反复差评会影响曝光。' },
  { emoji: '🎓', title: '学号认证', body: '只有校园登记匹配的同学才能入驻。' },
];

export default function CreditScreen() {
  const { user } = useAuth();
  if (!user) return null;

  return (
    <DotPaper>
      <ScrollView contentContainerStyle={styles.pad}>
        <WashiTape label="trust stickers" color={Colors.tapeMint} />
        <PaperCard style={styles.hero} rotate={-1}>
          <Text style={styles.label}>我的校园信用</Text>
          <Text style={styles.score}>{user.creditScore}</Text>
          <CreditBadge score={user.creditScore} />
          <View style={{ marginTop: 12 }}>
            <Sticker text="满分 100 · 新人起步 90" emoji="📒" bg={Colors.accentYellow} />
          </View>
        </PaperCard>

        <ScrapTitle title="规则便签" />
        {RULES.map((r, i) => (
          <PaperCard key={r.title} rotate={i % 2 === 0 ? 0.6 : -0.6} style={styles.row}>
            <Text style={{ fontSize: 22 }}>{r.emoji}</Text>
            <View style={{ flex: 1 }}>
              <Text style={styles.rowTitle}>{r.title}</Text>
              <Text style={styles.rowBody}>{r.body}</Text>
            </View>
          </PaperCard>
        ))}
      </ScrollView>
    </DotPaper>
  );
}

const styles = StyleSheet.create({
  pad: { padding: Spacing.xl, paddingBottom: 48, gap: 10 },
  hero: { alignItems: 'center', paddingVertical: Spacing.xxxl, marginTop: Spacing.md },
  label: { ...Typography.subhead, color: Colors.textSecondary },
  score: { ...Typography.largeTitle, color: Colors.primaryDark, marginVertical: Spacing.sm },
  row: { flexDirection: 'row', gap: Spacing.md, alignItems: 'flex-start' },
  rowTitle: { ...Typography.headline, color: Colors.ink },
  rowBody: { ...Typography.footnote, color: Colors.textSecondary, marginTop: 4, lineHeight: 18 },
});
