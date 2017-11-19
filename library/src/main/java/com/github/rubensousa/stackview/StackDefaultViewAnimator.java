package com.github.rubensousa.stackview;

import android.support.v4.view.ViewCompat;
import android.view.View;


public class StackDefaultViewAnimator implements StackViewAnimator {

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
        view.setTranslationY(position * (-verticalSpacing));
    }

    @Override
    public void translateViewToLeft(View view) {

    }

    @Override
    public void translateViewToRight(View view) {

    }
}
