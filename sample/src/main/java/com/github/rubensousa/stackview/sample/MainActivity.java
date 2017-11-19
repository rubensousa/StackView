package com.github.rubensousa.stackview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.rubensousa.stackview.StackAdapter;
import com.github.rubensousa.stackview.StackCardView;
import com.github.rubensousa.stackview.StackView;
import com.github.rubensousa.stackview.animator.StackDefaultAnimator;
import com.github.rubensousa.stackview.animator.StackSlideFadeAnimator;
import com.github.rubensousa.stackview.animator.StackFlipAnimator;
import com.github.rubensousa.stackview.animator.StackFlipSlideAnimator;
import com.github.rubensousa.stackview.animator.StackSlideShrinkAnimator;
import com.github.rubensousa.stackview.animator.StackSlideAnimator;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener,
        StackAdapter.StackListener<String>, StackView.StackEventListener {

    private StackCardView mStackView;
    private StringAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStackView = findViewById(R.id.stackView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.animators);
        toolbar.setOnMenuItemClickListener(this);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.pop(true);
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
    public void onStackEmpty(int lastPosition) {

    }

    @Override
    public void onPopLeft(String item) {

    }

    @Override
    public void onPopRight(String item) {

    }

    private ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        data.add("One");
        data.add("Two");
        data.add("Three");
        data.add("One");
        data.add("Two");
        data.add("Three");
        data.add("One");
        data.add("Two");
        data.add("Three");
        data.add("One");
        data.add("Two");
        data.add("Three");
        return data;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    /*@Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.defaultAnim:
                mStackView.setAnimator(new StackDefaultAnimator());
                return true;
            case R.id.slide:
                mStackView.setAnimator(new StackSlideAnimator(StackSlideAnimator.MOVE_LEFT));
                return true;
            case R.id.flip:
                mStackView.setAnimator(new StackFlipAnimator(StackFlipAnimator.MOVE_DOWN));
                return true;
            case R.id.flipSlide:
                mStackView.setAnimator(new StackFlipSlideAnimator(StackFlipSlideAnimator.MOVE_UP));
                return true;
            case R.id.fade:
                mStackView.setAnimator(new StackSlideFadeAnimator(StackSlideFadeAnimator.MOVE_DOWN));
                return true;
            case R.id.shrink:
                mStackView.setAnimator(new StackSlideShrinkAnimator(StackSlideShrinkAnimator.MOVE_DOWN));
                return true;
        }
        return false;
    }*/

}
