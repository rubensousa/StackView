package com.github.rubensousa.stackview;


import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class StackAnimator {

    public static final int ANIMATION_DURATION = 500;

    private StackEventListener mEventListener;

    public StackAnimator() {

    }

    public StackEventListener getEventListener() {
        return mEventListener;
    }

    public void setStackEventListener(StackEventListener eventListener) {
        mEventListener = eventListener;
    }

    public void animateReveal(Object item, View view) {

    }

    public void animatePop(Object item, final View view) {
        ViewCompat.animate(view)
                .translationX(-view.getWidth() * 1.1f)
                .translationZ(ViewCompat.getTranslationZ(view) * 1.2f)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        ViewCompat.animate(view).setListener(null);
                        mEventListener.onExitFinished();

                    }
                });
    }

}
