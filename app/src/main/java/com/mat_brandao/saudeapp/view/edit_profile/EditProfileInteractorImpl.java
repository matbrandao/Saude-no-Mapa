package com.mat_brandao.saudeapp.view.edit_profile;

import android.content.Context;
import android.text.TextUtils;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.DateUtil;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

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
        String[] splitDate = birthDate.substring(0, 10).split("-");
        return splitDate[2] + splitDate[1] + splitDate[0];
    }

    @Override
    public Observable<Response<ResponseBody>> requestUpdateNormalUser(String name, String email, String sex, String bio, String cep, long birthDate) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .updateUser(mUser.getId(), new User(name, null, email, null, null, null, cep, sex, null, DateUtil.getDate(birthDate), bio));
    }

    @Override
    public Observable<Response<ResponseBody>> requestUpdateFacebookUser(String name, String email, String sex, String bio, String cep, long birthDate) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .updateUser(mUser.getId(), new User(name, null, email, null, null, null, cep, sex, null, DateUtil.getDate(birthDate), bio));
    }

    @Override
    public Observable<Response<ResponseBody>> requestUpdateGoogleUser(String name, String email, String sex, String bio, String cep, long birthDate) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .updateUser(mUser.getId(), new User(name, null, email, null, null, null, cep, sex, null, DateUtil.getDate(birthDate), bio));
    }

    @Override
    public void updateRealmUser(String mName, String mEmail, String mSelectedSex, String date, String mCep, String mBio) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser.setName(mName);
            mUser.setEmail(mEmail);
            mUser.setSex(mSelectedSex);
            mUser.setBirthDate(date);
            mUser.setCep(mCep);
            mUser.setBio(mBio);
        });
    }
}