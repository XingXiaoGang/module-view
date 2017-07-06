package com.mine.view.gesture;

import android.content.Context;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.fenghuo.utils.Size;
import com.fenghuo.utils.Utils;

/**
 * Created by gang on 16-4-28.
 */
public class GestureHandler implements GestureDetector.OnGestureListener {

    private GestureCallBack mGestureCallBack;
    private GestureDetector mGestureDetector;
    private Size mScreenSize;
    boolean isHandled = false;

    public GestureHandler(Context context, GestureCallBack callBack) {
        this.mGestureDetector = new GestureDetector(context, this);
        this.mGestureCallBack = callBack;
        this.mScreenSize = Utils.getScreenSize(context);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                isHandled = false;
                break;
            }
        }
        mGestureDetector.onTouchEvent(event);
        return isHandled;
    }

    public void setGestureCallBack(GestureCallBack callBack) {
        mGestureCallBack = callBack;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        isHandled = false;
        return isHandled;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        isHandled = true;
        return mGestureCallBack != null && mGestureCallBack.onGestureClick();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        isHandled = true;
        return isHandled;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int dx = (int) Math.abs(e2.getX() - e1.getX());
        int dy = (int) Math.abs(e2.getY() - e1.getY());
        Rect rect = new Rect((int) (mScreenSize.width / 5 * 4), 0, (int) mScreenSize.width, (int) (mScreenSize.height / 5 * 3));
        if (rect.contains((int) e1.getRawX(), (int) e1.getRawY()) && rect.contains((int) e2.getRawX(), (int) e2.getRawY())) {
            if (dy > dx) {
                if (mGestureCallBack != null) {
                    if (velocityY > velocityX) {
                        mGestureCallBack.onSlideDown();
                    } else {
                        mGestureCallBack.onSlideUp();
                    }
                }
                return isHandled = true;
            }
        }
        isHandled = false;
        return isHandled;
    }

    public interface GestureCallBack {
        boolean onGestureClick();

        boolean onSlideDown();

        boolean onSlideUp();
    }
}
