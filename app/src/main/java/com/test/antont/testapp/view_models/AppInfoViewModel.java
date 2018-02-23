package com.test.antont.testapp.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.test.antont.testapp.databases.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfoViewModel extends AndroidViewModel {

    private List<AppInfo> mAppInfo = new ArrayList<>();

    public AppInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public void addAppItems(List<AppInfo> items){
        mAppInfo.addAll(items);
    }

    public void addAppInfo(AppInfo item) {
        mAppInfo.add(item);
    }

    public void removeAppInfoItem(AppInfo item) {
        if (mAppInfo.contains(item)) {
            mAppInfo.remove(item);
        }
    }

    public void removeItemByPackageName(String packageName) {
        for (AppInfo item : mAppInfo) {
            if (item.getPackageName().equals(packageName)) {
                mAppInfo.remove(mAppInfo.indexOf(item));
            }
        }
    }

    public List<AppInfo> getAppInfo() {
        return mAppInfo;
    }
}
