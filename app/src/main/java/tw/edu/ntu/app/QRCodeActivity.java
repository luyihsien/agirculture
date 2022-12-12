package tw.edu.ntu.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tw.edu.ntu.app.request.InsertRequest;

public class QRCodeActivity extends AppCompatActivity {
    String TAG = "#QR#";
    String COLOR_GOOD = "#CCFF80";
    String COLOR_BAD = "#FFD9EC";
    String JWT_TOKEN = "";
    float TEXT_SIZE = 24;

    GlobalVariable gv;
    RequestQueue requestQueue;

    Map<Integer, JSONObject> tags = new HashMap<>(); // (tag_id, tag)
    Map<Integer, JSONObject> actions = new HashMap<>(); // (action_id, action)
    Map<Integer, TextView> tags_tv = new HashMap<>(); // (tag_id, TextView)
    Map<Integer, Integer> record = new HashMap<>(); // (tag_id, action_id)

    LinearLayout ll;
    Button submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        gv = (GlobalVariable) getApplicationContext();
        JWT_TOKEN = gv.getToken();
        Log.i(JWT_TOKEN, gv.getToken());

        ll = findViewById(R.id.ll);

        requestQueue = Volley.newRequestQueue(getApplication());
        requestQueue.start(); // Start the queue

// region 取得所有要申報的項目(tags)
        String tagURL = gv.getWebApiURL() + "/tag";
        JsonArrayRequest getTagsRequest = new JsonArrayRequest(Request.Method.GET, tagURL, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0, len = response.length(); i < len; ++i) {
                    try {
                        JSONObject tag = response.getJSONObject(i);
                        tags.put(tag.getInt("tag_id"), tag);
                        TextView tv = new TextView(getApplicationContext());
                        tv.setText("請掃描 [" + tag.getString("name") + "] 的 QR Code");
                        tv.setTextSize(TEXT_SIZE);
                        tv.setPadding(50, 20, 50, 20);
                        tv.setBackgroundColor(Color.parseColor(COLOR_BAD));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(20, 10, 20, 10);
                        tv.setLayoutParams(lp);
                        ll.addView(tv);
                        tags_tv.put(tag.getInt("tag_id"), tv);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

                        e.printStackTrace();


                    }
                }
                Log.i(TAG, tags.toString());

                // region 送出申報的按鈕
                submit_btn = new Button(getApplicationContext());
                submit_btn.setText("送出申報");
                submit_btn.setTextSize(TEXT_SIZE);
                submit_btn.setPadding(50, 50, 50, 50);
                submit_btn.setVisibility(View.GONE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(20, 20, 20, 20);
                submit_btn.setLayoutParams(lp);
                submit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 準備資料
                        JSONArray actionIDs = new JSONArray();
                        for (Map.Entry<Integer, Integer> entry : record.entrySet()) {
                            actionIDs.put(entry.getValue());
                        }
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("actions", actionIDs);
                            Log.i("TAG", obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // region 申報一筆紀錄
                        InsertRequest insertRecordRequest = new InsertRequest(gv.getWebApiURL(), JWT_TOKEN, obj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.has("record_id")) {
                                    try {
                                        int rid = response.getInt("record_id");
                                        Toast.makeText(getApplicationContext(), "申報成功! \n" +
                                                "( record_id = " + Integer.toString(rid) + " )", Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    // TODO: 返回上一頁(?)
                                } else {
                                    Toast.makeText(getApplicationContext(), "申報失敗!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.toString() + error.getNetworkTimeMs(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                        });
                        requestQueue.add(insertRecordRequest);
                        // endregion
                    }
                });
                ll.addView(submit_btn);
                // endregion
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        requestQueue.add(getTagsRequest);
// endregion

// region 取得action的清單(等等用來查表 取得tag_id)
        String actionURL = gv.getWebApiURL() + "/action";
        JsonArrayRequest getActionsRequest = new JsonArrayRequest(Request.Method.GET, actionURL, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0, len = response.length(); i < len; ++i) {
                    try {
                        JSONObject action = response.getJSONObject(i);
                        actions.put(action.getInt("action_id"), action);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, actions.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        requestQueue.add(getActionsRequest);
// endregion

// region 新增申報資料條目的按鈕 (啟動掃描QR code功能的按鈕)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(QRCodeActivity.this);
                integrator.setOrientationLocked(false);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan a QR code");
                integrator.setCameraId(0); // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
// endregion
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                int action_id = Integer.parseInt(result.getContents());
                JSONObject action = actions.get(action_id); // !
                JSONObject tag;
                try {
                    tag = tags.get(action.getInt("tag_id")); // !

                    TextView tv = tags_tv.get(tag.getInt("tag_id"));
                    tv.setText(tag.getString("name") + "：" + action.getString("name"));
                    tv.setBackgroundColor(Color.parseColor(COLOR_GOOD));

                    record.put(action.getInt("tag_id"), action.getInt("action_id"));

                    if (checkRecord(this.tags, this.record) == true) {
                        submit_btn.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 檢查紀錄是否完備
    private boolean checkRecord(Map<Integer, JSONObject> tags, Map<Integer, Integer> record) {
        for (Map.Entry<Integer, JSONObject> entry : tags.entrySet()) {
            // entry.getKey()
            // entry.getValue()
            if (record.containsKey(entry.getKey()) == false) {
                return false;
            }
        }
        return true;
    }
}