package com.test.antont.testapp.receivers;

import android.arch.persistence.room.RoomDatabase;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.test.antont.testapp.databases.AppDatabase;
import com.test.antont.testapp.databases.AppInfo;
import com.test.antont.testapp.eventbus.*;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

public class ApplicationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        RoomDatabase database = AppDatabase.getInstance(context);
        PackageManager packageManager = context.getPackageManager();

        new CallToDBAsync(intent, packageManager, database).execute();
    }

    private class CallToDBAsync extends AsyncTask<Void, Void, Void> {

        private Intent mIntent;
        private PackageManager mPackageManager;
        private RoomDatabase mDatabase;
        private Boolean isAppInstalled = false;

        private CallToDBAsync(Intent intent, PackageManager packageManager, RoomDatabase database) {
            mIntent = intent;
            mPackageManager = packageManager;
            mDatabase = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String packageName = mIntent.getData().getEncodedSchemeSpecificPart();

            if (Objects.equals(mIntent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
                AppInfo installedApp = getAppInfoFromIntent();
                ((AppDatabase) mDatabase).appInfoDao().insertAppItem(installedApp);
                isAppInstalled = true;
            } else if (Objects.equals(mIntent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
                ((AppDatabase) mDatabase).appInfoDao().deleteItemByPackageName(packageName);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isAppInstalled) {
                AppInfo appInfo = getAppInfoFromIntent();

                EventBus.getDefault().post(new OnAppInstalledEvent(appInfo));
            } else {
                EventBus.getDefault().post(new OnAppRemovedEvent(mIntent.getData().getEncodedSchemeSpecificPart()));
            }
        }

        private AppInfo getAppInfoFromIntent() {
            try {
                String packageName = mIntent.getData().getEncodedSchemeSpecificPart();

                PackageInfo packageInfo = mPackageManager.getPackageInfo(packageName, 0);
                String appName = packageInfo.applicationInfo.loadLabel(mPackageManager).toString();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(mPackageManager);
                return new AppInfo(packageName, appName, "true", appIcon);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
    }
}
