package com.mat_brandao.saudeapp.view.walkthrough;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.view.WindowManager;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.domain.util.IntroPageTransformer;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

public class WalkthroughActivity extends AppCompatActivity {

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_walkthrough);
        ButterKnife.bind(this);

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#F2000000"));
        }

        viewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
        viewPager.setPageTransformer(false, new IntroPageTransformer());
    }

    @SuppressLint("NewApi")
    private void setupWindowAnimations() {
        Transition slideLeft = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_left);
        Transition slideRight = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right);
        getWindow().setExitTransition(slideLeft);
        getWindow().setEnterTransition(slideRight);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    public class IntroAdapter extends FragmentPagerAdapter {

        public IntroAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return WalkthroughtOneFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
