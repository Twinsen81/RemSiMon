package com.evartem.remsimon.BaseMVP;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void viewIsReady();

    void detachView();

    void destroy();
}
