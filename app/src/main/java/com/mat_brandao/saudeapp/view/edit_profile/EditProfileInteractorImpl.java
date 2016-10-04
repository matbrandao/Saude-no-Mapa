package com.mat_brandao.saudeapp.view.edit_profile;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.DateUtil;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    public Observable<Response<ResponseBody>> requestSaveProfilePhoto(Integer avatarDrawable) {
        User user = getUser();

        File file = getFile(avatarDrawable);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));

        return RestClient.getHeader(user.getAppToken(), null)
                .saveProfilePhoto(user.getId(), filePart);
    }

    private File getFile(Integer avatarDrawable) {
        File f = null;
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES);
            f = new File(path, "/" + "avatarImage.png");
            InputStream inputStream = mContext.getResources().openRawResource(avatarDrawable);
            OutputStream out= new FileOutputStream(f);
            byte buf[] = new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0)
                out.write(buf,0,len);
            out.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return f;
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