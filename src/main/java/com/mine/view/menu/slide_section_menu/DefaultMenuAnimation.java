package com.mine.view.menu.slide_section_menu;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingxiaogang on 2017/7/3.
 */

public class DefaultMenuAnimation implements IMenuAnimation {
    @Override
    public List<ViewPropertyAnimator> getAnimations(ViewGroup viewGroup) {
        List<ViewPropertyAnimator> viewPropertyAnimators = new ArrayList<>();
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View item = viewGroup.getChildAt(i);
            int top = (int) (item.getHeight() + (item.getHeight() * 3.f * i / count));
            item.setTranslationY(-top);
            ViewPropertyAnimator viewPropertyAnimator = ViewPropertyAnimator.animate(item);
            viewPropertyAnimator.setInterpolator(new OvershootInterpolator());
            viewPropertyAnimator.setDuration(getItemDuration());
            viewPropertyAnimator.translationYBy(top);
            viewPropertyAnimator.setStartDelay(Math.max(0, getInterval() * i));
            viewPropertyAnimators.add(viewPropertyAnimator);
        }
        return viewPropertyAnimators;
    }

    @Override
    public long getItemDuration() {
        return 280;
    }

    @Override
    public long getInterval() {
        return 50;
    }
}
