package com.test.antont.testapp.servises;


import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.IBinder;

import com.test.antont.testapp.activities.ListActivity;
import com.test.antont.testapp.models.AppItem;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsService extends Service {

    private List<AppItem> mAppItems;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PendingIntent pi = intent.getParcelableExtra(ListActivity.PARAM_PINTENT);

        mAppItems = getInstalledApps();

        BroadcastReceiver br = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String packageName = intent.getData().getEncodedSchemeSpecificPart();
                mAppItems.add(new AppItem(packageName, true));
//                pi.send();
                System.out.println("-----------App installed/uninstalled");
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private List<AppItem> getInstalledApps() {
        List<AppItem> res = new ArrayList<AppItem>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
            res.add(new AppItem(appName, true));
        }
        return res;
    }
}
