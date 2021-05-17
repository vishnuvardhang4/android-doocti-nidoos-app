package com.nidoos.doocti.views.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nidoos.doocti.R;
import com.nidoos.doocti.response.TimelineResponse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    int rowLayout;
    private Context context;
    private List<TimelineResponse.Datum> list;

    public TimeLineAdapter(List<TimelineResponse.Datum> list, int rowlayout, Context context) {
        this.rowLayout = rowlayout;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TimeLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new TimeLineAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineAdapter.ViewHolder holder, int position) {
        holder.tv_description.setText(list.get(position).getDescription());
        String date = list.get(position).getCreatedTime();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        String s1 = date;
        String s2 = null;
        Date d;
        try {
            d = sdf.parse(s1);
            s2 = (new SimpleDateFormat("dd/MM/yyyy : hh:mm")).format(d);

        } catch (ParseException e) {

            e.printStackTrace();
        }
        holder.tv_created_time.setText(s2);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_description)
        TextView tv_description;

        @BindView(R.id.tv_created_time)
        TextView tv_created_time;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
