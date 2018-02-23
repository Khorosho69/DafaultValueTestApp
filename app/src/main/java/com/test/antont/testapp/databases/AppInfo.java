package com.test.antont.testapp.databases;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

@Entity
public class AppInfo {

    private final static String PACKAGE_NAME_COLUMN = "package_name";
    private final static String APP_NAME_COLUMN = "app_name";
    private final static String ITEM_STATUS_COLUMN = "item_status";

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = PACKAGE_NAME_COLUMN)
    private String  mPackageName;

    @ColumnInfo(name = APP_NAME_COLUMN)
    private String mAppName;

    @ColumnInfo(name = ITEM_STATUS_COLUMN)
    private String mStatus;
    @Ignore
    private Drawable mAppIcon;

    public AppInfo() {
    }

    public AppInfo(@NonNull String mPackageName, String mAppName, String  mStatus, Drawable mAppIcon) {
        this.mPackageName = mPackageName;
        this.mAppName = mAppName;
        this.mStatus = mStatus;
        this.mAppIcon = mAppIcon;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public Drawable getAppIcon() {
        return mAppIcon;
    }

    public void setAppIcon(Drawable mAppIcon) {
        this.mAppIcon = mAppIcon;

    }
}
