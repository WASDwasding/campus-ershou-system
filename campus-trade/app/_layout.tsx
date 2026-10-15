import React from 'react';
import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { AuthProvider } from '@/context/AuthContext';
import { AppProvider } from '@/context/AppContext';
import { Colors } from '@/constants/theme';

export default function RootLayout() {
  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <AuthProvider>
          <AppProvider>
            <StatusBar style="dark" />
            <Stack
              screenOptions={{
                headerStyle: { backgroundColor: Colors.paper },
                headerTintColor: Colors.primaryDark,
                headerTitleStyle: { color: Colors.ink, fontWeight: '700' },
                headerShadowVisible: false,
                contentStyle: { backgroundColor: Colors.background },
              }}
            >
              <Stack.Screen name="index" options={{ headerShown: false }} />
              <Stack.Screen name="(auth)/login" options={{ headerShown: false }} />
              <Stack.Screen name="(auth)/register" options={{ title: '学号认证' }} />
              <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
              <Stack.Screen name="product/[id]" options={{ title: '好物详情' }} />
              <Stack.Screen name="chat/[id]" options={{ title: '私聊' }} />
              <Stack.Screen name="post/smart" options={{ title: '智能贴贴', presentation: 'modal' }} />
              <Stack.Screen name="post/manual" options={{ title: '手动发帖' }} />
              <Stack.Screen name="post/review" options={{ title: '确认发布' }} />
              <Stack.Screen name="orders/index" options={{ title: '订单手帐' }} />
              <Stack.Screen name="orders/[id]" options={{ title: '订单详情' }} />
              <Stack.Screen name="favorites" options={{ title: '收藏夹' }} />
              <Stack.Screen name="credit" options={{ title: '校园信用' }} />
              <Stack.Screen name="rate/[orderId]" options={{ title: '互评小贴纸', presentation: 'modal' }} />
              <Stack.Screen name="my-posts" options={{ title: '我的贴贴' }} />
            </Stack>
          </AppProvider>
        </AuthProvider>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}
