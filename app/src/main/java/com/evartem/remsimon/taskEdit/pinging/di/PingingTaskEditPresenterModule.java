package com.evartem.remsimon.taskEdit.pinging.di;

import com.evartem.remsimon.di.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.pinging.contractmvp.PingingTaskEditPresenter;
import com.evartem.remsimon.taskEdit.pinging.PingingTaskEditPresenterImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PingingTaskEditPresenterModule {
    @Binds
    @PerFragment
    abstract PingingTaskEditPresenter taskEditPresenter(PingingTaskEditPresenterImpl taskEditPresenter);
}
