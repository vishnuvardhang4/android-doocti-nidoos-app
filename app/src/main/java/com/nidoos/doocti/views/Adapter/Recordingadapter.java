package com.nidoos.doocti.views.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nidoos.doocti.R;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Recordingadapter extends RecyclerView.Adapter<Recordingadapter.viewHolder> {
    int rowLayout;
    private Context context;
    private List<Map<String,String>> list;
    private final Recordingadapter.OnRecordingingItemClickListener listener;

    public Recordingadapter(List<Map<String,String>> list, int layout_contact_screen, Context context, Recordingadapter.OnRecordingingItemClickListener listener) {
        this.list = list;
        this.rowLayout = layout_contact_screen;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new Recordingadapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Recordingadapter.viewHolder holder, int position) {
        holder.tv_recording_name.setText(list.get(position).get("name"));
        holder.tv_duration_time.setText(list.get(position).get("duration"));
        holder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listener.onItemClick(list.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_recording_name)
        TextView tv_recording_name;

        @BindView(R.id.tv_duration_time)
        TextView tv_duration_time;

        @BindView(R.id.iv_play)
        ImageView iv_play;


        viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRecordingingItemClickListener {
        void onItemClick(Map<String,String> item) throws IOException;
    }
}
