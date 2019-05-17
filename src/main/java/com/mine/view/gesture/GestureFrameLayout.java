package com.mine.view.gesture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fenghuo.utils.BuildConfig;
import com.fenghuo.utils.Size;
import com.fenghuo.utils.Utils;

/**
 * Created by gang on 16-4-28.
 */
public class GestureFrameLayout extends FrameLayout {

    private boolean isDim;
    private Size mScreenSize;
    private GestureCallBack mGestureCallBack;
    private final Rect mHotArea = new Rect();
    private Paint mDebugPaint;

    public GestureFrameLayout(Context context) {
        super(context);
        initView(context);
    }

    public GestureFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public GestureFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        setFocusable(true);
        this.mScreenSize = Utils.getScreenSize(context);
        if (BuildConfig.DEBUG) {
            setWillNotDraw(false);
            mDebugPaint = new Paint();
            mDebugPaint.setStyle(Paint.Style.FILL);
            mDebugPaint.setColor(Color.parseColor("#880000FF"));
        }
    }

    public void setGestureCallBack(GestureCallBack callBack) {
        mGestureCallBack = callBack;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateHotRect(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //用系统自带的手势处理:弊端就是它会拦截所有事件,导致不能向下分发
        if (isDim && !mHotArea.contains((int) ev.getX(), (int) ev.getY())) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mGestureCallBack != null) {
                        mGestureCallBack.onTap();
                    }
                    return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //用系统自带的手势处理:弊端就是它会拦截所有事件,导致不能向下分发
        if (isDim && !mHotArea.contains((int) ev.getX(), (int) ev.getY())) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mGestureCallBack != null) {
                        mGestureCallBack.onTap();
                    }
                    return true;
            }
        }
        if (!isDim && mHotArea.contains((int) ev.getX(), (int) ev.getY()) && mGestureCallBack != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = (int) ev.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (ev.getY() - mDownY > Utils.dp2px(20, getContext())) {
                        isSlideDown = true;
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isSlideDown) {
                        isSlideDown = false;
                        mGestureCallBack.onSlideDown();
                    }
                    return true;
            }
        }
        return super.onTouchEvent(ev);
    }

    private int mDownY;
    private boolean isSlideDown;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDebugPaint != null) {
            canvas.drawRect(mHotArea, mDebugPaint);
        }
    }

    private void updateHotRect(boolean small) {

        assert (getChildCount() == 1);

        final ViewGroup viewGroup = (ViewGroup) getChildAt(0);

        //计算边界
        int bottom = 0;
        //菜单展示的时候，hostRect与菜单区域相同； 不展示时，保留最小区域
        int left = small ? (int) (getRight() - Utils.dp2px(50, getContext())) :
                (int) (getRight() - Math.max(Utils.dp2px(50, getContext()), viewGroup.getMeasuredWidth()));
        if (viewGroup.getVisibility() == VISIBLE) {
            bottom = viewGroup.getMeasuredHeight() + getPaddingTop();
        }
        if (bottom <= getPaddingTop()) {
            bottom = (int) ((getMeasuredHeight()) * 0.5f);
        }
        this.mHotArea.set(left, getPaddingTop(), getRight(), bottom);

        invalidate();
    }

    public void dimBackground() {
        isDim = true;
        final ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(Color.TRANSPARENT, Color.parseColor("#6E000000"));
        anim.setEvaluator(LocalArgbEvaluator.getInstance());
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setBackgroundColor(Integer.parseInt(anim.getAnimatedValue().toString()));
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateHotRect(false);
            }
        });
        anim.start();
    }

    public void unDimBackground() {
        isDim = false;
        setBackgroundColor(Color.TRANSPARENT);
        updateHotRect(true);
    }

    public interface GestureCallBack {
        void onTap();

        void onSlideDown();
    }

}
