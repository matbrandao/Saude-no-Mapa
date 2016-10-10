package com.mat_brandao.saudeapp.view.group;

import android.content.Context;
import android.util.Log;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.Grupo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;


public class GroupPresenterImpl implements GroupPresenter {
    private static final String TAG = "GroupPresenterImpl";

    private GroupInteractorImpl mInteractor;
    private Context mContext;
    private GroupView mView;

    private Grupo mGroup;
    private final Establishment mEstablishment;

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onRetryClicked() {
        requestGroup();
    }

    public GroupPresenterImpl(GroupView view, Context context) {
        mInteractor = new GroupInteractorImpl(context);
        mContext = context;
        mView = view;

        mEstablishment = mView.getIntentEstablishment();
        mView.setToolbarTitle("Grupo: " + mEstablishment.getNomeFantasia());

        requestGroup();
    }

    private void requestGroup() {
        mView.showProgressDialog(mContext.getString(R.string.progress_wait));
        mInteractor.requestGroup(mEstablishment.getNomeFantasia())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getGroupsObserver);
    }

    private void requestCreateGroup() {
        mInteractor.requestCreateGroup(mEstablishment.getNomeFantasia())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createGroupObserver);
    }

    private Observer<Response<List<Grupo>>> getGroupsObserver = new Observer<Response<List<Grupo>>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            mView.showNoConnectionSnackBar();
            Log.d(TAG, "onError() called with: e = [" + e + "]");
        }

        @Override
        public void onNext(Response<List<Grupo>> listResponse) {
            if (listResponse.isSuccessful()) {
                if (listResponse.body().isEmpty()) {
                    requestCreateGroup();
                } else {
                    mView.dismissProgressDialog();
                    mGroup = listResponse.body().get(0);
                }
            } else {
                mView.dismissProgressDialog();
                mView.showNoConnectionSnackBar();
            }
        }
    };

    private Observer<Response<ResponseBody>> createGroupObserver = new Observer<Response<ResponseBody>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            mView.showNoConnectionSnackBar();
            Log.d(TAG, "onError() called with: e = [" + e + "]");
        }

        @Override
        public void onNext(Response<ResponseBody> responseBodyResponse) {
            if (responseBodyResponse.isSuccessful()) {
                requestGroup();
            } else {
                mView.dismissProgressDialog();
                mView.showNoConnectionSnackBar();
            }
        }
    };
}