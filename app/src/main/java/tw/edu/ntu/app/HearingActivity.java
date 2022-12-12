package tw.edu.ntu.app;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class HearingActivity extends AppCompatActivity {
    String TAG = "#QR#";
    String COLOR_GOOD = "#CCFF80";
    String COLOR_BAD = "#FFD9EC";
    String JWT_TOKEN = "";
    float TEXT_SIZE = 24;
    TextToSpeech t1;
    EditText ed1;
    Button b1;

    GlobalVariable gv;
    RequestQueue requestQueue;

    Map<Integer, JSONObject> tags = new HashMap<>(); // (tag_id, tag)
    Map<Integer, JSONObject> actions = new HashMap<>(); // (action_id, action)
    Map<Integer, TextView> tags_tv = new HashMap<>(); // (tag_id, TextView)
    Map<Integer, Integer> record = new HashMap<>(); // (tag_id, action_id)

    LinearLayout ll;
    Button submit_btn;
    TextView textView;
    Map<String, String> mistake_map = new HashMap<>();
    String Voice_input="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mistake_map.put("糙米免治盒包裝","糙米碾製和包裝");
        mistake_map.put("一飲機","曳引機");
        mistake_map.put("苦茶博","苦茶粕");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing);
        textView = findViewById(R.id.textView);
        ed1=(EditText)findViewById(R.id.editText);
        b1=(Button)findViewById(R.id.button1);
        ll = findViewById(R.id.ll);
        gv = (GlobalVariable) getApplicationContext();
        JWT_TOKEN = gv.getToken();
        Log.i(JWT_TOKEN, gv.getToken());
        requestQueue = Volley.newRequestQueue(getApplication());
        requestQueue.start(); // Start the queue
        String tagURL = gv.getWebApiURL() + "/tag";
        JsonArrayRequest getTagsRequest = new JsonArrayRequest(Request.Method.GET, tagURL, new JSONArray(), new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {

        }
        TextView tv = new TextView(HearingActivity.this);
        tv.setText("請掃描 [] 的 QR Code");
        tv.setTextSize(TEXT_SIZE);
        tv.setPadding(50, 20, 50, 20);
        tv.setBackgroundColor(Color.parseColor(COLOR_BAD));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 10, 20, 10);
        tv.setLayoutParams(lp);
        ll.addView(tv);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.CHINESE);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = ed1.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }


    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            textView.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            Voice_input=textView.getText().toString();
            Iterator keys = mistake_map.keySet().iterator();
            while(keys.hasNext()){
                String key = (String)keys.next();
                if(Voice_input.equals(key)){
                    textView.setText(mistake_map.get(Voice_input));
                }
            }
        }
    }

    @Nullable
    @Override
    public ActionMode startSupportActionMode(@NonNull ActionMode.Callback callback) {
        return super.startSupportActionMode(callback);
    }



}