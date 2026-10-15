export const Colors = {
  // 淡奶油蓝主色 — from scrapbook refs
  primary: '#8EC8DE',
  primaryDark: '#5A9BB5',
  primaryLight: '#B8DCEF',
  primarySoft: '#D9EBF4',
  secondary: '#C9B8E0', // soft lilac sticker
  accent: '#F0B8B0', // soft pink sticker
  accentYellow: '#F3E4A0',
  accentLilac: '#D9CDEB',
  accentPink: '#F5D0D0',
  danger: '#E88B84',
  success: '#8FCB9B',
  warning: '#E8B86D',
  // Pale cream-blue page
  background: '#E6F1F7',
  paper: '#FFFCF8',
  paperAlt: '#F2F7FB',
  kraft: '#EDE4D4',
  surface: '#FFFCF8',
  creamBlue: '#C5DFF0',
  creamBlueDeep: '#A8D0E6',
  text: '#3D5270',
  textSecondary: '#6B7F96',
  textTertiary: '#9AADC0',
  border: '#BFD6E4',
  separator: '#D5E6EF',
  tabInactive: '#8FA8B8',
  overlay: 'rgba(61, 82, 112, 0.4)',
  flaw: '#E88B84',
  creditGold: '#D4A84B',
  ink: '#3D5270',
  polka: '#3D5270',
  tapeBlue: 'rgba(142, 200, 222, 0.65)',
  tapePeach: 'rgba(240, 184, 176, 0.6)',
  tapeMint: 'rgba(180, 220, 210, 0.6)',
  tapeYellow: 'rgba(243, 228, 160, 0.75)',
  doodle: '#A8C5D6',
};

export const Spacing = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  xxl: 24,
  xxxl: 32,
};

export const Radius = {
  sm: 10,
  md: 14,
  lg: 18,
  xl: 24,
  full: 999,
};

export const Typography = {
  largeTitle: { fontSize: 32, fontWeight: '800' as const, letterSpacing: 0.4 },
  title1: { fontSize: 26, fontWeight: '800' as const, letterSpacing: 0.3 },
  title2: { fontSize: 22, fontWeight: '700' as const, letterSpacing: 0.25 },
  title3: { fontSize: 19, fontWeight: '700' as const, letterSpacing: 0.2 },
  headline: { fontSize: 16, fontWeight: '700' as const, letterSpacing: 0.15 },
  body: { fontSize: 16, fontWeight: '500' as const, letterSpacing: 0.12 },
  callout: { fontSize: 15, fontWeight: '500' as const, letterSpacing: 0.12 },
  subhead: { fontSize: 14, fontWeight: '500' as const, letterSpacing: 0.1 },
  footnote: { fontSize: 13, fontWeight: '500' as const, letterSpacing: 0.08 },
  caption1: { fontSize: 12, fontWeight: '600' as const, letterSpacing: 0.08 },
  caption2: { fontSize: 11, fontWeight: '600' as const, letterSpacing: 0.06 },
};

export const Categories = [
  { id: 'all', name: '全部', icon: 'apps-outline', emoji: '✨' },
  { id: 'electronics', name: '数码', icon: 'laptop-outline', emoji: '💻' },
  { id: 'books', name: '书籍', icon: 'book-outline', emoji: '📚' },
  { id: 'daily', name: '日用', icon: 'basket-outline', emoji: '🧺' },
  { id: 'fashion', name: '穿搭', icon: 'shirt-outline', emoji: '👕' },
  { id: 'sports', name: '运动', icon: 'bicycle-outline', emoji: '🚲' },
  { id: 'beauty', name: '美妆', icon: 'flower-outline', emoji: '🎀' },
  { id: 'other', name: '其他', icon: 'ellipsis-horizontal', emoji: '🎈' },
] as const;

export const ConditionLabels = {
  like_new: '几乎全新',
  '90_new': '九成新',
  '80_new': '八成新',
  '70_new': '七成新',
} as const;

export const DormBuildings = [
  'Maple Hall A',
  'Maple Hall B',
  'Willow Hall C',
  'Cedar Hall D',
  'Pine Hall E',
  'Lakeview Hall F',
  'East Court G',
  'West Court H',
] as const;

export const SortOptions = [
  { id: 'nearby', label: '离我最近' },
  { id: 'newest', label: '最新发布' },
  { id: 'price_asc', label: '价格↑' },
  { id: 'price_desc', label: '价格↓' },
] as const;

export const PriceFilters = [
  { id: 'all', label: '不限价', min: 0, max: Infinity },
  { id: '0-50', label: '¥50内', min: 0, max: 50 },
  { id: '50-200', label: '¥50-200', min: 50, max: 200 },
  { id: '200-500', label: '¥200-500', min: 200, max: 500 },
  { id: '500+', label: '¥500+', min: 500, max: Infinity },
] as const;

/** Local sticker assets from user references */
export const StickerAssets = {
  collage: require('../assets/stickers/collage-doodles.png'),
  puppies: require('../assets/stickers/puppy-stickers.jpg'),
  animals: require('../assets/stickers/animal-faces.png'),
} as const;
