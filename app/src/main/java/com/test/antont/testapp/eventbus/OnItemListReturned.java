package com.test.antont.testapp.eventbus;

import com.test.antont.testapp.databases.AppInfo;

import java.util.List;

public class OnItemListReturned {

    private List<AppInfo> mAppInfolist;

    public OnItemListReturned(List<AppInfo> appInfolist) {
        this.mAppInfolist = appInfolist;
    }

    public List<AppInfo> getAppInfolist() {
        return mAppInfolist;
    }
}
