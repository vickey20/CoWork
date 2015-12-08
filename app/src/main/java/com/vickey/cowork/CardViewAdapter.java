package com.vickey.cowork;

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

    ArrayList<CoWork> arrayList;

    CardViewAdapter(){

        arrayList = new ArrayList<CoWork>();

        CoWork coWork = new CoWork();

        coWork.setLocation("Location 1");
        coWork.setActivityType(1);
        coWork.setTime("4:30PM");
        coWork.setNumAttendees(2);

        arrayList.add(coWork);
        coWork =  null;

        coWork = new CoWork();
        coWork.setLocation("Location 2");
        coWork.setActivityType(2);
        coWork.setTime("10:00AM");
        coWork.setNumAttendees(3);

        arrayList.add(coWork);
        coWork =  null;
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

        holder.location.setText(arrayList.get(position).getLocation());
        if(arrayList.get(position).getActivityType() == 1)
            holder.activityType.setText("Hacking");
        else
            holder.activityType.setText("Reading");
        holder.numAttendees.setText(String.valueOf(arrayList.get(position).getNumAttendees()));
        holder.time.setText(arrayList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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
