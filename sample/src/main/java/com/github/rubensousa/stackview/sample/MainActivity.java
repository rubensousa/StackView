package com.github.rubensousa.stackview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.rubensousa.stackview.StackView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements StackView.StackEventListener {

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

        mStackView.setStackEventListener(this);
        mStackView.setAdapter(mAdapter);
        //mStackView.setAnimator(new StackFlipAnimator());
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

    private ArrayList<String> getData() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            data.add("One");
            data.add("Two");
            data.add("Three");
        }

        return data;
    }

}
