package com.test.antont.testapp.services;


import android.app.IntentService;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import com.test.antont.testapp.databases.AppDatabase;
import com.test.antont.testapp.databases.AppInfo;
import com.test.antont.testapp.eventbus.GlobalBus;
import com.test.antont.testapp.eventbus.OnItemListReturned;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsService extends IntentService {

    public ApplicationsService() {
        super("appService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<AppInfo> mAppItems = getActualPackagesNames();

        GlobalBus.getBus().post(new OnItemListReturned(mAppItems));

//        Intent localIntent = new Intent(ActionType.ON_ALL_ITEMS_RETURNED.name());
//
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(ListActivity.EXTRAS_SERIALIZED_APP_LIST, (Serializable) mAppItems);
//        localIntent.putExtras(bundle);
//
//        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private List<AppInfo> getActualPackagesNames() {
        RoomDatabase mDatabase = AppDatabase.getInstance(this);

        List<AppInfo> appInfoList = new ArrayList<>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : packs) {
            if (!isSystemPackage(packageInfo)) {
                AppInfo itemFroDB = ((AppDatabase) mDatabase).appInfoDao().findItemByPackageName(packageInfo.packageName);

                String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());

                if (itemFroDB != null) {
                    appInfoList.add(new AppInfo(packageInfo.packageName, appName, itemFroDB.getStatus(), appIcon));
                } else {
                    AppInfo newAppInfo = new AppInfo(packageInfo.packageName, appName, "true", appIcon);
                    ((AppDatabase) mDatabase).appInfoDao().insertAppItem(newAppInfo);
                    appInfoList.add(newAppInfo);
                }
            }
        }
        return appInfoList;
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}
