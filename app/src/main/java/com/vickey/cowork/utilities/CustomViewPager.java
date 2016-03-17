package com.vickey.cowork.utilities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.jar.Attributes;

/**
 * Created by vikramgupta on 3/17/16.
 */
public class CustomViewPager extends ViewPager{

    boolean pagingEnabled;

    public CustomViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        this.pagingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (pagingEnabled == true) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (pagingEnabled == true) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }
}
