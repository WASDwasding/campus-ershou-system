import React, { useState } from 'react';
import { Alert, ScrollView, StyleSheet, Text, View } from 'react-native';
import { router } from 'expo-router';
import { Button } from '@/components/Button';
import { TextField } from '@/components/TextField';
import { useAuth } from '@/context/AuthContext';
import { Colors, Spacing, Typography } from '@/constants/theme';
import { DormBuildings } from '@/constants/theme';

export default function RegisterScreen() {
  const { login } = useAuth();
  const [studentId, setStudentId] = useState('');
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);

  const onRegister = async () => {
    setLoading(true);
    try {
      await login(studentId.trim(), name.trim());
      Alert.alert('Verified', 'Campus identity confirmed. Welcome!');
      router.replace('/(tabs)');
    } catch (e) {
      Alert.alert('Cannot register', e instanceof Error ? e.message : 'Verification failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container} keyboardShouldPersistTaps="handled">
      <Text style={styles.lead}>
        Only students listed in the university registry can join. We match your student ID and legal name, then assign your dorm from campus records.
      </Text>
      <TextField
        label="Student ID"
        value={studentId}
        onChangeText={setStudentId}
        keyboardType="number-pad"
        placeholder="Enter campus student ID"
      />
      <TextField
        label="Legal name"
        value={name}
        onChangeText={setName}
        placeholder="Must match registry"
      />
      <View style={styles.infoBox}>
        <Text style={styles.infoTitle}>Supported demo registry IDs</Text>
        <Text style={styles.infoBody}>202405099 · Taylor Wu · Pine Hall E</Text>
        <Text style={styles.infoBody}>202406120 · Riley Zhang · West Court H</Text>
        <Text style={[styles.infoBody, { marginTop: 8 }]}>
          Dorm buildings: {DormBuildings.slice(0, 4).join(', ')}…
        </Text>
      </View>
      <Button title="Verify student identity" loading={loading} onPress={onRegister} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { padding: Spacing.xl, paddingBottom: 48 },
  lead: {
    ...Typography.callout,
    color: Colors.textSecondary,
    marginBottom: Spacing.xl,
    lineHeight: 22,
  },
  infoBox: {
    backgroundColor: Colors.primarySoft,
    borderRadius: 12,
    padding: Spacing.lg,
    marginBottom: Spacing.xl,
  },
  infoTitle: { ...Typography.footnote, fontWeight: '700', color: Colors.primaryDark, marginBottom: 6 },
  infoBody: { ...Typography.caption1, color: Colors.textSecondary, lineHeight: 18 },
});
