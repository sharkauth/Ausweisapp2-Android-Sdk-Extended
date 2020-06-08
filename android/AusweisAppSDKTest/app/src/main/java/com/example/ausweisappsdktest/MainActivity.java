package com.example.ausweisappsdktest;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.sharksystem.eID.SelfAuthWorkflow;
import net.sharksystem.eID.UserData;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {
    ForegroundDispatcher foregroundDispatcher;
    SelfAuthWorkflow extSdk;

    TextView passwordTextView;
    TextView replyTextView;
    ProgressBar progressBar;

    ExecutorService es;
    Future task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordTextView = findViewById(R.id.password);
        replyTextView = findViewById(R.id.reply);

        progressBar = findViewById(R.id.progress_loader);
        progressBar.setVisibility(View.INVISIBLE);

        es = Executors.newSingleThreadExecutor();

        try {
            extSdk = SelfAuthWorkflow.start(this);
        } catch (SelfAuthWorkflow.WorkflowException e) {
            Log.e("sdk", "", e);
        }

        foregroundDispatcher = new ForegroundDispatcher(this);
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
                if (task != null) {
                    task.get(100, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException ignored) {
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            progressBar.setVisibility(View.VISIBLE);
            passwordTextView.setEnabled(false);
            task = es.submit(() -> {
                try {
                    UserData user = extSdk.runSelfAuth(tag, passwordTextView.getText().toString());
                    Log.i("eID user data", user.toString());

                    runOnUiThread(() -> replyTextView.setText(user.toString()));
                } catch (SelfAuthWorkflow.WorkflowException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
                    Log.w("sdk", "runSelfAuth failed", e);
                } catch (InterruptedException ignored) {
                } finally {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        passwordTextView.setEnabled(true);
                    });
                }
            });
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

        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        extSdk.stop();
    }
}
