package com.github.rubensousa.stackview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;


import com.github.rubensousa.stackview.StackAdapter;
import com.github.rubensousa.stackview.StackView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener,
        StackAdapter.OnSwipeListener<String> {

    private StackView stackView;
    private StringAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stackView = findViewById(R.id.stackView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.animators);
        toolbar.setOnMenuItemClickListener(this);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stackView.swipeRight();
            }
        });

        findViewById(R.id.fabClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stackView.swipeLeft();
            }
        });

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.push(getData());
            }
        });

        adapter = new StringAdapter(this);

        if (savedInstanceState != null) {
            adapter.restoreState(savedInstanceState);
        } else {
            adapter.push(getData());
        }

        stackView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.setOnSwipeListener(null);
    }

    @Override
    public void onSwipeLeft(String item) {

    }

    @Override
    public void onSwipeRight(String item) {
        stackView.revertSwipe(item, true);
    }

    private ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        data.add("One");
        data.add("Two");
        data.add("Three");
        data.add("Four");
        data.add("Five");
        data.add("Six");
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
                stackView.setAnimator(new StackDefaultAnimator());
                return true;
            case R.id.slide:
                stackView.setAnimator(new StackSlideAnimator(StackSlideAnimator.MOVE_LEFT));
                return true;
            case R.id.flip:
                stackView.setAnimator(new StackFlipAnimator(StackFlipAnimator.MOVE_DOWN));
                return true;
            case R.id.flipSlide:
                stackView.setAnimator(new StackFlipSlideAnimator(StackFlipSlideAnimator.MOVE_UP));
                return true;
            case R.id.fade:
                stackView.setAnimator(new StackSlideFadeAnimator(StackSlideFadeAnimator.MOVE_DOWN));
                return true;
            case R.id.shrink:
                stackView.setAnimator(new StackSlideShrinkAnimator(StackSlideShrinkAnimator.MOVE_DOWN));
                return true;
        }
        return false;
    }*/

}
