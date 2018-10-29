package com.evartem.remsimon.DI.base;

import android.app.Fragment;
import android.app.FragmentManager;

import com.evartem.remsimon.DI.scopes.PerFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;


@Module
public abstract class BaseFragmentModule {

    public static final String FRAGMENT = "BaseFragmentModule.fragment";

    public static final String CHILD_FRAGMENT_MANAGER = "BaseFragmentModule.childFragmentManager";

    @Provides
    @Named(CHILD_FRAGMENT_MANAGER)
    @PerFragment
    static FragmentManager childFragmentManager(@Named(FRAGMENT) Fragment fragment) {
        return fragment.getChildFragmentManager();
    }

}

