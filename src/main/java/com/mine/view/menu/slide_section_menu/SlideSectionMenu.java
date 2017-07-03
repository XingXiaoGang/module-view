package com.mine.view.menu.slide_section_menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.List;

/**
 * Created by gang on 16-4-21.
 */
public class SlideSectionMenu extends LinearLayout implements Animation.AnimationListener {

    private static final String TAG = "test_slide";
    //当前的状态
    private static State mMenuState = State.CLOSED;
    private IMenuAnimation mIAnim;

    public enum State {
        OPENED, OPENING, CLOSING, CLOSED
    }

    public SlideSectionMenu(Context context) {
        super(context);
        initView(context, null);
    }

    public SlideSectionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SlideSectionMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setVisibility(GONE);
        setWillNotDraw(true);
        setOrientation(VERTICAL);
        mIAnim = new DefaultMenuAnimation();
    }

    public void openMenu(boolean anim) {
        if (mMenuState == State.CLOSED) {
            mMenuState = State.OPENING;
            if (anim) {
                if (getVisibility() == GONE) {
                    setVisibility(INVISIBLE);
                    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            runAnim();
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });
                } else if (getVisibility() == INVISIBLE) {
                    runAnim();
                }
            } else {
                for (int i = 0; i < getChildCount(); i++) {
                    getChildAt(i).setVisibility(VISIBLE);
                }
                setVisibility(VISIBLE);
            }
        }
    }

    public void runAnim() {
        //open
        if (mIAnim != null) {
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setVisibility(INVISIBLE);
            }

            List<ViewPropertyAnimator> mAnimators = mIAnim.getAnimations(this);

            final int count = mAnimators.size();

            for (int i = 0; i < count; i++) {
                final ViewPropertyAnimator animation = mAnimators.get(i);
                final int finalI = i;
                animation.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        //todo 改
                        getChildAt(finalI).setVisibility(VISIBLE);
                        if (finalI == 0) {
                            setVisibility(VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (finalI == count - 1) {
                            mMenuState = State.OPENED;
                        }
                    }
                });
                animation.start();
            }
        }
    }

    public void closeMenu() {
        if (mMenuState == State.OPENED) {
            mMenuState = State.CLOSED;
            setVisibility(INVISIBLE);
        }
    }

    public void setAnimationAdapter(IMenuAnimation adapter) {
        this.mIAnim = adapter;
    }

    public State getMenuState() {
        return mMenuState;
    }

    public void toggleMenu(boolean anim) {
        switch (mMenuState) {
            case OPENED: {
                closeMenu();
                break;
            }
            case CLOSED: {
                openMenu(anim);
                break;
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mMenuState == null) {
            return;
        }
        switch (mMenuState) {
            case OPENING: {
                mMenuState = State.OPENED;
                break;
            }
            case CLOSING: {
                mMenuState = State.CLOSED;
                break;
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


}
