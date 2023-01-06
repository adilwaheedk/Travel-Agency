package com.visionxoft.abacus.rehmantravel.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.viewpagerindicator.CirclePageIndicator;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.AllPackagesAdapter;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.task.SubmitPackageForm;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;
import java.util.List;

public class AllPackagesFragment extends Fragment {

    private MainActivity mainActivity;
    public Dialog apply_dialog;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.all_packages));
        mainActivity.setToolbarSubTitle(null);

        View rootView = inflater.inflate(R.layout.fragment_all_packages, container, false);

        List<ImageTag> imageTags = (List<ImageTag>) IntentHelper.getObjectForKey("imageTags");
        if (imageTags != null) {
            final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager_all_packages);
            CirclePageIndicator indicator = (CirclePageIndicator) rootView.findViewById(R.id.view_pager_all_packages_indicator);

            final AllPackagesAdapter packageAdapter = new AllPackagesAdapter(this,
                    getString(R.string.base_url_rt) + "/", imageTags);
            int sizeLimit = imageTags.size() - 1;
            if (sizeLimit > 10) sizeLimit = 10;
            viewPager.setOffscreenPageLimit(sizeLimit);
            viewPager.setAdapter(packageAdapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                indicator.setFillColor(mainActivity.getColor(R.color.red_orange));
            else indicator.setFillColor(mainActivity.getResources().getColor(R.color.red_orange));

            indicator.setCentered(true);
            indicator.setRadius(PhoneFunctionality.pxFromDp(mainActivity, 4));
            indicator.setViewPager(viewPager);
        } else {
            startActivity(new Intent(mainActivity, MainActivity.class));
        }
        return rootView;
    }

    public void createApplyPackageDialog(final ImageTag imageTag) {

        apply_dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_package_apply, Gravity.CENTER);
        apply_dialog.show();

        // region Find views
        final EditText all_packages_name = (EditText) apply_dialog.findViewById(R.id.all_packages_name);
        final EditText all_packages_email = (EditText) apply_dialog.findViewById(R.id.all_packages_email);
        final EditText all_packages_contact = (EditText) apply_dialog.findViewById(R.id.all_packages_contact);
        final View all_packages_name_clear = apply_dialog.findViewById(R.id.all_packages_name_clear);
        final View all_packages_email_clear = apply_dialog.findViewById(R.id.all_packages_email_clear);
        final View all_packages_contact_clear = apply_dialog.findViewById(R.id.all_packages_contact_clear);
        final HorizontalNumberPicker all_package_no_of_ppl = (HorizontalNumberPicker) apply_dialog.findViewById(R.id.all_package_no_of_ppl);
        final View btn_all_packages_apply = apply_dialog.findViewById(R.id.btn_all_packages_apply);
        final View btn_all_packages_back = apply_dialog.findViewById(R.id.btn_all_packages_back);
        // endregion

        all_package_no_of_ppl.setMinValue(1);
        all_package_no_of_ppl.setMaxValue(50);

        // region Clear editText values
        all_packages_name_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_packages_name.setText("");
            }
        });

        all_packages_email_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_packages_email.setText("");
            }
        });

        all_packages_contact_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_packages_contact.setText("");
            }
        });
        // endregion

        btn_all_packages_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply_dialog.dismiss();
            }
        });

        btn_all_packages_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = null;
                String name = all_packages_name.getText().toString();
                String email = all_packages_email.getText().toString();
                String contact = all_packages_contact.getText().toString();
                String no_of_ppl = String.valueOf(all_package_no_of_ppl.getValue());

                if (name.equals("")) editText = all_packages_name;
                else if (email.equals("")) editText = all_packages_email;
                else if (contact.equals("")) editText = all_packages_contact;

                if (editText != null) {
                    editText.setError(getString(R.string.error_field_required));
                    editText.requestFocus();
                } else {
                    if (!FormatString.isEmailValid(email)) {
                        all_packages_email.setError(getString(R.string.error_invalid_email));
                        all_packages_email.requestFocus();
                    } else {
                        final Hashtable<String, Object> params = new Hashtable<>();
                        params.put("pkgId", imageTag.extra);
                        params.put("name", name);
                        params.put("email", email);
                        params.put("phone", contact);
                        params.put("pkgImage", imageTag.src);
                        params.put("noofparsons", no_of_ppl);
                        new SubmitPackageForm(AllPackagesFragment.this, params, SubmitPackageForm.OTHER).execute();
                    }
                }
            }
        });
    }
}
