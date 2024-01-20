package it.uniba.dib.sms232417.asilapp.interfaces;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.entity.Treatment;

public interface OnTreatmentsCallback {
    void onCallback(List<Treatment> treatments);
    void onCallbackFailed(Exception e);
}