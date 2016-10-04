package com.mat_brandao.saudeapp.view.edit_profile;

import com.mat_brandao.saudeapp.view.base.BasePresenter;

public interface EditProfilePresenter extends BasePresenter {
    void onSaveFabClicked();

    void onAvatarClick();

    void onDateSet(int yy, int mm, int dd);

    void onBirthDateTouchListener();
}