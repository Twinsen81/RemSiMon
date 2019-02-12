package com.evartem.remsimon.basemvp.view;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.evartem.remsimon.basemvp.presenter.Presenter;

import javax.inject.Inject;

/**
 * Basic implementation of a view for a Fragment.
 * Injects the presenter.
 * Calls the presenter's methods in the corresponding lifecycle callbacks.
 * @param <T>
 */
public abstract class BaseViewFragment<T extends Presenter> extends BaseDIFragment
        implements MVPView {

    @Inject
    protected T presenter;

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // At this point all UI elements are ready -> notify the presenter that the view is ready
        presenter.onStart(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        presenter.onEnd();
        super.onDestroyView();
    }
}
