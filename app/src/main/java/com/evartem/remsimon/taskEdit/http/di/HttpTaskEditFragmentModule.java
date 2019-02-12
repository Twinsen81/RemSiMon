package com.evartem.remsimon.taskEdit.http.di;

import android.support.v4.app.Fragment;

import com.evartem.remsimon.di.base.BaseFragmentModule;
import com.evartem.remsimon.di.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.http.contractmvp.HttpTaskEditView;
import com.evartem.remsimon.taskEdit.http.HttpTaskEditFragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module(includes = {BaseFragmentModule.class, HttpTaskEditPresenterModule.class})
public abstract class HttpTaskEditFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(HttpTaskEditFragment fragment);

    @Binds
    @PerFragment
    abstract HttpTaskEditView tasksView(HttpTaskEditFragment taskEditFragment);
}
