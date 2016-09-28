package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.content.Context;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.MetaModelConstants;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.List;

import io.realm.Realm;
import retrofit2.Response;
import rx.Observable;

public class FavEstablishmentInteractorImpl implements FavEstablishmentInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private final User mUser;

    public FavEstablishmentInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
    }

    @Override
    public Observable<Response<List<PostResponse>>> requestGetUserPosts() {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getPosts(Long.valueOf(mContext.getString(R.string.app_id)), mUser.getId(),
                        MetaModelConstants.COD_OBJECT_ESTABLISHMENT);
    }

    @Override
    public Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem) {
        return RestClient
                .getHeader(mUser.getAppToken(), null)
                .getPostContent(mUser.getEstablishmentLikePost(), codConteudoPostagem);
    }

    @Override
    public void saveUserLikePostCode(Long likePostCode) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser.setEstablishmentLikePost(likePostCode);
        });
    }

    @Override
    public Observable<Response<List<Establishment>>> requestGetEstablishment(Long establishmentCode) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getEstablishmentByCod(establishmentCode);
    }
}