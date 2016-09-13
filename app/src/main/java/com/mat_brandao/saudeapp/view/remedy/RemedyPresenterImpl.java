package com.mat_brandao.saudeapp.view.remedy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Remedy;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class RemedyPresenterImpl implements RemedyPresenter {
    private static final String TAG = "RemedyPresenterImpl";

    private RemedyInteractorImpl mInteractor;
    private Context mContext;
    private RemedyView mView;

    private CompositeSubscription mSubscription = new CompositeSubscription();

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
    }

    @Override
    public void onScanBarcodeFabClick() {
        requestCameraPermissions();
    }

    @Override
    public void onScanSuccess(String data) {
        mView.showProgressDialog(mContext.getString(R.string.progress_searching_remedies));
        mSubscription.add(mInteractor.requestRemediesByBarCode(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getRemedyByBarCodeObserver));
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

    Observer<Response<List<Remedy>>> getRemedyByBarCodeObserver = new Observer<Response<List<Remedy>>>() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted() called with: " + "");
        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
        }

        @Override
        public void onNext(Response<List<Remedy>> listResponse) {
            mView.dismissProgressDialog();
            Log.d(TAG, "onNext() called with: " + "listResponse = [" + listResponse + "]");
        }
    };
}