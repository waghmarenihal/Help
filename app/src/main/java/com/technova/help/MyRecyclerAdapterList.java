package com.technova.help;

import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by N on 9/27/2017.
 */

public class MyRecyclerAdapterList extends RecyclerView
        .Adapter<MyRecyclerAdapterList
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerAdapterList";
    private ArrayList<DataObject> mDataset;
    private static MyRecyclerAdapterList.MyClickListener myClickListener;
    public static boolean[] checkStates = new boolean[1000];
    public static String[] choosedContacts = new String[1000];
    public static String[] choosedNames = new String[1000];


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        CardView contactsCardView;
        float latt[], lon[];

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            contactsCardView = (CardView) itemView.findViewById(R.id.card_view);
            for (int i = 0; i < 999; i++) {
                checkStates[i] = false;
                choosedContacts[i] = "";
                choosedNames[i] = "";
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyRecyclerAdapterList.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerAdapterList(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyRecyclerAdapterList.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_list, parent, false);

        MyRecyclerAdapterList.DataObjectHolder dataObjectHolder = new MyRecyclerAdapterList.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final MyRecyclerAdapterList.DataObjectHolder holder, final int position) {
        holder.label.setText(mDataset.get(position).getmText1().substring(0, mDataset.get(position).getmText1().indexOf("(")));
        holder.dateTime.setText(mDataset.get(position).getmText2());


        holder.contactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Firebase.setAndroidContext(view.getContext());
                final String name = mDataset.get(position).getmText1().substring(0, mDataset.get(position).getmText1().indexOf("("));
                final String number = mDataset.get(position).getmText1().substring(mDataset.get(position).getmText1().indexOf("(") + 1, mDataset.get(position).getmText1().indexOf(")"));
                Firebase ref = new Firebase("https://help-82424.firebaseio.com/Requests/" + number);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //Getting the data from snapshot
                            Person person = postSnapshot.getValue(Person.class);

                            if (person.getName().equals(name) && holder.dateTime.getText().toString().equals(person.getAddress())) {
                                Intent map = new Intent(view.getContext(), MapsActivity.class);
                                map.putExtra("name", person.getName());
                                map.putExtra("number", number);
                                map.putExtra("lattitude", Float.parseFloat(person.getLattitude()));
                                map.putExtra("logitude", Float.parseFloat(person.getLongitude()));
                                map.putExtra("reqNo",Integer.toString(person.getRequestCount()));
                                map.putExtra("emergency", person.getAddress());
                                map.putExtra("reqCount",Integer.toString(person.getRequestCount()));
                                view.getContext().startActivity(map);
                                break;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });


            }
        });
      /*  holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });*/

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