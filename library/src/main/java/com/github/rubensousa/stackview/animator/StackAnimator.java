/*
 * Copyright 2016 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
