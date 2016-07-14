package com.github.rubensousa.stackview.animator;

import android.view.View;

public abstract class StackAnimator {

    public static final int ANIMATION_DURATION = 500;

    private StackAnimationListener mEventListener;

    public StackAnimationListener getAnimationListener() {
        return mEventListener;
    }

    public void setStackAnimationListener(StackAnimationListener eventListener) {
        mEventListener = eventListener;
    }

    public abstract void animateReveal(Object item, View view);

    public abstract void animatePop(Object item, View view);
}
