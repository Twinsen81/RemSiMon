package com.evartem.remsimon.taskEdit.http.DI;

import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.http.ContractMVP.HttpTaskEditPresenter;
import com.evartem.remsimon.taskEdit.http.HttpTaskEditPresenterImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class HttpTaskEditPresenterModule {
    @Binds
    @PerFragment
    abstract HttpTaskEditPresenter taskEditPresenter(HttpTaskEditPresenterImpl taskEditPresenter);
}
