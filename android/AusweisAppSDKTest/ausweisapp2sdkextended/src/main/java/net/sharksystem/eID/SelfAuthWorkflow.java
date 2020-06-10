package net.sharksystem.eID;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SelfAuthWorkflow implements Workflow<UserData> {
    private static final String TAG = "SelfAuthWorkflow";

    private String pin;

    public SelfAuthWorkflow(@NonNull String pin) {
        this.pin = pin;
    }

    @Override
    public @NonNull
    UserData run(OutChan<String> receive, InChan<String> send) throws WorkflowException, InterruptedException {
        if (!pin.matches("\\d{6}")) {
            throw new WorkflowException("Invalid PIN");
        }

        // The following "state machine" is actually more or less stateless; we just assume the workflow
        // is in the right order. Otherwise the workflow will be canceled.

        boolean workflowStarted = false;
        // since we start the workflow with the READE command, this is used to cancel the workflow
        // on a reoccurring READER command
        boolean pinEntered = false;
        // safes the eID holder from a card block, if for some reasons,
        // the ENTER_PIN command is triggered multiple times
        while (true) {
            // blocks
            String json = receive.take();

            Log.d(TAG, "received: " + json);
            try {
                JSONObject obj = new JSONObject(json);

                switch (obj.getString("msg")) {
                    case "READER":
                        if (workflowStarted || obj.isNull("card")) {
                            cancel(send);
                        } else if (obj.getBoolean("attached") && !obj.isNull("card")) {
                            // nfc reader attached; start self auth workflow
                            sendToSdk(send, "{\"cmd\": \"RUN_SELF_AUTH\"}");
                        }
                        break;
                    case "ACCESS_RIGHTS":
                        // just accept them
                        sendToSdk(send, "{\"cmd\": \"ACCEPT\"}");
                        break;
                    case "INSERT_CARD":
                        // the eID card was removed during the workflow -> cancel
                        cancel(send);
                        break;
                    case "ENTER_PIN":
                        if (pinEntered) {
                            throw new WorkflowException("PIN already send");
                        }
                        sendToSdk(send, "{\"cmd\": \"SET_PIN\", \"value\": \"" + pin + "\"}");
                        pinEntered = true;
                        break;
                    case "BAD_STATE":
                        // not a bad sign; might be triggered by a cancel request send twice
                        // but at this state, the workflow has internally ended
                        throw new WorkflowException("Bad workflow state");
                    case "AUTH":
                        if (!obj.has("result")) {
                            workflowStarted = true;
                            break;
                        }

                        JSONObject result = obj.getJSONObject("result");

                        if (isMajorCode(result, "error")
                                && isMinorCode(result, "cancellationByUser")) {
                            throw new WorkflowException("Workflow ended: eID card was removed or workflow cancelled");
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
    }

    @Override
    public void cancel(InChan<String> send) {
        send.add("{\"cmd\": \"CANCEL\"}");
    }

    private boolean isMajorCode(JSONObject result, String code) throws JSONException {
        return result.getString("major").equalsIgnoreCase(
                "http://www.bsi.bund.de/ecard/api/1.1/resultmajor#" + code);
    }

    private boolean isMinorCode(JSONObject result, String code) throws JSONException {
        return result.getString("minor").equalsIgnoreCase(
                "http://www.bsi.bund.de/ecard/api/1.1/resultminor/sal#" + code);
    }

    private void sendToSdk(InChan<String> send, String msg) {
        send.add(msg);
        Log.d(TAG, msg);
    }

    private String jsonOptLike(JSONObject obj, String pattern) {
        Iterator<String> keys = obj.keys();

        while (keys.hasNext()) {
            String k = keys.next();

            if (sanitizeJsonField(k).matches(pattern)) {
                return sanitizeJsonField(obj.optString(k, ""));
            }
        }
        return "";
    }

    private String sanitizeJsonField(String value) {
        return value.toUpperCase().trim().replaceAll("\\s+", " ");
    }

    private UserData jsonToUser(JSONObject obj) {
        UserData u = new UserData();
        u.setAddress(jsonOptLike(obj, "(?i)Address"));
        u.setBirthName(jsonOptLike(obj, "(?i)Birth\\s?name")); // chip generation dependent
        u.setFamilyName(jsonOptLike(obj, "(?i)Family\\s?name"));
        u.setGivenNames(jsonOptLike(obj, "(?i)Given\\s?name(\\(s\\))?"));
        u.setPlaceOfBirth(jsonOptLike(obj, "(?i)(Place\\s?of\\s?birth)|(Birthplace)"));
        u.setDateOfBirth(jsonOptLike(obj, "(?i)(Date\\s?of\\s?birth)|(Birthdate)"));
        u.setDoctoralDegree(jsonOptLike(obj, "(?i)Doctoral\\s?degree"));
        u.setArtisticName(jsonOptLike(obj, "(?i)(Religious\\s?/\\s?)artistic name"));
        u.setNationality(jsonOptLike(obj, "(?i)Nationality"));
        u.setIssuingCountry(jsonOptLike(obj, "(?i)Issuing\\s?country"));
        u.setDocumentType(jsonOptLike(obj, "(?i)Document\\s?type"));
        return u;
    }
}
