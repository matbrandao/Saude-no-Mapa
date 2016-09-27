package com.mat_brandao.saudeapp.view.favorites;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mat_brandao.saudeapp.R;
import com.mat_brandao.saudeapp.view.favorites.fav_establishment.FavEstablishmentFragment;
import com.mat_brandao.saudeapp.view.favorites.fav_remedy.FavRemedyFragment;
import com.mat_brandao.saudeapp.view.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoritesFragment extends Fragment {

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).setToolbarTitle(getContext().getString(R.string.favorites_title));

        FragmentManager manager = getChildFragmentManager();
        PagerAdapter fragAdapter = new PagerAdapter(manager);

        viewPager.setAdapter(fragAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = FavEstablishmentFragment.newInstance();
                    break;
                case 1:
                    frag = FavRemedyFragment.newInstance();
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = "Estabelecimentos";
                    break;
                case 1:
                    title = "Remédios";
                    break;
            }
            return title;
        }
    }
}
