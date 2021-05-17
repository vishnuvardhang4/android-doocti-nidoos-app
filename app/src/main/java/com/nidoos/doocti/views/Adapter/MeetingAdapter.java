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
import com.nidoos.doocti.response.GetMeetingResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.viewHolder> {
    int rowLayout;
    private Context context;
    private List<GetMeetingResponse.Datum> list;
    private final OnMeetingItemClickListener listener;

    public MeetingAdapter(List<GetMeetingResponse.Datum> list, int layout_contact_screen, Context context,OnMeetingItemClickListener listener) {
        this.list = list;
        this.rowLayout = layout_contact_screen;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MeetingAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getLeadName());
        holder.tv_meeting_subject.setText(list.get(position).getSubject());
        holder.tv_meeting_time.setText(list.get(position).getScheduledTime());
        holder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tv_name;

        @BindView(R.id.tv_meeting_subject)
        TextView tv_meeting_subject;

        @BindView(R.id.tv_meeting_time)
        TextView tv_meeting_time;

        @BindView(R.id.iv_call)
        ImageView iv_call;


        viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnMeetingItemClickListener {
        void onItemClick(GetMeetingResponse.Datum item);
    }

}
