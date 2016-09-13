package com.mat_brandao.saudeapp.view.remedy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class RemedyPresenterImpl implements RemedyPresenter {
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
                    // TODO: 13/09/2016 setAdapter here
                    mView.setEmptyTextVisibility(View.GONE);
                    mView.setNoResultsTextVisibility(View.GONE);
                }
            } else {
                mView.setEmptyTextVisibility(View.GONE);
                mView.setNoResultsTextVisibility(View.VISIBLE);
            }
        }
    };
}