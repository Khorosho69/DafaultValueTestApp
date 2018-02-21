package com.test.antont.testapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.test.antont.testapp.R;
import com.test.antont.testapp.adapters.RecyclerViewAdapter;
import com.test.antont.testapp.enums.ActionType;
import com.test.antont.testapp.models.AppInfo;
import com.test.antont.testapp.receivers.ApplicationsReceiver;
import com.test.antont.testapp.services.ApplicationsService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListActivity extends AppCompatActivity {

    public static final String EXTRAS_NEW_ITEM_PACKAGE = "NEW_APP_PACKAGE";
    public static final String EXTRAS_NEW_ITEM_NAME = "NEW_APP_NAME";
    public static final String EXTRAS_REMOVE_ITEM = "REMOVE_ITEM";

    public static final String EXTRAS_SERIALIZED_APP_LIST = "SERIALIZED_LIST";

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        startService(new Intent(this, ApplicationsService.class));
        setupLocalBroadcastManager();
    }

    private void setupLocalBroadcastManager() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActionType.ON_ALL_ITEMS_RETURNED.name());
        intentFilter.addAction(ActionType.ON_PACKAGE_ADDED.name());
        intentFilter.addAction(ActionType.ON_PACKAGE_REMOVED.name());

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ActionType type = ActionType.valueOf(intent.getAction());

            String packageName;

            switch (type) {
                case ON_ALL_ITEMS_RETURNED:
                    Bundle bundle = intent.getExtras();

                    List<AppInfo> receivedItems = (List<AppInfo>) bundle.getSerializable(EXTRAS_SERIALIZED_APP_LIST);
                    if (receivedItems != null) {
                        setupRecyclerView(receivedItems);
                    }
                    break;

                case ON_PACKAGE_ADDED:
                    packageName = intent.getStringExtra(EXTRAS_NEW_ITEM_PACKAGE);
                    String name = intent.getStringExtra(EXTRAS_NEW_ITEM_NAME);

                    ((RecyclerViewAdapter)mRecyclerView.getAdapter()).addNewItem(new AppInfo(packageName, name, true));
                    break;

                case ON_PACKAGE_REMOVED:
                    packageName = intent.getStringExtra(EXTRAS_REMOVE_ITEM);
                    ((RecyclerViewAdapter)mRecyclerView.getAdapter()).removeItemByPackageName(packageName);
                    break;
            }

        }
    };

    private void setupRecyclerView(List<AppInfo> appItems) {
        mRecyclerView = findViewById(R.id.appRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        RecyclerView.Adapter adapter = new RecyclerViewAdapter(appItems);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
}
