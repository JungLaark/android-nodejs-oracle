package com.example.calllog;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @FormUrlEncoded
    @POST("/upload/calllist")
    Call<ResponseBody> postCallList(@Field("numberString") String numberString, @Field("dateString") String dateString, @Field("duration") String duration);
    //Call<CallList> postCallList(@Body JSONObject param);
}
