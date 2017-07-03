package com.mine.view.menu.slide_section_menu;

import android.view.ViewGroup;

import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.List;

/**
 * Created by gang on 16-4-21.
 */
public interface IMenuAnimation {

    List<ViewPropertyAnimator> getAnimations(ViewGroup viewGroup);

    long getItemDuration();

    long getInterval();
}
