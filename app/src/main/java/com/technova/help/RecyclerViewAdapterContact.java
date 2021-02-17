package com.technova.help;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
 * Created by N on 12/14/2017.
 */

public class RecyclerViewAdapterContact extends RecyclerView
        .Adapter<RecyclerViewAdapterContact
        .DataObjectHolder> {
    private ArrayList<DataObject> mDataset;
    private static RecyclerViewAdapterContact.MyClickListener myClickListener;
    Context myContext;
    static int[] flag = new int[1000];
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


    public RecyclerViewAdapterContact(ArrayList<DataObject> myDataset, Context context) {
        mDataset = myDataset;
        myContext = context;
    }

    @Override
    public RecyclerViewAdapterContact.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_follow_req

                        , parent, false);

        RecyclerViewAdapterContact.DataObjectHolder dataObjectHolder = new RecyclerViewAdapterContact.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterContact.DataObjectHolder holder, final int position) {

        SharedPreferences runCheck = myContext.getSharedPreferences("Name", 0); //load the preferences
        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
        final String number = runCheck.getString("number", "no number");


        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/FollowMe/" + mDataset.get(position).getmText2());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (com.firebase.client.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FollowMeAdapter followMeAdapter = postSnapshot.getValue(FollowMeAdapter.class);
                    if (followMeAdapter.getState().equals("pending")) {
                        flag[position] = 2;
                        holder.contactsCardView.setCardBackgroundColor(Color.parseColor("#AED581"));
                    } else if (followMeAdapter.getState().equals("accepted")) {
                        flag[position] = 1;
                        holder.contactsCardView.setCardBackgroundColor(Color.parseColor("#8BC34A"));
                    } else if (followMeAdapter.getState().equals("rejected")) {
                        flag[position] = 0;
                        holder.contactsCardView.setCardBackgroundColor(Color.parseColor("#616161"));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        holder.contactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (flag[position] == 1) {
                    //Toast.makeText(myContext, "Hotay", Toast.LENGTH_SHORT).show();
                    final String[] number = {""};
                    Firebase.setAndroidContext(view.getContext());
                    final Firebase ref = new Firebase("https://help-82424.firebaseio.com/CurrentLocation");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CurrentLocation currentLocation = snapshot.getValue(CurrentLocation.class);
                                if (currentLocation.getNumber().equals(mDataset.get(position).getmText2())) {
                                    number[0] = currentLocation.getNumber();
                                }
                            }
                            if (!number[0].equals(""))
                                myContext.startActivity(new Intent(myContext, TrackingActivity.class).putExtra("number", number[0]));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                } else {
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
                    }
                }
            }
        });

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

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}

