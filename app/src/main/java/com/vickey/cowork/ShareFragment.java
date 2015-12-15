package com.vickey.cowork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

public class ShareFragment extends Fragment {

    private final String TAG = "ShareFragment";

    private ShareListener mListener;

    private ImageView mImageShareFB;
    private int mCount = 0;
    ProgressDialog mProgressDialog;

    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        return fragment;
    }

    public ShareFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share, container, false);

        mImageShareFB = (ImageView) v.findViewById(R.id.imageViewShareFB);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mImageShareFB.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToFB();
            }
        });
    }

    public void shareToFB(){

//        if(mCount == 0) {
//            mCount = 1;
            showProgressDialog();
            ShareDialog shareDialog = new ShareDialog(getActivity());
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://developers.facebook.com"))
                    .build();

            shareDialog.show(content);
//        }
        /*else{
            mCount = 0;
            Toast.makeText(getActivity(), "You've shared already!", Toast.LENGTH_LONG).show();
        }*/
    }

    private void showProgressDialog(){
        mProgressDialog = ProgressDialog.show(getActivity(), "Preparing to share...", null, false);
        mProgressDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mProgressDialog != null){
            mProgressDialog.cancel();
        }
    }

    /**
     * Create a new instance of ShareFragment
     */
    static ShareFragment newInstance() {
        ShareFragment f = new ShareFragment();
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ShareListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ShareListener {
        public void shareEvent(int event);
    }

}
