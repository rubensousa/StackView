package com.github.rubensousa.stackview.animator;


import android.view.View;

public interface StackAnimationListener {

    void onExitFinished(View view);

    void onEnterFinished(View view);

}
