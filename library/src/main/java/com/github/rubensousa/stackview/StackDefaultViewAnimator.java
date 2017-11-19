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
    private StackCardView stackCardView;

    public StackDefaultViewAnimator(StackCardView stackCardView, float verticalSpacing,
                                    float elevationSpacing) {
        this.stackCardView = stackCardView;
        this.verticalSpacing = verticalSpacing;
        this.elevationSpacing = elevationSpacing;
    }

    @Override
    public void setupView(View view, int position) {
        ViewCompat.setElevation(view, elevationSpacing);
        ViewCompat.setTranslationZ(view, (stackCardView.getMaxViews() - 1 - position)
                * elevationSpacing);
        view.animate()
                .translationY(position * (-verticalSpacing))
                .rotation(0)
                .translationX(0)
                .setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public void animateAdd(View view) {
        view.setScaleY(0.8f);
        view.setTranslationX(0);
        view.animate()
                .scaleY(1f)
                .translationY((stackCardView.getCurrentViews() - 1) * (-verticalSpacing))
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public ViewPropertyAnimator animateToLeft(View view) {
        return view.animate()
                .translationX(-view.getWidth())
                .rotation(-MAX_ROTATION)
                .translationY(view.getTranslationY()*2)
                .setDuration(150)
                .setInterpolator(new LinearInterpolator());
    }

    @Override
    public ViewPropertyAnimator animateToRight(View view) {
        return view.animate()
                .translationX(view.getWidth())
                .rotation(MAX_ROTATION)
                .translationY(view.getTranslationY()*2)
                .setDuration(150)
                .setInterpolator(new LinearInterpolator());
    }

    @Override
    public void rotate(View view) {
        float rotation = view.getTranslationX() / view.getWidth() * MAX_ROTATION;
        view.setRotation(rotation);
    }
}
