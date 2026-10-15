import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Colors } from '@/constants/theme';
import type { FlawBox } from '@/types';

type Props = {
  flaws: FlawBox[];
  width: number;
  height: number;
};

/** Red bounding boxes over product imagery (relative 0–1 coordinates). */
export function FlawOverlay({ flaws, width, height }: Props) {
  if (!flaws.length) return null;
  return (
    <View pointerEvents="none" style={StyleSheet.absoluteFill}>
      {flaws.map((f) => (
        <View
          key={f.id}
          style={[
            styles.box,
            {
              left: f.x * width,
              top: f.y * height,
              width: f.width * width,
              height: f.height * height,
            },
          ]}
        />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  box: {
    position: 'absolute',
    borderWidth: 2,
    borderColor: Colors.flaw,
    backgroundColor: 'rgba(231, 76, 60, 0.12)',
    borderRadius: 2,
  },
});
