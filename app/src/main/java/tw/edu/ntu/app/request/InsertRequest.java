package tw.edu.ntu.app.request;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class InsertRequest extends JsonObjectRequest {
    private static final String INSERT_REQUEST_URL = "/api/record";
    private Map<String, String> params;
    private Map<String, String> headers;

    public InsertRequest(String baseURL, String android_id, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, baseURL + INSERT_REQUEST_URL, jsonRequest, listener, errorListener);
        params = new HashMap<>();


        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + android_id);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}