import React, { useEffect, useState } from 'react';
import {
  Alert,
  Dimensions,
  NativeScrollEvent,
  NativeSyntheticEvent,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { Image } from 'expo-image';
import { router, useLocalSearchParams, Stack } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { Button } from '@/components/Button';
import { CreditBadge } from '@/components/CreditBadge';
import { FlawOverlay } from '@/components/FlawOverlay';
import { PaperCard, Sticker, WashiTape } from '@/components/Scrapbook';
import { useAuth } from '@/context/AuthContext';
import { useAppState } from '@/context/AppContext';
import { api } from '@/services/api';
import { Colors, ConditionLabels, Categories, Spacing, Typography } from '@/constants/theme';
import type { Product, User } from '@/types';

const W = Dimensions.get('window').width;
const IMG_H = W * 1.0;

export default function ProductDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { user } = useAuth();
  const { bumpFavorites } = useAppState();
  const insets = useSafeAreaInsets();
  const [product, setProduct] = useState<Product | null>(null);
  const [seller, setSeller] = useState<User | null>(null);
  const [index, setIndex] = useState(0);
  const [fav, setFav] = useState(false);
  const [busy, setBusy] = useState(false);
  const [showOffer, setShowOffer] = useState(false);
  const [offerPrice, setOfferPrice] = useState('');

  useEffect(() => {
    (async () => {
      const p = await api.getProduct(id);
      if (!p) return;
      setProduct(p);
      setFav(api.isFavorite(p.id));
      setSeller((await api.getUser(p.sellerId)) ?? null);
    })();
  }, [id]);

  const onScroll = (e: NativeSyntheticEvent<NativeScrollEvent>) => {
    const i = Math.round(e.nativeEvent.contentOffset.x / W);
    setIndex(i);
  };

  const contact = async () => {
    if (!product || !user) return;
    const conv = await api.openOrCreateConversation(product.sellerId, product.id);
    router.push(`/chat/${conv.id}`);
  };

  const reserve = async () => {
    if (!product || !user) return;
    if (product.sellerId === user.id) {
      Alert.alert('不可以哦', '不能预定自己的贴贴～');
      return;
    }
    setBusy(true);
    try {
      const order = await api.reserveProduct(product.id, user.id);
      Alert.alert('预定成功 ✿', '记得约个校园见面点完成交易呀', [
        { text: '看订单', onPress: () => router.push(`/orders/${order.id}`) },
        { text: '好的' },
      ]);
      setProduct({ ...product, status: 'reserved' });
    } catch (e) {
      Alert.alert('失败了', e instanceof Error ? e.message : '再试一次');
    } finally {
      setBusy(false);
    }
  };

  const sendOffer = async () => {
    if (!product || !user) return;
    const price = Number(offerPrice);
    if (!price || price <= 0) {
      Alert.alert('填个价吧', '议价金额要大于 0');
      return;
    }
    const conv = await api.openOrCreateConversation(product.sellerId, product.id);
    await api.sendMessage(
      conv.id,
      user.id,
      `💬 想议价：¥${price}（原价 ¥${product.price}）· 关于「${product.title}」`,
    );
    setShowOffer(false);
    Alert.alert('议价已送出', '已发到私聊，去聊聊吧', [
      { text: '去聊天', onPress: () => router.push(`/chat/${conv.id}`) },
      { text: '稍后' },
    ]);
  };

  const toggleFav = async () => {
    if (!product) return;
    const on = await api.toggleFavorite(product.id);
    setFav(on);
    bumpFavorites();
  };

  if (!product) {
    return <View style={{ flex: 1, backgroundColor: Colors.background }} />;
  }

  return (
    <View style={{ flex: 1, backgroundColor: Colors.background }}>
      <Stack.Screen
        options={{
          title: '好物详情',
          headerRight: () => (
            <Pressable onPress={toggleFav} hitSlop={10}>
              <Ionicons
                name={fav ? 'heart' : 'heart-outline'}
                size={24}
                color={fav ? Colors.danger : Colors.primary}
              />
            </Pressable>
          ),
        }}
      />
      <ScrollView contentContainerStyle={{ paddingBottom: 120 + insets.bottom }}>
        <View>
          <ScrollView
            horizontal
            pagingEnabled
            showsHorizontalScrollIndicator={false}
            onScroll={onScroll}
            scrollEventThrottle={16}
          >
            {product.images.map((uri, i) => (
              <View key={uri + i} style={{ width: W, height: IMG_H }}>
                <Image source={{ uri }} style={{ width: W, height: IMG_H }} contentFit="cover" />
                {i === 0 ? <FlawOverlay flaws={product.flaws} width={W} height={IMG_H} /> : null}
              </View>
            ))}
          </ScrollView>
          <WashiTape
            label={`photo ${index + 1}/${product.images.length}`}
            color={Colors.tapeYellow}
            style={styles.photoTape}
          />
          {product.flaws.length > 0 ? (
            <View style={styles.flawHint}>
              <Sticker text={`${product.flaws.length} 处瑕疵标记`} emoji="🔍" bg="#FFE8E1" color={Colors.danger} />
            </View>
          ) : null}
        </View>

        <PaperCard style={styles.panel} rotate={-0.4}>
          <Text style={styles.price}>¥{product.price}</Text>
          <Text style={styles.title}>{product.title}</Text>
          <View style={styles.chips}>
            <Sticker text={ConditionLabels[product.condition]} emoji="✨" bg={Colors.accentYellow} />
            <Sticker
              text={Categories.find((c) => c.id === product.category)?.name ?? product.category}
              emoji="🏷️"
              bg={Colors.primarySoft}
              color={Colors.primaryDark}
            />
          </View>
          <Text style={styles.desc}>{product.description}</Text>
        </PaperCard>

        <PaperCard style={styles.seller} rotate={0.6}>
          <Image source={{ uri: seller?.avatar }} style={styles.sellerAvatar} />
          <View style={{ flex: 1 }}>
            <Text style={styles.sellerName}>{seller?.name}</Text>
            <Text style={styles.sellerDorm}>🏠 {product.dormBuilding}</Text>
          </View>
          {seller ? <CreditBadge score={seller.creditScore} /> : null}
        </PaperCard>

        {product.flaws.length > 0 ? (
          <PaperCard style={styles.panel}>
            <Text style={styles.sectionTitle}>智能检出的小瑕疵</Text>
            {product.flaws.map((f) => (
              <View key={f.id} style={styles.flawRow}>
                <View style={styles.flawDot} />
                <Text style={styles.flawLabel}>{f.label}</Text>
              </View>
            ))}
          </PaperCard>
        ) : null}

        {showOffer ? (
          <PaperCard style={styles.offerBox} tone="alt">
            <Text style={styles.sectionTitle}>友好议价 ✿</Text>
            <TextInput
              style={styles.offerInput}
              keyboardType="decimal-pad"
              placeholder={`原价 ¥${product.price}，你想出多少？`}
              placeholderTextColor={Colors.textTertiary}
              value={offerPrice}
              onChangeText={setOfferPrice}
            />
            <View style={styles.offerActions}>
              <Button title="取消" variant="ghost" style={{ flex: 1 }} onPress={() => setShowOffer(false)} />
              <Button title="送出议价" style={{ flex: 1 }} onPress={sendOffer} />
            </View>
          </PaperCard>
        ) : null}
      </ScrollView>

      <View style={[styles.bar, { paddingBottom: Math.max(insets.bottom, 12) }]}>
        <Pressable style={styles.offerBtn} onPress={() => setShowOffer(true)}>
          <Text style={styles.offerBtnText}>议价</Text>
        </Pressable>
        <Button title="联系卖家" variant="secondary" style={{ flex: 1 }} onPress={contact} />
        <Button
          title="预定购买"
          style={{ flex: 1.1 }}
          loading={busy}
          disabled={product.status !== 'available'}
          onPress={reserve}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  photoTape: { position: 'absolute', bottom: 16, alignSelf: 'center' },
  flawHint: { position: 'absolute', top: Spacing.lg, left: Spacing.lg },
  panel: { marginHorizontal: Spacing.lg, marginTop: Spacing.md },
  price: { ...Typography.title1, color: Colors.primaryDark },
  title: { ...Typography.title3, color: Colors.ink, marginTop: Spacing.sm },
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: Spacing.sm, marginTop: Spacing.md },
  desc: { ...Typography.callout, color: Colors.textSecondary, marginTop: Spacing.lg, lineHeight: 22 },
  seller: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.md,
    marginHorizontal: Spacing.lg,
    marginTop: Spacing.md,
    paddingVertical: Spacing.md,
  },
  sellerAvatar: {
    width: 48,
    height: 48,
    borderRadius: 16,
    backgroundColor: Colors.primarySoft,
  },
  sellerName: { ...Typography.headline, color: Colors.ink },
  sellerDorm: { ...Typography.footnote, color: Colors.textSecondary, marginTop: 2 },
  sectionTitle: { ...Typography.headline, color: Colors.ink, marginBottom: Spacing.md },
  flawRow: { flexDirection: 'row', alignItems: 'center', gap: Spacing.sm, marginBottom: Spacing.sm },
  flawDot: { width: 8, height: 8, borderRadius: 4, backgroundColor: Colors.flaw },
  flawLabel: { ...Typography.subhead, color: Colors.ink },
  offerBox: { marginHorizontal: Spacing.lg, marginTop: Spacing.md },
  offerInput: {
    ...Typography.body,
    borderWidth: 1.5,
    borderColor: Colors.border,
    borderStyle: 'dashed',
    borderRadius: 14,
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.md,
    color: Colors.ink,
    backgroundColor: Colors.paper,
    marginBottom: Spacing.md,
  },
  offerActions: { flexDirection: 'row', gap: Spacing.md },
  bar: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    flexDirection: 'row',
    gap: Spacing.sm,
    paddingHorizontal: Spacing.md,
    paddingTop: Spacing.md,
    backgroundColor: Colors.paper,
    borderTopWidth: 1.5,
    borderTopColor: Colors.separator,
    alignItems: 'center',
  },
  offerBtn: {
    paddingHorizontal: 12,
    paddingVertical: 12,
    borderRadius: 14,
    backgroundColor: Colors.accentYellow,
    borderWidth: 1.5,
    borderColor: '#E6C96A',
  },
  offerBtnText: { ...Typography.footnote, color: Colors.ink, fontWeight: '800' },
});
