package com.technova.help;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Training on 8/2/2017.
 */

public class GroupRecyclerViewAdapter extends RecyclerView
        .Adapter<GroupRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "GroupRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static GroupRecyclerViewAdapter.MyClickListener myClickListener;


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        ImageButton imageButton;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.tv_CreateGroup);
            imageButton = (ImageButton) itemView.findViewById(R.id.ib_CancelGroup);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(GroupRecyclerViewAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public GroupRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public GroupRecyclerViewAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_group_mem, parent, false);

        GroupRecyclerViewAdapter.DataObjectHolder dataObjectHolder = new GroupRecyclerViewAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final GroupRecyclerViewAdapter.DataObjectHolder holder, final int position) {
        holder.label.setText(CreateGroupActivity.finalContacts[position]);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
                CreateGroupActivity.finalContacts[position] = "";
                CreateGroupActivity.k--;
            }
        });
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
//        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return CreateGroupActivity.k;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}