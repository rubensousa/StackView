package com.github.rubensousa.stackview;


import android.view.View;

public interface StackViewAnimator {

    void setupView(View view, int position);

    void animateToLeft(View view);

    void animateToRight(View view);

    void rotate(View view);
}
