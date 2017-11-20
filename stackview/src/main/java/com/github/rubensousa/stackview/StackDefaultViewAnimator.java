package com.github.rubensousa.stackview;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;


public class StackDefaultViewAnimator implements StackViewAnimator {

    private static final int MAX_ROTATION = 20; // degrees

    private float verticalSpacing;
    private float elevationSpacing;
    private StackView stackView;

    public StackDefaultViewAnimator(StackView stackView, float verticalSpacing,
                                    float elevationSpacing) {
        this.stackView = stackView;
        this.verticalSpacing = verticalSpacing;
        this.elevationSpacing = elevationSpacing;
    }

    @Override
    public void reset(View view) {
        view.clearAnimation();
        view.setRotation(0);
        view.setTranslationX(0);
        view.setTranslationY(0);
        ViewCompat.setTranslationZ(view, 0);
        view.setScaleX((float) (1f - 0.02 * (stackView.getSize() - 1)));
        view.setScaleY(1f);
        view.setRotationX(0);
        view.setRotationY(0);
    }

    @Override
    public void setupView(View view, int position) {
        ViewCompat.setElevation(view, elevationSpacing);
        ViewCompat.setTranslationZ(view, (stackView.getSize() - 1 - position)
                * elevationSpacing);
        view.animate()
                .translationY(position * (-verticalSpacing))
                .scaleX(1f - 0.02f * position)
                .rotation(0)
                .setStartDelay(0)
                .translationX(0)
                .setDuration(150)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(null);
    }

    @Override
    public void animateAdd(View view) {
        view.setTranslationY((stackView.getSize() - 2) * (-verticalSpacing));
        view.setTranslationX(0);
        view.animate()
                .scaleY(1f)
                .translationY((stackView.getSize() - 1) * (-verticalSpacing))
                .setStartDelay(300)
                .setInterpolator(new LinearInterpolator())
                .setListener(null);
    }

    @Override
    public ViewPropertyAnimator animateToLeft(View view) {
        return view.animate()
                .translationX(-view.getWidth())
                .rotation(-MAX_ROTATION)
                .translationY(view.getTranslationY() * 2)
                .setStartDelay(0)
                .setDuration(150)
                .setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public ViewPropertyAnimator animateToRight(View view) {
        return view.animate()
                .translationX(view.getWidth())
                .rotation(MAX_ROTATION)
                .translationY(view.getTranslationY() * 2)
                .setStartDelay(0)
                .setDuration(150)
                .setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public void rotate(View view) {
        float rotation = view.getTranslationX() / view.getWidth() * MAX_ROTATION;
        view.setRotation(rotation);
    }

    @Override
    public void animateBack(View view, boolean right) {
        ViewCompat.setTranslationZ(view, (stackView.getSize() - 1) * elevationSpacing);
        view.setScaleX(1f);
        view.setTranslationX(right ? view.getWidth() : -view.getWidth());
        view.setRotation(right ? MAX_ROTATION : -MAX_ROTATION);
        view.animate()
                .translationX(0)
                .translationY(0)
                .rotation(0)
                .setStartDelay(0)
                .setDuration(250)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(null);
    }
}
