package com.vickey.cowork.fragment;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vickey.cowork.CoWork;
import com.vickey.cowork.R;
import com.vickey.cowork.adapter.CardViewAdapter;
import com.vickey.cowork.utilities.HelperClass;

import java.util.ArrayList;

public class CoworkHistoryFragment extends Fragment implements CardViewAdapter.CardViewAdapterListener {

    private static final String TAG = CoworkHistoryFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    int mDisplayType;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<CoWork> mCoWorks;
    CoworkHistoryListener mlistener;

    public interface CoworkHistoryListener {
        void onCoworkSelected(CoWork cowork);
    }

    /**
     * Create a new instance of SelectLocationFragment
     */
    public static CoworkHistoryFragment newInstance() {
        CoworkHistoryFragment f = new CoworkHistoryFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_cowork_history, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Bundle extras = getIntent().getExtras();

        if(extras != null) {
            mDisplayType = extras.getInt(CardViewAdapter.DISPLAY_TYPE);
        }

        if(mCoWorks.size() > 0){
            mAdapter = new CardViewAdapter(getApplicationContext(), CoworkHistoryFragment.this, mCoWorks, mDisplayType);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(RecyclerView.VISIBLE);
        }*/

        mAdapter = new CardViewAdapter(getActivity(), CoworkHistoryFragment.this, mCoWorks, mDisplayType);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(RecyclerView.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mlistener = (CoworkHistoryListener) activity;
            mDisplayType = activity.getIntent().getExtras().getInt(CardViewAdapter.DISPLAY_TYPE);

            HelperClass helperClass = new HelperClass(getActivity());

            if (mDisplayType == CardViewAdapter.DISPLAY_TYPE_COWORK_HISTORY) {
                mCoWorks = helperClass.getUserCoworkList();
            } else if (mDisplayType == CardViewAdapter.DISPLAY_TYPE_CHOOSE_FROM_HISTORY){
                mCoWorks = helperClass.getUserCreatedCoworkList();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActionClick(int position) {
        Log.d(TAG, "onActionClick()");

        mlistener.onCoworkSelected(mCoWorks.get(position));
    }
}
