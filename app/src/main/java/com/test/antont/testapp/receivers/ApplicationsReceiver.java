package com.test.antont.testapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import com.test.antont.testapp.databases.DBHelper;
import com.test.antont.testapp.enums.ActionType;
import com.test.antont.testapp.models.AppInfo;

import java.util.Objects;

public class ApplicationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper mDBHelper = new DBHelper(context);
        SQLiteDatabase mDataBase = mDBHelper.getWritableDatabase();

        String packageName = intent.getData().getEncodedSchemeSpecificPart();

        PackageManager packageManager= context.getPackageManager();
        try {
            if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
                String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                Intent localIntent = new Intent(ActionType.ON_PACKAGE_ADDED.name());
                localIntent.putExtra("new_item_package", packageName);
                localIntent.putExtra("new_item_name", appName);

                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                mDBHelper.writeAppInfo(mDataBase, new AppInfo(packageName, appName, true));
            } else if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
                Intent localIntent = new Intent(ActionType.ON_PACKAGE_REMOVED.name());
                localIntent.putExtra("remove_item", packageName);

                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                mDBHelper.deleteAppInfo(mDataBase, packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
