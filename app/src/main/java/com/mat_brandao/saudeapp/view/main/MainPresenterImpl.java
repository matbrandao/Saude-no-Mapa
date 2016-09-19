package com.mat_brandao.saudeapp.view.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.view.establishment.EstablishmentFragment;

import java.io.BufferedInputStream;
import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MainPresenterImpl implements MainPresenter, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainPresenterImpl";

    private MainInteractorImpl mInteractor;
    private Context mContext;
    private MainView mView;

    private User mUser;

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

    }

    public MainPresenterImpl(MainView view, Context context) {
        mInteractor = new MainInteractorImpl(context);
        mContext = context;
        mView = view;

        mUser = mInteractor.getUser();
        showUserData();

        mView.showFragment(EstablishmentFragment.newInstance());
    }

    private void showUserData() {
        mView.setProfileNameText(mUser.getName());
        mView.setProfileEmailText(mUser.getEmail());
        mView.setProfileImage(mInteractor.getProfilePhotoUrl());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}