/*
 * Copyright 2017 Rúben Sousa
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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;


public class StackView extends FrameLayout implements View.OnTouchListener {

    private static final int DEFAULT_SIZE = 4;

    private int layoutId;
    private int size;
    private int visibleItems;
    private float startX;
    private float startY;
    private boolean swipeRight;
    private List<View> views;
    private StackAdapter adapter;
    private StackViewAnimator animator;

    public StackView(@NonNull Context context) {
        this(context, null, 0);
    }

    public StackView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StackView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StackView, 0, 0);
        views = new ArrayList<>();
        size = a.getInt(R.styleable.StackView_stackview_size, DEFAULT_SIZE);
        layoutId = a.getResourceId(R.styleable.StackView_stackview_adapterLayout, 0);
        visibleItems = 0;
        a.recycle();
        setClipToPadding(false);
        setClipChildren(false);
        animator = new StackDefaultViewAnimator(this,
                getResources().getDimensionPixelOffset(R.dimen.stackview_vertical_spacing),
                getResources().getDimensionPixelOffset(R.dimen.stackview_elevation_spacing));
        if (isInEditMode()) {
            addViews();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                v.clearAnimation();
                return false;
            case MotionEvent.ACTION_UP:
                float translationX = v.getTranslationX();
                if (v.getLeft() + translationX >= 0.4 * v.getWidth()) {
                    animator.animateToRight(v).setListener(swipeListener);
                    swipeRight = true;
                    return true;
                } else if (v.getLeft() + translationX <= -0.4 * v.getWidth()) {
                    animator.animateToLeft(v).setListener(swipeListener);
                    swipeRight = false;
                    return true;
                } else if (translationX != 0) {
                    animator.setupView(v, 0);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - startX;
                float dy = event.getY() - startY;
                v.setY(v.getY() + dy);
                v.setX(v.getX() + dx);
                animator.rotate(v);
                return true;
        }
        return v.onTouchEvent(event);
    }

    public int getVisibleItems() {
        return visibleItems;
    }

    public int getSize() {
        return size;
    }

    public boolean swipeLeft() {
        if (adapter.getCount() > 0) {
            swipeRight = false;
            animator.animateToLeft(getCurrentView()).setListener(swipeListener);
            return true;
        } else {
            return false;
        }
    }

    public boolean swipeRight() {
        if (adapter.getCount() > 0) {
            swipeRight = true;
            animator.animateToRight(getCurrentView()).setListener(swipeListener);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public void revertSwipe(Object data, boolean right) {
        getCurrentView().setOnTouchListener(null);
        adapter.revertPop(data);

        // Bind the first item to the last view
        View lastView = getLastView();
        lastView.setVisibility(View.VISIBLE);
        lastView.setOnTouchListener(this);
        adapter.getView(0, lastView, this);

        // Make sure the view is the first child
        views.add(0, views.remove(size - 1));

        removeView(lastView);
        addView(lastView, size - 1);

        for (int i = 1; i < size; i++) {
            View view = views.get(i);
            animator.setupView(view, i);
            if (i < adapter.getCount()) {
                adapter.getView(i, view, this);
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }

        animator.animateBack(lastView, right);
    }

    public void setAnimator(@NonNull StackViewAnimator animator) {
        this.animator = animator;
    }

    public void setAdapter(@NonNull StackAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(observer);
        removeAllViews();
        addViews();
    }

    @Nullable
    public StackAdapter getAdapter() {
        return adapter;
    }

    private void onAdapterDataChanged() {
        int newSize = adapter.getCount() > size ? size : adapter.getCount();
        if (visibleItems < newSize) {
            int itemsToAdd = newSize - visibleItems;
            for (int i = 0; i < itemsToAdd; i++) {
                int stackPosition = visibleItems;
                View view = adapter.getView(stackPosition, views.get(stackPosition), this);
                view.setVisibility(View.VISIBLE);
                visibleItems++;
                view.setTranslationX(0);
                view.setRotation(0);
                animator.setupView(view, stackPosition);
            }
        }
    }

    private void addViews() {
        views.clear();
        for (int i = 0; i < size; i++) {
            views.add(null);
        }
        visibleItems = 0;
        // Start adding the last views
        for (int i = size - 1; i >= 0; i--) {
            View view = LayoutInflater.from(getContext()).inflate(layoutId, this, false);
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            if (adapter != null && i < adapter.getCount()) {
                visibleItems++;
                view = adapter.getView(i, view, this);
            } else {
                // If there's no adapter set or the adapter has less items than the current index,
                // hide the view
                if (!isInEditMode()) {
                    view.setVisibility(View.INVISIBLE);
                }
            }
            views.set(i, view);
            addView(view);
            animator.setupView(view, i);
            requestLayout();
            if (i == 0) {
                view.setOnTouchListener(this);
            } else {
                view.setOnTouchListener(null);
            }
        }
    }

    private void updateViews() {
        View view = getCurrentView();
        view.animate().setListener(null);
        views.add(views.remove(0));
        view.setOnTouchListener(null);
        views.get(0).setOnTouchListener(this);

        // Reset view properties
        animator.reset(view);

        removeView(view);
        addView(view, 0);

        // If the adapter now doesn't have more data for this view, hide it
        if (adapter.getCount() < size) {
            visibleItems--;
            view.setVisibility(View.INVISIBLE);
        } else {
            // Bind the next position data to this view
            adapter.getView(size - 1, view, this);
            animator.animateAdd(view);
        }

        for (int i = 0; i < size - 1; i++) {
            View v = views.get(i);
            v.setTranslationX(0);
            v.setRotation(0);
            animator.setupView(v, i);
        }
    }

    private View getCurrentView() {
        return getChildAt(size - 1);
    }

    private View getLastView() {
        return getChildAt(0);
    }

    private DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            onAdapterDataChanged();
        }

        @Override
        public void onInvalidated() {
            removeAllViews();
            addViews();
        }
    };

    private AnimatorListenerAdapter swipeListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            Object data = adapter.pop();
            updateViews();
            //noinspection unchecked
            adapter.notifySwipe(data, swipeRight);
        }
    };
}

