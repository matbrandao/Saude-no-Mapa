package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.content.Context;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Autor;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.Post;
import com.mat_brandao.saudeapp.domain.model.PostContent;
import com.mat_brandao.saudeapp.domain.model.PostResponse;
import com.mat_brandao.saudeapp.domain.model.PostType;
import com.mat_brandao.saudeapp.domain.model.User;
import com.mat_brandao.saudeapp.domain.repository.UserRepositoryImpl;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.domain.util.MaskUtil;
import com.mat_brandao.saudeapp.domain.util.MetaModelConstants;
import com.mat_brandao.saudeapp.network.retrofit.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public class FavEstablishmentInteractorImpl implements FavEstablishmentInteractor {

    private final Context mContext;
    private final UserRepositoryImpl mUserRepository;
    private final User mUser;
    private HashMap<Long, Long> mLikedEstablishments;
    private Long mDislikedContentCode;

    public FavEstablishmentInteractorImpl(Context context) {
        mContext = context;
        mUserRepository = new UserRepositoryImpl();
        mUser = mUserRepository.getUser();
        mLikedEstablishments = new HashMap<>();
    }

    @Override
    public Observable<Response<List<PostResponse>>> requestGetUserPosts() {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .getPosts(Long.valueOf(mContext.getString(R.string.app_id)), mUser.getId(),
                        MetaModelConstants.COD_OBJECT_ESTABLISHMENT,  MetaModelConstants.COD_POST_ESTABLISHMENT_LIKE, null);
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

    @Override
    public Observable<Response<ResponseBody>> requestLikeEstablishment(Long postCode, Long codEstablishment) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .createContent(postCode, assemblePostContent(codEstablishment));
    }

    @Override
    public Observable<Response<ResponseBody>> requestLikeEstablishment(Long codEstablishment) {
        return RestClient.getHeader(mUser.getAppToken(), null)
                .createContent(mUser.getEstablishmentLikePost(), assemblePostContent(codEstablishment));
    }

    @Override
    public Observable<Response<ResponseBody>> requestDislikeEstablishment(Long codEstablishment) {
        Long contentCode = 0L;
        for (Map.Entry<Long, Long> longLongEntry : mLikedEstablishments.entrySet()) {
            if (longLongEntry.getValue().equals(codEstablishment)) {
                contentCode = longLongEntry.getKey();
            }
        }

        mDislikedContentCode = contentCode;

        return RestClient.getHeader(mUser.getAppToken(), null)
                .deleteContent(mUser.getEstablishmentLikePost(), contentCode);
    }

    @Override
    public String getFluxoClientelaText(String fluxoClientela) {
        if (fluxoClientela.toLowerCase().contains("espontânea") && fluxoClientela.toLowerCase().contains("referenciada")) {
            return "Atendimento espontâneo e referenciado";
        } else if (fluxoClientela.contains("espontânea") && !fluxoClientela.contains("referenciada")) {
            return "Apenas atendimento espontâneo";
        } else if (!fluxoClientela.contains("espontânea") && fluxoClientela.contains("referenciada")) {
            return "Apenas atendimento referenciado";
        }
        return "Indeterminado";
    }

    @Override
    public String getAddressText(String logradouro, String numero, String bairro, String cidade, String uf, String cep) {
        String address = logradouro + ", Número: " + numero + ". " + GenericUtil.capitalize(bairro) + ", " +
                GenericUtil.capitalize(cidade) + ", " + uf + " - " + MaskUtil.mask("#####-###", cep);
        if (address.contains("null")) {
            return null;
        } else {
            return address;
        }
    }

    @Override
    public String getServicesText(Establishment establishment) {
        String result = "";
        if (establishment.getTemAtendimentoUrgencia().equals("Sim")) {
            result += "Atendimento Urgencial - ";
        }
        if (establishment.getTemAtendimentoAmbulatorial().equals("Sim")) {
            result += "Atendimento Ambulatorial - ";
        }
        if (establishment.getTemCentroCirurgico().equals("Sim")) {
            result += "Centro Cirúrgico - ";
        }
        if (establishment.getTemObstetra().equals("Sim")) {
            result += "Obstetra - ";
        }
        if (establishment.getTemNeoNatal().equals("Sim")) {
            result += "Neo Natal - ";
        }
        if (establishment.getTemDialise().equals("Sim")) {
            result += "Diálise - ";
        }

        return result.substring(0, result.length() - 3);
    }

    @Override
    public void addEstablishmentToLikedList(Long contentCode, Long establishmentCode) {
        mLikedEstablishments.put(contentCode, establishmentCode);
    }

    @Override
    public void removeDislikedContentCode() {
        mLikedEstablishments.remove(mDislikedContentCode);
    }

    @Override
    public void clearLikedEstablishments() {
        mLikedEstablishments.clear();
    }

    @Override
    public String getPostCode() {
        return String.valueOf(mUser.getEstablishmentLikePost());
    }

    @Override
    public int getLikedEstablishmentCount() {
        return mLikedEstablishments.size();
    }

    private Post assemblePost() {
        return new Post(new Autor(mUser.getId()), MetaModelConstants.COD_OBJECT_ESTABLISHMENT,
                new PostType(MetaModelConstants.COD_POST_ESTABLISHMENT_LIKE));
    }

    private PostContent assemblePostContent(Long codUnidade) {
        PostContent postContent = new PostContent();
        postContent.setJSON("{codEstabelecimento:" + codUnidade + "}");
        postContent.setTexto("");
        postContent.setLinks(null);
        postContent.setValor(0L);
        return postContent;
    }
}