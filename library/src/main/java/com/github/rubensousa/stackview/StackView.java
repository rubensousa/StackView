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

package com.github.rubensousa.stackview;


import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.github.rubensousa.stackview.animator.StackAnimator;
import com.github.rubensousa.stackview.animator.StackDefaultAnimator;
import com.github.rubensousa.stackview.animator.StackAnimationListener;

import java.util.ArrayList;
import java.util.Random;


public class StackView extends FrameLayout implements StackAnimationListener {

    public static final int ANIMATION_DURATION = 500;

    private ArrayList<View> mViews;
    private StackAdapter mAdapter;
    private StackEventListener mEventListener;
    private StackAnimator mAnimator;
    private boolean mCyclic;
    private float mHorizontalSpacing;
    private float mVerticalSpacing;
    private int mItemMaxRotation;
    private int mCurrentSize;
    private int mSize;
    private int mItemsShowing;
    private int mCount;
    private Random mRandom;
    private DataSetObserver mObserver;
    private boolean mPopping;
    private boolean mHardwareAccelerationEnabled;

    @LayoutRes
    private int mLayout;

    public StackView(Context context) {
        this(context, null);
    }

    public StackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViews = new ArrayList<>();
        mRandom = new Random();
        mHardwareAccelerationEnabled = true;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StackView, 0, 0);
        mSize = a.getInteger(R.styleable.StackView_stackview_size, 4);
        mHorizontalSpacing = a.getDimension(R.styleable.StackView_stackview_horizontalSpacing, 0f);
        mVerticalSpacing = a.getDimension(R.styleable.StackView_stackview_verticalSpacing, 10f);
        mItemMaxRotation = a.getInteger(R.styleable.StackView_stackview_rotationRandomMagnitude, 0);
        mCyclic = a.getBoolean(R.styleable.StackView_stackview_cyclic, false);
        mLayout = a.getResourceId(R.styleable.StackView_stackview_adapterLayout, 0);
        mAnimator = new StackDefaultAnimator();
        mAnimator.setStackAnimationListener(this);
        a.recycle();
        setClipToPadding(false);
        setClipChildren(false);

        if (isInEditMode()) {
            addViews();
        }

    }

    public void enableHardwareAcceleration(boolean enable) {
        mHardwareAccelerationEnabled = enable;
        for (View view : mViews) {
            if(view != null) {
                if (mHardwareAccelerationEnabled) {
                    view.setLayerType(LAYER_TYPE_HARDWARE, null);
                } else {
                    view.setLayerType(LAYER_TYPE_SOFTWARE, null);
                }
            }
        }
    }

    public void setStackEventListener(StackEventListener eventListener) {
        mEventListener = eventListener;
    }

    public void setAnimator(StackAnimator animator) {
        mAnimator = animator;
        mAnimator.setStackAnimationListener(this);
    }

    public void pop() {
        if (mPopping || mAdapter.isEmpty()) {
            return;
        }

        mPopping = true;
        View currentView = mViews.get(0);
        Object currentObj = mAdapter.getCurrentItem();

        mAnimator.animatePop(currentObj, currentView);

        if (!mAdapter.isEmpty()) {
            View nextView = mViews.get(1);

            mAnimator.animateReveal(mAdapter.getCurrentItem(), nextView);
        } else {
            mEventListener.onStackEmpty(mCount);
        }

        updateViews();
        mCount++;
    }

    public void setAdapter(StackAdapter<?> adapter) {

        if (mAdapter != null && mObserver != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }

        mAdapter = adapter;
        mObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                if (mItemsShowing < mSize && !mAdapter.isEmpty()
                        && mAdapter.getCount() > mItemsShowing) {
                    int itemsToAdd = mAdapter.getCount() >= mSize - mItemsShowing
                            ? mSize - mItemsShowing : mAdapter.getCount();

                    for (int i = 0; i < itemsToAdd; i++) {
                        int stackPosition = mItemsShowing;
                        mItemsShowing++;
                        View view = mAdapter.getView(i, mViews.get(stackPosition), StackView.this);
                        view.setVisibility(View.VISIBLE);
                        view.setRotation(setupRotation());
                        setupView(view, stackPosition);
                    }

                }
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                removeAllViews();
                addViews();
            }
        };

        mAdapter.registerDataSetObserver(mObserver);
        removeAllViews();
        addViews();
    }

    private void addViews() {
        mViews.clear();
        for (int i = 0; i < mSize; i++) {
            mViews.add(null);
        }
        for (int i = mSize - 1; i >= 0; i--) {
            View view = LayoutInflater.from(getContext()).inflate(mLayout, this, false);
            if (mHardwareAccelerationEnabled) {
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            if (mAdapter != null && i < mAdapter.getCount()) {
                mItemsShowing++;
                view = mAdapter.getView(i, view, this);
            } else {
                // If there's no adapter set or the adapter has less items and the current index,
                // then hide the view
                if (!isInEditMode()) {
                    view.setVisibility(View.INVISIBLE);
                }
            }
            mViews.set(i, view);
            addView(view);
            setupView(view, i);

            // Set a random rotation
            view.setRotation(setupRotation());
        }
    }

    private void updateViews() {
        View view = mViews.remove(0);
        mViews.add(view);

        // Animate translation and scaling
        for (int i = 0; i < mViews.size() - 1; i++) {
            view = mViews.get(i);
            if (view.getVisibility() == View.VISIBLE) {
                setupView(view, i);
            } else {
                // Set a random rotation
                view.setRotation(setupRotation());
            }
        }
    }

    @Override
    public void onExitFinished(View view) {

        // Reset view properties
        ViewCompat.setRotation(view, 0f);
        ViewCompat.setRotationY(view, 0f);
        ViewCompat.setRotationX(view, 0f);
        ViewCompat.setAlpha(view, 1f);
        ViewCompat.setScaleY(view, 1f);
        ViewCompat.setScaleX(view, 1 - (mSize - 1) * 0.05f);
        ViewCompat.setTranslationZ(view, 0f);
        ViewCompat.setTranslationY(view, (mSize - 2) * mVerticalSpacing);
        ViewCompat.setTranslationX(view, 0f);

        mPopping = false;

        if (mAdapter.getCount() - 1 < mSize) {
            if (!mCyclic) {
                mItemsShowing--;
                mAdapter.pop();
                view.setVisibility(View.INVISIBLE);
            }
            return;
        }

        if (!mCyclic) {
            mAdapter.pop();
        }

        // Get a new view for the next position
        if (mAdapter.getCount() >= mSize) {
            view = mAdapter.getView(mSize - 1, view, this);
        }

        // Animate reveal on bottom
        ViewCompat.animate(view)
                .translationY((mSize - 1) * mVerticalSpacing)
                .rotation(setupRotation())
                .setDuration(ANIMATION_DURATION / 2)
                .setInterpolator(new AccelerateInterpolator());
    }

    @Override
    public void onEnterFinished(View view) {
        // Reset view properties
        if (mItemMaxRotation <= 0) {
            ViewCompat.setRotation(view, 0f);
        }
        ViewCompat.setRotationY(view, 0f);
        ViewCompat.setRotationX(view, 0f);
        ViewCompat.setAlpha(view, 1f);
        ViewCompat.setScaleY(view, 1f);
        ViewCompat.setScaleX(view, 1f);
        ViewCompat.setTranslationZ(view, (mSize - 1) * 10f);
        ViewCompat.setTranslationY(view, 0f);
        ViewCompat.setTranslationX(view, 0f);
    }

    private int setupRotation() {
        if (mItemMaxRotation == 0) {
            return 0;
        }

        boolean leftRotation = mRandom.nextInt(2) == 0;

        return leftRotation ? mRandom.nextInt(mItemMaxRotation) * (-1)
                : mRandom.nextInt(mItemMaxRotation);
    }

    private void setupView(View view, int stackPosition) {
        if (!isInEditMode()) {
            ViewCompat.animate(view)
                    .scaleX(1 - stackPosition * 0.05f < 0f ? 0.05f : 1 - stackPosition * 0.05f)
                    .translationX(stackPosition * mHorizontalSpacing)
                    .translationZ((mSize - 1 - stackPosition) * 10)
                    .translationY(stackPosition * mVerticalSpacing)
                    .setDuration(ANIMATION_DURATION);
        } else {
            ViewCompat.setScaleX(view, 1 - stackPosition * 0.05f < 0f
                    ? 0.05f : 1 - stackPosition * 0.05f);
            ViewCompat.setTranslationX(view, stackPosition * mHorizontalSpacing);
            ViewCompat.setTranslationZ(view, (mSize - 1 - stackPosition) * 10);
            ViewCompat.setTranslationY(view, stackPosition * mVerticalSpacing);
        }
    }

    public interface StackEventListener {
        void onStackEmpty(int lastPosition);
    }
}
