package com.cesarparent.netnotes.sync;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cesar on 03/03/2016.
 * 
 */
public class APIResponse {
    
    public static final int INVALID_STATUS =    -3;
    public static final int CONNECTION_ERROR =  -2;
    public static final int DATA_ERROR =        -1;
    public static final int SUCCESS =           200;
    public static final int BAD_REQUEST =       400;
    public static final int UNAUTHORIZED =      401;
    public static final int CONFLICT =          409;
    public static final int SERVER_ERROR =      500;
    
    private int         _status;
    private String _transaction;
    private JSONObject  _body;
    
    public APIResponse(int status) {
        _status = status;
        _body = null;
    }
    
    public APIResponse(String data, int status, String time) {
        _status = status;
        try {
            _body = new JSONObject(data);
            Log.d("APIResponse", "Received Payload: " + _body);
        }
        catch(JSONException e) {
            _body = null;
            _status = DATA_ERROR;
        }
        _transaction = time;
    }
    
    public String getTransactionID() {
        return _transaction;
    }
    
    public int getStatus() {
        return _status;
    }
    
    public JSONObject getBody() {
        return _body;
    }
    
    public JSONArray getChangeSet() {
        try {
            return _body.getJSONArray("changes");
        }
        catch(JSONException e) {
            return null;
        }
    }
}
