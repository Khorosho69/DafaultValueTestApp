package com.test.antont.testapp.servises;


import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.test.antont.testapp.enums.ActionType;
import com.test.antont.testapp.models.AppItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplicationsService extends IntentService {

    private List<AppItem> mAppItems;
    LocalBroadcastManager localBroadcastManager;

    public ApplicationsService() {
        super("appService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mAppItems = getInstalledApps();

        Intent localIntent = new Intent(ActionType.ON_ALL_ITEMS_RETURNED.name());

        Bundle bundle = new Bundle();
        bundle.putSerializable("app_list", (Serializable) mAppItems);
        localIntent.putExtras(bundle);
//        localIntent.putExtra("action_type", ActionType.ON_ALL_ITEMS_RETURNED.name());

        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private List<AppItem> getInstalledApps() {
        List<AppItem> res = new ArrayList<AppItem>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (!isSystemPackage(p)) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                res.add(new AppItem(appName, true));
            }
        }
        return res;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}
