package com.test.antont.testapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import com.test.antont.testapp.databases.DBHelper;
import com.test.antont.testapp.enums.ActionType;
import com.test.antont.testapp.models.AppItem;

import java.util.Objects;

public class ApplicationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper mDBHelper = new DBHelper(context);
        SQLiteDatabase mDataBase = mDBHelper.getWritableDatabase();

        String packageName = intent.getData().getEncodedSchemeSpecificPart();

        if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            Intent localIntent = new Intent(ActionType.ON_PACKAGE_ADDED.name());
            localIntent.putExtra("new_item", packageName);

            mDBHelper.writeAppItem(mDataBase, new AppItem(packageName, true));
        } else if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            Intent localIntent = new Intent(ActionType.ON_PACKAGE_REMOVED.name());
            localIntent.putExtra("remove_item", packageName);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

            mDBHelper.deleteAppItem(mDataBase, packageName);
        }
    }
}
