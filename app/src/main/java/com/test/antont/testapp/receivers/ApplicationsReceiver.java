package com.test.antont.testapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.test.antont.testapp.enums.ActionType;

import java.util.Objects;

public class ApplicationsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getEncodedSchemeSpecificPart();

            Intent localIntent = new Intent(ActionType.ON_PACKAGE_ADDED.name());
            localIntent.putExtra("new_item", packageName);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } else if (Objects.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getEncodedSchemeSpecificPart();

            Intent localIntent = new Intent(ActionType.ON_PACKAGE_REMOVED.name());
            localIntent.putExtra("remove_item", packageName);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        }
    }
}
