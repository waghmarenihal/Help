package com.technova.help;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by N on 12/16/2017.
 */

public class RecyclerViewAdapterReqest extends RecyclerView
        .Adapter<RecyclerViewAdapterReqest
        .DataObjectHolder> {
    private ArrayList<DataObject> mDataset;
    private static RecyclerViewAdapterReqest.MyClickListener myClickListener;
    Context myContext;
    double latitude, longitude;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        CardView contactsCardView;
        ImageButton accept, reject;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            contactsCardView = (CardView) itemView.findViewById(R.id.card_view);
            accept = (ImageButton) itemView.findViewById(R.id.ib_AcceptRe);
            reject = (ImageButton) itemView.findViewById(R.id.ib_RejectRe);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


    public RecyclerViewAdapterReqest(ArrayList<DataObject> myDataset, Context context, double latitude, double longitude) {
        mDataset = myDataset;
        this.latitude = latitude;
        this.longitude = longitude;
        myContext = context;
    }

    @Override
    public RecyclerViewAdapterReqest.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_request, parent, false);

        RecyclerViewAdapterReqest.DataObjectHolder dataObjectHolder = new RecyclerViewAdapterReqest.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterReqest.DataObjectHolder holder, final int position) {
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
        SharedPreferences runCheck = myContext.getSharedPreferences("Name", 0); //load the preferences
        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
        final String number = runCheck.getString("number", "no number");
        Firebase.setAndroidContext(myContext);
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/FollowMe/" + number);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot poSnapshot : dataSnapshot.getChildren()) {
                    FollowMeAdapter adapter = poSnapshot.getValue(FollowMeAdapter.class);
                    if (adapter.getState().equals("rejected")) {
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.reject.setVisibility(View.INVISIBLE);
                    } else if (adapter.getState().equals("accepted")) {
                        holder.reject.setVisibility(View.VISIBLE);
                        holder.accept.setVisibility(View.INVISIBLE);
                    } else {
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.reject.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firebase.setAndroidContext(view.getContext());
                final Firebase ref = new Firebase("https://help-82424.firebaseio.com/FollowMe/" + number + "/" + mDataset.get(position).getmText2());
                FollowMeAdapter followMeAdapter = new FollowMeAdapter(mDataset.get(position).getmText2(), mDataset.get(position).getmText1(), "accepted");
                //ref.push();
                ref.setValue(followMeAdapter);
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.VISIBLE);
                //view.getContext().startService(new Intent(view.getContext(), MyService.class));

                /***************************Tracking Logic Nihal************************/
                int minTime = 5000;
                LocationManager locationManager = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                MyLocationListener myLocListener = new MyLocationListener(view.getContext());
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(true);
                criteria.setSpeedRequired(false);
                String bestProvider = locationManager.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                locationManager.requestLocationUpdates(bestProvider, minTime, 1, myLocListener);
                SharedPreferences runCheck = view.getContext().getSharedPreferences("Name", 0); //load the preferences
                final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
                final String number = runCheck.getString("number", "no number");
                final Firebase refNe = new Firebase("https://help-82424.firebaseio.com/CurrentLocation/" + number);
                CurrentLocation currentLocation = new CurrentLocation(name, number, latitude, longitude);
                refNe.setValue(currentLocation);
                Toast.makeText(view.getContext(), "Now " + mDataset.get(position).getmText1() + " can see your location", Toast.LENGTH_SHORT).show();
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firebase.setAndroidContext(view.getContext());
                final Firebase ref = new Firebase("https://help-82424.firebaseio.com/FollowMe/" + number + "/" + mDataset.get(position).getmText2());
                FollowMeAdapter followMeAdapter = new FollowMeAdapter(mDataset.get(position).getmText2(), mDataset.get(position).getmText1(), "rejected");
                //ref.push();
                ref.setValue(followMeAdapter);
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.INVISIBLE);
                Toast.makeText(view.getContext(), "Now " + mDataset.get(position).getmText1() + " cannot see your location", Toast.LENGTH_SHORT).show();
            }
        });
        //Toast.makeText(myContext, "Po: "+Integer.toString(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}


