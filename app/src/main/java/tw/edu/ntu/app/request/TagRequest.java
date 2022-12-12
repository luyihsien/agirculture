package tw.edu.ntu.app.request;


import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;


import org.json.JSONArray;



public class TagRequest extends JsonArrayRequest {
    private static final String TAG_REQUEST_URL = "/tag";

    public TagRequest(String baseURL, @Nullable JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(Method.GET, baseURL + TAG_REQUEST_URL, jsonRequest, listener, errorListener);

    }

}