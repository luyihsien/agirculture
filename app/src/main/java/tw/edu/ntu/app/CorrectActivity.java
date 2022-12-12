package tw.edu.ntu.app;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CorrectActivity extends AppCompatActivity {
    private SharedPreferences shared_pf;
    private EditText correct_et;
    private EditText corrected_et;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct);
        findViews();
    }

    private void findViews(){
        shared_pf = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = shared_pf.edit();
        correct_et = findViewById(R.id.correct_et);
        corrected_et = findViewById(R.id.corrected_et);
    }

    public void correctSubmitClick(View view){
        String correct_string = correct_et.getText().toString();
        String corrected_string = corrected_et.getText().toString();

        if( correct_string.isEmpty() || corrected_string.isEmpty() ){
            Toast.makeText(this, "請輸入錯誤詞彙", Toast.LENGTH_LONG).show();
        }else{
            editor.putString(correct_string, corrected_string);
            editor.commit();
            Toast.makeText(this, "資料庫已更新成功，請返回上頁 或 繼續輸入", Toast.LENGTH_LONG).show();
        }

    }

    public void correctCancelClick(View view){
        this.finish();
    }
}