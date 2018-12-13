package com.evartem.remsimon.BaseMVP.view;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.evartem.remsimon.DI.base.BaseActivityModule;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Base class for all activities.
 * Injects the fragment manager, provides a convenient method for adding fragments
 */
public abstract class BaseViewActivity extends DaggerAppCompatActivity {

    @Inject
    @Named(BaseActivityModule.ACTIVITY_FRAGMENT_MANAGER)
    protected FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected final void addFragment(@IdRes int containerViewId, Fragment fragment) {
        boolean isFirstFragment = fragmentManager.getFragments().isEmpty();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .add(containerViewId, fragment);
        if (!isFirstFragment)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
