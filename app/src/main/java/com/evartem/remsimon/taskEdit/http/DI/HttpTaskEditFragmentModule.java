package com.evartem.remsimon.taskEdit.http.DI;

import android.support.v4.app.Fragment;

import com.evartem.remsimon.DI.base.BaseFragmentModule;
import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.http.ContractMVP.HttpTaskEditView;
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
