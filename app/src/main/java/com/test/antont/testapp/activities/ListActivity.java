package com.test.antont.testapp.activities;

import android.content.pm.PackageInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.test.antont.testapp.R;
import com.test.antont.testapp.adapters.RecyclerViewAdapter;
import com.test.antont.testapp.models.AppItem;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private List<AppItem> appItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView mRecyclerView = findViewById(R.id.appRecyclerView);

        appItems = getInstalledApps();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new RecyclerViewAdapter(appItems);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.getAdapter().notifyDataSetChanged();
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
