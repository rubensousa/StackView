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
    private int mItemsShowing;
    private int mCount;
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
            if (mAdapter != null && i < mAdapter.getCount()) {
                mItemsShowing++;
                view = mAdapter.getView(i, view, this);
            } else {
                // If there's no adapter set or the adapter has less items and the current index,
                // then hide the view
                view.setVisibility(View.INVISIBLE);
            }
            mViews.set(i, view);
            addView(view);
            setupView(view, i);
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

        if (mAdapter.getCount() - 1 < mSize) {
            mItemsShowing--;
            mAdapter.pop();
            view.setVisibility(View.INVISIBLE);
            return;
        }

        mAdapter.pop();

        // Get a new view for the next position
        if (mAdapter.getCount() >= mSize) {
            view = mAdapter.getView(mSize - 1, view, this);
        }

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

    public void setupView(View view, int stackPosition) {
        ViewCompat.animate(view)
                .scaleX(1 - stackPosition * 0.05f < 0f ? 0.05f : 1 - stackPosition * 0.05f)
                .translationZ((mSize - 1 - stackPosition) * 10)
                .translationY(stackPosition * mSpacing)
                .setDuration(StackAnimator.ANIMATION_DURATION);
    }

    public interface StackEventListener {
        void onPop(int position);

        void onStackEmpty(int lastPosition);
    }
}
