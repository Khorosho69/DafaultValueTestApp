package com.test.antont.testapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.test.antont.testapp.R;
import com.test.antont.testapp.models.AppItem;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<AppItem> mDataset;

    public RecyclerViewAdapter(List<AppItem> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCheckBox.setText(mDataset.get(position).getName());
        holder.mCheckBox.setChecked(mDataset.get(position).getStatus());

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