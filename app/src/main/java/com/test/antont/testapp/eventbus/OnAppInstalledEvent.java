package com.test.antont.testapp.eventbus;

import com.test.antont.testapp.databases.AppInfo;

public class OnAppInstalledEvent {

    private AppInfo mAppInfo;

    public OnAppInstalledEvent(AppInfo appInfo) {
        this.mAppInfo = appInfo;
    }

    public AppInfo getAppInfo() {
        return mAppInfo;
    }
}
