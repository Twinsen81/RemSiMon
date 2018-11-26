package com.evartem.remsimon.taskEdit.DI;

import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.ContractMVP.TaskEditPresenter;
import com.evartem.remsimon.taskEdit.TaskEditPresenterImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TaskEditPresenterModule {
    @Binds
    @PerFragment
    abstract TaskEditPresenter taskEditPresenter(TaskEditPresenterImpl taskEditPresenter);
}
