package it.uniba.dib.sms232417.asilapp.interfaces;

public interface OnCountCallback {
    void onCallback(int count);
    void onCallbackFailed(Exception e);
}