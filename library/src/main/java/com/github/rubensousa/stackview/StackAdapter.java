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
import android.support.annotation.NonNull;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public abstract class StackAdapter<T> extends BaseAdapter {

    public static final String DATA_STATE = "state";

    private ArrayList<T> list;
    private OnSwipeListener<T> listener;

    public StackAdapter(@NonNull OnSwipeListener<T> listener) {
        this.list = new ArrayList<>();
        this.listener = listener;
    }

    public void setOnSwipeListener(OnSwipeListener<T> listener) {
        this.listener = listener;
    }

    public abstract void saveState(Bundle outState);

    public abstract void restoreState(Bundle savedInstanceState);

    public ArrayList<T> getData() {
        return list;
    }

    public void push(List<T> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void push(T data) {
        list.add(data);
        notifyDataSetChanged();
    }

    void revertPop(T data) {
        list.add(0, data);
    }

    T pop() {
        T data = list.remove(0);
        return data;
    }

    void notifySwipe(T data, boolean right) {
        if (right) {
            listener.onSwipeRight(data);
        } else {
            listener.onSwipeLeft(data);
        }
    }

    public T getCurrentItem() {
        return list.get(0);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public interface OnSwipeListener<T> {
        void onSwipeLeft(T data);

        void onSwipeRight(T data);
    }
}
