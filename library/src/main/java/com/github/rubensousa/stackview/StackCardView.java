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


public class StackCardView extends FrameLayout implements View.OnTouchListener {

    private static final int DEFAULT_SIZE = 4;

    private int layoutId;
    private int maxViews;
    private int currentViews;
    private float startX;
    private float startY;
    private List<View> views;
    private StackAdapter adapter;
    private StackViewAnimator animator;
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

    public StackCardView(@NonNull Context context) {
        this(context, null, 0);
    }

    public StackCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StackCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StackCardView, 0, 0);
        views = new ArrayList<>();
        maxViews = a.getInt(R.styleable.StackCardView_stackcardview_size, DEFAULT_SIZE);
        layoutId = a.getResourceId(R.styleable.StackCardView_stackcardview_adapterLayout, 0);
        currentViews = 0;
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
        View firstView = getChildAt(currentViews - 1);
        if (!firstView.equals(v)) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                v.clearAnimation();
                return true;
            case MotionEvent.ACTION_UP:
                float translationX = v.getTranslationX();
                if (v.getLeft() + translationX >= 0.5 * v.getWidth()) {
                    animator.animateToRight(v);
                    adapter.pop(true);
                } else if (v.getLeft() + translationX <= -0.5 * v.getWidth()) {
                    animator.animateToLeft(v);
                    adapter.pop(false);
                } else {
                    animator.setupView(v, 0);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - startX;
                float dy = event.getY() - startY;
                v.setY(v.getY() + dy);
                v.setX(v.getX() + dx);
                animator.rotate(v);
                return true;
        }

        return super.onTouchEvent(event);
    }

    public int getCurrentViews() {
        return currentViews;
    }

    public int getMaxViews() {
        return maxViews;
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
        int newSize = adapter.getCount() > maxViews ? maxViews : adapter.getCount();
        if (currentViews < newSize) {
            int itemsToAdd = newSize - currentViews;
            for (int i = 0; i < itemsToAdd; i++) {
                int stackPosition = currentViews;
                View view = adapter.getView(stackPosition, views.get(stackPosition), this);
                view.setVisibility(View.VISIBLE);
                currentViews++;
                animator.setupView(view, stackPosition);
            }
        }
    }

    private void addViews() {
        views.clear();
        for (int i = 0; i < maxViews; i++) {
            views.add(null);
        }
        currentViews = 0;
        // Start adding the last views
        for (int i = maxViews - 1; i >= 0; i--) {
            View view = LayoutInflater.from(getContext()).inflate(layoutId, this, false);
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            view.setOnTouchListener(this);
            if (adapter != null && i < adapter.getCount()) {
                currentViews++;
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
        }
    }
}

