package com.mat_brandao.saudeapp.view.register;

import com.mat_brandao.saudeapp.view.base.BasePresenter;

public interface RegisterPresenter extends BasePresenter {
    void onSaveFabClick();

    void onBirthDateTouchListener();

    void onDateSet(int yy, int mm, int dd);

    void onAvatarClick();
}