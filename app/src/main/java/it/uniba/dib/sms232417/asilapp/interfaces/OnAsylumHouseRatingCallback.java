package it.uniba.dib.sms232417.asilapp.interfaces;

public interface OnAsylumHouseRatingCallback {
    void onCallback(double rating);
    void onCallbackFailed(Exception e);
}
