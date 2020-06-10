package net.sharksystem.eID;

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
import androidx.annotation.Nullable;

import com.governikus.ausweisapp2.IAusweisApp2Sdk;
import com.governikus.ausweisapp2.IAusweisApp2SdkCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The {@link Connector} provides an abstraction to the extended AusweisApp2 SDK.
 */
public class Connector {
    public static class ServiceException extends AndroidException {
        public ServiceException(String msg) {
            super(msg);
        }
    }

    private static class SdkCallback extends IAusweisApp2SdkCallback.Stub {
        String sessionID = null;

        Chan<String> channel = new Chan<>(10);

        @Override
        public void sessionIdGenerated(String pSessionId, boolean pIsSecureSessionId) throws RemoteException {
            Log.d("sdk", "SdkCallback.sessionId: " + pSessionId);
            sessionID = pSessionId;
        }

        @Override
        public void receive(String pJson) throws RemoteException {
            Log.d("sdk", "SdkCallback.received: " + pJson);
            channel.offer(pJson);
        }

        @Override
        public void sdkDisconnected() throws RemoteException {
            sessionID = null;
        }
    }

    private static final String TAG = "Connector";

    private SdkCallback sdkCallback;
    private IAusweisApp2Sdk sdk;
    private Context context;
    private ServiceConnection serviceConnection;

    private Lock workflowLock = new ReentrantLock();

    /**
     * Binds the extended AusweisApp2 sdk to the given context.
     *
     * @param ctx a valid android context.
     * @return {@link SelfAuthWorkflow}
     * @throws ServiceException if the service connection failed or couldn't be bound.
     */
    public static @NonNull
    Connector start(@NonNull Context ctx) throws ServiceException {
        Connector w = new Connector();

        w.sdkCallback = new Connector.SdkCallback();

        w.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                try {
                    w.sdk = IAusweisApp2Sdk.Stub.asInterface(service);
                    Log.d(TAG, "AusweisApp2 service connected");

                    w.sdk.connectSdk(w.sdkCallback);
                    Log.d(TAG, "AusweisApp2 sdk connected");
                } catch (ClassCastException | RemoteException e) {
                    Log.e(TAG, "Couldn't connect to sdk");
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                Log.d(TAG, "AusweisApp2 service disconnected");
                w.sdk = null;
            }
        };

        w.context = ctx;
        String pkg = w.context.getApplicationContext().getPackageName();

        final String name = "com.governikus.ausweisapp2.START_SERVICE";
        Intent serviceIntent = new Intent(name);
        serviceIntent.setPackage(pkg);
        if (w.context.bindService(serviceIntent, w.serviceConnection, Context.BIND_AUTO_CREATE)) {
            return w;
        }
        throw new ServiceException("Couldn't bind service");
    }

    /**
     * Stop or unbind the underlying service connection to the sdk.
     *
     * @return false if {@link #runWorkflow} is still executed or stop was already called.
     */
    public boolean stop() {
        if (sdk == null || !workflowLock.tryLock()) {
            return false;
        }
        context.unbindService(serviceConnection);
        return true;
    }

    /**
     * Run the self auth workflow. A successful authentication may take several seconds,
     * therefore the method should be called on a separate thread.
     * @param tag the nfc tag or null if no nfc tag required.
     * @param workflow
     * @return the result of the workflow.
     * @throws Workflow.WorkflowException if an error occurred during the workflow handling or the workflow was cancelled.
     * @throws InterruptedException
     */
    public <T> T runWorkflow(@Nullable Tag tag, @NonNull Workflow<T> workflow) throws Workflow.WorkflowException, InterruptedException {
        if (sdk == null) {
            throw new Workflow.WorkflowException("Extended AusweisApp2 service not bound! Might be an result of a stop() call.");
        }

        try {
            workflowLock.lock();
            sdkCallback.channel.drain();

            try {
                sdk.updateNfcTag(sdkCallback.sessionID, tag);
            } catch (RemoteException e) {
                throw new Workflow.WorkflowException("Couldn't update nfc tag");
            }

            Chan<String> channel = new Chan<String>(10);

            AtomicBoolean workflowEnded = new AtomicBoolean(false);
            Chan<Object> wait = new Chan<>(1);
            Thread listener = new Thread(() -> {
                try {
                    while (true) {
                        if (workflowEnded.get()) {
                            List<String> l = new ArrayList<>(10);
                            channel.drainTo(l);
                            for (String msg : l) {
                                sdk.send(sdkCallback.sessionID, msg);
                            }
                            return;
                        } else {
                            sdk.send(sdkCallback.sessionID, channel.take());
                        }
                    }
                } catch (Exception e) {
                    wait.offer(e);
                }
            });

            Thread runner = new Thread(() -> {
                try {
                    T result = workflow.run(sdkCallback.channel, channel);
                    wait.put(result);
                } catch (Exception e) {
                    wait.offer(e);
                }
            });

            listener.start();
            runner.start();

            try {
                Object r = wait.take();

                if (r != null) {
                    if (r instanceof InterruptedException) {
                        throw (InterruptedException) r;
                    } else if (r instanceof Workflow.WorkflowException) {
                        throw (Workflow.WorkflowException) r;
                    } else if (r instanceof Exception) {
                        throw new RuntimeException((Exception) r);
                    } else {
                        // everything worked as expected
                        workflowEnded.set(true);
                        listener.join();

                        return (T) r;
                    }
                } else {
                    throw new IllegalStateException();
                }
            } finally {
                // clean up
                runner.interrupt();
                listener.interrupt();
            }
        } finally {
            try {
                // detach the nfc tag from the sdk
                sdk.updateNfcTag(sdkCallback.sessionID, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            workflowLock.unlock();
        }
    }
}
