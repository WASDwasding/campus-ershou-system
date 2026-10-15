import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Dimensions,
  FlatList,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { router } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { ProductCard } from '@/components/ProductCard';
import { DotPaper, PaperCard, ScrapTitle, Sticker, StickerStrip, WashiTape } from '@/components/Scrapbook';
import { useAuth } from '@/context/AuthContext';
import { api } from '@/services/api';
import { campusNotices } from '@/data/notices';
import {
  Categories,
  Colors,
  PriceFilters,
  SortOptions,
  Spacing,
  Typography,
} from '@/constants/theme';
import type { Product, ProductCategory } from '@/types';

const GAP = 12;
const SCREEN_W = Dimensions.get('window').width;
const CARD_W = (SCREEN_W - Spacing.lg * 2 - GAP) / 2;

export default function HomeScreen() {
  const { user } = useAuth();
  const insets = useSafeAreaInsets();
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState<ProductCategory | 'all'>('all');
  const [sort, setSort] = useState<(typeof SortOptions)[number]['id']>('nearby');
  const [priceId, setPriceId] = useState<(typeof PriceFilters)[number]['id']>('all');
  const [products, setProducts] = useState<Product[]>([]);
  const [nearby, setNearby] = useState<Product[]>([]);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async () => {
    if (!user) return;
    const [list, near] = await Promise.all([
      api.getProducts({ category, query, dormBuilding: user.dormBuilding }),
      api.getNearbyProducts(user.dormBuilding),
    ]);
    setProducts(list);
    setNearby(near);
  }, [user, category, query]);

  useEffect(() => {
    load();
  }, [load]);

  const filtered = useMemo(() => {
    const pf = PriceFilters.find((p) => p.id === priceId) ?? PriceFilters[0];
    let list = products.filter((p) => p.price >= pf.min && p.price <= pf.max);
    if (sort === 'newest') {
      list = [...list].sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );
    } else if (sort === 'price_asc') {
      list = [...list].sort((a, b) => a.price - b.price);
    } else if (sort === 'price_desc') {
      list = [...list].sort((a, b) => b.price - a.price);
    }
    // nearby is default from API order
    return list;
  }, [products, priceId, sort]);

  const columns = useMemo(() => {
    const left: Product[] = [];
    const right: Product[] = [];
    filtered.forEach((p, i) => (i % 2 === 0 ? left : right).push(p));
    return { left, right };
  }, [filtered]);

  const onRefresh = async () => {
    setRefreshing(true);
    await load();
    setRefreshing(false);
  };

  return (
    <DotPaper style={{ paddingTop: insets.top }}>
      <ScrollView
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={Colors.primary} />
        }
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <View style={{ flex: 1 }}>
            <Text style={styles.brand}>Campus Trade</Text>
            <Text style={styles.hello}>
              嗨 {user?.name.split(' ')[0]}，今日份闲置手帐 ✿
            </Text>
            <View style={{ marginTop: 8, alignSelf: 'flex-start' }}>
              <Sticker text={user?.dormBuilding ?? ''} emoji="🏠" bg={Colors.primarySoft} color={Colors.primaryDark} />
            </View>
          </View>
          <Pressable onPress={() => router.push('/(tabs)/post')} style={styles.fabNote}>
            <Text style={styles.fabEmoji}>📝</Text>
          </Pressable>
        </View>

        <WashiTape label="cream blue · slow living" dotted color={Colors.tapeBlue} style={{ marginBottom: 4 }} />
        <StickerStrip />

        <View style={styles.searchBar}>
          <Ionicons name="search" size={18} color={Colors.primaryDark} />
          <TextInput
            style={styles.searchInput}
            placeholder="搜一搜宿舍好物…"
            placeholderTextColor={Colors.textTertiary}
            value={query}
            onChangeText={setQuery}
            returnKeyType="search"
            clearButtonMode="while-editing"
          />
        </View>

        <ScrapTitle title="校园贴贴板" />
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.noticeRow}>
          {campusNotices.map((n, i) => (
            <PaperCard
              key={n.id}
              rotate={i % 2 === 0 ? -1.5 : 1.5}
              tone={i === 0 ? 'creamBlue' : i === 1 ? 'pink' : 'yellow'}
              style={styles.noticeCard}
            >
              <Text style={styles.noticeEmoji}>{n.emoji}</Text>
              <Text style={styles.noticeTitle}>{n.title}</Text>
              <Text style={styles.noticeBody}>{n.body}</Text>
            </PaperCard>
          ))}
        </ScrollView>

        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.cats}>
          {Categories.map((c) => {
            const active = category === c.id;
            return (
              <Pressable
                key={c.id}
                onPress={() => setCategory(c.id as ProductCategory | 'all')}
                style={[styles.catChip, active && styles.catChipActive]}
              >
                <Text style={styles.catEmoji}>{c.emoji}</Text>
                <Text style={[styles.catText, active && styles.catTextActive]}>{c.name}</Text>
              </Pressable>
            );
          })}
        </ScrollView>

        <View style={styles.filterBlock}>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filterRow}>
            {SortOptions.map((s) => (
              <Pressable
                key={s.id}
                onPress={() => setSort(s.id)}
                style={[styles.filterChip, sort === s.id && styles.filterOn]}
              >
                <Text style={[styles.filterText, sort === s.id && styles.filterTextOn]}>{s.label}</Text>
              </Pressable>
            ))}
          </ScrollView>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filterRow}>
            {PriceFilters.map((p) => (
              <Pressable
                key={p.id}
                onPress={() => setPriceId(p.id)}
                style={[styles.filterChip, priceId === p.id && styles.filterPriceOn]}
              >
                <Text style={[styles.filterText, priceId === p.id && styles.filterTextOn]}>{p.label}</Text>
              </Pressable>
            ))}
          </ScrollView>
        </View>

        {nearby.length > 0 ? (
          <>
            <ScrapTitle title="同楼附近" action="优先见面" />
            <FlatList
              horizontal
              data={nearby}
              keyExtractor={(item) => item.id}
              showsHorizontalScrollIndicator={false}
              contentContainerStyle={{ paddingHorizontal: Spacing.lg, gap: Spacing.md }}
              renderItem={({ item, index }) => (
                <ProductCard
                  product={item}
                  width={150}
                  badge="同楼"
                  tilt={index % 2 === 0 ? -1.5 : 1.5}
                  onPress={() => router.push(`/product/${item.id}`)}
                />
              )}
            />
          </>
        ) : null}

        <ScrapTitle title="今日拼贴" action={`${filtered.length} 件`} />
        {filtered.length === 0 ? (
          <PaperCard style={styles.empty}>
            <Text style={styles.emptyEmoji}>🍡</Text>
            <Text style={styles.emptyTitle}>这一页还空着</Text>
            <Text style={styles.emptyBody}>换个分类或价格试试，或去发一张智能贴贴～</Text>
            <Pressable onPress={() => router.push('/(tabs)/post')}>
              <Text style={styles.emptyLink}>去发帖 →</Text>
            </Pressable>
          </PaperCard>
        ) : (
          <View style={styles.waterfall}>
            <View style={styles.col}>
              {columns.left.map((p, i) => (
                <ProductCard
                  key={p.id}
                  product={p}
                  width={CARD_W}
                  tilt={i % 2 === 0 ? -0.8 : 0.8}
                  onPress={() => router.push(`/product/${p.id}`)}
                />
              ))}
            </View>
            <View style={styles.col}>
              {columns.right.map((p, i) => (
                <ProductCard
                  key={p.id}
                  product={p}
                  width={CARD_W}
                  tilt={i % 2 === 0 ? 1 : -1}
                  onPress={() => router.push(`/product/${p.id}`)}
                />
              ))}
            </View>
          </View>
        )}
        <View style={{ height: 28 }} />
      </ScrollView>
    </DotPaper>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: 'row',
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.md,
    paddingBottom: Spacing.sm,
    gap: Spacing.md,
  },
  brand: { ...Typography.title2, color: Colors.primaryDark },
  hello: { ...Typography.subhead, color: Colors.textSecondary, marginTop: 4 },
  fabNote: {
    width: 48,
    height: 48,
    borderRadius: 16,
    backgroundColor: Colors.accentYellow,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1.5,
    borderColor: '#E6C96A',
    transform: [{ rotate: '4deg' }],
  },
  fabEmoji: { fontSize: 22 },
  searchBar: {
    flexDirection: 'row',
    alignItems: 'center',
    marginHorizontal: Spacing.lg,
    marginTop: Spacing.md,
    backgroundColor: Colors.paper,
    borderRadius: 18,
    paddingHorizontal: Spacing.lg,
    height: 46,
    borderWidth: 1.5,
    borderColor: Colors.border,
    borderStyle: 'dashed',
    gap: Spacing.sm,
  },
  searchInput: { flex: 1, ...Typography.body, color: Colors.ink },
  noticeRow: { paddingHorizontal: Spacing.lg, gap: Spacing.md, paddingBottom: 4 },
  noticeCard: { width: 200, padding: Spacing.md },
  noticeEmoji: { fontSize: 22, marginBottom: 6 },
  noticeTitle: { ...Typography.headline, color: Colors.ink },
  noticeBody: { ...Typography.caption1, color: Colors.textSecondary, marginTop: 6, lineHeight: 16 },
  cats: { paddingHorizontal: Spacing.lg, gap: Spacing.sm, paddingTop: Spacing.md },
  catChip: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 16,
    backgroundColor: Colors.paper,
    borderWidth: 1.5,
    borderColor: Colors.border,
  },
  catChipActive: {
    backgroundColor: Colors.primary,
    borderColor: Colors.primaryDark,
  },
  catEmoji: { fontSize: 13 },
  catText: { ...Typography.footnote, color: Colors.ink, fontWeight: '700' },
  catTextActive: { color: '#fff' },
  filterBlock: { marginTop: Spacing.md, gap: 8 },
  filterRow: { paddingHorizontal: Spacing.lg, gap: 8 },
  filterChip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
    backgroundColor: 'rgba(255,254,249,0.8)',
    borderWidth: 1,
    borderColor: Colors.separator,
  },
  filterOn: { backgroundColor: Colors.primarySoft, borderColor: Colors.primary },
  filterPriceOn: { backgroundColor: '#FFE8E1', borderColor: Colors.accent },
  filterText: { ...Typography.caption1, color: Colors.textSecondary },
  filterTextOn: { color: Colors.ink, fontWeight: '800' },
  waterfall: {
    flexDirection: 'row',
    paddingHorizontal: Spacing.lg,
    gap: GAP,
  },
  col: { flex: 1 },
  empty: { marginHorizontal: Spacing.lg, alignItems: 'center', paddingVertical: 28 },
  emptyEmoji: { fontSize: 36 },
  emptyTitle: { ...Typography.title3, color: Colors.ink, marginTop: 8 },
  emptyBody: { ...Typography.subhead, color: Colors.textSecondary, textAlign: 'center', marginTop: 6 },
  emptyLink: { ...Typography.headline, color: Colors.primaryDark, marginTop: 14 },
});
