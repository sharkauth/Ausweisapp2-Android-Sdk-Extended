package com.example.ausweisappsdktest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.governikus.ausweisapp2.IAusweisApp2Sdk;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    IAusweisApp2Sdk mSdk;
    LocalCallback mCallback;
    ForegroundDispatcher foregroundDispatcher;

    Button sendAuthButton;
    Button sendCancelButton;
    TextView passwordTextView;
    TextView replyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendAuthButton = findViewById(R.id.send_auth);
        sendCancelButton = findViewById(R.id.send_cancel);
        passwordTextView = findViewById(R.id.password);
        replyTextView = findViewById(R.id.reply);

        mCallback = new LocalCallback(this);

        sendAuthButton.setOnClickListener(v ->
                sendToSdk("{\"cmd\": \"RUN_SELF_AUTH\"}")
        );

        sendCancelButton.setOnClickListener(v ->
                sendToSdk("{\"cmd\": \"CANCEL\"}")
        );

        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    mSdk = IAusweisApp2Sdk.Stub.asInterface(service);
                    Log.i("sdk", "onServiceConnected");

                    mSdk.connectSdk(mCallback);
                    Log.i("sdk", "connectSdk");
                } catch (ClassCastException | RemoteException e) {
                    Log.e("sdk", "", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                Log.i("onServiceDisconnected", "disconnected");
                mSdk = null;
            }
        };

        String pkg = getApplicationContext().getPackageName();

        String name = "com.governikus.ausweisapp2.START_SERVICE";
        Intent serviceIntent = new Intent(name);
        serviceIntent.setPackage(pkg);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        foregroundDispatcher = new ForegroundDispatcher(this);

    }

    void handleReply(String msg) {
        replyTextView.setText(replyTextView.getText() + "\n@@@@\n" + msg);

        try {
            JSONObject obj = new JSONObject(msg);

            switch (obj.getString("msg")) {
                case "ACCESS_RIGHTS":
                    sendToSdk("{\"cmd\": \"ACCEPT\"}");
                    break;
                case "ENTER_PIN":
                    sendToSdk("{\"cmd\": \"SET_PIN\", \"value\": \"" + passwordTextView.getText() + "\"}");
                    break;
                default:
                    Log.i("msg", obj.getString("msg"));
            }
        } catch (JSONException e) {
            Log.e("msg", msg, e);
        }
    }

    void sendToSdk(String msg) {
        try {
            mSdk.send(mCallback.mSessionID, msg);
            Log.i("send", msg);
        } catch (RemoteException e) {
            Log.e("send", msg, e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    void handleIntent(Intent intent) {
        final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            try {
                mSdk.updateNfcTag(mCallback.mSessionID, tag);
            } catch (RemoteException e) {
                Log.e("sdk", "", e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        foregroundDispatcher.enable();
    }

    @Override
    public void onPause() {
        super.onPause();
        foregroundDispatcher.disable();
    }
}
