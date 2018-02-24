package com.test.antont.testapp.databases;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface AppInfoDao {

    @Query("SELECT item_status FROM appinfo WHERE package_name LIKE :packageName")
    String getAppStatusByPackageName(String packageName);

    @Query("SELECT * FROM appinfo WHERE package_name LIKE :packageName LIMIT 1")
    AppInfo findItemByPackageName(String packageName);

    @Query("DELETE FROM appinfo WHERE package_name LIKE :packageName")
    void deleteItemByPackageName(String packageName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppItem(AppInfo item);
}
