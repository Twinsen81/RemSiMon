package com.evartem.remsimon.taskEdit.http.di;

import com.evartem.remsimon.di.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.http.contractmvp.HttpTaskEditPresenter;
import com.evartem.remsimon.taskEdit.http.HttpTaskEditPresenterImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class HttpTaskEditPresenterModule {
    @Binds
    @PerFragment
    abstract HttpTaskEditPresenter taskEditPresenter(HttpTaskEditPresenterImpl taskEditPresenter);
}
