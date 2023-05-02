import { useEffect, useState } from 'react';
import {
  addErrorListener,
  getVerificationCode,
  startSmsHandling,
} from 'src/smsUserConsentModule';

interface Props {
  codeLength: number;
}

export const useSmsConsent = (props: Props) => {
  const { codeLength } = props;
  const [code, setCode] = useState<string | null>(null);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const stopSmsHandling = startSmsHandling((event) => {
      const receivedSms = event?.sms;
      if (!receivedSms) {
        console.error('No SMS received!');
        return;
      }

      const retrievedCode = getVerificationCode(receivedSms, codeLength);

      if (!retrievedCode) {
        console.error('No code retrieved!');
        return;
      }

      setCode(retrievedCode);
    });

    return stopSmsHandling;
  }, [codeLength]);

  useEffect(() => {
    const removeErrorListener = addErrorListener((errorMap) => {
      const [err] = <string[]>Object.values(errorMap);
      setError(err!);
    });

    return removeErrorListener;
  });

  return { code, error };
};
