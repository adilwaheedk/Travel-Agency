package com.visionxoft.abacus.rehmantravel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.VisaStudyCountryAdapter;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;

import java.util.List;

public class VisaStudyCountryFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.visa_study_title));
        mainActivity.setToolbarSubTitle(getString(R.string.visa_study_countries));

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_visa_study_country, container, false);

            List<ImageTag> imageTags = (List<ImageTag>) IntentHelper.getObjectForKey("imageTags");
            if (imageTags != null) {
                GridView grid_view_visa_study = (GridView) rootView.findViewById(R.id.grid_view_visa_study);
                VisaStudyCountryAdapter adapter = new VisaStudyCountryAdapter(this,
                        getString(R.string.base_url_rt) + "/", imageTags);
                grid_view_visa_study.setAdapter(adapter);

            } else {
                startActivity(new Intent(mainActivity, MainActivity.class));
            }
        }
        return rootView;
    }
}
