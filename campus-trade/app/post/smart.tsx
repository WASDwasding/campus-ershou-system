import React, { useRef, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  StyleSheet,
  Text,
  View,
  Pressable,
} from 'react-native';
import { CameraView, useCameraPermissions } from 'expo-camera';
import * as ImagePicker from 'expo-image-picker';
import { router } from 'expo-router';
import { Image } from 'expo-image';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { Button } from '@/components/Button';
import { FlawOverlay } from '@/components/FlawOverlay';
import { useAppState } from '@/context/AppContext';
import { runSmartRecognition } from '@/services/vision';
import { Colors, ConditionLabels, Spacing, Typography } from '@/constants/theme';
import type { RecognitionStep } from '@/types';
import { Dimensions } from 'react-native';

const PREVIEW_W = Dimensions.get('window').width - Spacing.xl * 2;
const PREVIEW_H = PREVIEW_W * 1.2;

type Phase = 'camera' | 'processing' | 'result';

export default function SmartPostScreen() {
  const cameraRef = useRef<CameraView>(null);
  const [permission, requestPermission] = useCameraPermissions();
  const { applyRecognitionToDraft } = useAppState();
  const insets = useSafeAreaInsets();
  const [phase, setPhase] = useState<Phase>('camera');
  const [steps, setSteps] = useState<RecognitionStep[]>([]);
  const [previewUri, setPreviewUri] = useState<string | null>(null);
  const [resultUri, setResultUri] = useState<string | null>(null);
  const [meta, setMeta] = useState<{
    category: string;
    condition: string;
    flaws: number;
  } | null>(null);
  const [flaws, setFlaws] = useState<import('@/types').FlawBox[]>([]);

  const process = async (uri: string) => {
    setPreviewUri(uri);
    setPhase('processing');
    try {
      const result = await runSmartRecognition(uri, setSteps);
      applyRecognitionToDraft(result);
      setResultUri(result.enhancedUri);
      setFlaws(result.flaws);
      setMeta({
        category: result.category,
        condition: ConditionLabels[result.condition],
        flaws: result.flaws.length,
      });
      setPhase('result');
    } catch (e) {
      Alert.alert('Recognition failed', e instanceof Error ? e.message : 'Try again');
      setPhase('camera');
    }
  };

  const takePhoto = async () => {
    const photo = await cameraRef.current?.takePictureAsync({ quality: 0.8 });
    if (photo?.uri) await process(photo.uri);
  };

  const pickLibrary = async () => {
    const res = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.8,
    });
    if (!res.canceled && res.assets[0]) await process(res.assets[0].uri);
  };

  if (!permission) {
    return (
      <View style={styles.center}>
        <ActivityIndicator color={Colors.primary} />
      </View>
    );
  }

  if (!permission.granted) {
    return (
      <View style={[styles.center, { padding: Spacing.xl }]}>
        <Text style={styles.permTitle}>Camera access needed</Text>
        <Text style={styles.permBody}>
          Smart Post uses your iPhone camera for low-light enhancement and flaw detection.
        </Text>
        <Button title="Allow Camera" onPress={requestPermission} />
      </View>
    );
  }

  if (phase === 'processing') {
    return (
      <View style={[styles.center, { padding: Spacing.xl }]}>
        {previewUri ? (
          <Image source={{ uri: previewUri }} style={styles.thumb} contentFit="cover" />
        ) : null}
        <Text style={styles.procTitle}>Analyzing low-light photo…</Text>
        <View style={styles.steps}>
          {steps.map((s) => (
            <View key={s.id} style={styles.stepRow}>
              <Ionicons
                name={
                  s.status === 'done'
                    ? 'checkmark-circle'
                    : s.status === 'running'
                      ? 'ellipse'
                      : 'ellipse-outline'
                }
                size={20}
                color={
                  s.status === 'done'
                    ? Colors.success
                    : s.status === 'running'
                      ? Colors.primary
                      : Colors.textTertiary
                }
              />
              <View style={{ flex: 1 }}>
                <Text style={styles.stepLabel}>{s.label}</Text>
                {s.detail ? <Text style={styles.stepDetail}>{s.detail}</Text> : null}
              </View>
            </View>
          ))}
        </View>
      </View>
    );
  }

  if (phase === 'result' && resultUri) {
    return (
      <View style={[styles.result, { paddingBottom: insets.bottom + Spacing.lg }]}>
        <View style={{ width: PREVIEW_W, height: PREVIEW_H, alignSelf: 'center' }}>
          <Image
            source={{ uri: resultUri }}
            style={{ width: PREVIEW_W, height: PREVIEW_H, borderRadius: 16 }}
            contentFit="cover"
          />
          <FlawOverlay flaws={flaws} width={PREVIEW_W} height={PREVIEW_H} />
        </View>
        <View style={styles.metaBox}>
          <Text style={styles.metaLine}>Category · {meta?.category}</Text>
          <Text style={styles.metaLine}>Condition · {meta?.condition}</Text>
          <Text style={styles.metaLine}>
            Flaws · {meta?.flaws ? `${meta.flaws} red box(es)` : 'None detected'}
          </Text>
          <Text style={styles.metaHint}>Form auto-filled — you only need to enter a price.</Text>
        </View>
        <Button title="Continue to price & publish" onPress={() => router.replace('/post/review')} />
        <Button
          title="Retake"
          variant="ghost"
          style={{ marginTop: Spacing.md }}
          onPress={() => setPhase('camera')}
        />
      </View>
    );
  }

  return (
    <View style={{ flex: 1, backgroundColor: '#000' }}>
      <CameraView ref={cameraRef} style={{ flex: 1 }} facing="back" />
      <View style={[styles.overlay, { paddingBottom: insets.bottom + 16 }]}>
        <Text style={styles.hint}>Shoot in dorm lighting — we enhance & denoise automatically</Text>
        <View style={styles.controls}>
          <Pressable onPress={pickLibrary} style={styles.sideBtn}>
            <Ionicons name="images" size={26} color="#fff" />
          </Pressable>
          <Pressable onPress={takePhoto} style={styles.shutter}>
            <View style={styles.shutterInner} />
          </Pressable>
          <View style={styles.sideBtn} />
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: Colors.background,
  },
  permTitle: { ...Typography.title3, color: Colors.text, marginBottom: Spacing.sm },
  permBody: {
    ...Typography.callout,
    color: Colors.textSecondary,
    textAlign: 'center',
    marginBottom: Spacing.xl,
  },
  thumb: { width: 120, height: 120, borderRadius: 12, marginBottom: Spacing.lg },
  procTitle: { ...Typography.title3, color: Colors.text, marginBottom: Spacing.xl },
  steps: { width: '100%', gap: Spacing.md },
  stepRow: { flexDirection: 'row', gap: Spacing.md, alignItems: 'flex-start' },
  stepLabel: { ...Typography.headline, color: Colors.text },
  stepDetail: { ...Typography.caption1, color: Colors.textSecondary, marginTop: 2 },
  result: { flex: 1, padding: Spacing.xl, backgroundColor: Colors.background },
  metaBox: {
    backgroundColor: Colors.surface,
    borderRadius: 12,
    padding: Spacing.lg,
    marginVertical: Spacing.xl,
  },
  metaLine: { ...Typography.callout, color: Colors.text, marginBottom: 6 },
  metaHint: { ...Typography.footnote, color: Colors.primary, marginTop: Spacing.sm },
  overlay: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    paddingTop: Spacing.lg,
    backgroundColor: 'rgba(0,0,0,0.35)',
  },
  hint: {
    ...Typography.footnote,
    color: '#fff',
    textAlign: 'center',
    marginBottom: Spacing.lg,
    paddingHorizontal: Spacing.xl,
  },
  controls: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-around',
  },
  shutter: {
    width: 74,
    height: 74,
    borderRadius: 37,
    borderWidth: 4,
    borderColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  shutterInner: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: Colors.primary,
  },
  sideBtn: { width: 48, alignItems: 'center' },
});
