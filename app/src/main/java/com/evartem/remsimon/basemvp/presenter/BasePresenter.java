package com.evartem.remsimon.basemvp.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.evartem.remsimon.basemvp.view.MVPView;

/**
 * Provides empty implementations of the Presenter interface,
 * performs view binding.
 * @param <T> The corresponding view
 */
public abstract class BasePresenter<T extends MVPView> implements Presenter {

    protected final T view;

    protected BasePresenter(T view) {
        this.view = view;
    }

    @Override
    public void onStart(@Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onEnd() {
    }
}
