package com.github.rubensousa.stackview;

import android.os.Bundle;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public abstract class StackAdapter<T> extends BaseAdapter {

    public static final String DATA_STATE = "state";

    private ArrayList<T> mData;
    private StackListener<T> mListener;

    public StackAdapter() {
        mData = new ArrayList<>();
    }

    public void setStackListener(StackListener<T> listener) {
        mListener = listener;
    }

    public abstract void saveState(Bundle outState);

    public abstract void restoreState(Bundle savedInstanceState);

    public ArrayList<T> getData() {
        return mData;
    }

    public void push(ArrayList<T> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void push(T data) {
        mData.add(data);
        notifyDataSetChanged();
    }

    public T pop() {
        T data = mData.remove(0);
        notifyDataSetChanged();
        if (mListener != null) {
            mListener.onPop(data);
        }
        return data;
    }

    public T getCurrentItem() {
        return mData.get(0);
    }

    @Override
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public interface StackListener<T> {
        void onPop(T item);
    }
}
