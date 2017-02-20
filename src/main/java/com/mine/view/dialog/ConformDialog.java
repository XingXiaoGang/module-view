package com.mine.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ext.R;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by xingxiaogang on 2016/8/13.
 */
public abstract class ConformDialog extends Dialog implements View.OnClickListener {

    private String mContent;
    private String mLButtonText;
    private String mRButtonText;
    private int lColor = -1;
    private int rColor = -1;

    public ConformDialog(Context context) {
        super(context);
    }

    public ConformDialog(Context context, String text) {
        super(context);
        this.mContent = text;
    }

    public ConformDialog setButtonTextColor(@ColorInt int lColor, @ColorInt int rColor) {
        this.lColor = lColor;
        this.rColor = rColor;
        return this;
    }

    public ConformDialog setButtonText(String lText, String rText) {
        this.mLButtonText = lText;
        this.mRButtonText = rText;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conform_dialog);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
        final TextView textView = (TextView) findViewById(R.id.text_view);
        if (mContent != null) {
            textView.setText(mContent);
        }
        if (mLButtonText != null) {
            ((TextView) findViewById(R.id.cancel)).setText(mLButtonText);
        }
        if (mRButtonText != null) {
            ((TextView) findViewById(R.id.confirm)).setText(mRButtonText);
        }
        if (lColor != -1) {
            ((TextView) findViewById(R.id.cancel)).setTextColor(lColor);
        }
        if (rColor != -1) {
            ((TextView) findViewById(R.id.confirm)).setTextColor(rColor);
        }

        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (textView.getLineCount() > 10) {
                    int lineEndIndex = textView.getLayout().getLineEnd(10 - 1);
                    String locale = String.format(Locale.getDefault(), "...");
                    String text = mContent.subSequence(0, lineEndIndex - locale.length()) + locale;
                    textView.setText(text);
                } else {
                    removeGlobalOnLayoutListener(textView.getViewTreeObserver(), this);
                }
            }

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            private void removeGlobalOnLayoutListener(ViewTreeObserver obs, ViewTreeObserver.OnGlobalLayoutListener listener) {
                if (obs == null)
                    return;
                if (Build.VERSION.SDK_INT < 16) {
                    obs.removeGlobalOnLayoutListener(listener);
                } else {
                    obs.removeOnGlobalLayoutListener(listener);
                }
            }
        });
    }
}
