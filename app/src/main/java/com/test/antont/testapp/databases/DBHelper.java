package com.test.antont.testapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.test.antont.testapp.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "AppItemDataBase", null, 1);
    }

    private static boolean isAppInfoExists(SQLiteDatabase db, String packageName) {
        String query = "SELECT * FROM applicationsInfo WHERE packageName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{packageName});

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applicationsInfo ("
                + "packageName text primary key,"
                + "applicationName text,"
                + "itemStatus text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<AppInfo> readAppInfoList(SQLiteDatabase db) {
        List<AppInfo> items = new ArrayList<>();

        Cursor c = db.rawQuery("select * from applicationsInfo", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String packageName = c.getString(c.getColumnIndex("packageName"));
                String applicationName = c.getString(c.getColumnIndex("applicationName"));

                Boolean status = Boolean.parseBoolean(c.getString(c.getColumnIndex("itemStatus")));

                items.add(new AppInfo(packageName, applicationName, status));
                c.moveToNext();
            }
        }
        c.close();
        return items;
    }

    public void writeAppInfoList(SQLiteDatabase db, List<AppInfo> packageNameList) {
        for (AppInfo item : packageNameList) {
            if (!isAppInfoExists(db, item.getPackageName())) {
                ContentValues cv = new ContentValues();
                cv.put("packageName", item.getPackageName());
                cv.put("applicationName", item.getAppName());
                cv.put("itemStatus", "true");

                db.insert("applicationsInfo", null, cv);
            }
        }
    }

    public void writeAppInfo(SQLiteDatabase db, AppInfo item) {
        ContentValues cv = new ContentValues();
        cv.put("packageName", item.getPackageName());
        cv.put("applicationName", item.getAppName());
        cv.put("itemStatus", "true");

        db.insert("applicationsInfo", null, cv);
    }

    public void updateAppInfo(SQLiteDatabase db, AppInfo item) {
        if (isAppInfoExists(db, item.getPackageName())) {
            ContentValues cv = new ContentValues();
            cv.put("packageName", item.getPackageName());
            cv.put("applicationName", item.getAppName());
            cv.put("itemStatus", Boolean.toString(item.getStatus()));

            db.update("applicationsInfo", cv, "packageName=?", new String[]{item.getPackageName()});
        }
    }

    public void deleteAppInfo(SQLiteDatabase db, String packageName) {
        if (isAppInfoExists(db, packageName)) {
            db.delete("applicationsInfo", "packageName=?", new String[]{packageName});
        }
    }
}
