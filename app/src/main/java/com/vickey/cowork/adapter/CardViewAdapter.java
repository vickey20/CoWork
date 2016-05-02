package com.vickey.cowork.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vickey.cowork.CoWork;
import com.vickey.cowork.R;
import com.vickey.cowork.activity.HomeActivity;
import com.vickey.cowork.utilities.Constants;

import java.util.ArrayList;

/**
 * Created by vickey on 11/20/2015.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    public final String TAG = CardViewAdapter.class.getSimpleName();
    public static final String DISPLAY_TYPE = "DISPLAY_TYPE";
    public static final int DISPLAY_TYPE_ALL = 1;
    public static final int DISPLAY_TYPE_COWORK_HISTORY = 2;
    public static final int DISPLAY_TYPE_CHOOSE_FROM_HISTORY = 3;

    ArrayList<CoWork> mCoworkList;
    Context mContext;
    int mDisplayType = 1;
    int mSelectedPosition = -1;

    CardViewAdapterListener mCardViewAdapterListener;

    public interface CardViewAdapterListener {
        void onActionClick(int position);
    }

    public CardViewAdapter(Context context, CardViewAdapterListener listener, ArrayList<CoWork> coWorkList, int displayType){
        mContext = context;
        mCoworkList = coWorkList;
        mDisplayType = displayType;
        mCardViewAdapterListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.location.setText(mCoworkList.get(position).getLocationName());
        String[] activityType = mContext.getResources().getStringArray(R.array.array_activities);
        holder.activityType.setText(activityType[mCoworkList.get(position).getActivityType()]);
        holder.numAttendees.setText(String.valueOf(mCoworkList.get(position).getNumAttendees()));
        holder.time.setText(mCoworkList.get(position).getTime() + " " + mCoworkList.get(position).getDate());

        if (mCoworkList.get(position).getCreatorID().equals(HomeActivity.USER_ID)) {
            holder.badge.setVisibility(ImageView.VISIBLE);
        } else {
            holder.badge.setVisibility(ImageView.GONE);
        }

        if(mDisplayType == DISPLAY_TYPE_ALL) {
            holder.action.setText("Done");
        } else if(mDisplayType == DISPLAY_TYPE_COWORK_HISTORY) {
            holder.action.setText("View");
        } else if (mDisplayType == DISPLAY_TYPE_CHOOSE_FROM_HISTORY) {
            holder.action.setText("Re-use");
        }

        if (mDisplayType == DISPLAY_TYPE_CHOOSE_FROM_HISTORY) {
            if (position == mSelectedPosition) {
                holder.action.setTextColor(Color.parseColor(mContext.getResources().getString(R.color.colorPrimary)));
            } else {
                holder.action.setTextColor(Color.BLACK);
            }
        }

        holder.action.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mDisplayType == DISPLAY_TYPE_CHOOSE_FROM_HISTORY) {
                    if (mSelectedPosition != position) {
                        holder.action.setTextColor(Color.parseColor(mContext.getResources().getString(R.color.colorPrimary)));
                        mSelectedPosition = position;
                    } else {
                        holder.action.setTextColor(Color.BLACK);
                        mSelectedPosition = -1;
                    }
                    notifyDataSetChanged();
                }

                mCardViewAdapterListener.onActionClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCoworkList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView location, activityType, numAttendees, time, action;
        ImageView badge;

        public ViewHolder(View itemView) {
            super(itemView);

            location = (TextView) itemView.findViewById(R.id.textViewLocationName);
            activityType = (TextView) itemView.findViewById(R.id.textViewActivityTitle);
            numAttendees = (TextView) itemView.findViewById(R.id.textViewNumAttendees);
            time = (TextView) itemView.findViewById(R.id.textViewTime);
            action = (TextView) itemView.findViewById(R.id.textViewDone);
            badge = (ImageView) itemView.findViewById(R.id.imageViewMyCreation);
        }
    }
}
