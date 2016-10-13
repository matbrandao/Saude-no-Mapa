package com.mat_brandao.saudeapp.view.chat;

import android.content.Context;
import android.text.TextUtils;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class ChatInteractorImpl implements ChatInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public ChatInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }

    @Override
    public boolean isMessageValid(String message) {
        return !TextUtils.isEmpty(message.trim());
    }

    @Override
    public User getUser() {
        return mUserRepository.getUser();
    }

    @Override
    public String getPhotoUrl(Long userId) {
        return "http://mobile-aceite.tcu.gov.br/appCivicoRS/rest/pessoas/" + userId + "/fotoPerfil.png";
    }
}