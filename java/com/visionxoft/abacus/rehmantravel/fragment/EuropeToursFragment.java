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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.viewpagerindicator.CirclePageIndicator;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.EuropeToursAdapter;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.task.SubmitEuropeTourForm;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;
import java.util.List;

public class EuropeToursFragment extends Fragment {

    private MainActivity mainActivity;
    public Dialog apply_dialog, detail_dialog;
    public static final int SWISS_STD_CAMP = 1, POLAND_TOUR = 2, POLAND_TOUR_PKG = 3, AUSTRIA_TOUR_PKG = 4, GERMANY_TOUR = 5;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int tourType = (int) IntentHelper.getObjectForKey("europeTourType");
        String subtitle = null;
        switch (tourType) {
            case SWISS_STD_CAMP:
                subtitle = "Swiss Student Camps";
                break;
            case POLAND_TOUR:
                subtitle = "Poland Tours";
                break;
            case POLAND_TOUR_PKG:
                subtitle = "Poland Tour Packages";
                break;
            case AUSTRIA_TOUR_PKG:
                subtitle = "Austria Tours";
                break;
            case GERMANY_TOUR:
                subtitle = "Germany Tours";
                break;
        }
        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.europe_tours));
        mainActivity.setToolbarSubTitle(subtitle);

        View rootView = inflater.inflate(R.layout.fragment_europe_tours, container, false);

        List<ImageTag> imageTags = (List<ImageTag>) IntentHelper.getObjectForKey("imageTags");
        if (imageTags != null) {
            final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager_eur_tours);
            CirclePageIndicator indicator = (CirclePageIndicator) rootView.findViewById(R.id.view_pager_eur_tours_indicator);

            final EuropeToursAdapter packageAdapter = new EuropeToursAdapter(this,
                    getString(R.string.base_url_rt) + "/", imageTags, tourType);
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

    public void createTourDetailDialog(String data, final String tourID, final int tourType) {

        detail_dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_europe_tours_details, Gravity.CENTER);
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
                    apply_dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_europe_tours_apply, Gravity.CENTER);
                    apply_dialog.show();

                    // region Find views
                    final View eur_tour_school_name_rl = apply_dialog.findViewById(R.id.eur_tour_school_name_rl);
                    final RadioGroup rg_eur_tour_poland_pkg = (RadioGroup) apply_dialog.findViewById(R.id.rg_eur_tour_poland_pkg);
                    final RadioGroup rg_eur_tour_austria_pkg = (RadioGroup) apply_dialog.findViewById(R.id.rg_eur_tour_austria_pkg);
                    final View eur_tour_school_addr_rl = apply_dialog.findViewById(R.id.eur_tour_school_addr_rl);
                    final EditText eur_tour_school_name = (EditText) apply_dialog.findViewById(R.id.eur_tour_school_name);
                    final EditText eur_tour_school_addr = (EditText) apply_dialog.findViewById(R.id.eur_tour_school_addr);
                    final EditText eur_tour_name = (EditText) apply_dialog.findViewById(R.id.eur_tour_name);
                    final EditText eur_tour_email = (EditText) apply_dialog.findViewById(R.id.eur_tour_email);
                    final EditText eur_tour_contact = (EditText) apply_dialog.findViewById(R.id.eur_tour_contact);
                    final EditText eur_tour_remarks = (EditText) apply_dialog.findViewById(R.id.eur_tour_remarks);
                    final View eur_tour_school_name_clear = apply_dialog.findViewById(R.id.eur_tour_school_name_clear);
                    final View eur_tour_school_addr_clear = apply_dialog.findViewById(R.id.eur_tour_school_addr_clear);
                    final View eur_tour_name_clear = apply_dialog.findViewById(R.id.eur_tour_name_clear);
                    final View eur_tour_email_clear = apply_dialog.findViewById(R.id.eur_tour_email_clear);
                    final View eur_tour_contact_clear = apply_dialog.findViewById(R.id.eur_tour_contact_clear);
                    final View eur_tour_remarks_clear = apply_dialog.findViewById(R.id.eur_tour_remarks_clear);
                    final HorizontalNumberPicker eur_tour_no_of_ppl = (HorizontalNumberPicker) apply_dialog.findViewById(R.id.eur_tour_no_of_ppl);
                    final View btn_eur_tour_apply = apply_dialog.findViewById(R.id.btn_eur_tour_apply);
                    final View btn_eur_tour_back = apply_dialog.findViewById(R.id.btn_eur_tour_back);
                    // endregion

                    if (tourType != SWISS_STD_CAMP) {
                        eur_tour_school_name_rl.setVisibility(View.GONE);
                        eur_tour_school_addr_rl.setVisibility(View.GONE);
                    }
                    if (tourType != POLAND_TOUR_PKG) {
                        rg_eur_tour_poland_pkg.setVisibility(View.GONE);
                    }
                    if (tourType != AUSTRIA_TOUR_PKG) {
                        rg_eur_tour_austria_pkg.setVisibility(View.GONE);
                    }

                    eur_tour_no_of_ppl.setMinValue(1);
                    eur_tour_no_of_ppl.setMaxValue(10);

                    // region Clear editText values
                    eur_tour_school_name_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eur_tour_school_name.setText("");
                        }
                    });

                    eur_tour_school_addr_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eur_tour_school_addr.setText("");
                        }
                    });

                    eur_tour_name_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eur_tour_name.setText("");
                        }
                    });

                    eur_tour_email_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eur_tour_email.setText("");
                        }
                    });

                    eur_tour_contact_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eur_tour_contact.setText("");
                        }
                    });

                    eur_tour_remarks_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eur_tour_remarks.setText("");
                        }
                    });
                    // endregion

                    btn_eur_tour_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            apply_dialog.dismiss();
                        }
                    });

                    btn_eur_tour_apply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText editText = null;

                            if (tourType == SWISS_STD_CAMP) {
                                if (eur_tour_school_name.getText().toString().equals(""))
                                    editText = eur_tour_school_name;
                                else if (eur_tour_school_addr.getText().toString().equals(""))
                                    editText = eur_tour_school_addr;
                            }

                            String name = eur_tour_name.getText().toString();
                            String email = eur_tour_email.getText().toString();
                            String contact = eur_tour_contact.getText().toString();
                            String remarks = eur_tour_remarks.getText().toString();
                            String no_of_ppl = String.valueOf(eur_tour_no_of_ppl.getValue());

                            if (editText == null) {
                                if (name.equals("")) editText = eur_tour_name;
                                else if (email.equals("")) editText = eur_tour_email;
                                else if (contact.equals("")) editText = eur_tour_contact;
                                else if (remarks.equals("")) editText = eur_tour_remarks;
                            }

                            if (editText != null) {
                                editText.setError(getString(R.string.error_field_required));
                                editText.requestFocus();
                                return;
                            }
                            if (!FormatString.isEmailValid(email)) {
                                eur_tour_email.setError(getString(R.string.error_invalid_email));
                                eur_tour_email.requestFocus();
                                return;
                            }
                            if (!FormatString.isContactValid(contact)) {
                                eur_tour_contact.setError(getString(R.string.error_invalid_number));
                                eur_tour_contact.requestFocus();
                                return;
                            }

                            final Hashtable<String, Object> params = new Hashtable<>();
                            switch (tourType) {
                                case SWISS_STD_CAMP:
                                    params.put("europeTourId", tourID);
                                    params.put("schoolName", eur_tour_school_name.getText().toString());
                                    params.put("schoolAddress", eur_tour_school_addr.getText().toString());
                                    params.put("applyEuropeTour", true);
                                    break;
                                case POLAND_TOUR:
                                    params.put("polandTourId", tourID);
                                    params.put("applyPolandTour", true);
                                    break;
                                case POLAND_TOUR_PKG:
                                    String poland_dur = "";
                                    for (int i = 0; i < rg_eur_tour_poland_pkg.getChildCount(); i++) {
                                        RadioButton rb = ((RadioButton) rg_eur_tour_poland_pkg.getChildAt(i));
                                        if (rb.isChecked()) {
                                            poland_dur = rb.getText().toString();
                                            break;
                                        }
                                    }
                                    params.put("polandTourPackageId", tourID);
                                    params.put("pakageDuration", poland_dur);
                                    params.put("applyPolandTourPackage", true);
                                    break;
                                case AUSTRIA_TOUR_PKG:
                                    String austria_dur = "";
                                    for (int i = 0; i < rg_eur_tour_austria_pkg.getChildCount(); i++) {
                                        RadioButton rb = ((RadioButton) rg_eur_tour_austria_pkg.getChildAt(i));
                                        if (rb.isChecked()) {
                                            austria_dur = rb.getText().toString();
                                            break;
                                        }
                                    }
                                    params.put("austriaTourPackageId", tourID);
                                    params.put("pakageDuration", austria_dur);
                                    params.put("applyAustriaTourPackage", true);
                                    break;
                                case GERMANY_TOUR:
                                    break;
                            }
                            params.put("applicantName", name);
                            params.put("applicantEmail", email);
                            params.put("applicantContactNum", contact);
                            params.put("applicantNumPerson", no_of_ppl);
                            params.put("applicantRemarks", remarks);
                            new SubmitEuropeTourForm(EuropeToursFragment.this, params, tourType).execute();

                        }
                    });
                } else apply_dialog.show();
            }
        });
    }
}
