package com.mine.view.page.layer;

import android.content.Intent;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by xingxiaogang on 2016/6/6.
 * 悬浮窗的生命周期
 */
public interface ILayer {

    View onCreateView(Intent intent);

    void onNewIntent(Intent intent);

    void onResume();

    void onDestroy();

    boolean onKeyEvent(KeyEvent keyEvent);

    void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect);

}
