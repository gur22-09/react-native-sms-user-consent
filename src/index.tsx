import { NativeModules, Platform } from 'react-native';
import {
  getVerificationCode,
  addErrorListener,
  startSmsHandling,
  startSmsListener,
  stopSmsListener,
} from './smsUserConsentModule';

const LINKING_ERROR =
  `The package 'react-native-sms-user-consent' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const SmsUserConsent = NativeModules.SmsUserConsent
  ? NativeModules.SmsUserConsent
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return SmsUserConsent.multiply(a, b);
}

export {
  getVerificationCode,
  addErrorListener,
  startSmsHandling,
  startSmsListener,
  stopSmsListener,
};
