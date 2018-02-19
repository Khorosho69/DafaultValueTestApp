package com.test.antont.testapp.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.test.antont.testapp.R;
import com.test.antont.testapp.adapters.RecyclerViewAdapter;
import com.test.antont.testapp.models.AppItem;
import com.test.antont.testapp.servises.ApplicationsService;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    public final static int TASK_CODE = 1;

    public final static String PARAM_PINTENT = "pendingIntent";

    private List<AppItem> appItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setupRecyclerView();
    }

    private void setupRecyclerView(){
        RecyclerView mRecyclerView = findViewById(R.id.appRecyclerView);

        appItems = getInstalledApps();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(appItems);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.getAdapter().notifyDataSetChanged();


        PendingIntent pi;
        Intent intent;

        pi = createPendingResult(TASK_CODE, new Intent(this, ListActivity.class), 0);
        intent = new Intent(this, ApplicationsService.class).putExtra(PARAM_PINTENT, pi);
        startService(intent);

//        startService(new Intent(this, ApplicationsService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    }

    private List<AppItem> getInstalledApps() {
        List<AppItem> res = new ArrayList<AppItem>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
            res.add(new AppItem(appName, true));
        }
        return res;
    }
}
