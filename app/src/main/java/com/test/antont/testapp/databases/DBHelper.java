package com.test.antont.testapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.test.antont.testapp.models.AppItem;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "AppItemDataBase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table appItem ("
                + "packageName text primary key,"
                + "itemStatus bollean" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<AppItem> readAllAppItems(SQLiteDatabase db) {
        List<AppItem> items = new ArrayList<>();

        Cursor c = db.query("appItem", null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int packageNameColIndex = c.getColumnIndex("packageName");
            int itemStatusColIndex = c.getColumnIndex("itemStatus");

            do {
                items.add(new AppItem(c.getString(packageNameColIndex), Boolean.parseBoolean(c.getString(itemStatusColIndex))));
            } while (c.moveToNext());
        } else
            c.close();
        return items;
    }

    public void writeAllAppItems(SQLiteDatabase db, List<AppItem> appItems){
        for (AppItem item: appItems) {
            ContentValues cv = new ContentValues();
            cv.put("packageName", item.getName());
            cv.put("itemStatus", item.getStatus());

            db.insert("appItem", null, cv);
        }
    }

    public void writeAppItem(SQLiteDatabase db, AppItem item) {
        ContentValues cv = new ContentValues();
        cv.put("packageName", item.getName());
        cv.put("itemStatus", item.getStatus());

        db.insert("appItem", null, cv);
    }

    public void updateAppItem(SQLiteDatabase db, String packageName, Boolean newItemStatus) {
        if (isAppItemExists(db, packageName)) {
            ContentValues cv = new ContentValues();
            cv.put("packageName", packageName);
            cv.put("itemStatus", newItemStatus);

            db.update("appItem", cv, "packageName=" + packageName, null);
        }
    }

    public void deleteAppItem(SQLiteDatabase db, String packageName) {
        if (isAppItemExists(db, packageName)) {
            db.delete("appItem", "packageName=?", new String[]{packageName});
        }
    }

    private static boolean isAppItemExists(SQLiteDatabase db, String packageName) {
        String Query = "Select * from appItem  where  packageName  = " + packageName;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
