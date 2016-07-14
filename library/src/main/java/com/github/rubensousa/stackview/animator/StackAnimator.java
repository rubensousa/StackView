package com.github.rubensousa.stackview.animator;

import android.view.View;

public abstract class StackAnimator {

    public static final int MOVE_UP = 0;
    public static final int MOVE_DOWN = 1;
    public static final int MOVE_LEFT = 2;
    public static final int MOVE_RIGHT = 3;

    public static final int ANIMATION_DURATION = 500;

    private int mMoveDirection;
    private StackAnimationListener mEventListener;

    public StackAnimator(){
        mMoveDirection = MOVE_LEFT;
    }

    public StackAnimator(int moveDirection){
        mMoveDirection = moveDirection;
    }

    public int getMoveDirection(){
        return mMoveDirection;
    }

    public StackAnimationListener getAnimationListener() {
        return mEventListener;
    }

    public void setStackAnimationListener(StackAnimationListener eventListener) {
        mEventListener = eventListener;
    }

    public int getAnimationDuration(){
        return ANIMATION_DURATION;
    }

    public abstract void animateReveal(Object item, View view);

    public abstract void animatePop(Object item, View view);
}
