package com.smsuserconsent;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.IntentFilter;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

@ReactModule(name = SmsUserConsentModule.NAME)
public class SmsUserConsentModule extends ReactContextBaseJavaModule {
  public static final String NAME = "SmsUserConsent";
  private static final String SMS_RETRIEVED = "SMS_RETRIEVED";
  private static final String SMS_RETRIEVE_ERROR = "SMS_RETRIEVE_ERROR";
  public ReactApplicationContext reactContext;
  private SmsBroadcastReceiver broadcastReceiver;
  private SmsListener listener;
  public static long startSmsConsentTime;

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }





  public SmsUserConsentModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    listener = new SmsListener(this);
  }

  private void subscribe() {
    Activity activity = getCurrentActivity();
    if (activity == null) {
      throw new SmsUserConsentException(
        Errors.NULL_ACTIVITY,
        "activity is null"
      );
    }

    SmsRetriever.getClient(getCurrentActivity()).startSmsUserConsent(null);

    broadcastReceiver = new SmsBroadcastReceiver(getCurrentActivity(), this);

    getCurrentActivity()
      .registerReceiver(
        broadcastReceiver,
        new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
        SmsRetriever.SEND_PERMISSION,
        null
      );

    reactContext.addActivityEventListener(listener);
  }

  private void unsubscribe() {
    Activity activity = getCurrentActivity();

    if (activity == null) {
      throw new SmsUserConsentException(
        Errors.NULL_ACTIVITY,
        "Could not unsubscribe, activity is null"
      );
    }

    if (broadcastReceiver == null) {
      throw new SmsUserConsentException(
        Errors.NULL_BROADCAST_RECEIVER,
        "Could not unsubscribe, broadcastReceiver is null"
      );
    }

    activity.unregisterReceiver(broadcastReceiver);
    broadcastReceiver = null;

    reactContext.removeActivityEventListener(listener);
  }

  private void resubscribe() {
    try {
      unsubscribe();
    } catch (SmsUserConsentException err) {
      sendErrorEventToJs(err);
      return;
    }

    try {
      subscribe();
    } catch (SmsUserConsentException err) {
      sendErrorEventToJs(err);
    }
  }

  public void handleSms(String sms) {
    sendSmsEventToJs(sms);
    resubscribe();
  }

  public void handleError(SmsUserConsentException err) {
    sendErrorEventToJs(err);
    resubscribe();
  }

  private void sendSmsEventToJs(String sms) {
    WritableMap params = Arguments.createMap();
    params.putString("sms", sms);

    Log.d("sending sms event to JS", sms);

    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(SMS_RETRIEVED, params);
  }

  private void sendErrorEventToJs(SmsUserConsentException err) {
    WritableMap params = Arguments.createMap();
    params.putString(err.code, err.getMessage());

    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(SMS_RETRIEVE_ERROR, params);
  }

  @ReactMethod
  public void startSmsListener(Promise promise) {
    try {
      // subscribe to sms listener
      startSmsConsentTime = System.currentTimeMillis();
      subscribe();
      promise.resolve(null);
    } catch (SmsUserConsentException err) {
      promise.reject(err.code, err.getMessage());
    }
  }

  @ReactMethod
  public void stopSmsListener(Promise promise) {
    try {
      unsubscribe();
      promise.resolve(null);
    } catch (SmsUserConsentException err) {
      promise.reject(err.code, err.getMessage());
    }
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    for (Errors error : Errors.values()) {
      constants.put(error.toString(), error.toString());
    }

    return constants;
  }

   // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }


}
