package com.technova.help;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;
    public static boolean[] checkStates = new boolean[1000];
    public static String[] choosedContacts = new String[1000];
    public static String[] choosedNames = new String[1000];


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
            for (int i = 0; i < 999; i++) {
                checkStates[i] = false;
                choosedContacts[i] = "";
                choosedNames[i] = "";
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
        holder.checkBox.setChecked(checkStates[position]);
        holder.contactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checkbox Logic
                if (holder.checkBox.isChecked() == true) {
                    holder.checkBox.setChecked(false);
                    checkStates[position] = false;
                    choosedContacts[position] = "";
                    choosedNames[position] = "";
                } else {
                    holder.checkBox.setChecked(true);
                    checkStates[position] = true;
                    choosedContacts[position] = mDataset.get(position).getmText2();
                    choosedNames[position] = mDataset.get(position).getmText1();
                    /*if (Tab_1_Activity.searchFlag) {
                        Tab_1_Activity.search.setText("");
                    }
                   /* if (CreateGroupActivity.search.equals("")) {
                        CreateGroupActivity.search.setText("");
                    }*/
                }
            }
        });
      /*  holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });*/
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked() == true) {
                    // holder.checkBox.setChecked(false);
                    checkStates[position] = true;
                    choosedContacts[position] = mDataset.get(position).getmText2();
                    choosedNames[position] = mDataset.get(position).getmText1();
                } else {
                    //holder.checkBox.setChecked(true);
                    checkStates[position] = false;
                    choosedContacts[position] = "";
                    choosedNames[position] = "";
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