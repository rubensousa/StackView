/*
 * Copyright 2016 Rúben Sousa
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

    void revertPop(T data) {
        mData.add(0, data);
        notifyDataSetChanged();
    }

    T pop(boolean right) {
        T data = mData.remove(0);
        notifyDataSetChanged();
        if (mListener != null) {
            if (right) {
                mListener.onPopRight(data);
            } else {
                mListener.onPopLeft(data);
            }
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
        void onPopLeft(T item);

        void onPopRight(T item);
    }
}
