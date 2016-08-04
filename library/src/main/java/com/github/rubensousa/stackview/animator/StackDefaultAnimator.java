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
import android.view.animation.OvershootInterpolator;

import com.github.rubensousa.stackview.StackView;

public class StackDefaultAnimator extends StackAnimator {

    @Override
    public void animateAdd(View view) {
        ViewCompat.animate(view)
                .scaleY(1f)
                .scaleX(1 - getStackView().getCurrentSize() * getStackView().getScaleXFactor()
                        < StackView.SCALE_X_MIN ? StackView.SCALE_X_MIN
                        : 1 - getStackView().getCurrentSize() * getStackView().getScaleXFactor())
                .translationY((getStackView().getCurrentSize() * getStackView().getVerticalSpacing()))
                .rotation(getStackView().nextRotation())
                .setStartDelay(getStackView().getCurrentSize() * 80)
                .setDuration(getAnimationDuration())
                .setInterpolator(new OvershootInterpolator(0.3f));
    }

    @Override
    public void animateChange(View view, final int stackPosition) {
        ViewCompat.animate(view)
                .scaleX(1 - stackPosition * getStackView().getScaleXFactor() < StackView.SCALE_X_MIN ?
                        StackView.SCALE_X_MIN : 1 - stackPosition * getStackView().getScaleXFactor())
                .translationX(stackPosition * getStackView().getHorizontalSpacing())
                .translationZ((getStackView().getSize() - 1 - stackPosition) * 10)
                .setStartDelay(stackPosition * 50)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(getAnimationDuration())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        ViewCompat.animate(view).setListener(null);
                        getAnimationListener().onChangeFinished(view, stackPosition);
                    }
                });

        ViewCompat.animate(view)
                .translationY(stackPosition * getStackView().getVerticalSpacing())
                .setInterpolator(new OvershootInterpolator(3f))
                .setDuration(getAnimationDuration());
    }

    @Override
    public void animatePop(Object item, View view) {
        ViewCompat.animate(view)
                .translationX(-view.getWidth() * 1.4f)
                .translationY(view.getHeight() * 0.1f)
                .translationZ(ViewCompat.getTranslationZ(view) * 1.5f)
                .setDuration(getAnimationDuration())
                .rotation(-25)
                .setInterpolator(new AccelerateInterpolator())
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
