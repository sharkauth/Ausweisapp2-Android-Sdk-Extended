package com.example.ausweisappsdktest;

import android.os.RemoteException;
import android.util.Log;

import com.governikus.ausweisapp2.IAusweisApp2SdkCallback;

class LocalCallback extends IAusweisApp2SdkCallback.Stub {
    public String mSessionID = null;

    private final MainActivity activity;
    LocalCallback(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void sessionIdGenerated(String pSessionId, boolean pIsSecureSessionId) throws RemoteException {
        Log.i("pSID", pSessionId);
        mSessionID = pSessionId;
    }

    @Override
    public void receive(String pJson) throws RemoteException {
        activity.runOnUiThread(() -> {
            activity.handleReply(pJson);
        });
        Log.i("reply", pJson);
    }

    @Override
    public void sdkDisconnected() throws RemoteException {
        mSessionID = null;
    }
}
