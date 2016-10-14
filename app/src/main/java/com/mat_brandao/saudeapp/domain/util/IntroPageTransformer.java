package com.mat_brandao.saudeapp.domain.util;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;

/**
 * Created by Mateus Brandão on 8/24/2015.
 */
public class IntroPageTransformer implements ViewPager.PageTransformer {

    private static final float X_MIN_VEL = 0.1f;
    private static final float X_MAX_VEL = 0.35f;
    private static final float SCALE_Y_FACTOR = 0.0001f;

    @Override
    public void transformPage(View page, float position) {
        int pagePosition = (int) page.getTag();
        switch (pagePosition) {
            case 1:
                transformOne(page, position);
                break;
            case 2:
                transformTwo(page, position);
                break;
            case 3:
                transformThree(page, position);
                break;
        }
    }

    private void transformOne(View page, float position) {
        int pageWidth = page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);
        ImageView backgroundImage = (ImageView) page.findViewById(R.id.image_view);
        TextView titleView = (TextView) page.findViewById(R.id.title_text);
        TextView descriptionView = (TextView) page.findViewById(R.id.description);
        View indicatorView1 = page.findViewById(R.id.indicator_view_1);
        View indicatorView2 = page.findViewById(R.id.indicator_view_2);
        View indicatorView3 = page.findViewById(R.id.indicator_view_3);

        if (position <= -1.0f || position >= 1.0f) {
        } else if (position == 0.0f) {
        } else {
            if (pageWidthTimesPosition > 1) {
                backgroundImage.setScaleY(1 + (pageWidthTimesPosition * SCALE_Y_FACTOR));
                backgroundImage.setScaleX(1 + (pageWidthTimesPosition * SCALE_Y_FACTOR));
            } else if (pageWidthTimesPosition < 1) {
                backgroundImage.setScaleY(1 - (pageWidthTimesPosition * SCALE_Y_FACTOR));
                backgroundImage.setScaleX(1 - (pageWidthTimesPosition * SCALE_Y_FACTOR));
            }

            titleView.setTranslationX(pageWidthTimesPosition * X_MIN_VEL);
            descriptionView.setTranslationX(pageWidthTimesPosition * X_MAX_VEL);

            indicatorView1.setTranslationX(-pageWidthTimesPosition * 1f);
            indicatorView2.setAlpha(1.0f - (5 * absPosition));
            indicatorView3.setAlpha(1.0f - (5 * absPosition));
        }
    }

    private void transformTwo(View page, float position) {
        int pageWidth = page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);
        ImageView backgroundImage = (ImageView) page.findViewById(R.id.image_view);
        TextView titleView = (TextView) page.findViewById(R.id.title_text);
        TextView descriptionView = (TextView) page.findViewById(R.id.description);
        View indicatorView1 = page.findViewById(R.id.indicator_view_1);
        View indicatorView2 = page.findViewById(R.id.indicator_view_2);
        View indicatorView3 = page.findViewById(R.id.indicator_view_3);

        if (position <= -1.0f || position >= 1.0f) {
        } else if (position == 0.0f) {
        } else {
            if (pageWidthTimesPosition > 1) {
                backgroundImage.setScaleY(1 + (pageWidthTimesPosition * SCALE_Y_FACTOR));
                backgroundImage.setScaleX(1 + (pageWidthTimesPosition * SCALE_Y_FACTOR));
            } else if (pageWidthTimesPosition < 1) {
                backgroundImage.setScaleY(1 - (pageWidthTimesPosition * SCALE_Y_FACTOR));
                backgroundImage.setScaleX(1 - (pageWidthTimesPosition * SCALE_Y_FACTOR));
            }

            titleView.setTranslationX(pageWidthTimesPosition * X_MIN_VEL);
            descriptionView.setTranslationX(pageWidthTimesPosition * X_MAX_VEL);

            indicatorView2.setTranslationX(-pageWidthTimesPosition * 1f);
            indicatorView1.setAlpha(1.0f - (5 * absPosition));
            indicatorView3.setAlpha(1.0f - (5 * absPosition));
        }
    }

    private void transformThree(View page, float position) {
        int pageWidth = page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);
        ImageView backgroundImage = (ImageView) page.findViewById(R.id.image_view);
        TextView titleView = (TextView) page.findViewById(R.id.title_text);
        TextView descriptionView = (TextView) page.findViewById(R.id.description);
        View indicatorView1 = page.findViewById(R.id.indicator_view_1);
        View indicatorView2 = page.findViewById(R.id.indicator_view_2);
        View indicatorView3 = page.findViewById(R.id.indicator_view_3);

        if (position <= -1.0f || position >= 1.0f) {
        } else if (position == 0.0f) {
        } else {
            if (pageWidthTimesPosition > 1) {
                backgroundImage.setScaleY(1 + (pageWidthTimesPosition * SCALE_Y_FACTOR));
                backgroundImage.setScaleX(1 + (pageWidthTimesPosition * SCALE_Y_FACTOR));
            } else if (pageWidthTimesPosition < 1) {
                backgroundImage.setScaleY(1 - (pageWidthTimesPosition * SCALE_Y_FACTOR));
                backgroundImage.setScaleX(1 - (pageWidthTimesPosition * SCALE_Y_FACTOR));
            }

            titleView.setTranslationX(pageWidthTimesPosition * X_MIN_VEL);
            descriptionView.setTranslationX(pageWidthTimesPosition * X_MAX_VEL);

            indicatorView3.setTranslationX(-pageWidthTimesPosition * 1f);
            indicatorView1.setAlpha(1f - (5 * absPosition));
            indicatorView2.setAlpha(1f - (5 * absPosition));
        }
    }
}