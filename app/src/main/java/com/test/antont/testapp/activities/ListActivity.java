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
import com.test.antont.testapp.models.AppItem;
import com.test.antont.testapp.receivers.ApplicationsReceiver;
import com.test.antont.testapp.services.ApplicationsService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<AppItem> mAppItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mAppItems = new ArrayList<>();

        startService(new Intent(this, ApplicationsService.class));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActionType.ON_ALL_ITEMS_RETURNED.name());
        intentFilter.addAction(ActionType.ON_PACKAGE_ADDED.name());
        intentFilter.addAction(ActionType.ON_PACKAGE_REMOVED.name());

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);

        ApplicationsReceiver mAppReceiver = new ApplicationsReceiver();

        IntentFilter receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        receiverIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        receiverIntentFilter.addDataScheme("package");

        registerReceiver(mAppReceiver, receiverIntentFilter);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ActionType type = ActionType.valueOf(intent.getAction());

            switch (type) {
                case ON_ALL_ITEMS_RETURNED:
                    Bundle bundle = intent.getExtras();
                    mAppItems.clear();
                    mAppItems.addAll((List<AppItem>) bundle.getSerializable("app_list"));
                    setupRecyclerView(mAppItems);
                    break;

                case ON_PACKAGE_ADDED:
                    mAppItems.add(new AppItem(intent.getStringExtra("new_item"), true));
                    mRecyclerView.getAdapter().notifyItemInserted(mRecyclerView.getAdapter().getItemCount());
                    break;

                case ON_PACKAGE_REMOVED:
                    String packageName = intent.getStringExtra("remove_item");

                    List<AppItem> result = mAppItems.stream()
                            .filter(item -> item.getName().equals(packageName))
                            .collect(Collectors.toList());

                    int itemIndex = mAppItems.indexOf(result.get(0));
                    mAppItems.remove(itemIndex);
                    mRecyclerView.getAdapter().notifyItemRemoved(itemIndex);
                    break;
            }

        }
    };

    private void setupRecyclerView(List<AppItem> appItems) {
        mRecyclerView = findViewById(R.id.appRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(appItems);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
}
