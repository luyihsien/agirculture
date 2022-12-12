package tw.edu.ntu.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;
import tw.edu.ntu.app.db.AppDatabase;
import tw.edu.ntu.app.db.Data;
import tw.edu.ntu.app.request.WeatherRequest;

public class CalendarActivity extends AppCompatActivity {

    private class TemperatureData{
        int dataMonth;
        double mean;
        double maximum;
        double minimum;
        public TemperatureData(int dataMonth, double mean, double maximum, double minimum){
            this.dataMonth = dataMonth;
            this.mean = mean;
            this.maximum = maximum;
            this.minimum = minimum;
        }
    }

    private MCalendarView calendar_cv;
    private RecyclerView calendar_rv;
    private EditText calendar_et;
    private RequestQueue queue;
    private SharedPreferences shared_pf;
    private SharedPreferences.Editor editor;
    private Spinner sp;
    private Map<Integer, TemperatureData> tpData;
    private Map<String, TemperatureData> tp7Data;
    private static Map<Integer, Pair<Integer, Integer>> pre;
    private static Map<Integer, Pair<Integer, Integer>> pos;
    private static Map<Integer, String> preString;
    private static Map<Integer, String> posString;

    GlobalVariable gv;
    DataAdapter dataAdapter;
    Data nowSelectedData;
    DateData nowSelectedDate;
    String selectedStationCity;
    String selectedStationID;
    int preMonth;
    int preYear;

    /**
     00DD00 FF8800 FFB3FF EEEE00 33CCFF 880000 444444  9900FF
     Dot->2 left->3 right->4, default->10
    */
    static{
        preString = new HashMap<>();
        preString.put(0, "[施肥日]");
        preString.put(1, "[追肥一]");
        preString.put(2, "[追肥二]");
        preString.put(3, "[追肥三]");
        preString.put(4, "[追肥四]");
        preString.put(5, "[穗肥]");
        preString.put(6, "[追肥六]");
        preString.put(7, "[收割]");

        posString = new HashMap<>();
        posString.put(0, "[施肥日]");
        posString.put(1, "[追肥一]");
        posString.put(2, "[追肥二]");
        posString.put(3, "[追肥三]");
        posString.put(4, "[穗肥]");
        posString.put(5, "[追肥六]");
        posString.put(6, "[收割]");

        pre = new HashMap<>();
        pre.put(0,new Pair<Integer, Integer>(0xFF00DD00, 3));
        pre.put(1,new Pair<Integer, Integer>(0xFFFF8800, 10));
        pre.put(2,new Pair<Integer, Integer>(0xFFFFB3FF, 10));
        pre.put(3,new Pair<Integer, Integer>(0xFFFFD700, 10));
        pre.put(4,new Pair<Integer, Integer>(0xFF33CCFF, 10));
        pre.put(5,new Pair<Integer, Integer>(0xFF880000, 10));
        pre.put(6,new Pair<Integer, Integer>(0xFF444444, 10));
        pre.put(7,new Pair<Integer, Integer>(0xFF9900FF, 4));

        pos = new HashMap<>();
        pos.put(0,new Pair<Integer, Integer>(0xFF00DD00, 3));
        pos.put(1,new Pair<Integer, Integer>(0xFFFF8800, 10));
        pos.put(2,new Pair<Integer, Integer>(0xFFFFB3FF, 10));
        pos.put(3,new Pair<Integer, Integer>(0xFFFFD700, 10));
        pos.put(4,new Pair<Integer, Integer>(0xFF880000, 10));
        pos.put(5,new Pair<Integer, Integer>(0xFF444444, 10));
        pos.put(6,new Pair<Integer, Integer>(0xFF9900FF, 4));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Stetho.initializeWithDefaults(this);
        findViews();

    }


    private void findViews(){
        queue = Volley.newRequestQueue(this);
        gv = (GlobalVariable)getApplicationContext();
        tpData = new HashMap<>();
        tp7Data = new HashMap<>();

        shared_pf = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = shared_pf.edit();
        sp = findViewById(R.id.calendar_sp);
        sp.setSelection(shared_pf.getInt("lastSelect", 0), true);
        selectedStationID = sp.getSelectedItem().toString().split(",")[1].trim();
        selectedStationCity = sp.getSelectedItem().toString().split(",")[0].split(" ")[0].trim();

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStationID = parent.getItemAtPosition(position).toString().split(",")[1].trim();
                selectedStationCity = parent.getItemAtPosition(position).toString().split(",")[0].split(" ")[0].trim();
                editor.putInt("lastSelect", position);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        calendar_et = findViewById(R.id.calendar_et);
        calendar_cv = findViewById(R.id.calendar_cv);
        calendar_rv = findViewById(R.id.calendar_rv);
        calendar_rv.setLayoutManager(new LinearLayoutManager(this));
        calendar_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //設置分隔線
        setRecyclerFunction(calendar_rv);
        calendar_rv.setAdapter(new DataAdapter(CalendarActivity.this, null, null, calendar_cv));


        calendar_cv.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                nowSelectedDate = date;

                Log.d("TAG", "date click " + nowSelectedDate.getYear() + " " + nowSelectedDate.getMonth() + " " + nowSelectedDate.getDay());

                new Thread(() -> { //顯示這一天有那些東西

                    List<Data> data = AppDatabase.getInstance(CalendarActivity.this).dataDao().findByDate(nowSelectedDate.getYear() , nowSelectedDate.getMonth(), nowSelectedDate.getDay());
                    dataAdapter = new DataAdapter(CalendarActivity.this, data, nowSelectedDate, calendar_cv);
                    runOnUiThread(() -> {
                        calendar_rv.setAdapter(dataAdapter);
                        dataAdapter.setOnItemClickListener((myData)-> {
                            nowSelectedData = myData;
                            calendar_et.setText(myData.content);
                        });
                    });
                }).start();
            }
        });

        calendar_cv.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                Log.d("TAG", "switch month " + year + " " + month);
                nowSelectedDate = null;
                nowSelectedData = null;

                new Thread(() -> {
                    dataAdapter = new DataAdapter(CalendarActivity.this, null, null, calendar_cv);
                    List<Data> data = AppDatabase.getInstance(CalendarActivity.this).dataDao().findByYearMonth(year, month);
                    List<Data> preData = AppDatabase.getInstance(CalendarActivity.this).dataDao().findByYearMonth(preYear, preMonth);
                    runOnUiThread(() -> { //標記之前的東西
                        calendar_rv.setAdapter(dataAdapter);
                        dataAdapter.setOnItemClickListener((myData)-> {
                            nowSelectedData = myData;
                            calendar_et.setText(myData.content);
                        });
                        for(Data d: preData){
                            calendar_cv.unMarkDate(new DateData(d.year, d.month, d.day));
                        }

                        for(Data d: data){
                            Log.d("TAG", "month events " + d.year + " " + d.month + " " + d.day);
                            calendar_cv.markDate(new DateData(d.year, d.month, d.day).setMarkStyle(new MarkStyle(d.type, d.color)));
                        }
                        preMonth = month;
                        preYear = year;
                    });
                }).start();
            }
        });

        update();
   }


    private static class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        private Activity activity;
        private List<Data> data;
        private DateData dateData;
        private MCalendarView mCalendarView;
        private DataAdapter.OnItemClickListener onItemClickListener;

        public DataAdapter(Activity activity, List<Data> data, DateData dateData, MCalendarView mCalendarView) {
            this.activity = activity;
            this.data = data;
            this.dateData = dateData;
            this.mCalendarView = mCalendarView;
        }

        /**建立對外接口*/
        public void setOnItemClickListener(DataAdapter.OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            View view;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(android.R.id.text1);
                view = itemView;
            }
        }
        /**更新資料*/
        public void refreshView() {
            new Thread(()->{
                List<Data> data = AppDatabase.getInstance(activity).dataDao().findByDate(dateData.getYear(),dateData.getMonth(),dateData.getDay());
                this.data = data;
                activity.runOnUiThread(() -> {
                    notifyDataSetChanged();
                });
            }).start();
        }
        /**刪除資料*/
        public void deleteData(int position){
            new Thread(()->{

                List<Data> dataRemove = AppDatabase.getInstance(activity).dataDao().findByRelativeID(data.get(position).relative_id);

                activity.runOnUiThread(()->{
                    notifyItemRemoved(position);
                    refreshView();
                    for (Data o: dataRemove) {
                        DateData date = new DateData(o.year, o.month, o.day);
                        mCalendarView.unMarkDate(date);
                        Log.d("TAG","delete "+ o.year +" " + o.month +" "+ o.day);
                    }
                });

                AppDatabase.getInstance(activity).dataDao().deleteByRelativeID(data.get(position).relative_id);
            }).start();
        }

        @NonNull
        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, null);
            return new DataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
            String display = data.get(position).content;
            if(data.get(position).month < 6){
                display += preString.get(data.get(position).fertilize);
            }else{
                display += posString.get(data.get(position).fertilize);
            }

            holder.tvTitle.setText(display);
            holder.view.setOnClickListener((v)->{
                onItemClickListener.onItemClick(data.get(position));
            });

        }
        @Override
        public int getItemCount() {
            if(data == null){
                return 0;
            }else{
                return data.size();
            }
        }
        /**建立對外接口*/
        public interface OnItemClickListener {
            void onItemClick(Data data);
        }

    }

    /**設置RecyclerView的左滑刪除行為*/
    private void setRecyclerFunction(RecyclerView recyclerView){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() { //設置RecyclerView手勢功能
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch (direction){
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        dataAdapter.deleteData(position);
                        break;
                }
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }



    public void calendarRecordClick(View view) {

        Thread modifyDB = new Thread(() -> {
            String content = calendar_et.getText().toString();
            if(nowSelectedData == null ) { //新增

                if(content.isEmpty()) {
                    Toast.makeText(CalendarActivity.this, "請勿留白！", Toast.LENGTH_LONG).show();
                    return;
                }

                /*
                min    26.6    99.4    185.2    309.5    351.3    764.2    1401.6
                max    109.4   232.0   299.0    667.0    782.0    902.3    1611.2
                avg    70.0    160.2   240.2    397.9    535.0    856.2    1499.6

                min    37.7    153.8   364.6    695.6    1055.7   1681.6
                max    266.4   554.9   868.2    1014.6   1074.1   1848.0
                avg    137.8   330.6   663.1    868.0    1064.9   1761.4
                */

                Calendar c = Calendar.getInstance(); // 1月=0, 2月=1
                c.set(nowSelectedDate.getYear(), nowSelectedDate.getMonth()-1, nowSelectedDate.getDay());
                long relativeID = System.currentTimeMillis() / 1000L;
                double gdd = 0;
                int status = 0;
                boolean chg = false;
                if(c.get(Calendar.MONTH) < 5){ // 1 ~ 5 月
                    TemperatureData tp = tpData.get((c.get(Calendar.MONTH)+1));
                    while(true){
                        if (gdd >= 0 && status == 0){
                            chg = true;
                        }else if(gdd >= 70 && status == 1){
                            chg = true;
                        }else if(gdd >= 160.2 && status == 2){
                            chg = true;
                        }else if (gdd >= 240.2 && status == 3) {
                            chg = true;
                        }else if (gdd >= 397.9 && status == 4){
                            chg = true;
                        }else if (gdd >= 535.0 && status == 5){
                            chg = true;
                        }else if (gdd >= 856.2 && status == 6){
                            chg = true;
                        }else if (gdd >= 1499.6 && status == 7){
                            chg = true;
                        }else if(status == 8){
                            break;
                        }
                        if(chg){
                            int color = pre.get(status).first;
                            int type = pre.get(status).second;
                            int month = c.get(Calendar.MONTH) + 1;

                            Data data = new Data(relativeID, c.get(Calendar.YEAR), month, c.get(Calendar.DAY_OF_MONTH), status, 0, color, type, true, false, content);
                            AppDatabase.getInstance(this).dataDao().insertData(data);

                            DateData date = new DateData(c.get(Calendar.YEAR), month, c.get(Calendar.DAY_OF_MONTH));
                            if(nowSelectedDate.getYear() == c.get(Calendar.YEAR) && month == nowSelectedDate.getMonth()){
                                runOnUiThread(() -> {
                                        calendar_cv.markDate(date.setMarkStyle(new MarkStyle(type, color)));
                                });
                            }
                            status++;
                        }

                        chg = false;
                        gdd += (tp.maximum +tp.minimum) / 2 - 10;
                        c.add(Calendar.DATE, 1);
                    }

                }else {

                    while(true){
                        TemperatureData tp = tpData.get((c.get(Calendar.MONTH)+1));

                        if (gdd >= 0 && status == 0){
                            chg = true;
                        }else if(gdd >= 137.8 && status == 1){
                            chg = true;
                        }else if(gdd >= 330.6 && status == 2){
                            chg = true;
                        }else if (gdd >= 663.1 && status == 3) {
                            chg = true;
                        }else if (gdd >= 868.0 && status == 4){
                            chg = true;
                        }else if (gdd >= 1064.9 && status == 5){
                            chg = true;
                        }else if (gdd >= 1761.4 && status == 6){
                            chg = true;
                        }else if(status == 7){
                            break;
                        }
                        if(chg){

                            int color = pos.get(status).first;
                            int type = pos.get(status).second;
                            int month = c.get(Calendar.MONTH) + 1;

                            Data data = new Data(relativeID, c.get(Calendar.YEAR), month, c.get(Calendar.DAY_OF_MONTH), status, 0, color, type, true,false, content);
                            AppDatabase.getInstance(this).dataDao().insertData(data);

                            DateData date = new DateData(c.get(Calendar.YEAR), month, c.get(Calendar.DAY_OF_MONTH));
                            if(nowSelectedDate.getYear() == c.get(Calendar.YEAR) && month == nowSelectedDate.getMonth()){
                                runOnUiThread(() -> {
                                    calendar_cv.markDate(date.setMarkStyle(new MarkStyle(type, color)));
                                });
                            }
                            status++;
                        }
                        chg = false;
                        gdd += (tp.maximum +tp.minimum) / 2 - 10;
                        c.add(Calendar.DATE, 1);
                    }
                }

                runOnUiThread(() -> {
                    calendar_et.setText("");
                    Toast.makeText(CalendarActivity.this, "資訊新增成功！", Toast.LENGTH_LONG).show();
                });

            }else {

                Data data = new Data(nowSelectedData.record_id, nowSelectedData.relative_id, nowSelectedData.year, nowSelectedData.month,
                                    nowSelectedData.day, nowSelectedData.fertilize, nowSelectedData.gdd,
                                    nowSelectedData.color, nowSelectedData.type, nowSelectedData.history, nowSelectedData.update, content);
                AppDatabase.getInstance(this).dataDao().updateData(data);
                runOnUiThread(() -> {
                    calendar_et.setText("");
                    nowSelectedData = null;
                    Toast.makeText(CalendarActivity.this, "已更新資訊！", Toast.LENGTH_LONG).show();
                });
            }
            dataAdapter.refreshView();
        });




        Response.Listener<String> responseListener = response -> {
            try {
                int i;
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");
                if(success){
                    JSONArray records = jsonResponse.getJSONObject("records").getJSONObject("data").getJSONObject("surfaceObs").getJSONArray("location").getJSONObject(0)
                            .getJSONObject("stationObsStatistics").getJSONObject("temperature").getJSONArray("monthly");
                    if(records.length() < 12){
                        Toast.makeText(CalendarActivity.this, "觀測站資料不齊全請用其他觀測站", Toast.LENGTH_SHORT).show();
                    }else{
                        for(i=0; i <records.length(); i++){
                            if ( records.get(i) instanceof JSONObject ) {
                                JSONObject record = (JSONObject) records.get(i);
                                tpData.put(Integer.parseInt(record.getString("dataMonth")),
                                        new TemperatureData(Integer.parseInt(record.getString("dataMonth")),
                                                Double.parseDouble(record.getString("mean")),
                                                Double.parseDouble(record.getString("maximum")),
                                                Double.parseDouble(record.getString("minimum"))));
                            }
                        }
                        if(nowSelectedDate != null){
                            modifyDB.start();
                        }else{
                            Toast.makeText(CalendarActivity.this, "請先選取日期", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(CalendarActivity.this, "氣象局有病", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(CalendarActivity.this,  "他媽json有鬼", Toast.LENGTH_LONG).show();
            }
        };

        Response.ErrorListener errorListener = error -> {
            Toast.makeText(CalendarActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            error.printStackTrace();
        };

        String url = String.format("https://opendata.cwb.gov.tw/api/v1/rest/datastore/C-B0027-001?Authorization=%s&format=JSON&" +
                "stationId=%s&weatherElement=temperature", gv.getWeather_token(), selectedStationID);
        Log.d("TAG", "平均溫度的url=" + url);
        WeatherRequest weatherRequest = new WeatherRequest(url, responseListener, errorListener);

        queue.add(weatherRequest);

    }


    private void update(){

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        preMonth = month;
        preYear = year;
        nowSelectedDate = new DateData(year, month, day);
        Log.d("TAG", "init day info: " + nowSelectedDate.getYear() + " " + nowSelectedDate.getMonth() + " " + nowSelectedDate.getDay());



        Thread s = new Thread(() -> {
            List<Data> data = AppDatabase.getInstance(CalendarActivity.this).dataDao().findByDate(year, month, day);
            if(data != null){
                for (Data d: data) {
                    if((! d.history && ! d.update) || (d.history && d.fertilize==0)){
                        double gdd = d.gdd;
                        int status = 0;
                        boolean chg = false;

                        if(d.month < 6){
                            for (Map.Entry<String, TemperatureData> entry : tp7Data.entrySet()) {
                            }
                        }else{
                            for (Map.Entry<String, TemperatureData> entry : tp7Data.entrySet()) {
                                System.out.println("key:" + entry.getKey() + ",value:" + entry.getValue());
                            }
                        }
                    }
                }
            }
        });

        Thread a = new Thread(() -> { //select today as init
            try{
                s.join();
            }catch (InterruptedException e){
                Log.d("TAG", e.toString());
            }
            List<Data> data = AppDatabase.getInstance(CalendarActivity.this).dataDao().findByDate(nowSelectedDate.getYear(), nowSelectedDate.getMonth(), nowSelectedDate.getDay());
            dataAdapter = new DataAdapter(CalendarActivity.this, data, nowSelectedDate, calendar_cv);
            runOnUiThread(() -> {
                calendar_rv.setAdapter(dataAdapter);
                dataAdapter.setOnItemClickListener((myData)-> {
                    nowSelectedData = myData;
                    calendar_et.setText(myData.content);
                });
                calendar_cv.markDate(new DateData(year, month, day).setMarkStyle(new MarkStyle(MarkStyle.DEFAULT, 0xFF99BBFF)));
            });
        });

        Thread b = new Thread(() -> { //標記初始
            try{
                a.join();
            }catch (InterruptedException e){
                Log.d("TAG", e.toString());
            }
            List<Data> data = AppDatabase.getInstance(CalendarActivity.this).dataDao().findByYearMonth(year, month);
            runOnUiThread(() -> {
                for(Data d: data){
                    Log.d("TAG", "month events "+d.year+" "+d.month+" "+d.day);
                    calendar_cv.markDate(new DateData(d.year, d.month, d.day).setMarkStyle(new MarkStyle(d.type, d.color)));
                }
            });
        });


        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                if(jsonResponse.getBoolean("success")){

                    JSONArray minTp = jsonResponse.getJSONObject("records").getJSONArray("locations").getJSONObject(0)
                            .getJSONArray("location").getJSONObject(0).getJSONArray("weatherElement").getJSONObject(0).getJSONArray("time");
                    JSONArray maxTP = jsonResponse.getJSONObject("records").getJSONArray("locations").getJSONObject(0)
                            .getJSONArray("location").getJSONObject(0).getJSONArray("weatherElement").getJSONObject(1).getJSONArray("time");

                    if(minTp.length() != 15 || maxTP.length() != 15){
                        Toast.makeText(CalendarActivity.this, "氣象局的問題不甘我的事", Toast.LENGTH_SHORT).show();
                    }else {
                        for (int i = 0; i < 15; i++) {
                            JSONObject recordMin = minTp.getJSONObject(i);
                            JSONObject recordMax = maxTP.getJSONObject(i);

                            String startTime = recordMin.getString("startTime").substring(0,10);
                            double valueMin = Double.parseDouble(recordMin.getJSONArray("elementValue").getJSONObject(0).getString("value"));
                            double valueMax = Double.parseDouble(recordMax.getJSONArray("elementValue").getJSONObject(0).getString("value"));

                            TemperatureData tp = tp7Data.getOrDefault(startTime, null);
                            if(tp == null){
                                tp7Data.put(startTime, new TemperatureData(0,0, valueMax, valueMin));
                            }else{
                                double max = Math.max(valueMax, tp.maximum);
                                double min =  Math.min(valueMin, tp.maximum);
                                tp7Data.put(startTime, new TemperatureData(0,0, max, min));
                            }

                        }
                        s.start();
                    }


                }else{
                    Toast.makeText(CalendarActivity.this, "氣象局有病", Toast.LENGTH_SHORT).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(CalendarActivity.this,  "他媽json有鬼", Toast.LENGTH_LONG).show();
            }
        };

        Response.ErrorListener errorListener = error -> {
            Toast.makeText(CalendarActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            error.printStackTrace();
        };


        String url = String.format("https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-D0047-091?Authorization=%s&format=JSON&locationName=%s&elementName=MinT,MaxT&sort=time",
                gv.getWeather_token(), selectedStationCity);


        WeatherRequest weatherRequest = new WeatherRequest(url, responseListener, errorListener);
        queue.add(weatherRequest);

        a.start();
        b.start();

    }

}