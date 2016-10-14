package com.mat_brandao.saudeapp.view.walkthrough;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.login.LoginActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalkthroughtOneFragment extends Fragment {
    private static final String WALKTHROUGH_POSITION = "walkthrough_position";

    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.description)
    TextView description;
    @Bind(R.id.skip_view)
    TextView skipView;
    @Bind(R.id.intro_background)
    FrameLayout introBackground;
    @Bind(R.id.indicator_view_1)
    View indicatorView1;
    @Bind(R.id.indicator_view_2)
    View indicatorView2;
    @Bind(R.id.indicator_view_3)
    View indicatorView3;
    @Bind(R.id.page_image_view)
    ImageView pageImageView;

    private int mPosition;

    public WalkthroughtOneFragment() {
    }

    public static WalkthroughtOneFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(WALKTHROUGH_POSITION, position);
        WalkthroughtOneFragment fragment = new WalkthroughtOneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(WALKTHROUGH_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walkthrought_one, container, false);
        ButterKnife.bind(this, view);

        if (mPosition == 0) {
            view.setTag(1);

            titleText.setText(getString(R.string.title_one));
            description.setText(getString(R.string.description_one));
            indicatorView1.setBackground(getResources().getDrawable(R.drawable.round_indicator_selected));
        } else if (mPosition == 1) {
            view.setTag(2);

            titleText.setText(getString(R.string.title_two));
            description.setText(getString(R.string.description_two));
            Picasso.with(getContext())
                    .load(R.drawable.ic_remedy_big)
                    .into(pageImageView);

            indicatorView2.setBackground(getResources().getDrawable(R.drawable.round_indicator_selected));
        } else if (mPosition == 2) {
            view.setTag(3);
            titleText.setText(getString(R.string.title_three));
            description.setText(getString(R.string.description_three));
            Picasso.with(getContext())
                    .load(R.drawable.app_icon)
                    .into(pageImageView);

            skipView.setText("Entrar");
            indicatorView3.setBackground(getResources().getDrawable(R.drawable.round_indicator_selected));
        }

        return view;
    }

    @OnClick(R.id.skip_view)
    public void skipTutorial() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
