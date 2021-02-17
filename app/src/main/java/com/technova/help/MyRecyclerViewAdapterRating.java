package com.technova.help;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.technova.help.MyRequests.count;

/**
 * Created by N on 12/3/2017.
 */

public class MyRecyclerViewAdapterRating extends RecyclerView
        .Adapter<MyRecyclerViewAdapterRating
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapterRating";
    private ArrayList<DataObject> mDataset;
    private static MyRecyclerViewAdapterRating.MyClickListener myClickListener;
    Context myContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        RatingBar ratingBar;
        CardView cardView;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            cardView=(CardView)itemView.findViewById(R.id.card_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    public MyRecyclerViewAdapterRating(ArrayList<DataObject> myDataset, Context context) {
        mDataset = myDataset;
        myContext = context;
    }

    @Override
    public MyRecyclerViewAdapterRating.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_rating, parent, false);

        MyRecyclerViewAdapterRating.DataObjectHolder dataObjectHolder = new MyRecyclerViewAdapterRating.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final MyRecyclerViewAdapterRating.DataObjectHolder holder, final int position) {
        holder.label.setText(mDataset.get(position).getmText1().substring(0, mDataset.get(position).getmText1().indexOf("(")));
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ///Toast.makeText(myContext, "Rate: "+Float.toString(v), Toast.LENGTH_SHORT).show();
                RatingHelpers.takeRating[position]=Float.toString(v);
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


    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }
}