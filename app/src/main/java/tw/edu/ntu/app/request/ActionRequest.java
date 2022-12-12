package tw.edu.ntu.app.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import tw.edu.ntu.app.GlobalVariable;

public class ActionRequest extends StringRequest {
    private static final String ACTION_REQUEST_URL = "/api/action";
    private Map<String, String> params;
    private Map<String, String> headers;

    public ActionRequest(String baseURL, String android_id, String tag_id, String name, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, baseURL + ACTION_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("tag_id", tag_id);
        params.put("name", name);

        headers = new HashMap<>();
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
