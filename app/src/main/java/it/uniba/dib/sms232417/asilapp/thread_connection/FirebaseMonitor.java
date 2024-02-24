package it.uniba.dib.sms232417.asilapp.thread_connection;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import it.uniba.dib.sms232417.asilapp.adapters.databaseAdapter.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnValueChangeInterface;


public class FirebaseMonitor extends Thread {

    private String token;
    private Context context;
    private Dialog dialog;
    int i = 0;
    boolean isConnect = true;
    private OnValueChangeInterface callback;
    DatabaseAdapterPatient dbAdapter;

    public FirebaseMonitor(String token, Context context, Dialog dialog, OnValueChangeInterface callback) {
        this.token = token;
        this.context = context;
        dbAdapter = new DatabaseAdapterPatient(context);
        this.dialog = dialog;
        this.callback = callback;

    }

    @Override
    public void run() {
        while (isConnect) {
           dbAdapter.checkIsConnectedToContainer(token, new DatabaseAdapterPatient.OnIsConnectedCallback() {
                @Override
                public void onCallback(boolean isConnect) {
                    if (!isConnect) {
                        dialog.dismiss();
                        stopThread();
                        callback.onValueChange(false);

                    }else{
                        Log.d("Firebase Monitor: isConnect", String.valueOf(isConnect));
                    }
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void stopThread() {
        isConnect = false;
    }

}