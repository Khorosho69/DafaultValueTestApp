package com.test.antont.testapp.receivers;

import android.arch.persistence.room.RoomDatabase;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.test.antont.testapp.activities.ListActivity;
import com.test.antont.testapp.databases.AppDatabase;
import com.test.antont.testapp.databases.AppInfo;
import com.test.antont.testapp.enums.ActionType;

import java.util.Objects;

public class ApplicationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        RoomDatabase database = AppDatabase.getInstance(context);
        PackageManager packageManager = context.getPackageManager();

        String packageName = intent.getData().getEncodedSchemeSpecificPart();

        if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            AppInfo installedApp = getAppInfoFromIntent(intent, packageManager);
            if (installedApp != null) {
                Intent localIntent = new Intent(ActionType.ON_PACKAGE_ADDED.name());
                localIntent.putExtra(ListActivity.EXTRAS_NEW_ITEM_PACKAGE, installedApp.getPackageName());
                localIntent.putExtra(ListActivity.EXTRAS_NEW_ITEM_NAME, installedApp.getAppName());

                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);


                new CallToDBAsync(database, packageName, true).execute(installedApp);
            }

        } else if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            Intent localIntent = new Intent(ActionType.ON_PACKAGE_REMOVED.name());
            localIntent.putExtra(ListActivity.EXTRAS_REMOVE_ITEM, packageName);

            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

            new CallToDBAsync(database, packageName, false).execute();
        }
    }


    private AppInfo getAppInfoFromIntent(Intent intent, PackageManager packageManager) {
        try {
            String packageName = intent.getData().getEncodedSchemeSpecificPart();

            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
            return new AppInfo(packageName, appName, "true", appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private class CallToDBAsync extends AsyncTask<AppInfo, Void, Void> {
        private RoomDatabase mDatabase;
        private String mPackageName;

        private Boolean isAppInstalled;

        CallToDBAsync(RoomDatabase database, String packageName, Boolean isAppInstalled) {
            mDatabase = database;
            mPackageName = packageName;
            this.isAppInstalled = isAppInstalled;
        }

        @Override
        protected Void doInBackground(AppInfo... appInfos) {
            if (isAppInstalled) {
                ((AppDatabase) mDatabase).appInfoDao().insertAppItem(appInfos[0]);
            } else {
                ((AppDatabase) mDatabase).appInfoDao().deleteItemByPackageName(mPackageName);
            }
            return  null;
        }
    }
}
