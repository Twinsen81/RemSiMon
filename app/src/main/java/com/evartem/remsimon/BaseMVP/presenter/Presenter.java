package com.evartem.remsimon.BaseMVP.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Defines common interface for all presenters in the app.
 * The methods are called by the corresponding view according to
 * the view's lifecycle
 */
public interface Presenter {

    /**
     * The view is ready for communication with the presenter and the user
     * @param savedInstanceState
     */
    void onStart(@Nullable Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onSaveInstanceState(Bundle outState);

    /**
     * The view is about to be destroyed - perform cleanup if needed
     */
    void onEnd();
}
