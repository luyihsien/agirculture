package tw.edu.ntu.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import android.provider.Settings.Secure;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import tw.edu.ntu.app.request.LoginRequest;
import tw.edu.ntu.app.request.RegisterRequest;


public class MainActivity extends AppCompatActivity {
    private String android_id;
    private String token;
    private TextView id_tv;

    private RequestQueue queue;
    GlobalVariable gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    private void findViews() {
        id_tv = findViewById(R.id.id_tv);
        android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        String show ="Android id為:\n"+ android_id;
        id_tv.setText(show);
        gv = (GlobalVariable) getApplicationContext();
        gv.setAndroid_id(android_id);
        gv.setWeather_token("CWB-3FC882FE-25DE-4250-B991-A484F53D341B");
        queue = Volley.newRequestQueue(this);
    }

    public void mainSubmitClick(View view) {
        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                token = jsonResponse.getString("token");
                if (!token.isEmpty()) {
                    gv.setToken(token);
                    Intent intent = new Intent(MainActivity.this, MethodActivity.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                } else {
                    int errorCode = jsonResponse.getInt("code");
                    String errorMessage = jsonResponse.getString("msg");
                    Toast.makeText(MainActivity.this, errorMessage + " " + errorCode, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, R.string.login_fail, Toast.LENGTH_LONG).show();
            }
        };

        Response.ErrorListener errorListener = error -> {
            Toast.makeText(MainActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
            error.printStackTrace();
        };

        LoginRequest loginRequest = new LoginRequest(gv.getWebApiURL(), android_id, responseListener, errorListener);

        queue.add(loginRequest);
    }

    public void mainRegisterClick(View view) {

        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                Toast.makeText(MainActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, R.string.register_fail, Toast.LENGTH_LONG).show();
            }
        };

        Response.ErrorListener errorListener = error -> {
            Toast.makeText(MainActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
            error.printStackTrace();
        };

        RegisterRequest registerRequest = new RegisterRequest(gv.getWebApiURL(), android_id, responseListener, errorListener);

        queue.add(registerRequest);
    }

}