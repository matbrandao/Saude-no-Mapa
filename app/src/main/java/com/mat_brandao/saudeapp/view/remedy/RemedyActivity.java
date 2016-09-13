package com.mat_brandao.saudeapp.view.remedy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.base.BaseActivity;
import com.mat_brandao.saudeapp.view.base.BasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import timber.log.Timber;

public class RemedyActivity extends BaseActivity implements RemedyView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.scan_barcode_fab)
    FloatingActionButton fab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.search_remedy_edit_text)
    TextInputEditText searchRemedyEditText;
    @Bind(R.id.progress_layout)
    LinearLayout progressLayout;
    @Bind(R.id.empty_serch_text)
    TextView emptySerchText;
    @Bind(R.id.no_remedies_found_text)
    TextView noRemediesFoundText;
    @Bind(R.id.remedy_recycler)
    RecyclerView remedyRecycler;

    private RemedyPresenterImpl mPresenter;

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remedy);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mPresenter = new RemedyPresenterImpl(this, this);

        remedyRecycler.setHasFixedSize(true);
        remedyRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.scan_barcode_fab)
    void scanBarcodeFabClick() {
        mPresenter.onScanBarcodeFabClick();
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
        finish();
    }

    @Override
    public void showNoConnectionSnackBar() {
        super.showConnectionError(coordinatorLayout, view -> {
            mPresenter.onRetryClicked();
        });
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(this, message);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @Override
    public void gotoActivityWithResult(Intent intent) {
        startActivityForResult(intent, 101);
    }

    @Override
    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchRemedyEditText.getWindowToken(), 0);
    }

    @Override
    public void setProgressLayoutVisibility(int visibility) {
        progressLayout.setVisibility(visibility);
    }

    @Override
    public void setEmptyTextVisibility(int visibility) {
        emptySerchText.setVisibility(visibility);
    }

    @Override
    public void setNoResultsTextVisibility(int visibility) {
        noRemediesFoundText.setVisibility(visibility);
    }

    @Override
    public void setRemedyAdapter(RemedyAdapter adapter) {
        remedyRecycler.setAdapter(adapter);
    }

    @Override
    public Observable<CharSequence> registerSearchObservable() {
        return RxTextView.textChanges(searchRemedyEditText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("data");
                mPresenter.onScanSuccess(contents);
                Timber.d("contents: " + contents);
            } else if (resultCode == RESULT_CANCELED) {
                Timber.d("RESULT_CANCELED");
            }
        }
    }
}