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


import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class StackSlideFadeAnimator extends StackAnimator {

    public StackSlideFadeAnimator(int moveDirection) {
        super(moveDirection);
    }

    @Override
    public void animateAdd(View view) {

    }

    @Override
    public void animateChange(View view, int stackPosition, int stackSize) {

    }

    @Override
    public void animatePop(Object item, final View view) {
        ViewCompat.animate(view)
                .alpha(0.0f)
                .translationX(getMoveDirection() == MOVE_LEFT ? -view.getWidth() * 1.2f :
                        getMoveDirection() == MOVE_RIGHT ? view.getWidth() * 1.2f : 0.0f)
                .translationY(getMoveDirection() == MOVE_UP ? -view.getHeight()
                        : getMoveDirection() == MOVE_DOWN ? view.getHeight() : 0f)
                .translationZ(ViewCompat.getTranslationZ(view) * 1.5f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(getAnimationDuration())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        ViewCompat.animate(view).setListener(null);
                        getAnimationListener().onExitFinished(view);
                    }
                });
    }
}
