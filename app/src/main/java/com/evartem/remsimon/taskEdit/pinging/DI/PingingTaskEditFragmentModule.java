package com.evartem.remsimon.taskEdit.pinging.DI;

import android.support.v4.app.Fragment;

import com.evartem.remsimon.DI.base.BaseFragmentModule;
import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.pinging.ContractMVP.PingingTaskEditView;
import com.evartem.remsimon.taskEdit.pinging.PingingTaskEditFragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module(includes = {BaseFragmentModule.class, PingingTaskEditPresenterModule.class})
public abstract class PingingTaskEditFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(PingingTaskEditFragment fragment);

    @Binds
    @PerFragment
    abstract PingingTaskEditView tasksView(PingingTaskEditFragment taskEditFragment);
}
