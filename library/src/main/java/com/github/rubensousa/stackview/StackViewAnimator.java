package com.github.rubensousa.stackview;


import android.view.View;

public interface StackViewAnimator {

    void setupView(View view, int position);

    void translateViewToLeft(View view);

    void translateViewToRight(View view);
}
