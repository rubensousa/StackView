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

import com.github.rubensousa.stackview.StackView;

public abstract class StackAnimator {

    public static final int ANIMATION_DURATION = 500;

    private StackAnimationListener mEventListener;
    private StackView mStackView;

    public StackAnimator() {

    }

    public void setStackView(StackView stackView){
        mStackView = stackView;
    }

    public StackView getStackView(){
        return mStackView;
    }

    public StackAnimationListener getAnimationListener() {
        return mEventListener;
    }

    public void setStackAnimationListener(StackAnimationListener eventListener) {
        mEventListener = eventListener;
    }

    public int getAnimationDuration() {
        return ANIMATION_DURATION;
    }

    public abstract void animateAdd(View view);

    public abstract void animateChange(View view, int stackPosition);

    public abstract void animatePop(Object item, View view);
}
