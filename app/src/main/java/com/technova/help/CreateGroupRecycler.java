package com.technova.help;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Training on 8/6/2017.
 */

public class CreateGroupRecycler extends RecyclerView
        .Adapter<CreateGroupRecycler
        .DataObjectHolder> {
    private static String LOG_TAG = "CreateGroupRecycler";
    private ArrayList<DataObject> mDataset;
    private static CreateGroupRecycler.MyClickListener myClickListener;
    public static boolean[] checkStates=new boolean[1000];
    public static String[] choosedGroups=new String[1000];


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        CardView contactsCardView;
        CheckBox checkBox;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textViewGroup);
            dateTime = (TextView) itemView.findViewById(R.id.textViewGroupMembers);
            contactsCardView = (CardView) itemView.findViewById(R.id.card_viewGroup);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkboxGroup);
            for (int i=0;i<999;i++){
                checkStates[i]=false;
                choosedGroups[i]="";
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(CreateGroupRecycler.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public CreateGroupRecycler(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public CreateGroupRecycler.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_group, parent, false);

        CreateGroupRecycler.DataObjectHolder dataObjectHolder = new CreateGroupRecycler.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final CreateGroupRecycler.DataObjectHolder holder, final int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
        holder.checkBox.setChecked(checkStates[position]);
        holder.contactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checkbox Logic
                if (holder.checkBox.isChecked() == true) {
                    holder.checkBox.setChecked(false);
                    checkStates[position]=false;
                    choosedGroups[position]="";
                } else {
                    holder.checkBox.setChecked(true);
                    checkStates[position]=true;
                    choosedGroups[position]=mDataset.get(position).getmText2();
                }
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked() == true) {
                    // holder.checkBox.setChecked(false);
                    checkStates[position]=true;
                    choosedGroups[position]=mDataset.get(position).getmText2();
                    //choosedNames[position]=mDataset.get(position).getmText1();
                } else {
                    //holder.checkBox.setChecked(true);
                    checkStates[position]=false;
                    choosedGroups[position]="";
                    //choosedNames[position]="";
                }
            }
        });
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}