package com.visionxoft.abacus.rehmantravel.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.UmrahPackageAdapter;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.List;

public class UmrahPackagesFragment extends Fragment {

    private MainActivity mainActivity;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.umrah_package));
        mainActivity.setToolbarSubTitle(null);

        View rootView = inflater.inflate(R.layout.fragment_umrah_packages, container, false);

        List<ImageTag> imageTags = (List<ImageTag>) IntentHelper.getObjectForKey("imageTags");
        if (imageTags != null) {
            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager_umrah);
            CirclePageIndicator indicator = (CirclePageIndicator) rootView.findViewById(R.id.view_pager_umrah_indicator);

            UmrahPackageAdapter packageAdapter = new UmrahPackageAdapter(mainActivity, getString(R.string.base_url_rt), imageTags);
            int sizeLimit = imageTags.size() - 1;
            if (sizeLimit > 10) sizeLimit = 10;
            viewPager.setOffscreenPageLimit(sizeLimit);
            viewPager.setAdapter(packageAdapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                indicator.setFillColor(mainActivity.getColor(R.color.red_orange));
            else indicator.setFillColor(mainActivity.getResources().getColor(R.color.red_orange));

            indicator.setCentered(true);
            indicator.setRadius(PhoneFunctionality.pxFromDp(mainActivity, 6));
            indicator.setViewPager(viewPager);
        } else {
            startActivity(new Intent(getContext(), MainActivity.class));
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity.action_call_rt != null) mainActivity.action_call_rt.setVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mainActivity.action_call_rt != null) mainActivity.action_call_rt.setVisible(false);
    }
}
