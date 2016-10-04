package com.mat_brandao.saudeapp.view.edit_profile;

import android.content.Context;
import android.text.TextUtils;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;

public class EditProfileInteractorImpl implements EditProfileInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private final User mUser;

    public EditProfileInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
    }

    @Override
    public User getUser() {
        return mUser;
    }

    @Override
    public String getProfilePhotoUrl() {
        return "http://mobile-aceite.tcu.gov.br/appCivicoRS/rest/pessoas/" + mUser.getId() + "/fotoPerfil.png";
    }

    @Override
    public String parseDate(String birthDate) {
        if (TextUtils.isEmpty(birthDate)) return "";
        String[] splitDate = birthDate.split("/")[0].split("-");
        return splitDate[2] + splitDate[1] + splitDate[0];
    }
}