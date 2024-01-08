package it.uniba.dib.sms232417.asilapp.entity.interface_entity;

import it.uniba.dib.sms232417.asilapp.entity.Patient;

public interface OnPatientDataCallback {
    void onCallback(Patient patient);
    void onCallbackError(Exception exception);
}
