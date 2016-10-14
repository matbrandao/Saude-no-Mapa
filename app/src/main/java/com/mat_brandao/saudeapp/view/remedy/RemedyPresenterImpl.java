package com.mat_brandao.saudeapp.view.remedy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Conteudo;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.mat_brandao.saudeapp.view.main.MainActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class RemedyPresenterImpl implements RemedyPresenter, GenericObjectClickListener<Remedy> {
    private static final String TAG = "RemedyPresenterImpl";
    private static final long DEBOUNCE_TIMER = 300;
    private static final long DISMISS_TIMER = 1000;

    private RemedyInteractorImpl mInteractor;
    private Context mContext;
    private RemedyView mView;

    private CompositeSubscription mSubscription = new CompositeSubscription();
    private Handler mHandler;
    private Runnable mDismissKeyboardRunnable;

    private ProgressBar mRemedyProgress;
    private boolean isLiked;

    @Override
    public void onResume() {
        ((MainActivity) mContext).setNavigationItemChecked(R.id.menu_item_remedy);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mSubscription.unsubscribe();
    }

    @Override
    public void onRetryClicked() {
        // TODO: 27/09/2016
    }

    public RemedyPresenterImpl(RemedyView view, Context context) {
        mInteractor = new RemedyInteractorImpl(context);
        mContext = context;
        mView = view;

        mHandler = new Handler();
        mDismissKeyboardRunnable = () -> {
            mView.dismissKeyboard();
        };
        setupSearchObservable();

        requestLikedRemedies();
    }

    private void requestLikedRemedies() {
        mSubscription.add(mInteractor.requestGetUserPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .subscribe(listResponse -> {
                    if (listResponse != null && listResponse.isSuccessful()) {
                        if (listResponse.body() != null && listResponse.body().size() > 0) {
                            mInteractor.saveUserLikePostCode(listResponse.body().get(0).getCodPostagem());
                            for (Conteudo conteudo : listResponse.body().get(0).getConteudos()) {
                                mInteractor.requestGetPostContent(conteudo.getCodConteudoPostagem())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .onErrorReturn(throwable -> null)
                                        .subscribe(postContentResponse -> {
                                            if (postContentResponse != null && postContentResponse.isSuccessful()) {
                                                mInteractor.addRemedyToLikedList(postContentResponse.body().getCodConteudoPost(),
                                                        GenericUtil.getNumbersFromString(postContentResponse.body().getJSON()));
                                            }
                                        });
                            }
                        } else {
                            mSubscription.add(mInteractor.requestCreateLikePost()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .onErrorReturn(throwable -> null)
                                    .subscribe(createPostResponse -> {
                                        mView.dismissProgressDialog();
                                        if (createPostResponse != null && createPostResponse.isSuccessful()) {
                                            mInteractor.saveUserLikePostCode(GenericUtil.
                                                    getNumbersFromString(createPostResponse.headers().get("location")));
                                        }
                                    }));
                        }
                    }
                }));
    }

    private void setupSearchObservable() {
        mView.registerSearchObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    mHandler.removeCallbacks(mDismissKeyboardRunnable);
                });

        mView.registerSearchObservable()
                .debounce(DEBOUNCE_TIMER, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if (charSequence.length() > 2) {
                        mHandler.postDelayed(mDismissKeyboardRunnable, DISMISS_TIMER);
                        mView.setProgressLayoutVisibility(View.VISIBLE);
                        mInteractor.requestRemediesByName(charSequence.toString().toLowerCase())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(getRemediesObserver);
                    } else {
                        mView.setProgressLayoutVisibility(View.GONE);
                        mView.setNoResultsTextVisibility(View.VISIBLE);
                        mView.setEmptyTextVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onScanBarcodeFabClick() {
        requestCameraPermissions();
    }

    @Override
    public void onScanSuccess(String data) {
        mView.setProgressLayoutVisibility(View.VISIBLE);
        mView.setEmptyTextVisibility(View.GONE);
        mView.setNoResultsTextVisibility(View.GONE);
        mSubscription.add(mInteractor.requestRemediesByBarCode(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getRemediesObserver));
    }

    private void requestCameraPermissions() {
        RxPermissions.getInstance(mContext)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        Intent intent = new Intent(mContext, ScanActivity.class);
                        intent.setAction("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SAVE_HISTORY", false);
                        mView.gotoActivityWithResult(intent);
                    } else {
                        mView.showToast(mContext.getString(R.string.needed_camera_permission));
                    }
                });
    }

    /**
     * Remedy recycler item click listener;
     *
     * @param remedy
     */
    @Override
    public void onItemClick(Remedy remedy) {
        showRemedyBottomDialog(remedy);
    }

    private void showRemedyBottomDialog(Remedy remedy) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_sheet_remedy, null);
        BottomViews bottomViews = new BottomViews();
        ButterKnife.bind(bottomViews, dialogView);

        mRemedyProgress = bottomViews.remedyProgress;

        bottomViews.establishmentTitle.setText(GenericUtil.capitalize(remedy.getProduto().toLowerCase()));
        bottomViews.apresentacaoText.setText(GenericUtil.capitalize(remedy.getApresentacao().toLowerCase()));
        bottomViews.classeTerapeuticaText.setText(GenericUtil.capitalize(remedy.getClasseTerapeutica().toLowerCase()));
        bottomViews.laboratorioText.setText(GenericUtil.capitalize(remedy.getLaboratorio().toLowerCase()));
        bottomViews.principioAtivoText.setText(GenericUtil.capitalize(remedy.getPrincipioAtivo().toLowerCase()));
        bottomViews.registroText.setText(remedy.getRegistro());
        bottomViews.cnpjText.setText(remedy.getCnpj());

        if (remedy.getRestricao().equals("Sim")) {
            bottomViews.possuiRestricaoText.setVisibility(View.VISIBLE);
        }

        if (mInteractor.isRemedyLiked(Long.valueOf(remedy.getCodBarraEan()))) {
            bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
        }

        bottomViews.likeImage.setOnClickListener(v -> {
            mRemedyProgress.setVisibility(View.VISIBLE);
            if (mInteractor.isRemedyLiked(Long.valueOf(remedy.getCodBarraEan()))) {
                Timber.i("Already liked");
                mSubscription.add(mInteractor.requestDisLikeRemedy(Long.valueOf(remedy.getCodBarraEan()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn(throwable -> null)
                        .subscribe(responseBodyResponse -> {
                            mRemedyProgress.setVisibility(View.GONE);
                            mInteractor.removeRemedyFromLikedList(Long.valueOf(remedy.getCodBarraEan()));
                            if (responseBodyResponse != null && responseBodyResponse.isSuccessful()) {
                                isLiked = false;
                                bottomViews.likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_empty));
                            } else {
                                mView.showToast(mContext.getString(R.string.http_error_generic));
                            }
                        }));
            } else {
                Timber.i("Not Liked yet");
                requestLikeRemedies(Long.valueOf(remedy.getCodBarraEan()), bottomViews.likeImage);
            }
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(R.color.default_dialog_background);

        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight(mView.getRootHeight() - 200);

        bottomSheetDialog.show();
    }

    private void requestLikeRemedies(Long codRemedy, ImageView likeImage) {
        if (mInteractor.hasLikePostCode()) {
            mSubscription.add(mInteractor.requestLikeRemedy(codRemedy)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(throwable -> null)
                    .subscribe(likeResponse -> {
                        mRemedyProgress.setVisibility(View.GONE);
                        if (likeResponse != null && likeResponse.isSuccessful()) {
                            mInteractor.addRemedyToLikedList(GenericUtil.getContentIdFromUrl(String.valueOf(mInteractor.getPostCode()),
                                    likeResponse.headers().get("location")), codRemedy);
                            likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
                        } else {
                            mView.showToast(mContext.getString(R.string.http_error_generic));
                        }
                    }));
        } else {
            mSubscription.add(mInteractor.requestCreateLikePost()
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(throwable -> null)
                    .subscribe(createPostResponse -> {
                        if (createPostResponse != null && createPostResponse.isSuccessful()) {
                            Long postCode = GenericUtil.getNumbersFromString(createPostResponse.headers().get("location"));
                            mInteractor.saveUserLikePostCode(postCode);
                            mSubscription.add(mInteractor.requestLikeRemedy(postCode, codRemedy)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .onErrorReturn(throwable -> null)
                                    .subscribe(likeResponse -> {
                                        mRemedyProgress.setVisibility(View.GONE);
                                        if (likeResponse != null && likeResponse.isSuccessful()) {
                                            mInteractor.addRemedyToLikedList(GenericUtil.getContentIdFromUrl(String.valueOf(postCode),
                                                    likeResponse.headers().get("location")), codRemedy);
                                            likeImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_like_filled));
                                        } else {
                                            mView.showToast(mContext.getString(R.string.http_error_generic));
                                        }
                                    }));
                        } else {
                            mRemedyProgress.setVisibility(View.GONE);
                            mView.showToast(mContext.getString(R.string.http_error_generic));
                        }
                    }));
        }
    }

    Observer<Response<List<Remedy>>> getRemediesObserver = new Observer<Response<List<Remedy>>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            mView.setProgressLayoutVisibility(View.GONE);
            mView.setEmptyTextVisibility(View.GONE);
            mView.setNoResultsTextVisibility(View.VISIBLE);
            mView.showNoConnectionSnackBar();
        }

        @Override
        public void onNext(Response<List<Remedy>> listResponse) {
            mView.setProgressLayoutVisibility(View.GONE);
            if (listResponse.isSuccessful()) {
                if (listResponse.body().size() == 0) {
                    mView.setEmptyTextVisibility(View.GONE);
                    mView.setNoResultsTextVisibility(View.VISIBLE);
                } else {
                    mView.setRemedyAdapter(new RemedyAdapter(mContext, listResponse.body(), RemedyPresenterImpl.this));
                    mView.setEmptyTextVisibility(View.GONE);
                    mView.setNoResultsTextVisibility(View.GONE);
                }
            } else {
                mView.setEmptyTextVisibility(View.GONE);
                mView.setNoResultsTextVisibility(View.VISIBLE);
            }
        }
    };

    class BottomViews {
        @Bind(R.id.establishment_title)
        TextView establishmentTitle;
        @Bind(R.id.principio_ativo_text)
        TextView principioAtivoText;
        @Bind(R.id.classe_terapeutica_text)
        TextView classeTerapeuticaText;
        @Bind(R.id.apresentacao_text)
        TextView apresentacaoText;
        @Bind(R.id.laboratorio_text)
        TextView laboratorioText;
        @Bind(R.id.cnpj_text)
        TextView cnpjText;
        @Bind(R.id.registro_text)
        TextView registroText;
        @Bind(R.id.possui_restricao_text)
        TextView possuiRestricaoText;
        @Bind(R.id.bottom_sheet)
        NestedScrollView bottomSheet;
        @Bind(R.id.remedy_like_image)
        ImageView likeImage;
        @Bind(R.id.establishment_progress)
        ProgressBar remedyProgress;
    }
}