package com.test.antont.testapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.test.antont.testapp.activities.ListActivity;
import com.test.antont.testapp.databases.DBHelper;
import com.test.antont.testapp.enums.ActionType;
import com.test.antont.testapp.models.AppInfo;

import java.util.Objects;

public class ApplicationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("zlo", "onReceive: ");
        DBHelper mDBHelper = new DBHelper(context);
        SQLiteDatabase mDataBase = mDBHelper.getWritableDatabase();

        String packageName = intent.getData().getEncodedSchemeSpecificPart();

        PackageManager packageManager = context.getPackageManager();
        String appName;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }
        if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {

            Intent localIntent = new Intent(ActionType.ON_PACKAGE_ADDED.name());
            localIntent.putExtra(ListActivity.EXTRAS_NEW_ITEM_PACKAGE, packageName);
            localIntent.putExtra(ListActivity.EXTRAS_NEW_ITEM_NAME, appName);

            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

            mDBHelper.writeAppInfo(mDataBase, new AppInfo(packageName, appName, true));
        } else if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            Intent localIntent = new Intent(ActionType.ON_PACKAGE_REMOVED.name());
            localIntent.putExtra(ListActivity.EXTRAS_REMOVE_ITEM, packageName);

            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

            mDBHelper.deleteAppInfo(mDataBase, packageName);
        }
    }
}
