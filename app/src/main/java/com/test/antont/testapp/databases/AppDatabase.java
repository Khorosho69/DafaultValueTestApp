package com.test.antont.testapp.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {AppInfo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppInfoDao userDao();
}
