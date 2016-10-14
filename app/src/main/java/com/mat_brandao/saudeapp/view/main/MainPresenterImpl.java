package com.mat_brandao.saudeapp.view.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.view.about.AboutFragment;
import com.mat_brandao.saudeapp.view.edit_profile.EditProfileActivity;
import com.mat_brandao.saudeapp.view.establishment.EstablishmentFragment;
import com.mat_brandao.saudeapp.view.favorites.FavoritesFragment;
import com.mat_brandao.saudeapp.view.login.LoginActivity;
import com.mat_brandao.saudeapp.view.my_groups.MyGroupsFragment;
import com.mat_brandao.saudeapp.view.remedy.RemedyFragment;

public class MainPresenterImpl implements MainPresenter, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainPresenterImpl";

    private MainInteractorImpl mInteractor;
    private Context mContext;
    private MainView mView;

    private User mUser;

    private int mItemShowing;

    @Override
    public void onResume() {
        showUserData();
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

        mView.showFragment(EstablishmentFragment.newInstance());
        mView.setNavigationItemChecked(R.id.menu_item_establishments);
    }

    private void showUserData() {
        mView.setProfileNameText(mUser.getName());
        mView.setProfileEmailText(mUser.getEmail());
        mView.setProfileImage(mInteractor.getProfilePhotoUrl());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mView.setNavigationItemChecked(item.getItemId());
        mView.closeDrawer();
        if (item.getItemId() == R.id.menu_item_remedy) {
            mView.showFragment(RemedyFragment.newInstance());
        } else if (item.getItemId() == R.id.menu_item_establishments) {
            mView.showFragment(EstablishmentFragment.newInstance());
        } else if (item.getItemId() == R.id.menu_item_my_list) {
            mView.showFragment(FavoritesFragment.newInstance());
        } else if (item.getItemId() == R.id.menu_item_my_groups) {
            mView.showFragment(MyGroupsFragment.newInstance());
        } else if (item.getItemId() == R.id.menu_item_about) {
            mView.showFragment(AboutFragment.newInstance());
        } else if (item.getItemId() == R.id.menu_item_logout) {
            mInteractor.logout();
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            mView.goToActivity(intent);
        }
        return false;
    }

    @Override
    public void onAvatarImageClick() {
        mView.closeDrawer();
        mView.goToActivity(EditProfileActivity.class);
    }
}