package com.example.voiceupload2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

public class MainActivity extends AppCompatActivity {

    ApiService service;
    static final int PERMISSIONS_REQUEST = 0x0000001;
    //static final int PERMISSIONS_REQUEST_CALL = 200;
    private static final String TAG = "MainActivity";
    //private static final String URL = "http://192.168.10.142:3030";
    private static final String URL = "http://61.32.218.74:28100";
    //private static final String URL = "http://192.168.0.44:28100";
    EditText editTextFilePath;
    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //이거 화면에서 allow 해줘야 함ㅋㅋㅋㅋ

        dbHelper = new DBHelper(MainActivity.this, "RECODED_VOICE.db", null, 1);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);;

        //ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_CALL);

        editTextFilePath = findViewById(R.id.txtFilePath);
        editTextFilePath.setText("/storage/emulated/0/Download/");
        //editTextFilePath.setText("/storage/emulated/0/Call/");

        onCheckPermission();
        initRetrofit();

        //타이머
        task();

        //통화목록 db 저장
        //getCallHistory();
    }

    public void task(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getFileList();
                Log.i(TAG, "task 함수");
            }
        };

        timer.schedule(timerTask, 0,1000);
        //timer.cancel();
    }

    public void onCheckPermission(){

//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
//        != PackageManager.PERMISSION_GRANTED){
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_CALL_LOG
//                                }
//                                , PERMISSIONS_REQUEST_CALL);
//        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
               ){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
             ){
                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ,Manifest.permission.READ_EXTERNAL_STORAGE
                                },
                        PERMISSIONS_REQUEST);

            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ,Manifest.permission.READ_EXTERNAL_STORAGE
                                    },
                        PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정되었습니다", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "앱 실행을 위한 권한이 취소되었습니다.", Toast.LENGTH_LONG).show();
                }
//            case PERMISSIONS_REQUEST_CALL:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "앱 실행을 위한 권한이 설정되었습니다", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(this, "앱 실행을 위한 권한이 취소되었습니다.", Toast.LENGTH_LONG).show();
//                }

        }
    }

    private void initRetrofit(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        service = new Retrofit.Builder().baseUrl(URL)
                .client(client).build().create(ApiService.class);
    }

    private void getFileList(){

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString();

        filePath = editTextFilePath.getText().toString();

        try{
            File file = new File(filePath);
            File[] fileList = file.listFiles();

            if(fileList.length > 0){
                //파일 명을 조건으로 두지말고
                //db에서 해당 파일 이름 조회해서 있는지 없는지 체크해서
                //파일이 없으면 해당 파일 전송하고 sqlite 에 넣고
                for(int i=0; i<fileList.length; i++){
                    if(!fileList[i].getName().contains("checked") && !fileList[i].getName().contains("nomedia")){

                        if(dbHelper.selectRecodedVoice(fileList[i].getName()) <= 0){
                            //파일 명 db 저장
                            if(dbHelper.selectRecodedVoice(fileList[i].getName()) < 0){
                                dbHelper.insertRecordedVoice(fileList[i].getName(), "010-1111-1111");
                            }
                            voiceFileUpload(fileList[i]);
                        }
                    }
                }
            }else{
                Toast.makeText(getApplicationContext(), "저장되어있는 녹음파일이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            Log.v(TAG, e.toString());
        }
    }

    private void renameFile(File originFile){

        String tempString = originFile.getAbsolutePath();
        ///storage/emulated/0/Download/6505551212_0_1633583349243.mp3
        String tempString2 = originFile.getPath();
        ///storage/emulated/0/Download/6505551212_0_1633583349243.mp3
        String tempString3 = originFile.getName();
        //6505551212_0_1633583349243.mp3
        String ext = tempString3.substring(tempString3.length()-4, tempString3.length());
        //확장자 명f

        String tempString4 = tempString3.substring(0, tempString3.length()-4);
        //파일명

        String folderName = "/storage/emulated/0/Download/";
        //String folderName = "/storage/emulated/0/Call/";

        if(ext.equals(".mp3")){
            tempString4 = folderName + tempString4 + "-checked.mp3";
        }else if(ext.equals(".m4a")){
            tempString4 = folderName + tempString4 + "-checked.m4a";
        }

        File changedFile = new File(tempString4);
        originFile.renameTo(changedFile);
    }

    private boolean voiceFileUpload(File sentFile){
        try{
            File file = sentFile;
            //파일 사이즈가 왜 0가 될까.
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            byte[] byteData = bos.toByteArray();
//
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(byteData);
//            fos.flush();
//            fos.close();

            //RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            //원래는 이거였음
            //MultipartBody.Part body2 = MultipartBody.Part.create(reqFile);
            //MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
            //RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload voice file");

            //Call<ResponseBody> req = service.postImage(body, name);
            //Call<ResponseBody> req = service.postImage(body);
            //Call<ResponseBody> req = service.postImage(body2);


            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/*"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("upload", file.getName(), fileBody);
            Call<ResponseBody> req = service.postImage(filePart);
            //당연 파일을 사이즈 0인걸 보내니깐 당연 오는 사이즈도 0 아니겠나 ㅋㅋㅋㅋㅋㅌㅋ
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200){
                        Log.v(TAG, "Uploaded Successfully");
                        Toast.makeText(getApplicationContext(), "파일 업로드 완료", Toast.LENGTH_LONG).show();
                    }else{
                        Log.e(TAG, "Response code : " + response.code() + ", " + response.body() + ", " + response.toString() );
                        Toast.makeText(getApplicationContext(), "파일 업로드 실패" + "Response code : " + response.code() + ", " + response.body() + ", " + response.toString(), Toast.LENGTH_LONG).show();
                    }
                  //  renameFile(file);
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "onFailure 진입" + t.toString());
                    Toast.makeText(getApplicationContext(), "파일 업로드 실패" + t.toString(), Toast.LENGTH_LONG).show();
                }
            });

            return true;

        }catch(Exception e){
            Log.e(TAG, "onFailure 진입 Exception" + e.toString());
            return false;
        }
    }

    public void getCallHistory(){

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

        if(cursor != null){
            cursor.moveToFirst();

            while(cursor.moveToNext()){
                dateString = dateFormat.format(new Date(cursor.getLong(0)));
                number = cursor.getString(1);
                duration = cursor.getString(2);
                //그냥 이거 바로 db에 넣으면 되는거아님?

            }
        }
    }
}