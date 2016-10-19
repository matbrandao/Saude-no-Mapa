package com.mat_brandao.saudeapp.view.my_groups;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.model.Grupo;
import com.mat_brandao.saudeapp.domain.util.GenericObjectClickListener;
import com.mat_brandao.saudeapp.view.group.GroupActivity;
import com.mat_brandao.saudeapp.view.main.MainActivity;

import java.util.List;

import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mat_brandao.saudeapp.view.establishment.EstablishmentPresenterImpl.ESTABLISHMENT_INTENT_KEY;

public class MyGroupsPresenterImpl implements MyGroupsPresenter, GenericObjectClickListener<Grupo> {
    private static final String TAG = "MyGroupsPresenterImpl";

    private MyGroupsInteractorImpl mInteractor;
    private Context mContext;
    private MyGroupsView mView;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    public void onResume() {
        ((MainActivity) mContext).setNavigationItemChecked(R.id.menu_item_my_groups);
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
        requestMyGroups();
    }

    public MyGroupsPresenterImpl(MyGroupsView view, Context context) {
        mInteractor = new MyGroupsInteractorImpl(context);
        mContext = context;
        mView = view;

        requestMyGroups();
    }

    private void requestMyGroups() {
        mView.setProgressBarVisibility(View.VISIBLE);
        mSubscription.add(mInteractor.requestMyGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myGroupsObserver));
    }

    @Override
    public void onItemClick(Grupo grupo) {
        Intent intent = new Intent(mContext, GroupActivity.class);
        intent.putExtra(ESTABLISHMENT_INTENT_KEY, grupo.getDescricao());
        mView.goToActivity(intent);
    }
    
    private Observer<Response<List<Grupo>>> myGroupsObserver = new Observer<Response<List<Grupo>>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mView.setProgressBarVisibility(View.GONE);
            mView.showNoConnectionSnackBar();
            Log.d(TAG, "onError() called with: e = [" + e + "]");
        }

        @Override
        public void onNext(Response<List<Grupo>> listResponse) {
            mView.setProgressBarVisibility(View.GONE);
            if (listResponse.isSuccessful()) {
                if (listResponse.body().isEmpty()) {
                    mView.setEmptyViewVisibility(View.VISIBLE);
                } else {
                    mView.setEmptyViewVisibility(View.GONE);
                    mView.setGroupsAdapter(new GroupsAdapter(mContext, listResponse.body(), MyGroupsPresenterImpl.this));
                }
            } else {
                mView.setProgressBarVisibility(View.GONE);
                mView.showNoConnectionSnackBar();
            }
        }
    };
}