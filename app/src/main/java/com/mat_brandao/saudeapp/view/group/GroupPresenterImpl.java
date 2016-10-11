package com.mat_brandao.saudeapp.view.group;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

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


public class GroupPresenterImpl implements GroupPresenter, GenericObjectClickListener<MembroGrupo>,
        SwipeRefreshLayout.OnRefreshListener {
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
        mView.showProgressDialog(mContext.getString(R.string.progress_wait));
        requestGroup();
    }

    public GroupPresenterImpl(GroupView view, Context context) {
        mInteractor = new GroupInteractorImpl(context);
        mContext = context;
        mView = view;

        mEstablishment = mView.getIntentEstablishment();
        mView.setToolbarTitle("Grupo: " + mEstablishment.getNomeFantasia());

        mView.showProgressDialog(mContext.getString(R.string.progress_wait));
        requestGroup();
    }

    private void requestGroup() {
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

    @Override
    public void onItemClick(MembroGrupo membroGrupo) {
        if (membroGrupo.getUsuarioId() == mInteractor.getUser().getId()) {
            mView.showLeaveGroupDialog(() -> {
                requestLeaveGroup(membroGrupo);
            });
        }
    }

    private void requestLeaveGroup(MembroGrupo membroGrupo) {
        mView.showProgressDialog(mContext.getString(R.string.progress_wait));
        mSubscription.add(mInteractor.requestLeaveGroup(mGroup.getCodGrupo(), membroGrupo.getMembroId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(leaveGroupObserver));
    }

    @Override
    public void onRefresh() {
        if (mGroup != null) {
            requestGroupMembers();
        } else {
            requestGroup();
        }
    }

    private Observer<Response<List<Grupo>>> getGroupsObserver = new Observer<Response<List<Grupo>>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mView.dismissProgressDialog();
            mView.setIsRefreshing(false);
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
                mView.setIsRefreshing(false);
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
            mView.setIsRefreshing(false);
            mView.showNoConnectionSnackBar();
            Log.d(TAG, "onError() called with: e = [" + e + "]");
        }

        @Override
        public void onNext(Response<ResponseBody> responseBodyResponse) {
            if (responseBodyResponse.isSuccessful()) {
                mView.showProgressDialog(mContext.getString(R.string.progress_wait));
                requestGroup();
            } else {
                mView.setIsRefreshing(false);
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
            mView.setIsRefreshing(false);
            mView.showNoConnectionSnackBar();
            Log.d(TAG, "onError() called with: e = [" + e + "]");
        }

        @Override
        public void onNext(Response<List<MembroGrupo>> responseBodyResponse) {
            if (responseBodyResponse.isSuccessful()) {
                mView.dismissProgressDialog();
                mView.setIsRefreshing(false);
                mGroupMembers = responseBodyResponse.body();
                for (MembroGrupo mGroupMember : mGroupMembers) {
                    mGroupMember.setMembroId(GenericUtil
                            .getContentIdFromUrl(String.valueOf(mGroup.getCodGrupo()),
                                    mGroupMember.getLinks().get(0).getHref()));
                    mGroupMember.setUsuarioId(GenericUtil
                            .getNumbersFromString(mGroupMember.getLinks().get(1).getHref()));
                }

                mView.setGroupMembersAdapter(new GroupMembersAdapter(mContext, mGroupMembers, GroupPresenterImpl.this));
                if (mGroupMembers.isEmpty()) {
                    mView.setEmptyTextVisibility(View.VISIBLE);
                } else {
                    mView.setEmptyTextVisibility(View.GONE);
                }
            } else {
                mView.setIsRefreshing(false);
                mView.dismissProgressDialog();
                mView.showNoConnectionSnackBar();
            }
        }
    };

    private Observer<Response<ResponseBody>> leaveGroupObserver = new Observer<Response<ResponseBody>>() {
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
                requestGroupMembers();
            } else {
                mView.dismissProgressDialog();
                mView.showNoConnectionSnackBar();
            }
        }
    };
}