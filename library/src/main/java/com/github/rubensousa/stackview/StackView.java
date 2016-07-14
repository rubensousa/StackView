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


public class StackView extends FrameLayout implements StackAnimationListener {

    private ArrayList<View> mViews;
    private StackAdapter<?> mAdapter;
    private StackEventListener mEventListener;
    private StackAnimator mAnimator;
    private boolean mCyclic;
    private float mSpacing;
    private int mSize;
    private int mCount;
    private int mCurrentStackPosition;
    private int mPreviousStackPosition;
    private DataSetObserver mObserver;
    private boolean mPopping;

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

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StackView, 0, 0);
        mSize = a.getInteger(R.styleable.StackView_stackview_size, 4);
        mSpacing = a.getDimension(R.styleable.StackView_stackview_spacing, 10f);
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
        mPreviousStackPosition = mCurrentStackPosition;
        View currentView = mViews.get(mCurrentStackPosition);
        Object currentObj = mAdapter.pop();

        mAnimator.animatePop(currentObj, currentView);

        if (!mAdapter.isEmpty()) {
            View nextView = mCurrentStackPosition + 1 > mSize - 1 ?
                    mViews.get(0) : mViews.get(mCurrentStackPosition + 1);

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
            if (mAdapter != null) {
                view = mAdapter.getView(i, view, this);
            }
            mViews.set(i, view);
            addView(view);
            if (1 - i * 0.05f < 0f) {
                ViewCompat.setScaleX(view, 0.05f);
            } else {
                ViewCompat.setScaleX(view, (1 - i * 0.05f));
            }
            ViewCompat.setTranslationZ(view, (mSize - 1 - i) * 10);
            ViewCompat.setTranslationY(view, i * mSpacing);
        }
    }

    private void updateViews() {
        mCurrentStackPosition++;

        if (mCurrentStackPosition == mSize) {
            mCurrentStackPosition = 0;
        }

        // Animate translation and scaling
        for (int i = 0; i < mViews.size(); i++) {
            View view = mViews.get(i);

            if (i != mPreviousStackPosition) {
                int stackPosition = i - mCurrentStackPosition;
                if (stackPosition < 0) {
                    stackPosition = mSize + stackPosition;
                }
                ViewCompat.animate(view)
                        .scaleX(1 - stackPosition * 0.05f)
                        .translationZ((mSize - 1 - stackPosition) * 10)
                        .setDuration(StackAnimator.ANIMATION_DURATION)
                        .translationY(stackPosition * mSpacing);
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
        ViewCompat.setTranslationY(view, (mSize - 2) * mSpacing);
        ViewCompat.setTranslationX(view, 0f);

        mPopping = false;

        if (mAdapter.getCount() < mSize) {
            view.setVisibility(View.INVISIBLE);
            return;
        }

        // Get a new view for the next position
        view = mAdapter.getView(mSize - 1, view, this);

        // Animate reveal on bottom
        ViewCompat.animate(view)
                .translationY((mSize - 1) * mSpacing)
                .setDuration(StackAnimator.ANIMATION_DURATION / 2)
                .setInterpolator(new AccelerateInterpolator());


    }

    @Override
    public void onEnterFinished(View view) {
        // Reset view properties
        ViewCompat.setRotation(view, 0f);
        ViewCompat.setRotationY(view, 0f);
        ViewCompat.setRotationX(view, 0f);
        ViewCompat.setAlpha(view, 1f);
        ViewCompat.setScaleY(view, 1f);
        ViewCompat.setScaleX(view, 1f);
        ViewCompat.setTranslationZ(view, (mSize - 1) * 10f);
        ViewCompat.setTranslationY(view, 0f);
        ViewCompat.setTranslationX(view, 0f);
    }

    public interface StackEventListener {
        void onPop(int position);

        void onStackEmpty(int lastPosition);
    }
}
