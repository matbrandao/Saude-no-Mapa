package com.mat_brandao.saudeapp.view.favorites.fav_remedy;

import android.content.Context;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Autor;
import com.mat_brandao.saudeapp.domain.model.Post;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.PostType;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.MetaModelConstants;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public class FavRemedyInteractorImpl implements FavRemedyInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private final User mUser;
    private HashMap<Long, Long> mLikedRemedies;
    private Long mDislikedContentCode;

    public FavRemedyInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
        mLikedRemedies = new HashMap<>();
    }

    @Override
    public Observable<Response<List<PostResponse>>> requestGetUserPosts() {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getPosts(Long.valueOf(mContext.getString(R.string.app_id)), mUser.getId(),
                        MetaModelConstants.COD_OBJECT_REMEDY);
    }

    @Override
    public Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem) {
        return RestClient
                .getHeader(mUser.getAppToken(), null)
                .getPostContent(mUser.getRemedyLikePostCode(), codConteudoPostagem);
    }

    @Override
    public void saveUserLikePostCode(Long likePostCode) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser.setRemedyLikePostCode(likePostCode);
        });
    }

    @Override
    public void removeDislikedContentCode() {
        mLikedRemedies.remove(mDislikedContentCode);
    }

    @Override
    public void clearLikedRemedies() {
        mLikedRemedies.clear();
    }

    @Override
    public Observable<Response<List<Remedy>>> requestGetRemedy(Long remedyCode) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getRemedyByCod(remedyCode);
    }

    @Override
    public Observable<Response<ResponseBody>> requestLikeRemedy(Long postCode, Long codRemedy) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .likeEstablishment(postCode, assemblePostContent(codRemedy));
    }

    @Override
    public Observable<Response<ResponseBody>> requestLikeRemedy(Long codRemedy) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .likeEstablishment(mUser.getRemedyLikePostCode(), assemblePostContent(codRemedy));
    }

    @Override
    public Observable<Response<ResponseBody>> requestDisLikeRemedy(Long codRemedy) {
        Long contentCode = 0L;
        for (Map.Entry<Long, Long> longLongEntry : mLikedRemedies.entrySet()) {
            if (longLongEntry.getValue().equals(codRemedy)) {
                contentCode = longLongEntry.getKey();
            }
        }

        mDislikedContentCode = contentCode;

        return RestClient.getHeader(mUser.getAppToken(), null)
                .deleteContent(mUser.getRemedyLikePostCode(), contentCode);
    }

    @Override
    public void addRemedyToLikedList(Long contentCode, Long remedyCode) {
        mLikedRemedies.put(contentCode, remedyCode);
    }

    @Override
    public String getPostCode() {
        return String.valueOf(mUser.getRemedyLikePostCode());
    }

    @Override
    public int getLikedRemedyCount() {
        return mLikedRemedies.size();
    }

    private Post assemblePost() {
        return new Post(new Autor(mUser.getId()), MetaModelConstants.COD_OBJECT_REMEDY,
                new PostType(MetaModelConstants.COD_POST_REMEDY));
    }

    private PostContent assemblePostContent(Long corRemedy) {
        PostContent postContent = new PostContent();
        postContent.setJSON("{codRemedio:" + corRemedy + "}");
        postContent.setTexto("");
        postContent.setLinks(null);
        postContent.setValor(0L);
        return postContent;
    }
}