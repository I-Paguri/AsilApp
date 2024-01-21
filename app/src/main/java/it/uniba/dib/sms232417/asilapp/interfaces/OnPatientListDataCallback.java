package it.uniba.dib.sms232417.asilapp.interfaces;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.entity.Patient;

public interface OnPatientListDataCallback {
    void onCallback(List<Patient> patientList);
    void onCallbackError(Exception exception, String message);
}
