package com.mine.view.page.layer;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.apusapps.tools.unreadtips.UnreadConfig;

/**
 * Created by xingxiaogang on 2016/6/6.
 */
public abstract class Layer implements ILayer {

    private static final boolean DEBUG = UnreadConfig.DEBUG;
    private static final String TAG = "test.gang.layer";
    private LayerManager mLayerManager;
    public int mScreenWidth;
    public int mScreenHeight;

    public Layer(LayerManager layerManager) {
        this.mLayerManager = layerManager;
        WindowManager wm = layerManager.getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    public Context getContext() {
        return mLayerManager.getContext();
    }

    public LayerManager getLayerManager() {
        return mLayerManager;
    }

    public void finish() {
        onDestroy();
        boolean res = mLayerManager.removeLayer(getClass());
        if (DEBUG) {
            Log.d(TAG, "finish: remote:" + res);
        }
    }

    protected abstract WindowManager.LayoutParams onCreateLayoutParams();

    public final WindowManager.LayoutParams createDefaultLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.type = PhoneDeviceMatchUtils.isOsToastLevel() ? WindowManager.LayoutParams.TYPE_TOAST : WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.flags = layoutParams.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        return layoutParams;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return LayerManager.getInstance(getContext()).removeLayer(getClass());
    }
}
