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

    private static final String APPLICATIONS_TABLE_NAME = "applicationsInfo";

    private static final String PACKAGE_NAME_FIELD = "packageName";
    private static final String APPLICATION_NAME_FIELD = "applicationName";
    private static final String ITEM_STATUS_FIELD = "itemStatus";

    public DBHelper(Context context) {
        super(context, "AppItemDataBase", null, 1);
    }

    private static boolean isAppInfoExists(SQLiteDatabase db, String packageName) {
        String query = "SELECT * FROM " + APPLICATIONS_TABLE_NAME + " WHERE " + PACKAGE_NAME_FIELD + " = ?";
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
        db.execSQL("create table " + APPLICATIONS_TABLE_NAME + "( "
                + PACKAGE_NAME_FIELD + " text primary key,"
                + APPLICATION_NAME_FIELD + " text,"
                + ITEM_STATUS_FIELD + " text );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<AppInfo> readAppInfoList(SQLiteDatabase db) {
        List<AppInfo> items = new ArrayList<>();

        Cursor c = db.rawQuery("select * from " + APPLICATIONS_TABLE_NAME, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String packageName = c.getString(c.getColumnIndex(PACKAGE_NAME_FIELD));
                String applicationName = c.getString(c.getColumnIndex(APPLICATION_NAME_FIELD));

                Boolean status = Boolean.parseBoolean(c.getString(c.getColumnIndex(ITEM_STATUS_FIELD)));

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
                cv.put(PACKAGE_NAME_FIELD, item.getPackageName());
                cv.put(APPLICATION_NAME_FIELD, item.getAppName());
                cv.put(ITEM_STATUS_FIELD, "true");

                db.insert(APPLICATIONS_TABLE_NAME, null, cv);
            }
        }
    }

    public void writeAppInfo(SQLiteDatabase db, AppInfo item) {
        ContentValues cv = new ContentValues();
        cv.put(PACKAGE_NAME_FIELD, item.getPackageName());
        cv.put(APPLICATION_NAME_FIELD, item.getAppName());
        cv.put(ITEM_STATUS_FIELD, "true");

        db.insert(APPLICATIONS_TABLE_NAME, null, cv);
    }

    public void updateAppInfo(SQLiteDatabase db, AppInfo item) {
        if (isAppInfoExists(db, item.getPackageName())) {
            ContentValues cv = new ContentValues();
            cv.put(PACKAGE_NAME_FIELD, item.getPackageName());
            cv.put(APPLICATION_NAME_FIELD, item.getAppName());
            cv.put(ITEM_STATUS_FIELD, Boolean.toString(item.getStatus()));

            db.update(APPLICATIONS_TABLE_NAME, cv, PACKAGE_NAME_FIELD + "=?", new String[]{item.getPackageName()});
        }
    }

    public void deleteAppInfo(SQLiteDatabase db, String packageName) {
        if (isAppInfoExists(db, packageName)) {
            db.delete(APPLICATIONS_TABLE_NAME, PACKAGE_NAME_FIELD + "=?", new String[]{packageName});
        }
    }
}
