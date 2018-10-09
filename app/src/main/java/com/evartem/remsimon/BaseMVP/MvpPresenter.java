package com.evartem.remsimon.BaseMVP;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void viewIsReady();

    void viewIsNotReady();

    void detachView();

    void destroy();
}
