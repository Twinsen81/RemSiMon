package com.evartem.remsimon.taskEdit.DI;

import android.support.v4.app.Fragment;

import com.evartem.remsimon.DI.base.BaseFragmentModule;
import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.ContractMVP.TaskEditView;
import com.evartem.remsimon.taskEdit.TaskEditFragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module(includes = {BaseFragmentModule.class, TaskEditPresenterModule.class})
public abstract class TaskEditFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(TaskEditFragment fragment);

    @Binds
    @PerFragment
    abstract TaskEditView tasksView(TaskEditFragment taskEditFragment);

}
