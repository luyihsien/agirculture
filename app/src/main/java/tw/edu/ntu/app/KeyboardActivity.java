package tw.edu.ntu.app;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
public class KeyboardActivity extends AppCompatActivity {
    GlobalVariable gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        //
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        //String URL = gv.getWebApiURL()+"/cal";
        String URL="https://agontu.herokuapp.com/";
        WebView web_view = (WebView) findViewById(R.id.web_view);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.loadUrl(URL);
    }

    private  void init(){
        gv = (GlobalVariable) getApplicationContext();
    }
}