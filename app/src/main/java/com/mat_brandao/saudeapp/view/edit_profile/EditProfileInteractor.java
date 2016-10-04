package com.mat_brandao.saudeapp.view.edit_profile;

import com.mat_brandao.saudeapp.domain.model.User;

public interface EditProfileInteractor {
    User getUser();

    String getProfilePhotoUrl();

    String parseDate(String birthDate);
}