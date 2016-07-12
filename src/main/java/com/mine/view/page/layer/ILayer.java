package com.mine.view.page.layer;

import android.content.Intent;
import android.view.View;

/**
 * Created by xingxiaogang on 2016/6/6.
 * 悬浮窗的生命周期
 */
public interface ILayer {

    View onCreateView(Intent intent);

    void onNewIntent(Intent intent);

    boolean onKeyEvent(int keycode);

    void onResume();

    void onDestroy();

}
