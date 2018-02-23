package com.test.antont.testapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.test.antont.testapp.databases.AppDatabase;
import com.test.antont.testapp.databases.AppInfo;
import com.test.antont.testapp.eventbus.*;

import java.util.Objects;

public class ApplicationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("zlo", "onReceive: ");

        String packageName = intent.getData().getEncodedSchemeSpecificPart();

        if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            PackageManager packageManager = context.getPackageManager();
            Drawable appIcon;
            String appName;
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
            } catch (PackageManager.NameNotFoundException e) {
                return;
            }

            AppInfo installedApp = new AppInfo(packageName, appName, "true", appIcon);

            GlobalBus.getBus().post(new OnAppInstalledEvent(installedApp));

            new InsertToDBAsync(context).execute(installedApp);
        } else if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            GlobalBus.getBus().post(new OnAppRemovedEvent(packageName));
            new RemoveFromDBAsync(context).execute(packageName);
        }
    }

    private class InsertToDBAsync extends AsyncTask<AppInfo, Void, Void> {

        private Context mContext;

        InsertToDBAsync(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Void doInBackground(AppInfo... appInfos) {
            AppDatabase.getInstance(mContext).appInfoDao().insertAppItem(appInfos[0]);
            return null;
        }
    }

    private class RemoveFromDBAsync extends AsyncTask<String, Void, Void> {

        private Context mContext;

        private RemoveFromDBAsync(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Void doInBackground(String... strings) {
            AppDatabase.getInstance(mContext).appInfoDao().deleteItemByPackageName(strings[0]);
            return null;
        }
    }
}
