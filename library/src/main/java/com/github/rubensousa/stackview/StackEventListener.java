package com.github.rubensousa.stackview;


public interface StackEventListener {

    void onPop(int position);

    void onStackEmpty(int lastPosition);

    void onExitFinished();

    void onEnterFinished();

}
