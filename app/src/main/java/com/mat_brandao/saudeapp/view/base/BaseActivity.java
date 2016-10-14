package com.mat_brandao.saudeapp.view.base;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.mat_brandao.saudeapp.R;


/**
 * Created by Mateus Brandão on 04-Apr-16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public boolean shouldAnimate = true;
    private ProgressDialog mProgressDialog;

    @Override protected void onResume() {
        super.onResume();
        getPresenter().onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        getPresenter().onPause();
    }

    @Override protected void onDestroy() {
        getPresenter().onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "onCreate: shouldAnimate " + shouldAnimate);
            if (shouldAnimate)
                setupWindowAnimations();
        }
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NewApi")
    private void setupWindowAnimations() {
        Transition slideLeft = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_left);
        Transition slideRight = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right);
        getWindow().setExitTransition(slideLeft);
        getWindow().setEnterTransition(slideRight);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
    }

    protected void showToast(String text) {
        Toast toast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void goToActivity(Class<?> activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                startActivity(new Intent(this, activity), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } catch (Exception e) {
                startActivity(new Intent(this, activity));
            }
        } else {
            startActivity(new Intent(this, activity));
        }
    }

    protected void goToActivity(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    protected void showConnectionError(View coordinatorLayout, View.OnClickListener listener) {
        Snackbar.make(coordinatorLayout, getString(R.string.connection_error_snack), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.connection_retry, listener)
                .show();
    }

    protected void showProgressDialog(Context context, String message) {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {}
    }

    protected abstract BasePresenter getPresenter();
}