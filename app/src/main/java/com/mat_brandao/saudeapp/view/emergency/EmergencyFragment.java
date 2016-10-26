package com.mat_brandao.saudeapp.view.emergency;

import android.app.Activity;
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
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import rx.Observable;

public class EmergencyFragment extends BaseFragment implements EmergencyView {
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.search_remedy_edit_text)
    TextInputEditText searchRemedyEditText;
    @Bind(R.id.filter_fab)
    FloatingActionButton filterFab;
    @Bind(R.id.progress_fab)
    ProgressBar progressFab;
    @Bind(R.id.uf_spinner)
    Spinner ufSpinner;

    private EmergencyPresenterImpl mPresenter;
    private static View view;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    public static EmergencyFragment newInstance() {
        Bundle args = new Bundle();
        EmergencyFragment fragment = new EmergencyFragment();
        fragment.setArguments(args);
        return fragment;
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
            view = inflater.inflate(R.layout.fragment_emergency, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).setToolbarTitle(getContext().getString(R.string.emergency_title));

        toggleFabButton(false);

        mPresenter = new EmergencyPresenterImpl(this, getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mPresenter);

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
    public void showProgressDialog(String message) {
        super.showProgressDialog(getContext(), message);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @Override
    public void showNoConnectionSnackBar() {
        super.showConnectionError(coordinatorLayout, v -> {
            mPresenter.onRetryClicked();
        });
    }

    @Override
    public void showGpsDialog(DialogInterface.OnClickListener onAcceptListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle(R.string.dialog_gps_title);
        alertBuilder.setMessage(R.string.dialog_gps_message);
        alertBuilder.setPositiveButton(R.string.dialog_gps_positive, onAcceptListener);
        alertBuilder.setNegativeButton(R.string.dialog_gps_negative, (dialog, which) -> {
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
    public void toggleFabButton(boolean enabled) {
        try {
            if (enabled) {
                filterFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
                filterFab.setEnabled(true);
            } else {
                filterFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.login_edit_text_color)));
                filterFab.setEnabled(false);
            }
        } catch (Exception e) {
        }
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
    public void setUfSpinnerSelection(int selection) {
        ufSpinner.setSelection(selection);
    }

    @Override
    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void setUfSpinnerAdapter(ArrayAdapter adapter) {
        ufSpinner.setAdapter(adapter);
    }

    @Override
    public double getMapContainerHeight() {
        return (double) coordinatorLayout.getHeight();
    }

    @Override
    public void setProgressFabVisibility(int visibility) {
        try {
            progressFab.setVisibility(visibility);
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}