package com.vickey.cowork.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vickey.cowork.CoWork;
import com.vickey.cowork.R;
import com.vickey.cowork.adapter.CardViewAdapter;
import com.vickey.cowork.fragment.CoworkHistoryFragment;

public class CoworkHistoryActivity extends AppCompatActivity implements CoworkHistoryFragment.CoworkHistoryListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cowork_history);
    }

    @Override
    public void onCoworkSelected(CoWork cowork) {

    }
}
