package com.github.rubensousa.stackview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.rubensousa.stackview.StackAdapter;
import com.github.rubensousa.stackview.StackView;
import com.github.rubensousa.stackview.animator.StackAnimator;
import com.github.rubensousa.stackview.animator.StackDefaultAnimator;
import com.github.rubensousa.stackview.animator.StackSlideFadeAnimator;
import com.github.rubensousa.stackview.animator.StackFlipAnimator;
import com.github.rubensousa.stackview.animator.StackFlipSlideAnimator;
import com.github.rubensousa.stackview.animator.StackSlideShrinkAnimator;
import com.github.rubensousa.stackview.animator.StackSlideAnimator;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener,
        StackAdapter.StackListener<String>, StackView.StackEventListener {

    private StackView mStackView;
    private StringAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStackView = (StackView) findViewById(R.id.stackView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.animators);
        toolbar.setOnMenuItemClickListener(this);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStackView.pop();
            }
        });

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.push(getData());
            }
        });

        mAdapter = new StringAdapter();

        if (savedInstanceState != null) {
            mAdapter.restoreState(savedInstanceState);
        } else {
            mAdapter.push(getData());
        }

        mAdapter.setStackListener(this);
        mStackView.setAdapter(mAdapter);
        mStackView.setStackEventListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.saveState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.setStackListener(null);
    }

    @Override
    public void onPop(String data) {
        // Snackbar.make(mStackView, data, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onStackEmpty(int lastPosition) {

    }

    private ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            data.add("One");
            data.add("Two");
            data.add("Three");
        }

        return data;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.defaultAnim:
                mStackView.setAnimator(new StackDefaultAnimator(mStackView));
                return true;
            case R.id.slide:
                mStackView.setAnimator(new StackSlideAnimator(StackAnimator.MOVE_LEFT));
                return true;
            case R.id.flip:
                mStackView.setAnimator(new StackFlipAnimator(StackAnimator.MOVE_DOWN));
                return true;
            case R.id.flipSlide:
                mStackView.setAnimator(new StackFlipSlideAnimator(StackAnimator.MOVE_UP));
                return true;
            case R.id.fade:
                mStackView.setAnimator(new StackSlideFadeAnimator(StackAnimator.MOVE_DOWN));
                return true;
            case R.id.shrink:
                mStackView.setAnimator(new StackSlideShrinkAnimator(StackAnimator.MOVE_DOWN));
                return true;
        }
        return false;
    }

}
