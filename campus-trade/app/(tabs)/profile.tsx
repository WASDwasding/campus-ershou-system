import React, { useCallback, useState } from 'react';
import {
  Alert,
  Image,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { CreditBadge } from '@/components/CreditBadge';
import { DotPaper, PaperCard, Sticker, WashiTape } from '@/components/Scrapbook';
import { useAuth } from '@/context/AuthContext';
import { api } from '@/services/api';
import { Colors, Spacing, Typography } from '@/constants/theme';

const MENU = [
  { icon: 'cube-outline' as const, label: '我的贴贴', href: '/my-posts', emoji: '📌' },
  { icon: 'heart-outline' as const, label: '收藏夹', href: '/favorites', emoji: '💛' },
  { icon: 'receipt-outline' as const, label: '订单手帐', href: '/orders', emoji: '📦' },
  { icon: 'shield-checkmark-outline' as const, label: '校园信用', href: '/credit', emoji: '🏅' },
];

export default function ProfileScreen() {
  const { user, logout, refreshUser } = useAuth();
  const [stats, setStats] = useState({ posts: 0, favs: 0, orders: 0 });

  useFocusEffect(
    useCallback(() => {
      (async () => {
        if (!user) return;
        await refreshUser();
        const [posts, favs, orders] = await Promise.all([
          api.getUserProducts(user.id),
          api.getFavorites(user.id),
          api.getOrders(user.id),
        ]);
        setStats({ posts: posts.length, favs: favs.length, orders: orders.length });
      })();
    }, [user, refreshUser]),
  );

  const onLogout = () => {
    Alert.alert('先走啦？', '要退出这本校园手帐吗？', [
      { text: '再逛逛', style: 'cancel' },
      {
        text: '退出',
        style: 'destructive',
        onPress: async () => {
          await logout();
          router.replace('/(auth)/login');
        },
      },
    ]);
  };

  if (!user) return null;

  return (
    <DotPaper>
      <ScrollView contentContainerStyle={{ paddingBottom: 48 }}>
        <WashiTape label="my scrapbook" color={Colors.tapePeach} style={{ marginTop: 12 }} />
        <PaperCard style={styles.hero} rotate={-0.8}>
          <View style={styles.heroRow}>
            <Image source={{ uri: user.avatar }} style={styles.avatar} />
            <View style={{ flex: 1 }}>
              <Text style={styles.name}>{user.name}</Text>
              <Text style={styles.meta}>学号 {user.studentId}</Text>
              <View style={{ marginTop: 8, alignSelf: 'flex-start' }}>
                <Sticker text={user.dormBuilding} emoji="🏠" bg={Colors.primarySoft} />
              </View>
              <View style={{ marginTop: 8 }}>
                <CreditBadge score={user.creditScore} />
              </View>
            </View>
            {user.verified ? (
              <Sticker text="已认证" emoji="✔" bg={Colors.accentYellow} />
            ) : null}
          </View>
          <View style={styles.stats}>
            <View style={styles.stat}>
              <Text style={styles.statNum}>{stats.posts}</Text>
              <Text style={styles.statLabel}>在售</Text>
            </View>
            <View style={styles.stat}>
              <Text style={styles.statNum}>{stats.favs}</Text>
              <Text style={styles.statLabel}>收藏</Text>
            </View>
            <View style={styles.stat}>
              <Text style={styles.statNum}>{stats.orders}</Text>
              <Text style={styles.statLabel}>订单</Text>
            </View>
          </View>
        </PaperCard>

        <View style={styles.menu}>
          {MENU.map((item, i) => (
            <Pressable key={item.href} onPress={() => router.push(item.href as never)}>
              <PaperCard rotate={i % 2 === 0 ? 0.5 : -0.5} style={styles.row}>
                <Text style={{ fontSize: 18 }}>{item.emoji}</Text>
                <Text style={styles.rowLabel}>{item.label}</Text>
                <Ionicons name="chevron-forward" size={18} color={Colors.textTertiary} />
              </PaperCard>
            </Pressable>
          ))}
        </View>

        <Pressable onPress={onLogout} style={{ marginHorizontal: Spacing.lg, marginTop: Spacing.lg }}>
          <PaperCard style={styles.logout}>
            <Text style={styles.logoutText}>退出登录</Text>
          </PaperCard>
        </Pressable>
      </ScrollView>
    </DotPaper>
  );
}

const styles = StyleSheet.create({
  hero: { marginHorizontal: Spacing.lg, marginTop: Spacing.md },
  heroRow: { flexDirection: 'row', gap: Spacing.md },
  avatar: {
    width: 72,
    height: 72,
    borderRadius: 24,
    backgroundColor: Colors.primarySoft,
    borderWidth: 2,
    borderColor: Colors.primaryLight,
  },
  name: { ...Typography.title3, color: Colors.ink },
  meta: { ...Typography.footnote, color: Colors.textSecondary, marginTop: 2 },
  stats: {
    flexDirection: 'row',
    marginTop: Spacing.xl,
    paddingTop: Spacing.lg,
    borderTopWidth: 1.5,
    borderTopColor: Colors.separator,
    borderStyle: 'dashed',
  },
  stat: { flex: 1, alignItems: 'center' },
  statNum: { ...Typography.title2, color: Colors.primaryDark },
  statLabel: { ...Typography.caption1, color: Colors.textSecondary, marginTop: 2 },
  menu: { marginTop: Spacing.lg, paddingHorizontal: Spacing.lg, gap: 10 },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.md,
    paddingVertical: Spacing.md,
  },
  rowLabel: { flex: 1, ...Typography.body, color: Colors.ink, fontWeight: '700' },
  logout: { alignItems: 'center', backgroundColor: '#FFE8E1' },
  logoutText: { ...Typography.headline, color: Colors.danger },
});
