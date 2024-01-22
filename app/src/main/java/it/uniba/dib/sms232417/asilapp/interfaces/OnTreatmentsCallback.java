package it.uniba.dib.sms232417.asilapp.interfaces;

import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.entity.Treatment;

public interface OnTreatmentsCallback {
    void onCallback(Map<String, Treatment> treatments);
    void onCallbackFailed(Exception e);
}