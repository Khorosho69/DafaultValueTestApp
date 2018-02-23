package com.test.antont.testapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.test.antont.testapp.R;
import com.test.antont.testapp.adapters.RecyclerViewAdapter;
import com.test.antont.testapp.databases.AppInfo;
import com.test.antont.testapp.eventbus.GlobalBus;
import com.test.antont.testapp.eventbus.OnAppInstalledEvent;
import com.test.antont.testapp.eventbus.OnAppRemovedEvent;
import com.test.antont.testapp.eventbus.OnItemListReturned;
import com.test.antont.testapp.receivers.ApplicationsReceiver;
import com.test.antont.testapp.services.ApplicationsService;
import com.test.antont.testapp.view_models.AppInfoViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private AppInfoViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mViewModel = ViewModelProviders.of(this).get(AppInfoViewModel.class);

        GlobalBus.getBus().register(this);

        if (mViewModel.getAppInfo().size() == 0) {
            startService(new Intent(this, ApplicationsService.class));
        } else{
            setupRecyclerView(mViewModel.getAppInfo());
        }
        setupAppReceiver();
    }

    private void setupAppReceiver() {
        ApplicationsReceiver mAppReceiver = new ApplicationsReceiver();

        IntentFilter receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        receiverIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        receiverIntentFilter.addDataScheme("package");

        registerReceiver(mAppReceiver, receiverIntentFilter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventOnListReturned(OnItemListReturned event) {
        mViewModel.addAppItems(event.getAppInfolist());
        setupRecyclerView(mViewModel.getAppInfo());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventOnAppInstaled(OnAppInstalledEvent event) {
        mViewModel.addAppInfo(event.getAppInfo());
        mRecyclerView.getAdapter().notifyItemInserted(mRecyclerView.getAdapter().getItemCount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventOnAppRemoved(OnAppRemovedEvent event) {
        ((RecyclerViewAdapter) mRecyclerView.getAdapter()).notifyItemRemovedByPackageName(event.getPackageName());
        mViewModel.removeItemByPackageName(event.getPackageName());
    }

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
        GlobalBus.getBus().unregister(this);
    }
}
