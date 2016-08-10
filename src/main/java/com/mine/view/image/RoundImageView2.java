package com.mine.view.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xingxiaogang on 2016/8/9.
 */
public class RoundImageView2 extends View {

    private Rect mDisplayRect = new Rect();
    private Path mCirclePath = new Path();

    public RoundImageView2(Context context) {
        super(context);
    }

    public RoundImageView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDisplayRect.set(0, 0, right - left, bottom - top);
    }

    @Override
    public void draw(Canvas canvas) {
        mCirclePath.reset();
        mCirclePath.addCircle(mDisplayRect.width() / 2, mDisplayRect.height() / 2, mDisplayRect.width() / 2, Path.Direction.CCW);
        canvas.clipPath(mCirclePath);
        super.draw(canvas);
    }
}
