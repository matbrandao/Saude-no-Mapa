package com.mat_brandao.saudeapp.domain.repository;

import com.mat_brandao.saudeapp.domain.model.User;

import io.realm.Realm;

/**
 * Created by Mateus Brandão on 4/11/2016.
 */
public class UserRepositoryImpl implements UserRepository {
    private User mUser;

    public User getUser() {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser = realm.where(User.class).findFirst();
        });
        return mUser;
    }

    @Override
    public void saveUser(User user) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            realm.copyToRealm(user);
        });
    }

    @Override
    public void clearUsers() {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            realm.delete(User.class);
        });
    }
}
