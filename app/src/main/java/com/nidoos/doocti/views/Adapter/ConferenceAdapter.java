package com.nidoos.doocti.views.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.engineio.client.Socket;
import com.nidoos.doocti.R;
import com.nidoos.doocti.views.CallScreenActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ConferenceAdapter extends RecyclerView.Adapter<ConferenceAdapter.ViewHolder> {
    private int rowLayout;
    private List list = new ArrayList();
    private Socket mSocket;

    public ConferenceAdapter(ArrayList<String[]> list, int rowlayout, Context context) {
        Log.e("adapter","page_called");
        this.rowLayout = rowlayout;
        this.list = list;
    }

    @NonNull
    @Override
    public ConferenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ConferenceAdapter.ViewHolder holder, int position) {
        String[] myString= new String[8];
        myString= (String[]) list.get(position);
            holder.tv_name.setText(myString[0]);
            holder.tv_created_time.setText(myString[1]);

        String[] finalMyString = myString;
        holder.ib_conference_mute.setVisibility(View.INVISIBLE);
        holder.ib_conference_hold.setVisibility(View.INVISIBLE);
        holder.ib_conference_hangup.setVisibility(View.INVISIBLE);

        holder.ib_conference_hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        CallScreenActivity obj = new CallScreenActivity();
        holder.ib_conference_hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("hold","clicked");

                String tenant_id = finalMyString[0];
                String add_number = finalMyString[1];
                Log.e("selected_data",tenant_id+" "+add_number);
                try {
                    obj.triggerEvent("hold",add_number,tenant_id,finalMyString[2],finalMyString[7]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.ib_conference_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("mute","clicked");

                String tenant_id = finalMyString[0];
                String add_number = finalMyString[1];
                Log.e("selected_data",tenant_id+" "+add_number);
                try {
                    obj.triggerEvent("mute",add_number,tenant_id,finalMyString[2],finalMyString[7]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tv_name;

        @BindView(R.id.tv_created_time)
        TextView tv_created_time;

        @BindView(R.id.ib_conference_hold)
        ImageButton ib_conference_hold;

        @BindView(R.id.ib_conference_mute)
        ImageButton ib_conference_mute;

        @BindView(R.id.ib_conference_hangup)
        ImageButton ib_conference_hangup;

        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
