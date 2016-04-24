package com.vickey.cowork.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vickey.cowork.CoWork;
import com.vickey.cowork.R;
import com.vickey.cowork.UserProfile;

import java.util.ArrayList;

/**
 * Created by vikramgupta on 4/21/16.
 */

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.PeopleViewHolder> {

    UserProfile[] mUserProfiles;
    Context mContext;

    CardViewAdapterListener mCardViewAdapterListener;

    public interface CardViewAdapterListener {
        void onNumAttendeesClick();
    }

    public AttendeesAdapter(Context context, UserProfile[] userProfiles){
        mContext = context;
        mUserProfiles = userProfiles;
    }

    @Override
    public PeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_people_item, parent, false);
        PeopleViewHolder viewHolder = new PeopleViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PeopleViewHolder holder, int position) {

        holder.name.setText(mUserProfiles[position].getName());
        holder.age.setText(mUserProfiles[position].getBirthday());
        holder.email.setText(mUserProfiles[position].getEmail());
        holder.profession.setText(mUserProfiles[position].getProfession());

        holder.impageProfile.setImageResource(R.mipmap.ic_launcher);

        holder.impageProfile.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                mCardViewAdapterListener.onNumAttendeesClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserProfiles.length;
    }

    class PeopleViewHolder extends RecyclerView.ViewHolder{
        TextView name, age, email, profession;
        ImageView impageProfile;

        public PeopleViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            email = (TextView) itemView.findViewById(R.id.email);
            profession = (TextView) itemView.findViewById(R.id.profession);
            impageProfile = (ImageView) itemView.findViewById(R.id.imageProfile);
        }
    }
}

