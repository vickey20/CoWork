package com.vickey.cowork;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ajay on 11/20/2015.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    ArrayList<CoWork> mCoworkList;
    Context mContext;

    CardViewAdapter(Context context, ArrayList<CoWork> coWorkList){
        mContext = context;
        mCoworkList = coWorkList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.location.setText(mCoworkList.get(position).getLocationName());
        String[] activityType = mContext.getResources().getStringArray(R.array.array_activities);
        holder.activityType.setText(activityType[mCoworkList.get(position).getActivityType()]);
        holder.numAttendees.setText(String.valueOf(mCoworkList.get(position).getNumAttendees()));
        holder.time.setText(mCoworkList.get(position).getTime() + " " + mCoworkList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mCoworkList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView location, activityType, numAttendees, time;

        public ViewHolder(View itemView) {
            super(itemView);

            location = (TextView) itemView.findViewById(R.id.textViewLocationName);
            activityType = (TextView) itemView.findViewById(R.id.textViewActivityTitle);
            numAttendees = (TextView) itemView.findViewById(R.id.textViewNumAttendees);
            time = (TextView) itemView.findViewById(R.id.textViewTime);
        }
    }
}
