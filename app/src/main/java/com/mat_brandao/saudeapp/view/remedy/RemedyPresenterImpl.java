package com.mat_brandao.saudeapp.view.remedy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

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

    @Override
    public void onResume() {

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

        bottomSheetDialog.setContentView(dialogView);
//        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
//                .setBackgroundResource(R.color.default_dialog_background);

        dialogView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.setPeekHeight(mView.getRootHeight() - 200);

        bottomSheetDialog.show();
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
    }
}