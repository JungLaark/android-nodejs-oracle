package com.example.voiceupload2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;
    //ContentValues cv;

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS RECODED_VOICE_TBL(" +
                    "FILE_NAME TEXT PRIMARY KEY," +
                    "PHONE_NUMBER TEXT," +
                    "CREATED_DATE TEXT)";

            db.execSQL(sql);
        } catch (Exception e) {
            Log.e("onCreate : ", e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        String sql = "DROP TABLE IF EXISTS RECODED_VOICE_TBL";
//
//        db.execSQL(sql);
//        onCreate(db);
//        ;
    }

    public void insertRecordedVoice(String fileName, String phoneNum) {

        try {
            db = this.getWritableDatabase();
            //cv = new ContentValues();

            //시간은 그냥 db query로 넣어주는게 더 나을거같음
//        String date = LocalDateTime.now().getYear() + "-"
//                + LocalDateTime.now().getMonth().toString()

//        cv.put("FILE_NAME", fileName);
//        cv.put("PHONE_NUMBER", phoneNum);
//        cv.put("CREATED_DATE", )

            String sql = "INSERT INTO RECODED_VOICE_TBL VALUES('" + fileName + "','" + phoneNum + "',"
                    + "DATETIME('NOW', 'LOCALTIME'))";

            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            Log.e("onCreate : ", e.toString());
        }

    }

    public int selectRecodedVoice(String fileName) {
        int count = 0;
        try {

            db = this.getReadableDatabase();

            String sql = "SELECT COUNT(*) FROM RECODED_VOICE_TBL " +
                    "WHERE FILE_NAME = '" + fileName + "'";

            Cursor cursor = db.rawQuery(sql, null);
            count = cursor.getCount() - 1;

            cursor.close();

        } catch (Exception e) {
            Log.e("selectRecodedVoice : ", e.toString());
        }
        return count;
    }
}

