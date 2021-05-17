package com.nidoos.doocti.views.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nidoos.doocti.R;
import com.nidoos.doocti.response.FetchLeadsResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewLeadAutoDial extends RecyclerView.Adapter<ViewLeadAutoDial.viewHolder> {
    int rowLayout;
    public Context context;
    public List<FetchLeadsResponse.Datum> resultList;
    public List<FetchLeadsResponse.Datum> selectedList;

    public ViewLeadAutoDial(List<FetchLeadsResponse.Datum> resultList, List<FetchLeadsResponse.Datum> selectedList, int layout_view_log, Context context) {
        this.rowLayout = layout_view_log;
        this.context = context;
        this.resultList = resultList;
        this.selectedList = selectedList;
    }

    @NonNull
    @Override
    public ViewLeadAutoDial.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewLeadAutoDial.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        if (position < 20) {
            holder.tv_call_status.setText(resultList.get(position).getStatus());
            holder.tv_lead_name.setText(resultList.get(position).getLeadName());
            holder.tv_lead_number.setText(resultList.get(position).getLeadNumber());
        }
        if (selectedList.contains(resultList.get(position))) {
            holder.cv_leads.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));
        } else {
            holder.cv_leads.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        }
    }

    @Override
    public int getItemCount() {
        if (resultList.size() >= 20) {
            return 20;
        } else {
            return resultList.size();
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_lead_number)
        TextView tv_lead_number;

        @BindView(R.id.tv_lead_name)
        TextView tv_lead_name;

        @BindView(R.id.tv_call_status)
        TextView tv_call_status;

        @BindView(R.id.cv_leads)
        CardView cv_leads;


        viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
