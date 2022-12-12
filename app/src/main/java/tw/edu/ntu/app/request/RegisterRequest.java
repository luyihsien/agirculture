package tw.edu.ntu.app.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "/auth/user";
    private Map<String,String> params;

    public RegisterRequest(String baseURL, String android_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, baseURL + LOGIN_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("uuid", android_id);
        params.put("password", android_id);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
