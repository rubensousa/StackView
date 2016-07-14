package com.github.rubensousa.stackview.animator;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;


public class StackSlideShrinkAnimator extends StackAnimator {

    public StackSlideShrinkAnimator(int slide) {
        super(slide);
    }

    @Override
    public void animateReveal(Object item, View view) {

    }

    @Override
    public void animatePop(Object item, View view) {
        ViewCompat.animate(view)
                .scaleY(0f)
                .scaleX(0f)
                .translationY(getMoveDirection() == MOVE_UP ? -view.getHeight()
                        : getMoveDirection() == MOVE_DOWN ? view.getHeight() : 0f)
                .translationX(getMoveDirection() == MOVE_LEFT ? -view.getWidth() * 1.2f :
                        getMoveDirection() == MOVE_RIGHT ? view.getWidth() * 1.2f : 0.0f)
                .translationZ(ViewCompat.getTranslationZ(view) * 1.5f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(getAnimationDuration())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        ViewCompat.animate(view).setListener(null);
                        getAnimationListener().onExitFinished(view);
                    }
                });
    }
}
