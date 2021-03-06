package com.cesarparent.netnotes.sync.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cesarparent.utils.NotificationCenter;
import com.cesarparent.netnotes.model.Model;
import com.cesarparent.netnotes.sync.APIRequest;
import com.cesarparent.netnotes.sync.APIResponse;
import com.cesarparent.netnotes.sync.Authenticator;
import com.cesarparent.netnotes.sync.Sync;
import com.cesarparent.utils.Notification;
import com.cesarparent.utils.Utils;
import org.json.JSONException;

/**
 * Created by cesar on 04/03/2016.
 * 
 * APILoginOperation is used to send login or sign-up requests to the server in a background thread. 
 */
public class APILoginOperation extends AsyncTask<String, Void, APIResponse> {

    private Sync.ResultCallback _onResult;  // The callback called when the request finishes.
    private String              _email;     // Used to store the user's email after doInBackground.
    private boolean             _signup;    // Whether this is a sign-up of login request.


    /**
     * Creates a new APILoginOperation.
     * @param signup        Whether this is a sign-up of login request.
     * @param onResult      A callback that will be called on request return. 
     */
    public APILoginOperation(boolean signup, @Nullable Sync.ResultCallback onResult) {
        _onResult = onResult;
        _signup = signup;
    }

    /**
     * Sends the login request.
     * @param params    The login credentials, email + password.
     * @return  An APIResponse object that represents the server's response.
     */
    @NonNull
    @Override
    protected APIResponse doInBackground(String... params) {
        // This is a programming error, and should never be triggered in deploy.
        // If there anything else than two parameters, fail.
        if(params.length != 2) {
            Log.e("APILoginOperation", "Invalid number of parameters given for Login");
            cancel(true);
            System.exit(2);
        }
        _email = params[0];
        APIRequest.Endpoint e = _signup ? APIRequest.Endpoint.SIGNUP : APIRequest.Endpoint.LOGIN;
        APIRequest request = new APIRequest(e, "0");
        request.setAuthtorization(Utils.HTTPBasicAuth(params[0], params[1]));
        return request.send();
    }

    /**
     * Dispatches the response's status to the callback, and registers the credentials in
     * SharedPreferences if the login was successful
     * @param response  The server's response.
     */
    @Override
    protected void onPostExecute(@NonNull APIResponse response) {
        Authenticator.invalidateSyncDates();
        Model.flushDeleted();   // flush the deletes table to prevent deleting data from the server.
        if(response.getStatus() == Sync.Status.SUCCESS && response.getBody() != null) {
            try {
                String token = response.getBody().getString("token");
                Authenticator.setCredentials(_email, token);
                NotificationCenter.postNotification(Notification.LOGIN_SUCCESS);
                callback(Sync.Status.SUCCESS);
            }
            catch(JSONException e) {
                Log.e("APILoginOperation", "Invalid Response Format");
                callback(Sync.Status.FAIL);
            }
        } else {
            Authenticator.invalidateCredentials();
            NotificationCenter.postNotification(Notification.LOGIN_FAIL);
            Log.e("APILoginOperation", "Failed to log in: " + response.getStatus());
            callback(response.getStatus());
        }
    }

    /**
     * Wraps callback calls in a null-check
     * @param status    The status to forward to the callback, if it exists.
     */
    private void callback(Sync.Status status) {
        if(_onResult != null) {
            _onResult.onSyncResult(status);
        }
    }
}
