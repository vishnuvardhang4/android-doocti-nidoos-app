package com.nidoos.doocti.views.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.nidoos.doocti.R;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.response.CallLogResponse;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.views.DialerActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {
    int rowLayout;
    private Context context;
    private List<CallLogResponse.Datum> call_log;
    private final OnItemClickListener listener;
    private SharedPreferences sharedpreferences;
    private Object SimpleDateFormat;
    private final PermissionInfo permissionInfo;
    private UserInfo userInfo;




    public CallLogAdapter(List<CallLogResponse.Datum> callLog, int layout_view_log, Context context, OnItemClickListener listener) {
        this.rowLayout = layout_view_log;
        this.context = context;
        this.call_log = callLog;
        this.listener = listener;
        sharedpreferences = context.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
        permissionInfo = new Gson().fromJson(sharedpreferences.getString(SharedPrefConstants.PERMISSION_INFO,"permissionInfo Not found"), PermissionInfo.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (call_log.get(position).getCallType().equals("INBOUND") && call_log.get(position).getCallStatus().equals("Unanswered")) {
            holder.iv_missed.setVisibility(View.VISIBLE);
            holder.iv_inbound.setVisibility(View.GONE);
            holder.iv_outbound.setVisibility(View.GONE);
        } else if (call_log.get(position).getCallType().equals("INBOUND") && call_log.get(position).getCallStatus().equals("Answered")) {
            holder.iv_missed.setVisibility(View.GONE);
            holder.iv_inbound.setVisibility(View.VISIBLE);
            holder.iv_outbound.setVisibility(View.GONE);
        } else if (call_log.get(position).getCallType().equals("MANUAL") || call_log.get(position).getCallType().equals("AUTO")) {
            holder.iv_missed.setVisibility(View.GONE);
            holder.iv_inbound.setVisibility(View.GONE);
            holder.iv_outbound.setVisibility(View.VISIBLE);
        }

        if (call_log.get(position).getCallStatus().equals("Answered") && permissionInfo.getRecordingPlay()) {
            holder.iv_play_recordings.setVisibility(View.VISIBLE);
        } else {
            holder.iv_play_recordings.setVisibility(View.GONE);
        }
        holder.tv_phone_number.setText(call_log.get(position).getPhoneNumber());
        holder.tv_dispo_status.setText(call_log.get(position).getDispoStatus());
        holder.tv_call_duration.setText(call_log.get(position).getDuration());
        String date = call_log.get(position).getCalldate();
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

        holder.tv_call_date.setText(s2);
        if (!permissionInfo.getClickToCall()) {
            holder.rv_call_log.setEnabled(false);
        }
        holder.rv_call_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof DialerActivity) {
                    if (call_log.get(position).getCallType().equals("INBOUND") && call_log.get(position).getCallStatus().equals("Unanswered")) {
                        userInfo.setMissed(true);
                        SharedPrefHelper.putUserInfo(sharedpreferences,userInfo);
                    }
                    ((DialerActivity) context).clickToCall(call_log.get(position).getPhoneNumber(), "MANUAL");
                }
            }
        });

        holder.iv_play_recordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(call_log.get(position));
            }
        });

    }


    @Override
    public int getItemCount() {
        return call_log.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_phone_number)
        TextView tv_phone_number;

        @BindView(R.id.tv_call_date)
        TextView tv_call_date;

        @BindView(R.id.tv_dispo_status)
        TextView tv_dispo_status;

        @BindView(R.id.tv_call_duration)
        TextView tv_call_duration;

        @BindView(R.id.iv_inbound)
        ImageView iv_inbound;

        @BindView(R.id.iv_outbound)
        ImageView iv_outbound;

        @BindView(R.id.iv_missed)
        ImageView iv_missed;

        @BindView(R.id.iv_play_recordings)
        ImageView iv_play_recordings;

        @BindView(R.id.rv_call_log)
        RelativeLayout rv_call_log;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(CallLogResponse.Datum item);
    }
}
