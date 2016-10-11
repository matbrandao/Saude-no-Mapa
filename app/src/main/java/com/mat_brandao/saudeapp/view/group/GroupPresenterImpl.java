package com.mat_brandao.saudeapp.view.group;

import android.content.Context;
import android.util.Log;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Establishment;
import com.mat_brandao.saudeapp.domain.model.Grupo;
import com.mat_brandao.saudeapp.domain.model.MembroGrupo;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.domain.util.GenericUtil;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


public class GroupPresenterImpl implements GroupPresenter, GenericObjectClickListener<MembroGrupo> {
    private static final String TAG = "GroupPresenterImpl";

    private GroupInteractorImpl mInteractor;
    private Context mContext;
    private GroupView mView;

    private Grupo mGroup;
    private List<MembroGrupo> mGroupMembers;
    private final Establishment mEstablishment;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mSubscription.unsubscribe();
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
        mSubscription.add(mInteractor.requestGroup(mEstablishment.getNomeFantasia())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getGroupsObserver));
    }

    private void requestCreateGroup() {
        mSubscription.add(mInteractor.requestCreateGroup(mEstablishment.getNomeFantasia())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createGroupObserver));
    }

    private void requestGroupMembers() {
        mSubscription.add(mInteractor.requestGroupMembers(mGroup.getCodGrupo())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groupMemberObserver));
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
                    mGroup = listResponse.body().get(0);
                    requestGroupMembers();
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

    private Observer<Response<List<MembroGrupo>>> groupMemberObserver = new Observer<Response<List<MembroGrupo>>>() {
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
        public void onNext(Response<List<MembroGrupo>> responseBodyResponse) {
            if (responseBodyResponse.isSuccessful()) {
                mView.dismissProgressDialog();
                mGroupMembers = responseBodyResponse.body();
                for (MembroGrupo mGroupMember : mGroupMembers) {
                    mGroupMember.setMembroId(GenericUtil
                            .getContentIdFromUrl(String.valueOf(mGroup.getCodGrupo()),
                                    mGroupMember.getLinks().get(0).getHref()));
                    mGroupMember.setUsuarioId(GenericUtil
                            .getNumbersFromString(mGroupMember.getLinks().get(1).getHref()));
                }
                if (mGroupMembers.size() > 0) {
                    mView.setGroupMembersAdapter(new GroupMembersAdapter(mContext, mGroupMembers, GroupPresenterImpl.this));
                } else {
                    // TODO: 11/10/2016 show empty view here;
                }
            } else {
                mView.dismissProgressDialog();
                mView.showNoConnectionSnackBar();
            }
        }
    };

    @Override
    public void onItemClick(MembroGrupo membroGrupo) {
        // TODO: 11/10/2016
    }
}