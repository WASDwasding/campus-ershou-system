import React from 'react';
import { StyleSheet, Text, View, Pressable } from 'react-native';
import { Tabs, router } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import * as Haptics from 'expo-haptics';
import { Colors, Typography } from '@/constants/theme';

function PostTabButton() {
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel="发帖"
      onPress={() => {
        Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);
        router.push('/(tabs)/post');
      }}
      style={styles.postBtnWrap}
    >
      <View style={styles.postBtn}>
        <Text style={{ fontSize: 22 }}>📷</Text>
      </View>
      <Text style={styles.postLabel}>发帖</Text>
    </Pressable>
  );
}

export default function TabsLayout() {
  return (
    <Tabs
      screenOptions={{
        headerStyle: { backgroundColor: Colors.paper },
        headerTitleStyle: { ...Typography.headline, color: Colors.ink },
        headerShadowVisible: false,
        tabBarActiveTintColor: Colors.primaryDark,
        tabBarInactiveTintColor: Colors.tabInactive,
        tabBarStyle: {
          backgroundColor: Colors.paper,
          borderTopColor: Colors.separator,
          borderTopWidth: 1.5,
          height: 88,
          paddingTop: 6,
        },
        tabBarLabelStyle: { ...Typography.caption2, fontWeight: '700' },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: '首页',
          headerShown: false,
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="home" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="post"
        options={{
          title: '发帖',
          headerShown: false,
          tabBarButton: () => <PostTabButton />,
        }}
      />
      <Tabs.Screen
        name="messages"
        options={{
          title: '消息',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="chatbubbles" size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="profile"
        options={{
          title: '我的',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="person" size={size} color={color} />
          ),
        }}
      />
    </Tabs>
  );
}

const styles = StyleSheet.create({
  postBtnWrap: {
    top: -14,
    alignItems: 'center',
    justifyContent: 'center',
    width: 76,
  },
  postBtn: {
    width: 58,
    height: 58,
    borderRadius: 20,
    backgroundColor: Colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: Colors.primaryDark,
    borderStyle: 'dashed',
    shadowColor: Colors.primaryDark,
    shadowOpacity: 0.28,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    transform: [{ rotate: '-3deg' }],
  },
  postLabel: {
    ...Typography.caption2,
    color: Colors.primaryDark,
    fontWeight: '800',
    marginTop: 4,
  },
});
