package com.github.rubensousa.stackview.sample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rubensousa.stackview.StackAdapter;


public class StringAdapter extends StackAdapter<String> {

    public StringAdapter(OnSwipeListener<String> listener) {
        super(listener);
    }

    public void saveState(Bundle savedInstanceState) {
        savedInstanceState.putStringArrayList(DATA_STATE, getData());
    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            push(savedInstanceState.getStringArrayList(DATA_STATE));
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(getItem(i));
        return view;
    }
}
