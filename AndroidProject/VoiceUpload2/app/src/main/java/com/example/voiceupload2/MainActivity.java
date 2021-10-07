package com.example.voiceupload2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //이거 화면에서 allow 해줘야 함ㅋㅋㅋㅋ
        onCheckPermission();
        initRetrofit();
        task();
        //getFileList();
    }

    public void task(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getFileList();
            }
        };

        timer.schedule(timerTask, 5000);
        //timer.cancel();
    }

    public void onCheckPermission(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST);

            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
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
                    Toast.makeText(this, "앱 실행을 위한 권한이 취소되었씁니다.", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void initRetrofit(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        service = new Retrofit.Builder().baseUrl("http://192.168.10.142:3030")
                .client(client).build().create(ApiService.class);
    }

    private void getFileList(){

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString();

        try{
            File file = new File(filePath);
            File[] fileList = file.listFiles();

            for(int i=0; i<fileList.length; i++){
                if(!fileList[i].getName().contains("checked") && !fileList[i].getName().contains("nomedia")){
                    voiceFileUpload(fileList[i]);
                    renameFile(fileList[i]);
                }
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

        if(ext.equals(".mp3")){
            tempString4 = folderName + tempString4 + "-checked.mp3";
        }else if(ext.equals(".m4a")){
            tempString4 = folderName + tempString4 + "-checked.m4a";
        }

        File changedFile = new File(tempString4);
        originFile.renameTo(changedFile);
    }

    private void voiceFileUpload(File sentFile){
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
                    }else{
                        Log.e(TAG, "Response code : " + response.code() + ", " + response.body() + ", " + response.toString() );
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "onFailure 진입");
                }
            });

        }catch(Exception e){
            Log.e(TAG, "onFailure 진입 Exception" + e.toString());
        }
    }
}