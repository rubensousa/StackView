package com.github.rubensousa.stackview.animator;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;


public class StackFlipSlideAnimator extends StackAnimator {

    public StackFlipSlideAnimator(int moveDirection) {
        super(moveDirection);
    }

    @Override
    public int getAnimationDuration() {
        return 1000;
    }

    @Override
    public void animateReveal(Object item, View view) {

    }

    @Override
    public void animatePop(Object item, View view) {
        ViewCompat.animate(view)
                .scaleX(0.3f)
                .scaleY(0.3f)
                .setDuration(getAnimationDuration() / 10);

        if (getMoveDirection() == MOVE_LEFT || getMoveDirection() == MOVE_RIGHT) {
            ViewCompat.animate(view)
                    .translationX(getMoveDirection() == MOVE_LEFT
                            ? -view.getWidth() : view.getWidth())
                    .translationZ(ViewCompat.getTranslationZ(view) * 1.5f)
                    .rotationY(getMoveDirection() == MOVE_LEFT ? 250 : -250)
                    .setDuration(getAnimationDuration())
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(View view) {
                            super.onAnimationEnd(view);
                            ViewCompat.animate(view).setListener(null);
                            getAnimationListener().onExitFinished(view);
                        }
                    });

        } else {
            ViewCompat.animate(view)
                    .translationY(getMoveDirection() == MOVE_UP
                            ? -view.getHeight() : view.getHeight())
                    .translationZ(ViewCompat.getTranslationZ(view) * 1.5f)
                    .rotationX(getMoveDirection() == MOVE_UP ? 250 : -250)
                    .setDuration(getAnimationDuration())
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
}
