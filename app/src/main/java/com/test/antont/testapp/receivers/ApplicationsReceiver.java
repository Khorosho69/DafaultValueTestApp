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

//                Intent localIntent = new Intent(ActionType.ON_PACKAGE_ADDED.name());
//                localIntent.putExtra(ListActivity.EXTRAS_NEW_ITEM_PACKAGE, packageName);
//                localIntent.putExtra(ListActivity.EXTRAS_NEW_ITEM_NAME, appName);
//
//                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);

            AppInfo installedApp = new AppInfo(packageName, appName, "true", appIcon);

            GlobalBus.getBus().post(new OnAppInstalledEvent(installedApp));

//            AppDatabase.getInstance(context).appInfoDao().insertAppItem(installedApp);
            new InsertToDBAsync(context).execute(installedApp);
        } else if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
//                Intent localIntent = new Intent(ActionType.ON_PACKAGE_REMOVED.name());
//                localIntent.putExtra(ListActivity.EXTRAS_REMOVE_ITEM, packageName);
//
//                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);

            GlobalBus.getBus().post(new OnAppRemovedEvent(packageName));

//            AppDatabase.getInstance(context).appInfoDao().deleteItemByPackageName(packageName);
            new RemoveFromDBAsync(context).execute(packageName);
        }

//        new CallToBDAsync(context, intent).execute();
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
