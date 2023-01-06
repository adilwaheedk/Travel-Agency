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
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.viewpagerindicator.CirclePageIndicator;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.VisaVisitPackageAdapter;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.task.SubmitPackageForm;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;
import java.util.List;

public class VisaVisitPackagesFragment extends Fragment {

    private MainActivity mainActivity;
    public Dialog apply_dialog, detail_dialog;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.visa_visit_title));
        mainActivity.setToolbarSubTitle(getString(R.string.visa_visit_package));

        View rootView = inflater.inflate(R.layout.fragment_visa_visit_packages, container, false);

        List<ImageTag> imageTags = (List<ImageTag>) IntentHelper.getObjectForKey("imageTags");
        if (imageTags != null) {
            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager_visa_visit);
            CirclePageIndicator indicator = (CirclePageIndicator) rootView.findViewById(R.id.view_pager_visa_visit_indicator);

            VisaVisitPackageAdapter packageAdapter = new VisaVisitPackageAdapter(this,
                    getString(R.string.base_url_rt) + "/", imageTags);
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
            startActivity(new Intent(mainActivity, MainActivity.class));
        }
        return rootView;
    }

    public void createVisaDetailsDialog(final String visaId, final String data, final List<String> visa_visits) {

        detail_dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_visa_visit_details, Gravity.CENTER);
        WebView web_view_details = (WebView) detail_dialog.findViewById(R.id.web_view_details);
        web_view_details.setInitialScale(1);
        web_view_details.getSettings().setLoadWithOverviewMode(true);
        web_view_details.getSettings().setUseWideViewPort(true);
        web_view_details.getSettings().setBuiltInZoomControls(true);
        web_view_details.getSettings().setDisplayZoomControls(false);

        View btn_details_back = detail_dialog.findViewById(R.id.btn_details_back);
        View btn_details_apply = detail_dialog.findViewById(R.id.btn_details_apply);
        web_view_details.loadData(data, "text/html", null);
        detail_dialog.show();

        btn_details_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail_dialog.dismiss();
            }
        });

        btn_details_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apply_dialog == null) {
                    apply_dialog = DialogHelper.createCustomDialog(mainActivity,
                            R.layout.dialog_visa_visit_apply, Gravity.CENTER);
                    apply_dialog.show();

                    final Spinner choose_visa = (Spinner) apply_dialog.findViewById(R.id.visa_visit_choose_visa);
                    final EditText visa_visit_name = (EditText) apply_dialog.findViewById(R.id.visa_visit_name);
                    final EditText visa_visit_email = (EditText) apply_dialog.findViewById(R.id.visa_visit_email);
                    final EditText visa_visit_contact = (EditText) apply_dialog.findViewById(R.id.visa_visit_contact);
                    final EditText visa_visit_desc = (EditText) apply_dialog.findViewById(R.id.visa_visit_desc);
                    View visa_visit_name_clear = apply_dialog.findViewById(R.id.visa_visit_name_clear);
                    View visa_visit_email_clear = apply_dialog.findViewById(R.id.visa_visit_email_clear);
                    View visa_visit_contact_clear = apply_dialog.findViewById(R.id.visa_visit_contact_clear);
                    View visa_visit_desc_clear = apply_dialog.findViewById(R.id.visa_visit_desc_clear);
                    View btn_visa_visit_back = apply_dialog.findViewById(R.id.btn_visa_visit_back);
                    View btn_visa_visit_submit = apply_dialog.findViewById(R.id.btn_visa_visit_submit);

                    visa_visit_name_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            visa_visit_name.setText("");
                        }
                    });

                    visa_visit_email_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            visa_visit_email.setText("");
                        }
                    });

                    visa_visit_contact_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            visa_visit_contact.setText("");
                        }
                    });

                    visa_visit_desc_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            visa_visit_desc.setText("");
                        }
                    });

                    choose_visa.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, visa_visits));

                    btn_visa_visit_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            apply_dialog.hide();
                        }
                    });

                    btn_visa_visit_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText editText = null;
                            if (visa_visit_name.getText().toString().equals("")) {
                                editText = visa_visit_name;
                            } else if (visa_visit_email.getText().toString().equals("")) {
                                editText = visa_visit_email;
                            } else if (visa_visit_contact.getText().toString().equals("")) {
                                editText = visa_visit_contact;
                            } else if (visa_visit_desc.getText().toString().equals("")) {
                                editText = visa_visit_desc;
                            }

                            if (editText != null) {
                                editText.setError(getString(R.string.error_field_required));
                                editText.requestFocus();
                            } else {
                                if (!FormatString.isEmailValid(visa_visit_email.getText().toString())) {
                                    visa_visit_email.setError(getString(R.string.error_invalid_email));
                                    visa_visit_email.requestFocus();
                                    return;
                                }
                                if (!FormatString.isContactValid(visa_visit_contact.getText().toString())) {
                                    visa_visit_contact.setError(getString(R.string.error_invalid_number));
                                    visa_visit_contact.requestFocus();
                                    return;
                                }

                                final Hashtable<String, Object> params = new Hashtable<>();
                                params.put("visaId", visaId);
                                params.put("name", visa_visit_name.getText().toString().trim());
                                params.put("email", visa_visit_email.getText().toString().trim());
                                params.put("phone", visa_visit_contact.getText().toString().trim());
                                params.put("description", visa_visit_desc.getText().toString().trim());
                                params.put("visa_type", choose_visa.getSelectedItem().toString());
                                new SubmitPackageForm(VisaVisitPackagesFragment.this, params, SubmitPackageForm.VISIT_VISA).execute();
                            }
                        }
                    });
                } else apply_dialog.show();
            }
        });
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
