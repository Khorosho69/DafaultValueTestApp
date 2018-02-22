package com.test.antont.testapp.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.test.antont.testapp.R;
import com.test.antont.testapp.databases.DBHelper;
import com.test.antont.testapp.models.AppInfo;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<AppInfo> mDataset;
    public RecyclerViewAdapter(List<AppInfo> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppInfo item = mDataset.get(position);

        holder.mCheckBox.setText(item.getAppName());
        holder.mCheckBox.setChecked(item.getStatus());
        holder.mImageView.setImageDrawable(item.getAppIcon());
        holder.mCheckBox.setOnCheckedChangeListener((compoundButton, b) -> onCheckedChanged(position, b, compoundButton));
    }

    private void onCheckedChanged(int position, Boolean status, CompoundButton compoundButton) {
        if (!compoundButton.isPressed()) {
            return;
        }
        AppInfo newItem = new AppInfo(mDataset.get(position).getAppIcon(), mDataset.get(position).getPackageName(), mDataset.get(position).getAppName(), status);
        mDataset.set(position, newItem);

        DBHelper DBHelper = new DBHelper(compoundButton.getContext());
        SQLiteDatabase dataBase = DBHelper.getWritableDatabase();
        DBHelper.updateAppInfo(dataBase, newItem);
        DBHelper.close();
    }

    public void addNewItem(AppInfo item){
        mDataset.add(item);
        notifyItemInserted(getItemCount());
    }

    public void removeItemByPackageName(String packageName){
        for (AppInfo item: mDataset) {
            if(item.getPackageName().equals(packageName)){
                mDataset.remove(mDataset.indexOf(item));
                notifyItemRemoved(mDataset.indexOf(item));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        ImageView mImageView;

        ViewHolder(View v) {
            super(v);
            mCheckBox = v.findViewById(R.id.appItemCheckBox);
            mImageView = v.findViewById(R.id.appIconImageView);
        }
    }
}