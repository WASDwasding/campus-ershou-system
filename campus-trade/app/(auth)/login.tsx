import React, { useState } from 'react';
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Link, router } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Button } from '@/components/Button';
import { TextField } from '@/components/TextField';
import { ImageSticker, PaperCard, Sticker, WashiTape } from '@/components/Scrapbook';
import { useAuth } from '@/context/AuthContext';
import { Colors, Spacing, StickerAssets, Typography } from '@/constants/theme';

export default function LoginScreen() {
  const { login } = useAuth();
  const [studentId, setStudentId] = useState('202301001');
  const [name, setName] = useState('Alex Chen');
  const [loading, setLoading] = useState(false);

  const onSubmit = async () => {
    if (!studentId.trim() || !name.trim()) {
      Alert.alert('等等', '先填学号和姓名呀～');
      return;
    }
    setLoading(true);
    try {
      await login(studentId.trim(), name.trim());
      router.replace('/(tabs)');
    } catch (e) {
      Alert.alert('认证失败', e instanceof Error ? e.message : '再试一次');
    } finally {
      setLoading(false);
    }
  };

  return (
    <LinearGradient colors={['#D4E8F4', '#E6F1F7', '#FFFCF8']} style={{ flex: 1 }}>
      <SafeAreaView style={{ flex: 1 }}>
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : undefined}
          style={styles.container}
        >
          <View style={styles.decorRow}>
            <ImageSticker source={StickerAssets.animals} size={56} rotate={-6} />
            <ImageSticker source={StickerAssets.collage} size={56} rotate={4} />
            <ImageSticker source={StickerAssets.puppies} size={56} rotate={-2} />
          </View>
          <Text style={styles.brand}>Campus Trade</Text>
          <Text style={styles.tagline}>淡奶油蓝的校园闲置手帐 · 慢慢逛，慢慢换</Text>
          <WashiTape label="students only ✿" dotted color={Colors.tapeBlue} style={{ marginVertical: 16 }} />

          <PaperCard rotate={-1} tone="paper">
            <Sticker text="学号认证" emoji="🎓" bg={Colors.creamBlue} />
            <Text style={styles.cardTitle}>翻开你的一页</Text>
            <TextField
              label="学号"
              value={studentId}
              onChangeText={setStudentId}
              keyboardType="number-pad"
              autoCapitalize="none"
              placeholder="例如 202301001"
            />
            <TextField
              label="姓名（与校园登记一致）"
              value={name}
              onChangeText={setName}
              placeholder="真实姓名"
              autoCorrect={false}
            />
            <Button title="验证并进入 ✿" loading={loading} onPress={onSubmit} />
            <Link href="/(auth)/register" asChild>
              <Pressable style={styles.linkWrap}>
                <Text style={styles.link}>新同学？去登记认证</Text>
              </Pressable>
            </Link>
            <Text style={styles.hint}>演示：202301001 · Alex Chen · Lakeview Hall F</Text>
          </PaperCard>
        </KeyboardAvoidingView>
      </SafeAreaView>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: Spacing.xl,
    justifyContent: 'center',
  },
  decorRow: {
    flexDirection: 'row',
    gap: 10,
    marginBottom: Spacing.md,
  },
  brand: {
    ...Typography.largeTitle,
    color: Colors.primaryDark,
  },
  tagline: {
    ...Typography.callout,
    color: Colors.textSecondary,
    marginTop: Spacing.sm,
    lineHeight: 22,
  },
  cardTitle: {
    ...Typography.headline,
    color: Colors.ink,
    marginVertical: Spacing.md,
  },
  linkWrap: { marginTop: Spacing.lg, alignItems: 'center' },
  link: { ...Typography.subhead, color: Colors.primaryDark, fontWeight: '700' },
  hint: {
    ...Typography.caption1,
    color: Colors.textTertiary,
    textAlign: 'center',
    marginTop: Spacing.lg,
  },
});
