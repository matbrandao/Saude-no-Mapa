package com.mat_brandao.saudeapp.view.edit_profile;

import android.content.Context;

public class EditProfilePresenterImpl implements EditProfilePresenter {

    private EditProfileInteractorImpl mInteractor;
    private Context mContext;
    private EditProfileView mView;

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onRetryClicked() {
        // TODO: 03-Oct-16
    }

    public EditProfilePresenterImpl(EditProfileView view, Context context) {
        mInteractor = new EditProfileInteractorImpl(context);
        mContext = context;
        mView = view;
    }
}