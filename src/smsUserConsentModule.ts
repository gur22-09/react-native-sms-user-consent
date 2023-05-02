import { NativeModules, NativeEventEmitter } from 'react-native';
import { Errors, Events } from './constants';

const { SmsUserConsentModule } = NativeModules;
const eventEmitter = new NativeEventEmitter(SmsUserConsentModule);

export async function startSmsListener() {
  try {
    await SmsUserConsentModule.startSmsListener();
  } catch (err) {
    console.error(err);
  }
}

export async function stopSmsListener() {
  try {
    await SmsUserConsentModule.stopSmsListener();
  } catch (err) {
    console.error(err);
  }
}

export function startSmsHandling(onSmsReceived: (event: any) => void) {
  startSmsListener();
  const listener = eventEmitter.addListener(Events.SMS_RETRIVED, onSmsReceived);

  function stopSmsHandling() {
    stopSmsListener();
    listener.remove();
  }

  return stopSmsHandling;
}

export function getVerificationCode(sms: string, codeLength = 4) {
  const codeRegExp = new RegExp(`\\d{${codeLength}}`, 'm');
  const code = sms?.match(codeRegExp)?.[0];
  return code ?? null;
}

export function addErrorListener(onErrorReceived: (event: any) => void) {
  const listener = eventEmitter.addListener(
    Events.SMS_RETRIEVE_ERROR,
    onErrorReceived
  );

  function removeErrorListener() {
    listener.remove();
  }

  return removeErrorListener;
}

export function getErrors(): Record<Errors, string> {
  return SmsUserConsentModule.getConstants();
}
