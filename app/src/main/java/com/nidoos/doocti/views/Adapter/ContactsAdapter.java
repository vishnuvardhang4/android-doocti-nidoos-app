package com.nidoos.doocti.views.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nidoos.doocti.R;
import com.nidoos.doocti.response.GetContactsReponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int rowLayout;
    private Context context;
    private List<GetContactsReponse.Datum> list;
    private OnContactItemClickListener listener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public ContactsAdapter(List<GetContactsReponse.Datum> list, int layout_contact_screen, Context context, OnContactItemClickListener listener) {
        this.list = list;
        this.rowLayout = layout_contact_screen;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new viewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progress_bar, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof viewHolder) {
            populateItemRows((viewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private void populateItemRows(viewHolder viewHolder, int position) {
        viewHolder.tv_contact_name.setText(list.get(position).getContactName());
        viewHolder.tv_contact_number.setText(list.get(position).getContactNumber());
        if (list.get(position).getContactNumber() == null || list.get(position).getContactNumber().equals("")) {
            viewHolder.iv_call.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.iv_call.setVisibility(View.VISIBLE);
        }
        viewHolder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(list.get(position));
            }
        });
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }


    class viewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_contact_name)
        TextView tv_contact_name;

        @BindView(R.id.tv_contact_number)
        TextView tv_contact_number;

        @BindView(R.id.iv_call)
        ImageView iv_call;


        viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnContactItemClickListener {
        void onItemClick(GetContactsReponse.Datum item);
    }
}
