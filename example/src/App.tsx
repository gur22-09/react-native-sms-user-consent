import * as React from 'react';
import { useSmsConsent } from 'react-native-sms-user-consent';
import { StyleSheet, View, Text } from 'react-native';

export default function App() {
  const { code, error } = useSmsConsent({ codeLength: 4 });
  console.log(code);
  return (
    <View style={styles.container}>
      <Text>Result: {code}</Text>
      <Text>Error: {error}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
