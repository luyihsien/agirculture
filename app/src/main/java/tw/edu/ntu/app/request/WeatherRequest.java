package tw.edu.ntu.app.request;

import com.android.volley.Response;

import com.android.volley.toolbox.StringRequest;


public class WeatherRequest extends StringRequest {

    public WeatherRequest(String ACTION_REQUEST_URL,  Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, ACTION_REQUEST_URL, listener, errorListener);
    }

}
