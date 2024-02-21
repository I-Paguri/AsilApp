package it.uniba.dib.sms232417.asilapp.interfaces;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.entity.AsylumHouse;

public interface OnAsylumHouseDataCallback {
    void onCallback(List<AsylumHouse> asylumHouse);
    void onCallbackFailed(Exception e);
}
