package com.github.rubensousa.stackview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.rubensousa.stackview.StackEventListener;
import com.github.rubensousa.stackview.StackView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements StackEventListener {

    private StackView mStackView;
    private StringAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStackView = (StackView) findViewById(R.id.stackView);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStackView.pop();
            }
        });

        mAdapter = new StringAdapter();

        if (savedInstanceState != null) {
            mAdapter.restoreState(savedInstanceState);
        } else {
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                data.add("One");
                data.add("Two");
                data.add("Three");
            }
            mAdapter.push(data);
        }

        mStackView.setStackEventListener(this);
        mStackView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.saveState(outState);
    }

    @Override
    public void onPop(int position) {
        String string = mAdapter.getItem(position);
    }

    @Override
    public void onStackEmpty(int lastPosition) {

    }

    @Override
    public void onExitFinished() {

    }

    @Override
    public void onEnterFinished() {

    }
}
