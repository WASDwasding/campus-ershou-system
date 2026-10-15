import React from 'react';
import {
  Dimensions,
  Image,
  ImageSourcePropType,
  StyleSheet,
  Text,
  View,
  type ViewStyle,
  type StyleProp,
} from 'react-native';
import { Colors, Radius, Spacing, StickerAssets, Typography } from '@/constants/theme';

const { width: SW, height: SH } = Dimensions.get('window');

/** Pale cream-blue page with polka stationery edge + soft doodles */
export function DotPaper({
  children,
  style,
  framed = true,
}: {
  children?: React.ReactNode;
  style?: StyleProp<ViewStyle>;
  framed?: boolean;
}) {
  const edgeDots = [];
  const step = 18;
  for (let x = 10; x < SW - 10; x += step) {
    edgeDots.push({ key: `t${x}`, left: x, top: 8 });
    edgeDots.push({ key: `b${x}`, left: x, top: SH - 100 });
  }
  for (let y = 20; y < SH - 120; y += step) {
    edgeDots.push({ key: `l${y}`, left: 8, top: y });
    edgeDots.push({ key: `r${y}`, left: SW - 16, top: y });
  }

  return (
    <View style={[styles.dotRoot, style]}>
      {framed ? (
        <View pointerEvents="none" style={styles.polkaFrame}>
          {edgeDots.map((d) => (
            <View key={d.key} style={[styles.polka, { left: d.left, top: d.top }]} />
          ))}
        </View>
      ) : null}
      <View pointerEvents="none" style={styles.dotLayer}>
        {Array.from({ length: 28 }).map((_, i) => (
          <View
            key={i}
            style={[
              styles.dot,
              {
                left: ((i % 7) * SW) / 7 + 24,
                top: Math.floor(i / 7) * 90 + 80,
                opacity: 0.22 + ((i * 7) % 5) * 0.05,
              },
            ]}
          />
        ))}
        {Array.from({ length: 8 }).map((_, i) => (
          <Text
            key={`s${i}`}
            style={[
              styles.starDoodle,
              {
                left: 30 + ((i * 47) % (SW - 60)),
                top: 100 + ((i * 73) % 420),
                opacity: 0.18,
                transform: [{ rotate: `${(i % 5) * 12}deg` }],
              },
            ]}
          >
            ✦
          </Text>
        ))}
      </View>
      <View style={styles.content}>{children}</View>
    </View>
  );
}

export function PaperCard({
  children,
  style,
  rotate,
  tone = 'paper',
}: {
  children: React.ReactNode;
  style?: StyleProp<ViewStyle>;
  rotate?: number;
  tone?: 'paper' | 'alt' | 'kraft' | 'creamBlue' | 'pink' | 'yellow' | 'lilac';
}) {
  const bgMap = {
    paper: Colors.paper,
    alt: Colors.paperAlt,
    kraft: Colors.kraft,
    creamBlue: Colors.creamBlue,
    pink: Colors.accentPink,
    yellow: Colors.accentYellow,
    lilac: Colors.accentLilac,
  };
  return (
    <View
      style={[
        styles.paper,
        {
          backgroundColor: bgMap[tone],
          transform: rotate ? [{ rotate: `${rotate}deg` }] : undefined,
        },
        style,
      ]}
    >
      {children}
    </View>
  );
}

export function WashiTape({
  label,
  color = Colors.tapeBlue,
  dotted,
  style,
}: {
  label?: string;
  color?: string;
  dotted?: boolean;
  style?: StyleProp<ViewStyle>;
}) {
  return (
    <View style={[styles.tape, { backgroundColor: color }, style]}>
      {dotted
        ? Array.from({ length: 6 }).map((_, i) => (
            <View key={i} style={[styles.tapeDot, { left: 10 + i * 14 }]} />
          ))
        : null}
      {label ? <Text style={styles.tapeText}>{label}</Text> : null}
    </View>
  );
}

export function Sticker({
  text,
  emoji,
  bg = Colors.accentYellow,
  color = Colors.ink,
}: {
  text: string;
  emoji?: string;
  bg?: string;
  color?: string;
}) {
  return (
    <View style={styles.stickerHalo}>
      <View style={[styles.sticker, { backgroundColor: bg }]}>
        {emoji ? <Text style={styles.stickerEmoji}>{emoji}</Text> : null}
        <Text style={[styles.stickerText, { color }]}>{text}</Text>
      </View>
    </View>
  );
}

export function ImageSticker({
  source,
  size = 64,
  rotate = -4,
  style,
}: {
  source: ImageSourcePropType;
  size?: number;
  rotate?: number;
  style?: StyleProp<ViewStyle>;
}) {
  return (
    <View
      style={[
        styles.imageSticker,
        {
          width: size + 10,
          height: size + 10,
          transform: [{ rotate: `${rotate}deg` }],
        },
        style,
      ]}
    >
      <Image source={source} style={{ width: size, height: size, borderRadius: 12 }} resizeMode="cover" />
    </View>
  );
}

export function StickerStrip() {
  return (
    <View style={styles.strip}>
      <ImageSticker source={StickerAssets.collage} size={70} rotate={-3} />
      <ImageSticker source={StickerAssets.animals} size={70} rotate={2} />
      <ImageSticker source={StickerAssets.puppies} size={70} rotate={-2} />
      <WashiTape label="cream blue ✿" dotted color={Colors.tapeBlue} style={{ marginLeft: 4 }} />
    </View>
  );
}

export function ScrapTitle({
  title,
  action,
  onAction,
}: {
  title: string;
  action?: string;
  onAction?: () => void;
}) {
  return (
    <View style={styles.titleRow}>
      <View>
        <Text style={styles.title}>{title}</Text>
        <View style={styles.underline} />
      </View>
      {action ? (
        <Text onPress={onAction} style={styles.action}>
          {action}
        </Text>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  dotRoot: {
    flex: 1,
    backgroundColor: Colors.background,
    overflow: 'hidden',
  },
  content: { flex: 1 },
  polkaFrame: {
    ...StyleSheet.absoluteFill,
  },
  polka: {
    position: 'absolute',
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: Colors.polka,
    opacity: 0.2,
  },
  dotLayer: {
    ...StyleSheet.absoluteFill,
  },
  dot: {
    position: 'absolute',
    width: 4,
    height: 4,
    borderRadius: 2,
    backgroundColor: Colors.doodle,
  },
  starDoodle: {
    position: 'absolute',
    fontSize: 10,
    color: Colors.primaryDark,
  },
  paper: {
    backgroundColor: Colors.paper,
    borderRadius: Radius.lg,
    borderWidth: 2,
    borderColor: Colors.border,
    padding: Spacing.lg,
    shadowColor: Colors.primaryDark,
    shadowOpacity: 0.1,
    shadowRadius: 8,
    shadowOffset: { width: 2, height: 3 },
  },
  tape: {
    alignSelf: 'center',
    paddingHorizontal: Spacing.lg,
    paddingVertical: 6,
    minWidth: 80,
    overflow: 'hidden',
    transform: [{ rotate: '-3deg' }],
  },
  tapeDot: {
    position: 'absolute',
    top: 8,
    width: 5,
    height: 5,
    borderRadius: 3,
    backgroundColor: 'rgba(61,82,112,0.25)',
  },
  tapeText: {
    ...Typography.caption1,
    color: Colors.ink,
    textAlign: 'center',
    fontWeight: '700',
  },
  stickerHalo: {
    padding: 2,
    backgroundColor: '#fff',
    borderRadius: 16,
    alignSelf: 'flex-start',
    shadowColor: '#000',
    shadowOpacity: 0.06,
    shadowRadius: 2,
    shadowOffset: { width: 0, height: 1 },
  },
  sticker: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 14,
    borderWidth: 1.5,
    borderColor: 'rgba(61,82,112,0.1)',
  },
  stickerEmoji: { fontSize: 12 },
  stickerText: { ...Typography.caption1, fontWeight: '700' },
  imageSticker: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 5,
    borderWidth: 2,
    borderColor: '#fff',
    shadowColor: Colors.ink,
    shadowOpacity: 0.12,
    shadowRadius: 4,
    shadowOffset: { width: 1, height: 2 },
    overflow: 'hidden',
  },
  strip: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: Spacing.lg,
    gap: 10,
    marginTop: Spacing.sm,
    marginBottom: Spacing.sm,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    justifyContent: 'space-between',
    paddingHorizontal: Spacing.lg,
    marginTop: Spacing.lg,
    marginBottom: Spacing.md,
  },
  title: { ...Typography.title3, color: Colors.ink },
  underline: {
    marginTop: 4,
    height: 7,
    width: 52,
    borderRadius: 4,
    backgroundColor: Colors.creamBlue,
    transform: [{ rotate: '-2deg' }],
  },
  action: { ...Typography.footnote, color: Colors.primaryDark, fontWeight: '700' },
});
