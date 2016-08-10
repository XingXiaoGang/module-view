package com.mine.view.image.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by xingxiaogang on 2016/8/9.
 */
public class TextDrawable extends Drawable {

    private Paint mTextPaint;
    private String mText = "TextDrawable";
    private int mBackgroundColor;
    private Rect mTextRect;
    private Rect mDrawRect;

    public TextDrawable() {
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(18);
        mBackgroundColor = Color.argb(150, 255, 255, 255);
        mTextRect = new Rect();
        mDrawRect = new Rect();
    }

    public void setTextColor(int mTextColor) {
        mTextPaint.setColor(mTextColor);
    }

    public void setTextSize(int mTextSize) {
        this.mTextPaint.setTextSize(mTextSize);
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(mBackgroundColor);
            mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
            canvas.drawText(mText, mDrawRect.width() / 2 - mTextRect.width() / 2, mDrawRect.height() / 2 + mTextRect.height() / 2, mTextPaint);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mDrawRect.set(bounds);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
