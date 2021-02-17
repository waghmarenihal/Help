package com.technova.help;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import static com.technova.help.MyRequests.count;

/**
 * Created by N on 12/2/2017.
 */

public class MyRecyclerViewAdapterRequests extends RecyclerView
        .Adapter<MyRecyclerViewAdapterRequests
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapterRequests";
    private ArrayList<DataObject> mDataset;
    private static MyRecyclerViewAdapterRequests.MyClickListener myClickListener;
    Context myContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        CardView contactsCardView;
        CheckBox checkBox;
        ImageButton cancel;
        ImageButton forward;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            contactsCardView = (CardView) itemView.findViewById(R.id.card_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkboxAll);
            cancel = (ImageButton) itemView.findViewById(R.id.imageButtonCancel);
            forward = (ImageButton) itemView.findViewById(R.id.imageButtonForward);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    public MyRecyclerViewAdapterRequests(ArrayList<DataObject> myDataset, Context context) {
        mDataset = myDataset;
        myContext = context;
    }

    @Override
    public MyRecyclerViewAdapterRequests.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_requests, parent, false);

        MyRecyclerViewAdapterRequests.DataObjectHolder dataObjectHolder = new MyRecyclerViewAdapterRequests.DataObjectHolder(view);
        return dataObjectHolder;
    }
    @Override
    public void onBindViewHolder(final MyRecyclerViewAdapterRequests.DataObjectHolder holder, final int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText("Replies: " + mDataset.get(position).getmText2());
        final String address=mDataset.get(position).getmText1();

        holder.contactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDataset.get(position).getmText2().equals("0")) {
                    myContext.startActivity(new Intent(myContext, AllReqResponses.class)
                            .putExtra("reqCount", count[position]));
                }
            }
        });
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setMessage("Do you want to delete your request?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        holder.contactsCardView.setCardBackgroundColor(Color.parseColor("#AED581"));
                                        if (!mDataset.get(position).getmText2().equals("0")) {
                                            myContext.startActivity(new Intent(myContext, RatingHelpers.class)
                                                    .putExtra("reqCount", Integer.toString(count[position]))
                                                    .putExtra("address",address));

                                        }else{
                                            SharedPreferences runCheck = myContext.getSharedPreferences("Name", 0); //load the preferences
                                            final String number = runCheck.getString("number", "no number");

                                            Firebase.setAndroidContext(myContext);
                                            final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Requests/" + number);
                                            ref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        final Person person = postSnapshot.getValue(Person.class);
                                                        if (person.getAddress().equals(address)){
                                                            postSnapshot.getRef().removeValue();
                                                            // Toast.makeText(myContext, person.getAddress(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(FirebaseError firebaseError) {

                                                }
                                            });
                                            deleteItem(position);
                                        }
                                    }
                                });
                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });



        holder.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.reason=mDataset.get(position).getmText1();
                myContext.startActivity(new Intent(myContext,ChooseContactActivity.class));
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
