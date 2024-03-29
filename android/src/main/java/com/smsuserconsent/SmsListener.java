package com.smsuserconsent;

import static android.app.Activity.RESULT_OK;
import static com.smsuserconsent.SmsBroadcastReceiver.SMS_CONSENT_REQUEST;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.facebook.react.bridge.BaseActivityEventListener;

public class SmsListener extends BaseActivityEventListener {

  private SmsUserConsentModule moduleInstance;

  SmsListener(SmsUserConsentModule moduleInstance) {
    super();
    this.moduleInstance = moduleInstance;
  }

  @Override
  public void onActivityResult(
      Activity activity,
      int requestCode,
      int resultCode,
      Intent intent) {
    super.onActivityResult(activity, requestCode, resultCode, intent);

    if (requestCode != SMS_CONSENT_REQUEST)
      return;

    if (resultCode == RESULT_OK) {
      String sms = intent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
      moduleInstance.handleSms(sms);
    } else {
      moduleInstance.handleError(
          new SmsUserConsentException(
              Errors.CONSENT_CANCELED,
              "Consent was canceled"));
    }
  }
}
