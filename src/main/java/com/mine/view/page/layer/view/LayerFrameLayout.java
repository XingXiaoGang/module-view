package com.mine.view.page.layer.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ext.BuildConfig;
import android.widget.FrameLayout;

import com.mine.view.page.layer.ILayer;
import com.mine.view.page.layer.Layer;


/**
 * Created by xingxiaogang on 2016/7/28.
 * 各个窗口的根容器，可以用来分发一些重要的事件
 */
public class LayerFrameLayout extends FrameLayout {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "test.gang.BaseFrameView";

    private ILayer mLayer;

    public LayerFrameLayout(Context context) {
        super(context);
    }

    public LayerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LayerFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void attachLayer(Layer layer) {
        mLayer = layer;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean res = false;
        if (DEBUG) {
            Log.d(TAG, "dispatchKeyEvent " + event);
        }
        if (mLayer != null) {
            res = mLayer.onKeyEvent(event);
        }
        return res || super.dispatchKeyEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (mLayer != null) {
            mLayer.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }
    }
}
