package com.evartem.remsimon.taskEdit.pinging.DI;

import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.pinging.ContractMVP.TaskEditPresenter;
import com.evartem.remsimon.taskEdit.pinging.TaskEditPresenterImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TaskEditPresenterModule {
    @Binds
    @PerFragment
    abstract TaskEditPresenter taskEditPresenter(TaskEditPresenterImpl taskEditPresenter);
}
