package com.technova.help;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by N on 12/3/2017.
 */

public class MyRecyclerViewAdapterAllReqResponse extends RecyclerView
        .Adapter<MyRecyclerViewAdapterAllReqResponse
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapterAllReqResponse";
    private ArrayList<DataObject> mDataset;
    private static MyRecyclerViewAdapterAllReqResponse.MyClickListener myClickListener;
    Context myContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    public MyRecyclerViewAdapterAllReqResponse(ArrayList<DataObject> myDataset, Context context) {
        mDataset = myDataset;
        myContext = context;
    }

    @Override
    public MyRecyclerViewAdapterAllReqResponse.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_req_response, parent, false);

        MyRecyclerViewAdapterAllReqResponse.DataObjectHolder dataObjectHolder = new MyRecyclerViewAdapterAllReqResponse.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final MyRecyclerViewAdapterAllReqResponse.DataObjectHolder holder, final int position) {
        holder.label.setText(mDataset.get(position).getmText1().substring(0, mDataset.get(position).getmText1().indexOf("(")));
        holder.dateTime.setText(mDataset.get(position).getmText2());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }
}

