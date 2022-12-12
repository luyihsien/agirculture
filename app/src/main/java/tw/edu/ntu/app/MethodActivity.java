package tw.edu.ntu.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Calendar;

public class MethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);
    }
    public void calendarClick(View view){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    public void hearingClick(View view){
        Intent intent = new Intent(this, HearingActivity.class);
        startActivity(intent);
    }

    public void keyboardClick(View view){
        Intent intent = new Intent(this, KeyboardActivity.class);
        startActivity(intent);
    }

    public void QRCodeClick(View view){
        Intent intent = new Intent(this,QRCodeActivity.class);
        startActivity(intent);
    }

    public void correctClick(View view){
        Intent intent = new Intent(this, HearingActivity.class);
        startActivity(intent);
    }
    public void gotoWeb(View view){
        Intent intent = new Intent(this,FlowerActivity.class);
        startActivity(intent);
    }
}