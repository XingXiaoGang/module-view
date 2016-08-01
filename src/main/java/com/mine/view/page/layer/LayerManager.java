package com.mine.view.page.layer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ext.BuildConfig;
import android.view.ext.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingxiaogang on 2016/6/6.
 */
public class LayerManager implements Handler.Callback {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "test.gang.layerManager";

    public static LayerManager mInstance;
    private Context mContext;
    private WindowManager mWindowManager;
    private List<LayerWrapper> layerWrapperList = new ArrayList<>();//不能用软引用 否则有些手机会回收,导致无法移除窗口
    private Handler mHandler;

    public synchronized static LayerManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LayerManager(context);
        }
        return mInstance;
    }

    private LayerManager(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //注册home接收
        context.registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        mHandler = new Handler(this);
    }

    public Context getContext() {
        return mContext;
    }

    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    public boolean hasLayer() {
        return layerWrapperList != null && !layerWrapperList.isEmpty();
    }

    //添加悬浮窗
    public synchronized boolean addLayer(Intent intent) {
        if (DEBUG) {
            Log.d(TAG, "addLayer: 1");
        }
        ComponentName componentName = intent.getComponent();
        if (componentName == null) {
            throw new RuntimeException("ComponentName is null");
        }
        Class clazz = null;
        try {
            clazz = Class.forName(componentName.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz != null && Layer.class.isAssignableFrom(clazz)) {
            final List<LayerWrapper> list = layerWrapperList;
            if (list != null) {
                //检查是否已经添加
                for (LayerWrapper wrapper : list) {
                    if (wrapper.layer.getClass().isAssignableFrom(clazz)) {
                        wrapper.layer.onNewIntent(intent);
                        wrapper.layer.onResume();
                        if (DEBUG) {
                            Log.d(TAG, "addLayer:3 已存在  :" + componentName);
                        }
                        return false;
                    }
                }

                final LayerWrapper wrapper = new LayerWrapper();
                Layer layer = null;
                try {
                    Constructor constructor = clazz.getConstructor(LayerManager.class);
                    layer = (Layer) constructor.newInstance(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (layer != null) {
                    wrapper.decorView = layer.onCreateView(intent);
                    wrapper.layer = layer;
                    WindowManager.LayoutParams lp = layer.onCreateLayoutParams();
                    if (lp == null) {
                        lp = layer.createDefaultLayoutParams();
                    }
                    mWindowManager.addView(wrapper.decorView, lp);
                    final Layer finalLayer = layer;
                    wrapper.decorView.post(new Runnable() {
                        @Override
                        public void run() {
                            finalLayer.onResume();
                        }
                    });
                    if (DEBUG) {
                        Log.d(TAG, "addLayer: step 4 , layer added");
                    }
                    list.add(wrapper);
                    return true;
                }
            }
        }
        return false;
    }

    //移除悬浮窗
    public synchronized boolean removeLayer(Class<? extends Layer> clazz) {
        if (DEBUG) {
            Log.d(TAG, "removeLayer: step 1, " + clazz.getSimpleName());
        }
        final List<LayerWrapper> list = layerWrapperList;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                LayerWrapper wrapper = list.get(i);
                if (wrapper.layer.getClass().isAssignableFrom(clazz)) {
                    wrapper.layer.onDestroy();
                    try {
                        mWindowManager.removeView(wrapper.decorView);
                        boolean remove = layerWrapperList.remove(wrapper);
                        if (DEBUG) {
                            Log.d(TAG, "removeLayer:step 2 从列表删除 ：" + remove);
                        }
                    } catch (Throwable t) {
                        if (DEBUG) {
                            Log.e(TAG, "remove layer err:", t);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    //隐藏悬浮窗
    @Deprecated
    public boolean hideLayer(Class<? extends Layer> clazz) {
        if (DEBUG) {
            Log.d(TAG, "hideLayer: " + clazz.getSimpleName());
        }
        final List<LayerWrapper> list = layerWrapperList;
        if (list != null) {
            for (LayerWrapper wrapper : list) {
                if (wrapper.layer.getClass().isAssignableFrom(clazz)) {
                    wrapper.decorView.setVisibility(View.INVISIBLE);
                    return true;
                }
            }
        }
        return false;
    }

    //移除所有
    public synchronized void removeAllLayer() {
        if (DEBUG) {
            Log.d(TAG, "removeAllLayer step 1", new RuntimeException());
        }
        final List<LayerWrapper> list = layerWrapperList;
        if (list != null) {
            //这里代码虽然不多，但至关重要 请不要轻易更改
            while (!list.isEmpty()) {
                int last = list.size() - 1;
                try {
                    ((Layer) list.get(last).layer).finish();
                    list.remove(last);
                } catch (Throwable t) {
                    if (DEBUG) {
                        Log.e(TAG, "removeLayer error: " + t);
                    }
                }
            }
        }
    }

    public ILayer getTopLayer() {
        return layerWrapperList == null || layerWrapperList.isEmpty() ? null : layerWrapperList.get(layerWrapperList.size() - 1).layer;
    }

    private void notifyKeyEvent(KeyEvent keyEvent) {
        for (int i = layerWrapperList.size() - 1; i >= 0; i--) {
            final LayerWrapper wrapper = layerWrapperList.get(i);
            if (wrapper.layer != null) {
                if (wrapper.layer.onKeyEvent(keyEvent)) {
                    break;
                }
            }
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.what == R.id.on_home_key) {
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME);
            notifyKeyEvent(keyEvent);
        }
        return false;
    }

    /**
     * 监听是否点击了home键
     */
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                //SYSTEM_HOME_KEY:表示按了home键   SYSTEM_HOME_KEY_LONG:表示长按home键
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY) || TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                    if (DEBUG) {
                        Log.d(TAG, "onReceive: LayerManager  :SYSTEM_HOME_KEY ");
                    }
                    mHandler.sendEmptyMessage(R.id.on_home_key);
                }
            }
        }
    };
}
