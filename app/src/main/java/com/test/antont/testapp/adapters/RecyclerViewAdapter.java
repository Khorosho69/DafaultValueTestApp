package com.test.antont.testapp.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.test.antont.testapp.R;
import com.test.antont.testapp.databases.DBHelper;
import com.test.antont.testapp.models.AppItem;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<AppItem> mDataset;

    private Context mContext;

    public RecyclerViewAdapter(List<AppItem> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCheckBox.setText(mDataset.get(position).getName());
        holder.mCheckBox.setChecked(mDataset.get(position).getStatus());
        holder.mCheckBox.setOnCheckedChangeListener(null);
        holder.mCheckBox.setOnCheckedChangeListener((compoundButton, b) -> updateItemStatus(holder, b));
    }

    private void updateItemStatus(RecyclerViewAdapter.ViewHolder holder, Boolean status) {
        DBHelper mDBHelper = new DBHelper(mContext);
        SQLiteDatabase mDataBase = mDBHelper.getWritableDatabase();

        AppItem newItem = new AppItem(mDataset.get(holder.getAdapterPosition()).getName(), status);

        mDBHelper.updateAppItem(mDataBase, newItem);
        mDataset.set(holder.getAdapterPosition(), newItem);
        notifyItemChanged(holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;

        ViewHolder(View v) {
            super(v);
            mCheckBox = v.findViewById(R.id.appItemCheckBox);
        }
    }
}