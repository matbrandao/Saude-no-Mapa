package com.mat_brandao.saudeapp.view.remedy;

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

public class RemedyInteractorImpl implements RemedyInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private HashMap<Long, Long> mLikedRemedies;
    private User mUser;

    public RemedyInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
        mLikedRemedies = new HashMap<>();
    }

    @Override
    public Observable<Response<List<Remedy>>> requestRemediesByName(String name) {
        return RestClient
                .getHeader(mUser.getAppToken(), null)
                .getRemedies(null, name);
    }

    @Override
    public Observable<Response<List<Remedy>>> requestRemediesByBarCode(String barcode) {
        return RestClient
                .getHeader(mUser.getAppToken(), null)
                .getRemedies(barcode, null);
    }

    @Override
    public Observable<Response<ResponseBody>> requestCreateLikePost() {
        return RestClient.getHeader(mUser.getAppToken(), mContext.getString(R.string.app_id))
                .createPost(assemblePost());
    }

    @Override
    public Observable<Response<List<PostResponse>>> requestGetUserPosts() {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getPosts(Long.valueOf(mContext.getString(R.string.app_id)), mUser.getId(),
                        MetaModelConstants.COD_OBJECT_REMEDY, null, null);
    }

    @Override
    public Observable<Response<PostContent>> requestGetPostContent(Long codConteudoPostagem) {
        return RestClient
                .getHeader(mUser.getAppToken(), null)
                .getPostContent(mUser.getRemedyLikePostCode(), codConteudoPostagem);
    }

    @Override
    public Observable<Response<ResponseBody>> requestLikeRemedy(Long postCode, Long codRemedy) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .createContent(postCode, assemblePostContent(codRemedy));
    }

    @Override
    public Observable<Response<ResponseBody>> requestLikeRemedy(Long codRemedy) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .createContent(mUser.getRemedyLikePostCode(), assemblePostContent(codRemedy));
    }

    @Override
    public Observable<Response<ResponseBody>> requestDisLikeRemedy(Long codRemedy) {
        Long contentCode = 0L;
        for (Map.Entry<Long, Long> longLongEntry : mLikedRemedies.entrySet()) {
            if (longLongEntry.getValue().equals(codRemedy)) {
                contentCode = longLongEntry.getKey();
            }
        }

        return RestClient.getHeader(mUser.getAppToken(), null)
                .deleteContent(mUser.getRemedyLikePostCode(), contentCode);
    }

    @Override
    public boolean isRemedyLiked(Long codRemedy) {
        if (mLikedRemedies.isEmpty()) {
            return false;
        } else {
            for (Long code : mLikedRemedies.values()) {
                if (code.equals(codRemedy)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void saveUserLikePostCode(Long likePostCode) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            mUser.setRemedyLikePostCode(likePostCode);
        });
    }

    @Override
    public void addRemedyToLikedList(Long contentCode, Long remedyCode) {
        mLikedRemedies.put(contentCode, remedyCode);
    }

    @Override
    public boolean hasLikePostCode() {
        return mUser.getRemedyLikePostCode() != null;
    }

    @Override
    public String getPostCode() {
        return String.valueOf(mUser.getRemedyLikePostCode());
    }

    @Override
    public void removeRemedyFromLikedList(Long codBarraEan) {
        Long key = 12312312312L;
        for (Long code : mLikedRemedies.keySet()) {
            if (mLikedRemedies.get(code).equals(Long.valueOf(codBarraEan))) {
                key = code;
            }
        }
        mLikedRemedies.remove(key);
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