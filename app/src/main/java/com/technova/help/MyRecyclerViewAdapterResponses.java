package com.technova.help;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by N on 11/30/2017.
 */

public class MyRecyclerViewAdapterResponses extends RecyclerView
        .Adapter<MyRecyclerViewAdapterResponses
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapterResponses";
    private ArrayList<DataObject> mDataset;
    private static MyRecyclerViewAdapterResponses.MyClickListener myClickListener;
    Context myContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        CardView contactsCardView;
        CheckBox checkBox;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            contactsCardView = (CardView) itemView.findViewById(R.id.card_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkboxAll);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    public MyRecyclerViewAdapterResponses(ArrayList<DataObject> myDataset, Context context) {
        mDataset = myDataset;
        myContext = context;
    }

    @Override
    public MyRecyclerViewAdapterResponses.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_responses, parent, false);

        MyRecyclerViewAdapterResponses.DataObjectHolder dataObjectHolder = new MyRecyclerViewAdapterResponses.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final MyRecyclerViewAdapterResponses.DataObjectHolder holder, final int position) {
        holder.label.setText(mDataset.get(position).getmText1().substring(0, mDataset.get(position).getmText1().indexOf("(")));
        holder.dateTime.setText(mDataset.get(position).getmText2().substring(0, mDataset.get(position).getmText2().indexOf(":")));

        holder.contactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myContext.startActivity(new Intent(myContext, AllResponses.class)
                        .putExtra("number", mDataset.get(position).getmText1() )
                        .putExtra("help", mDataset.get(position).getmText2()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
