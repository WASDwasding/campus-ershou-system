import React, { useEffect, useRef, useState } from 'react';
import {
  FlatList,
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  View,
  Pressable,
} from 'react-native';
import { Stack, useLocalSearchParams } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { api } from '@/services/api';
import { useAuth } from '@/context/AuthContext';
import { Colors, Radius, Spacing, Typography } from '@/constants/theme';
import type { ChatMessage, Conversation, User } from '@/types';

export default function ChatScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const insets = useSafeAreaInsets();
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [peer, setPeer] = useState<User | null>(null);
  const [text, setText] = useState('');
  const [sending, setSending] = useState(false);
  const listRef = useRef<FlatList>(null);

  useEffect(() => {
    (async () => {
      const convs = await api.getConversations();
      const conv = convs.find((c) => c.id === id) as Conversation | undefined;
      if (conv) setPeer((await api.getUser(conv.peerId)) ?? null);
      setMessages(await api.getMessages(id));
    })();
  }, [id]);

  const send = async () => {
    if (!text.trim() || !user) return;
    setSending(true);
    try {
      const msg = await api.sendMessage(id, user.id, text.trim());
      setMessages((prev) => [...prev, msg]);
      setText('');
      setTimeout(() => listRef.current?.scrollToEnd({ animated: true }), 50);
    } finally {
      setSending(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={{ flex: 1, backgroundColor: Colors.background }}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      keyboardVerticalOffset={88}
    >
      <Stack.Screen options={{ title: peer?.name ?? 'Chat' }} />
      <View style={styles.banner}>
        <Ionicons name="shield-checkmark" size={14} color={Colors.primary} />
        <Text style={styles.bannerText}>
          微信 / 手机号会被自动挡住哦，安心校园聊
        </Text>
      </View>
      <FlatList
        ref={listRef}
        data={messages}
        keyExtractor={(m) => m.id}
        contentContainerStyle={{ padding: Spacing.lg, paddingBottom: Spacing.lg }}
        onContentSizeChange={() => listRef.current?.scrollToEnd({ animated: false })}
        renderItem={({ item }) => {
          const mine = item.senderId === user?.id;
          return (
            <View style={[styles.bubbleWrap, mine && styles.bubbleWrapMine]}>
              <View style={[styles.bubble, mine ? styles.mine : styles.theirs, item.blocked && styles.blocked]}>
                <Text style={[styles.msg, mine && styles.msgMine]}>{item.text}</Text>
                {item.blocked ? (
                  <Text style={styles.blockedTag}>已贴上屏蔽条</Text>
                ) : null}
              </View>
            </View>
          );
        }}
      />
      <View style={[styles.composer, { paddingBottom: Math.max(insets.bottom, 8) }]}>
        <TextInput
          style={styles.input}
          value={text}
          onChangeText={setText}
          placeholder="说点什么（别留微信/手机号）"
          placeholderTextColor={Colors.textTertiary}
          multiline
        />
        <Pressable
          onPress={send}
          disabled={sending || !text.trim()}
          style={[styles.send, (!text.trim() || sending) && { opacity: 0.4 }]}
        >
          <Ionicons name="send" size={20} color="#fff" />
        </Pressable>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  banner: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.sm,
    backgroundColor: Colors.primarySoft,
  },
  bannerText: { ...Typography.caption1, color: Colors.primaryDark, flex: 1 },
  bubbleWrap: { marginBottom: Spacing.md, alignItems: 'flex-start' },
  bubbleWrapMine: { alignItems: 'flex-end' },
  bubble: {
    maxWidth: '78%',
    borderRadius: Radius.lg,
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
  },
  mine: { backgroundColor: Colors.primary, borderBottomRightRadius: 4 },
  theirs: { backgroundColor: Colors.surface, borderBottomLeftRadius: 4 },
  blocked: { borderWidth: 1, borderColor: Colors.warning },
  msg: { ...Typography.callout, color: Colors.text },
  msgMine: { color: '#fff' },
  blockedTag: {
    ...Typography.caption2,
    color: Colors.warning,
    marginTop: 4,
  },
  composer: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    gap: Spacing.sm,
    paddingHorizontal: Spacing.md,
    paddingTop: Spacing.sm,
    backgroundColor: Colors.surface,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: Colors.separator,
  },
  input: {
    flex: 1,
    minHeight: 40,
    maxHeight: 100,
    ...Typography.body,
    backgroundColor: Colors.background,
    borderRadius: Radius.md,
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    color: Colors.text,
  },
  send: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: Colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
