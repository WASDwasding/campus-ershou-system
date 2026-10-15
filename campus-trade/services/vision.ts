/**
 * Frontend simulation of low-light enhancement + product recognition.
 * Mirrors the intended backend pipeline so the full UX can be demoed offline.
 * Swap runSmartRecognition internals with api.analyzeProductImage when ready.
 */

import * as ImageManipulator from 'expo-image-manipulator';
import type {
  ConditionGrade,
  FlawBox,
  ProductCategory,
  RecognitionResult,
  RecognitionStep,
} from '@/types';

export type ProgressCallback = (steps: RecognitionStep[]) => void;

const CATEGORY_HINTS: { keywords: string[]; category: ProductCategory; title: string }[] = [
  { keywords: ['phone', 'iphone', 'pad', 'laptop', 'airpods', 'headphone', 'watch'], category: 'electronics', title: 'Electronics Device' },
  { keywords: ['book', 'textbook', 'novel'], category: 'books', title: 'Campus Textbook' },
  { keywords: ['shoe', 'shirt', 'jacket', 'bag'], category: 'fashion', title: 'Fashion Item' },
  { keywords: ['lamp', 'bottle', 'desk', 'fridge'], category: 'daily', title: 'Dorm Daily Item' },
  { keywords: ['bike', 'mat', 'ball'], category: 'sports', title: 'Sports Gear' },
  { keywords: ['cream', 'dryer', 'makeup'], category: 'beauty', title: 'Beauty Product' },
];

function sleep(ms: number) {
  return new Promise((r) => setTimeout(r, ms));
}

function pickCategory(seed: number): { category: ProductCategory; title: string; confidence: number } {
  const pick = CATEGORY_HINTS[seed % CATEGORY_HINTS.length];
  return {
    category: pick.category,
    title: pick.title,
    confidence: 0.82 + (seed % 15) / 100,
  };
}

function pickCondition(flawCount: number): { condition: ConditionGrade; confidence: number } {
  if (flawCount === 0) return { condition: 'like_new', confidence: 0.91 };
  if (flawCount === 1) return { condition: '90_new', confidence: 0.88 };
  if (flawCount === 2) return { condition: '80_new', confidence: 0.85 };
  return { condition: '70_new', confidence: 0.8 };
}

function generateFlaws(seed: number): FlawBox[] {
  const count = seed % 4; // 0–3
  const labels = ['Surface scratch', 'Corner dent', 'Paint chip', 'Wear mark', 'Scuff'];
  const boxes: FlawBox[] = [];
  for (let i = 0; i < count; i++) {
    const n = (seed * (i + 3) * 17) % 100;
    boxes.push({
      id: `detect_${i}`,
      x: 0.12 + ((n * 13) % 55) / 100,
      y: 0.18 + ((n * 19) % 50) / 100,
      width: 0.14 + ((n * 7) % 12) / 100,
      height: 0.08 + ((n * 5) % 10) / 100,
      label: labels[(seed + i) % labels.length],
    });
  }
  return boxes;
}

async function enhanceLowLight(uri: string): Promise<string> {
  // Simulate enhancement: normalize + slight contrast via manipulator compress/resize
  const result = await ImageManipulator.manipulateAsync(
    uri,
    [{ resize: { width: 1200 } }],
    { compress: 0.85, format: ImageManipulator.SaveFormat.JPEG },
  );
  return result.uri;
}

function hashUri(uri: string): number {
  let h = 0;
  for (let i = 0; i < uri.length; i++) h = (h * 33 + uri.charCodeAt(i)) >>> 0;
  return h;
}

export async function runSmartRecognition(
  originalUri: string,
  onProgress?: ProgressCallback,
): Promise<RecognitionResult> {
  const steps: RecognitionStep[] = [
    { id: 'enhance', label: 'Low-light enhancement', status: 'pending' },
    { id: 'denoise', label: 'Noise reduction', status: 'pending' },
    { id: 'category', label: 'Category recognition', status: 'pending' },
    { id: 'flaws', label: 'Scratch & flaw detection', status: 'pending' },
    { id: 'condition', label: 'Condition evaluation', status: 'pending' },
  ];

  const emit = (partial: Partial<RecognitionStep> & { id: string }) => {
    const idx = steps.findIndex((s) => s.id === partial.id);
    if (idx >= 0) steps[idx] = { ...steps[idx], ...partial };
    onProgress?.([...steps]);
  };

  emit({ id: 'enhance', status: 'running', detail: 'Boosting shadows & midtones…' });
  await sleep(700);
  const enhancedUri = await enhanceLowLight(originalUri);
  emit({ id: 'enhance', status: 'done', detail: 'Exposure normalized' });

  emit({ id: 'denoise', status: 'running', detail: 'Bilateral filter pass…' });
  await sleep(600);
  emit({ id: 'denoise', status: 'done', detail: 'Grain reduced' });

  const seed = hashUri(originalUri);
  const { category, title, confidence: catConf } = pickCategory(seed);

  emit({ id: 'category', status: 'running', detail: 'CNN classifier…' });
  await sleep(800);
  emit({
    id: 'category',
    status: 'done',
    detail: `${title} (${Math.round(catConf * 100)}%)`,
  });

  emit({ id: 'flaws', status: 'running', detail: 'Edge & texture anomaly scan…' });
  await sleep(900);
  const flaws = generateFlaws(seed);
  emit({
    id: 'flaws',
    status: 'done',
    detail: flaws.length ? `${flaws.length} region(s) marked` : 'No flaws detected',
  });

  emit({ id: 'condition', status: 'running', detail: 'Scoring surface integrity…' });
  await sleep(500);
  const { condition, confidence: condConf } = pickCondition(flaws.length);
  emit({
    id: 'condition',
    status: 'done',
    detail: `${condition.replace('_', ' ')} (${Math.round(condConf * 100)}%)`,
  });

  const suggestedTitle = `${title} · Campus Resale`;
  const flawText =
    flaws.length > 0
      ? `Detected marks: ${flaws.map((f) => f.label.toLowerCase()).join(', ')}.`
      : 'No visible surface flaws detected.';
  const suggestedDescription = `Auto-assessed via campus smart posting. ${flawText} Pickup preferred near dorm.`;

  return {
    enhancedUri,
    originalUri,
    category,
    categoryConfidence: catConf,
    condition,
    conditionConfidence: condConf,
    flaws,
    suggestedTitle,
    suggestedDescription,
    processingSteps: steps,
  };
}
