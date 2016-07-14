package com.github.rubensousa.stackview.animator;


import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class StackFlipAnimator extends StackAnimator {

    @Override
    public void animateReveal(Object item, View view) {

    }

    @Override
    public void animatePop(Object item, View view) {
        ViewCompat.animate(view)
                .scaleX(0.3f)
                .translationX(-view.getWidth() * 0.8f)
                .translationZ(ViewCompat.getTranslationZ(view) * 1.2f)
                .rotation(-30)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
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
