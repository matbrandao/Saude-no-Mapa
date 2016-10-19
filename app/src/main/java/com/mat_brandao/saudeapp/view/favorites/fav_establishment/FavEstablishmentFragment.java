package com.mat_brandao.saudeapp.view.favorites.fav_establishment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseFragment;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavEstablishmentFragment extends BaseFragment implements FavEstablishmentView {

    @Bind(R.id.fav_establishment_recycler)
    RecyclerView favEstablishmentRecycler;
    @Bind(R.id.empty_text_view)
    TextView emptyTextView;
    @Bind(R.id.progress_layout)
    LinearLayout progressLayout;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    private FavEstablishmentPresenterImpl mPresenter;

    public static FavEstablishmentFragment newInstance() {
        Bundle args = new Bundle();
        FavEstablishmentFragment fragment = new FavEstablishmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav_establishment, container, false);
        ButterKnife.bind(this, view);

        favEstablishmentRecycler.setHasFixedSize(true);
        favEstablishmentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mPresenter = new FavEstablishmentPresenterImpl(this, getContext());

        return view;
    }

    @Override
    public void showToast(String text) {
        super.showToast(text);
    }

    @Override
    public void goToActivity(Class<?> activity) {
        super.goToActivity(activity);
    }

    @Override
    public void goToActivity(Intent intent) {
        super.goToActivity(intent);
    }

    @Override
    public void finishActivity() {
        getActivity().supportFinishAfterTransition();
    }

    @Override
    public void showNoConnectionSnackBar() {
        super.showConnectionError(coordinatorLayout, v -> {
            mPresenter.onRetryClicked();
        });
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(getContext(), message);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @Override
    public void setRecyclerAdapter(FavEstablishmentAdapter adapter) {
        favEstablishmentRecycler.setAdapter(adapter);
    }

    @Override
    public void setProgressLayoutVisibility(int visibility) {
        try {
            progressLayout.setVisibility(visibility);
        } catch (Exception e) {}
    }

    @Override
    public void setEmptyViewVisibility(int visibility) {
        try {
        emptyTextView.setVisibility(visibility);
        } catch (Exception e) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}