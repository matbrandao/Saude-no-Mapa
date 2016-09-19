package com.mat_brandao.saudeapp.view.main;

import android.support.v4.app.Fragment;

import com.mat_brandao.saudeapp.view.base.BaseView;

import java.io.File;

public interface MainView extends BaseView.BaseProgressView {
    void showFragment(Fragment fragment);

    void closeDrawer();

    void setProfileNameText(String name);

    void setProfileEmailText(String email);

    void setProfileImage(String profilePhotoUrl);
}