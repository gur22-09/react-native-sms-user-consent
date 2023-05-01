package com.smsuserconsent;

public class SmsUserConsentException extends RuntimeException {
  public String code;

  SmsUserConsentException(Errors error, String message) {
      super(message);
      this.code = error.toString();
  }
}
