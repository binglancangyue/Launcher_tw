package com.bixin.launcher_tw.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.bean.AppInfo;
import com.bixin.launcher_tw.model.listener.OnRecyclerViewItemListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecyclerGridViewAdapter extends RecyclerView.Adapter<RecyclerGridViewAdapter.ViewHolder> {
    private ArrayList<AppInfo> mData;
    private LayoutInflater inflater;
    private OnRecyclerViewItemListener mListener;

    public void setOnRecyclerViewItemListener(OnRecyclerViewItemListener listener) {
        this.mListener = listener;
    }

    public RecyclerGridViewAdapter(Context context, ArrayList<AppInfo> data) {
        this.mData = data;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recycle_gridview, parent, false);
        return new ViewHolder(view, this);
    }

    public void setAppInfoArrayList(ArrayList<AppInfo> infoArrayList) {
        this.mData = infoArrayList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo info = mData.get(position);
        holder.tvAppName.setText(info.getAppName());
        holder.ivAppIcon.setImageDrawable(info.getAppIcon());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppName;
        ImageView ivAppIcon;

        private ViewHolder(final View itemView, RecyclerGridViewAdapter adapter) {
            super(itemView);
            WeakReference<RecyclerGridViewAdapter> adapterWeakReference =
                    new WeakReference<>(adapter);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            ivAppIcon = itemView.findViewById(R.id.iv_app_icon);
            final RecyclerGridViewAdapter gridViewAdapter = adapterWeakReference.get();
            if (gridViewAdapter.mListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        String packageName = adapter.mData.get(position).getPkgName();
                        gridViewAdapter.mListener.onItemClickListener(position, packageName);
                    }
                });
            }
        }
    }

}