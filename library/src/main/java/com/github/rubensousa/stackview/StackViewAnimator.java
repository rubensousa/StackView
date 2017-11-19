package com.github.rubensousa.stackview;


import android.view.View;
import android.view.ViewPropertyAnimator;

public interface StackViewAnimator {

    void setupView(View view, int position);

    void animateAdd(View view);

    void rotate(View view);

    ViewPropertyAnimator animateToLeft(View view);

    ViewPropertyAnimator animateToRight(View view);
}
