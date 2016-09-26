package com.mat_brandao.saudeapp.view.register;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;
import com.mat_brandao.saudeapp.view.login.LoginActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public class RegisterInteractorImpl implements RegisterInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;

    public RegisterInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateNormalUser(String name, String email, String sex,
                                                                      String cep, long birthDate, String password) {
        return RestClient.get()
                .createUser(new User(name, email, name.toLowerCase().replace(" ", ""), password, null, null, cep, sex));
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateFacebookUser(String name, String email, String sex,
                                                                        String cep, long birthDate, String password) {
        return RestClient.get()
                .createUser(new User(name, email, name.toLowerCase().replace(" ", ""), null, password, null, cep, sex));
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateGoogleUser(String name, String email, String sex,
                                                                      String cep, long birthDate, String password) {
        return RestClient.get()
                .createUser(new User(name, email, name.toLowerCase().replace(" ", ""), null, null, password, cep, sex));
    }

    @Override
    public Observable<Response<ResponseBody>> requestSaveProfilePhoto(Integer avatarDrawable) {
        User user = getUser();

        File file = getFile(avatarDrawable);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));

        return RestClient.getHeader(user.getAppToken(), null)
                .saveProfilePhoto(user.getId(), filePart);
    }

    @Override
    public Observable<Response<User>> requestLoginWithAccount(String email, String password) {
        return RestClient.get()
                .login(email, password);
    }

    @Override
    public void saveUserToRealm(User user) {
        mUserRepository.clearUsers();
        mUserRepository.saveUser(user);
    }

    @Override
    public Observable<Response<User>> requestLoginWithFacebook(String email, String token) {
        return RestClient.get()
                .loginWithFacebook(email, token);
    }

    @Override
    public Observable<Response<User>> requestLoginWithGoogle(String email, String token) {
        return RestClient.get()
                .loginWithGoogle(email, token);
    }

    @Override
    public User getUser() {
        return mUserRepository.getUser();
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
}