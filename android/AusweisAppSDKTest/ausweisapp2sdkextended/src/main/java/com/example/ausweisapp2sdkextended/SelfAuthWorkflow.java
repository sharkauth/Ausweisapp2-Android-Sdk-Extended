package com.example.ausweisapp2sdkextended;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.AndroidException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.governikus.ausweisapp2.IAusweisApp2Sdk;
import com.governikus.ausweisapp2.IAusweisApp2SdkCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SelfAuthWorkflow {
    public static class WorkflowException extends AndroidException {
        public WorkflowException(String msg) {
            super(msg);
        }
    }

    private static class SdkCallback extends IAusweisApp2SdkCallback.Stub {
        String sessionID = null;

        BlockingQueue<String> receiverQueue = new LinkedBlockingQueue<>(10);

        @Override
        public void sessionIdGenerated(String pSessionId, boolean pIsSecureSessionId) throws RemoteException {
            Log.i("pSID", pSessionId);
            sessionID = pSessionId;
        }

        @Override
        public void receive(String pJson) throws RemoteException {
            Log.i("reply", pJson);
            receiverQueue.offer(pJson);
        }

        @Override
        public void sdkDisconnected() throws RemoteException {
            sessionID = null;
        }
    }

    private static final String TAG = "AusweisApp2ExtSelfAuth";

    private SdkCallback sdkCallback;
    private IAusweisApp2Sdk sdk;
    private Context context;
    private ServiceConnection serviceConnection;

    private Lock workflowLock = new ReentrantLock();

    public static SelfAuthWorkflow start(@NonNull Context ctx) throws WorkflowException {
        SelfAuthWorkflow w = new SelfAuthWorkflow();

        w.sdkCallback = new SelfAuthWorkflow.SdkCallback();

        w.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    w.sdk = IAusweisApp2Sdk.Stub.asInterface(service);
                    Log.i(TAG, "AusweisApp2 service connected");

                    w.sdk.connectSdk(w.sdkCallback);
                    Log.i(TAG, "AusweisApp2 sdk connected");
                } catch (ClassCastException | RemoteException e) {
                    Log.e(TAG, "Couldn't connect to sdk");
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                Log.i(TAG, "AusweisApp2 service disconnected");
                w.sdk = null;
            }
        };

        w.context = ctx;
        String pkg = w.context.getApplicationContext().getPackageName();

        String name = "com.governikus.ausweisapp2.START_SERVICE";
        Intent serviceIntent = new Intent(name);
        serviceIntent.setPackage(pkg);
        if (w.context.bindService(serviceIntent, w.serviceConnection, Context.BIND_AUTO_CREATE)) {
            return w;
        }
        throw new WorkflowException("Couldn't bind service");
    }

    public void stop() {
        context.unbindService(serviceConnection);
    }

    public UserData runSelfAuth(@NonNull Tag tag, @NonNull String pin) throws WorkflowException, InterruptedException {
        if (!pin.matches("\\d{6}")) {
            throw new WorkflowException("Invalid PIN");
        }

        try {
            workflowLock.lock();
            sdkCallback.receiverQueue.clear();

            try {
                sdk.updateNfcTag(sdkCallback.sessionID, tag);
            } catch (RemoteException e) {
                throw new WorkflowException("Couldn't update nfc tag");
            }

            boolean workflowStarted = false;
            while (true) {
                // blocks
                String json = sdkCallback.receiverQueue.take();

                Log.i(TAG, "received: " + json);
                try {
                    JSONObject obj = new JSONObject(json);

                    switch (obj.getString("msg")) {
                        case "READER":
                            if (workflowStarted || obj.isNull("card")) {
                                cancel();
                            } else if (obj.getBoolean("attached") && !obj.isNull("card")) {
                                // nfc reader attached; start self auth workflow
                                sendToSdk("{\"cmd\": \"RUN_SELF_AUTH\"}");
                            }
                            break;
                        case "ACCESS_RIGHTS":
                            // just accept them
                            sendToSdk("{\"cmd\": \"ACCEPT\"}");
                            break;
                        case "INSERT_CARD":
                            cancel();
                            break;
                        case "ENTER_PIN":
                            sendToSdk("{\"cmd\": \"SET_PIN\", \"value\": \"" + pin + "\"}");
                            break;
                        case "BAD_STATE":
                            throw new WorkflowException("Bad workflow state");
                        case "AUTH":
                            if (!obj.has("result")) {
                                workflowStarted = true;
                                break;
                            }

                            JSONObject result = obj.getJSONObject("result");

                            if (isMajorCode(result, "error")
                                    && isMinorCode(result, "cancellationByUser")) {
                                throw new WorkflowException("Workflow ended: eID card was removed");
                            } else if (isMajorCode(result, "error")) {
                                throw new WorkflowException("Workflow ended: unknown reason");
                            } else if (isMajorCode(result, "ok")) {
                                // workflow ended with eID user data
                                JSONObject data = obj.getJSONObject("data");
                                return jsonToUser(data);
                            } else {
                                throw new WorkflowException("Workflow reached unhandled state!");
                            }
                    }
                } catch (JSONException e) {
                    throw new WorkflowException("Received invalid data from sdk");
                }
            }
        } finally {
            try {
                sdk.updateNfcTag(sdkCallback.sessionID, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            workflowLock.unlock();
        }
    }

    private boolean isMajorCode(JSONObject result, String code) throws JSONException {
        return result.getString("major").equalsIgnoreCase(
                "http://www.bsi.bund.de/ecard/api/1.1/resultmajor#" + code);
    }

    private boolean isMinorCode(JSONObject result, String code) throws JSONException {
        return result.getString("minor").equalsIgnoreCase(
                "http://www.bsi.bund.de/ecard/api/1.1/resultminor/sal#" + code);
    }

    private void cancel() throws WorkflowException {
        sendToSdk("{\"cmd\": \"CANCEL\"}");
    }

    private void sendToSdk(String msg) throws WorkflowException {
        try {
            sdk.send(sdkCallback.sessionID, msg);
            Log.i(TAG, msg);
        } catch (RemoteException e) {
            Log.e(TAG, msg, e);
            throw new WorkflowException("Couldn't send '" + msg + "' to AusweisApp2 sdk");
        }
    }

    private UserData jsonToUser(JSONObject obj) {
        UserData u = new UserData();
        u.setAddress(obj.optString("address", ""));
        u.setBirthName(obj.optString("birthName", ""));
        u.setFamilyName(obj.optString("familyName", ""));
        u.setGivenNames(obj.optString("givenNames", ""));
        u.setPlaceOfBirth(obj.optString("placeOfBirth", ""));
        u.setDateOfBirth(obj.optString("dateOfBirth", ""));
        u.setDoctoralDegree(obj.optString("doctoralDegree", ""));
        u.setArtisticName(obj.optString("artisticName", ""));
        u.setPseudonym(obj.optString("pseudonym", ""));
        u.setValidUntil(obj.optString("validUntil", ""));
        u.setNationality(obj.optString("nationality", ""));
        u.setIssuingCountry(obj.optString("issuingCountry", ""));
        u.setDocumentType(obj.optString("documentType", ""));
        u.setResidencePermitI(obj.optString("residencePermitI", ""));
        u.setResidencePermitII(obj.optString("residencePermitII", ""));
        u.setCommunityID(obj.optString("communityID", ""));
        u.setAddressVerification(obj.optString("addressVerification", ""));
        u.setAgeVerification(obj.optString("ageVerification", ""));
        return u;
    }
}
