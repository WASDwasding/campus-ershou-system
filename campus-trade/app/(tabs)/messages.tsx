import React, { useCallback, useState } from 'react';
import {
  FlatList,
  Image,
  Pressable,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { api } from '@/services/api';
import { DotPaper, PaperCard, Sticker } from '@/components/Scrapbook';
import { Colors, Spacing, Typography } from '@/constants/theme';
import type { Conversation, User } from '@/types';

type Row = Conversation & { peer?: User };

export default function MessagesScreen() {
  const [rows, setRows] = useState<Row[]>([]);

  const load = useCallback(async () => {
    const convs = await api.getConversations();
    const withPeers = await Promise.all(
      convs.map(async (c) => ({
        ...c,
        peer: await api.getUser(c.peerId),
      })),
    );
    setRows(withPeers);
  }, []);

  useFocusEffect(
    useCallback(() => {
      load();
    }, [load]),
  );

  return (
    <DotPaper>
      <View style={styles.banner}>
        <Sticker text="仅校园私聊" emoji="🛡️" bg={Colors.primarySoft} color={Colors.primaryDark} />
        <Text style={styles.bannerText}>微信 / 手机号会被自动贴上屏蔽条</Text>
      </View>
      <FlatList
        data={rows}
        keyExtractor={(item) => item.id}
        contentContainerStyle={{ padding: Spacing.lg, paddingBottom: 32, gap: 12 }}
        ListEmptyComponent={
          <PaperCard style={styles.empty}>
            <Text style={{ fontSize: 40 }}>💌</Text>
            <Text style={styles.emptyTitle}>信箱还空着</Text>
            <Text style={styles.emptyBody}>去商品页点「联系卖家」，开启第一段闲聊～</Text>
          </PaperCard>
        }
        renderItem={({ item, index }) => (
          <Pressable onPress={() => router.push(`/chat/${item.id}`)}>
            <PaperCard rotate={index % 2 === 0 ? -0.6 : 0.6} style={styles.row}>
              <Image source={{ uri: item.peer?.avatar }} style={styles.avatar} />
              <View style={styles.mid}>
                <Text style={styles.name}>{item.peer?.name ?? '同学'}</Text>
                <Text numberOfLines={1} style={styles.preview}>
                  {item.lastMessage || '开始聊天吧'}
                </Text>
              </View>
              <View style={styles.right}>
                <Text style={styles.time}>
                  {new Date(item.lastAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </Text>
                {item.unread > 0 ? (
                  <View style={styles.badge}>
                    <Text style={styles.badgeText}>{item.unread}</Text>
                  </View>
                ) : null}
              </View>
            </PaperCard>
          </Pressable>
        )}
      />
    </DotPaper>
  );
}

const styles = StyleSheet.create({
  banner: {
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.md,
    gap: 8,
    alignItems: 'flex-start',
  },
  bannerText: { ...Typography.caption1, color: Colors.textSecondary },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.md,
    paddingVertical: Spacing.md,
  },
  avatar: {
    width: 52,
    height: 52,
    borderRadius: 18,
    backgroundColor: Colors.primarySoft,
    borderWidth: 2,
    borderColor: Colors.paper,
  },
  mid: { flex: 1 },
  name: { ...Typography.headline, color: Colors.ink },
  preview: { ...Typography.subhead, color: Colors.textSecondary, marginTop: 2 },
  right: { alignItems: 'flex-end', gap: 6 },
  time: { ...Typography.caption2, color: Colors.textTertiary },
  badge: {
    minWidth: 20,
    height: 20,
    borderRadius: 10,
    backgroundColor: Colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 6,
  },
  badgeText: { color: '#fff', fontSize: 11, fontWeight: '800' },
  empty: { alignItems: 'center', marginTop: 40, paddingVertical: 36 },
  emptyTitle: { ...Typography.title3, color: Colors.ink, marginTop: 8 },
  emptyBody: {
    ...Typography.subhead,
    color: Colors.textSecondary,
    textAlign: 'center',
    marginTop: 6,
  },
});
