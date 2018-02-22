package com.test.antont.testapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.test.antont.testapp.activities.ListActivity;
import com.test.antont.testapp.databases.AppDatabase;
import com.test.antont.testapp.databases.AppInfo;
import com.test.antont.testapp.enums.ActionType;

import java.util.Objects;

public class ApplicationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("zlo", "onReceive: ");

        new CallToBDAsync(context, intent).execute();
    }

    private class CallToBDAsync extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private Intent mIntent;

        CallToBDAsync(Context mContext, Intent mIntent) {
            this.mContext = mContext;
            this.mIntent = mIntent;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String packageName = mIntent.getData().getEncodedSchemeSpecificPart();

            PackageManager packageManager = mContext.getPackageManager();
            Drawable appIcon;
            String appName;
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
            } catch (PackageManager.NameNotFoundException e) {
                return  null;
            }
            if (Objects.equals(mIntent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {

                Intent localIntent = new Intent(ActionType.ON_PACKAGE_ADDED.name());
                localIntent.putExtra(ListActivity.EXTRAS_NEW_ITEM_PACKAGE, packageName);
                localIntent.putExtra(ListActivity.EXTRAS_NEW_ITEM_NAME, appName);

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);

                AppInfo installedApp = new AppInfo(packageName, appName, "true", appIcon);

                AppDatabase.getInstance(mContext).appInfoDao().insertAppItem(installedApp);
            } else if (Objects.equals(mIntent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
                Intent localIntent = new Intent(ActionType.ON_PACKAGE_REMOVED.name());
                localIntent.putExtra(ListActivity.EXTRAS_REMOVE_ITEM, packageName);

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);

                AppDatabase.getInstance(mContext).appInfoDao().deleteItemByPackageName(packageName);
            }
            return null;
        }
    }
}
