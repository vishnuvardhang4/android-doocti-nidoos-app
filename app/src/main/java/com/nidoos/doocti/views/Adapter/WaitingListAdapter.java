package com.nidoos.doocti.views.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.engineio.client.Socket;
import com.nidoos.doocti.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.ViewHolder> {
    private int rowLayout;
    private List list = new ArrayList();

    public WaitingListAdapter(ArrayList<String[]> list, int rowlayout, Context context) {
        Log.e("adapter","page_called");
        this.rowLayout = rowlayout;
        this.list = list;
    }

    @NonNull
    @Override
    public WaitingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new WaitingListAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitingListAdapter.ViewHolder holder, int position) {
        String[] myString= new String[4];
        myString= (String[]) list.get(position);

        holder.tv_data_phone.setText(myString[0]);
        holder.tv_data_queue.setText(myString[1]);
        holder.tv_data_entry.setText(myString[2]);
        holder.tv_data_duration.setText(myString[3]);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_data_phone)
        TextView tv_data_phone;

        @BindView(R.id.tv_data_queue)
        TextView tv_data_queue;

        @BindView(R.id.tv_data_entry)
        TextView tv_data_entry;

        @BindView(R.id.tv_data_duration)
        TextView tv_data_duration;

        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
