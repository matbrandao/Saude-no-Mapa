package com.mat_brandao.saudeapp.view.establishment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.maps.SupportMapFragment;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseFragment;
import com.mat_brandao.saudeapp.view.base.BasePresenter;
import com.mat_brandao.saudeapp.view.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class EstablishmentFragment extends BaseFragment implements EstablishmentView {
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.filter_fab)
    FloatingActionButton filterFab;
    @Bind(R.id.progress_fab)
    ProgressBar progressFab;
    @Bind(R.id.search_remedy_edit_text)
    TextInputEditText searchRemedyEditText;
    @Bind(R.id.uf_spinner)
    Spinner ufSpinner;

    private EstablishmentPresenterImpl mPresenter;

    private static View view;

    public EstablishmentFragment() {
        // Required empty public constructor
    }

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    public static EstablishmentFragment newInstance() {
        return new EstablishmentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.establishment_fragment, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).setToolbarTitle(getContext().getString(R.string.establishments_title));

        toggleFabButton(false);
        mPresenter = new EstablishmentPresenterImpl(this, getContext(), getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mPresenter);

        return view;
    }

    @OnClick(R.id.filter_fab)
    void onFilterFabClick() {
        mPresenter.onFilterFabClick();
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
        super.showConnectionError(coordinatorLayout, view -> {
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
    public void toggleFabButton(boolean enabled) {
        try {
            if (enabled) {
                filterFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
                filterFab.setEnabled(true);
            } else {
                filterFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.login_edit_text_color)));
                filterFab.setEnabled(false);
            }
        } catch (Exception e) {}
    }

    @Override
    public void showGpsDialog(DialogInterface.OnClickListener onAcceptListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle(R.string.dialog_gps_title);
        alertBuilder.setMessage(R.string.dialog_gps_message);
        alertBuilder.setPositiveButton("Ligar GPS", onAcceptListener);
        alertBuilder.setNegativeButton("Continuar", (dialog, which) -> {
            mPresenter.onGpsTurnedOff();
        });
        alertBuilder.create().show();
    }

    @Override
    public void startGpsIntent() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 202);
    }

    @Override
    public double getMapContainerHeight() {
        return (double) coordinatorLayout.getHeight();
    }

    @Override
    public void setProgressFabVisibility(int visibility) {
        try {
            progressFab.setVisibility(visibility);
        } catch (Exception e) {}
    }

    @Override
    public void setFabVisibility(int visibility) {
        filterFab.setVisibility(visibility);
    }

    @Override
    public void setUfSpinnerAdapter(ArrayAdapter<String> adapter) {
        ufSpinner.setAdapter(adapter);
    }

    @Override
    public void setUfSpinnerSelection(int selection) {
        ufSpinner.setSelection(selection);
    }

    @Override
    public Observable<CharSequence> registerSearchEditTextObserver() {
        return RxTextView.textChanges(searchRemedyEditText);
    }

    @Override
    public Observable<Integer> registerUfSpinnerObserver() {
        return RxAdapterView.itemSelections(ufSpinner);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!TextUtils.isEmpty(provider)) {
            mPresenter.onGpsTurnedOn();
        } else {
            mPresenter.onGpsTurnedOff();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}