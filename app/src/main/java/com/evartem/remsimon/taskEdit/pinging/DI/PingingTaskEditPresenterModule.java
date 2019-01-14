package com.evartem.remsimon.taskEdit.pinging.DI;

import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.pinging.ContractMVP.PingingTaskEditPresenter;
import com.evartem.remsimon.taskEdit.pinging.PingingTaskEditPresenterImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PingingTaskEditPresenterModule {
    @Binds
    @PerFragment
    abstract PingingTaskEditPresenter taskEditPresenter(PingingTaskEditPresenterImpl taskEditPresenter);
}
