package com.visionxoft.abacus.rehmantravel.fragment;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.VisaStudyPackageAdapter;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.task.SubmitPackageForm;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import paul.arian.fileselector.FileSelectionActivity;

public class VisaStudyPackagesFragment extends Fragment {

    private MainActivity mainActivity;
    private int REQUEST_CODE = 0;
    private String attach_docs = "NO", visa_country_id;
    private TextView attach_doc_names;
    private ArrayList<File> attach_files;
    public Dialog apply_dialog;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        visa_country_id = (String) IntentHelper.getObjectForKey("visa_country_id");

        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.visa_study_title));
        mainActivity.setToolbarSubTitle((String) IntentHelper.getObjectForKey("visa_country_name"));

        View rootView = inflater.inflate(R.layout.fragment_visa_study_packages, container, false);

        List<ImageTag> imageTags = (List<ImageTag>) IntentHelper.getObjectForKey("imageTags");
        if (imageTags != null) {
            final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager_visa_study);
            CirclePageIndicator indicator = (CirclePageIndicator) rootView.findViewById(R.id.view_pager_visa_study_indicator);

            final VisaStudyPackageAdapter packageAdapter = new VisaStudyPackageAdapter(this,
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

    public void createVisaApplyDialog() {

        apply_dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_visa_study_apply, Gravity.CENTER);
        apply_dialog.show();

        // region Find views
        final EditText visa_study_name = (EditText) apply_dialog.findViewById(R.id.visa_study_name);
        final EditText visa_study_qual = (EditText) apply_dialog.findViewById(R.id.visa_study_qual);
        final EditText visa_study_cgpa = (EditText) apply_dialog.findViewById(R.id.visa_study_cgpa);
        final EditText visa_study_uni = (EditText) apply_dialog.findViewById(R.id.visa_study_uni);
        final EditText visa_study_email = (EditText) apply_dialog.findViewById(R.id.visa_study_email);
        final EditText visa_study_contact = (EditText) apply_dialog.findViewById(R.id.visa_study_contact);
        final EditText visa_study_course = (EditText) apply_dialog.findViewById(R.id.visa_study_course);
        final EditText visa_study_desc = (EditText) apply_dialog.findViewById(R.id.visa_study_desc);
        final View btn_visa_study_back = apply_dialog.findViewById(R.id.btn_visa_study_back);
        final View btn_visa_study_submit = apply_dialog.findViewById(R.id.btn_visa_study_submit);
        final LinearLayout visa_study_rd_grp = (LinearLayout) apply_dialog.findViewById(R.id.visa_study_rd_grp);
        final RadioButton rb_int_foundation = (RadioButton) apply_dialog.findViewById(R.id.rb_int_foundation);
        final RadioButton rb_int_diploma = (RadioButton) apply_dialog.findViewById(R.id.rb_int_diploma);
        final RadioButton rb_int_bachelor = (RadioButton) apply_dialog.findViewById(R.id.rb_int_bachelor);
        final RadioButton rb_int_masters = (RadioButton) apply_dialog.findViewById(R.id.rb_int_masters);
        final RadioButton rb_int_phd = (RadioButton) apply_dialog.findViewById(R.id.rb_int_phd);
        final Spinner visa_study_city = (Spinner) apply_dialog.findViewById(R.id.visa_study_city_spinner);
        final View btn_visa_study_choose_file = apply_dialog.findViewById(R.id.btn_visa_study_choose_file);
        final CheckBox attach_doc_cb = (CheckBox) apply_dialog.findViewById(R.id.attach_doc_cb);
        final View visa_study_name_clear = apply_dialog.findViewById(R.id.visa_study_name_clear);
        final View visa_study_qual_clear = apply_dialog.findViewById(R.id.visa_study_qual_clear);
        final View visa_study_cgpa_clear = apply_dialog.findViewById(R.id.visa_study_cgpa_clear);
        final View visa_study_uni_clear = apply_dialog.findViewById(R.id.visa_study_uni_clear);
        final View visa_study_email_clear = apply_dialog.findViewById(R.id.visa_study_email_clear);
        final View visa_study_contact_clear = apply_dialog.findViewById(R.id.visa_study_contact_clear);
        final View visa_study_course_clear = apply_dialog.findViewById(R.id.visa_study_course_clear);
        final View visa_study_desc_clear = apply_dialog.findViewById(R.id.visa_study_desc_clear);
        attach_doc_names = (TextView) apply_dialog.findViewById(R.id.attach_doc_names);
        // endregion

        // region Clear editText values
        btn_visa_study_choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mainActivity, FileSelectionActivity.class), REQUEST_CODE);
            }
        });

        visa_study_name_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visa_study_name.setText("");
            }
        });

        visa_study_qual_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visa_study_qual.setText("");
            }
        });

        visa_study_cgpa_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visa_study_cgpa.setText("");
            }
        });

        visa_study_uni_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visa_study_uni.setText("");
            }
        });

        visa_study_email_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visa_study_email.setText("");
            }
        });

        visa_study_contact_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visa_study_contact.setText("");
            }
        });

        visa_study_course_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visa_study_course.setText("");
            }
        });

        visa_study_desc_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visa_study_desc.setText("");
            }
        });
        // endregion

        // region Manage radio buttons
        rb_int_foundation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageRadioButtons(visa_study_rd_grp, v);
            }
        });

        rb_int_diploma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageRadioButtons(visa_study_rd_grp, v);
            }
        });

        rb_int_bachelor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageRadioButtons(visa_study_rd_grp, v);
            }
        });

        rb_int_masters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageRadioButtons(visa_study_rd_grp, v);
            }
        });

        rb_int_phd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageRadioButtons(visa_study_rd_grp, v);
            }
        });
        // endregion

        attach_doc_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btn_visa_study_choose_file.setVisibility(View.VISIBLE);
                    attach_doc_names.setVisibility(View.VISIBLE);
                    attach_docs = "YES";
                } else {
                    btn_visa_study_choose_file.setVisibility(View.GONE);
                    attach_doc_names.setVisibility(View.GONE);
                    attach_files = null;
                    attach_doc_names.setText(getString(R.string.no_file_selected));
                    attach_docs = "NO";
                }
            }
        });

        btn_visa_study_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply_dialog.dismiss();
            }
        });

        btn_visa_study_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = null;
                String name = visa_study_name.getText().toString();
                String qualification = visa_study_qual.getText().toString();
                String cgpa = visa_study_cgpa.getText().toString();
                String uni = visa_study_uni.getText().toString();
                String email = visa_study_email.getText().toString();
                String contact = visa_study_contact.getText().toString();
                String course = visa_study_course.getText().toString();
                String description = visa_study_desc.getText().toString();

                if (name.equals("")) editText = visa_study_name;
                else if (qualification.equals("")) editText = visa_study_qual;
                else if (cgpa.equals("")) editText = visa_study_cgpa;
                else if (uni.equals("")) editText = visa_study_uni;
                else if (email.equals("")) editText = visa_study_email;
                else if (contact.equals("")) editText = visa_study_contact;
                else if (course.equals("")) editText = visa_study_course;
                else if (description.equals("")) editText = visa_study_desc;

                if (editText != null) {
                    editText.setError(getString(R.string.error_field_required));
                    editText.requestFocus();
                } else {
                    if (!FormatString.isEmailValid(email)) {
                        visa_study_email.setError(getString(R.string.error_invalid_email));
                        visa_study_email.requestFocus();
                        return;
                    }
                    if (!FormatString.isContactValid(contact)) {
                        visa_study_contact.setError(getString(R.string.error_invalid_number));
                        visa_study_contact.requestFocus();
                        return;
                    }
                    final Hashtable<String, Object> params = new Hashtable<>();
                    params.put("visaRequestSubmit", "true");
                    params.put("fullname", name);
                    params.put("qualification", qualification);
                    params.put("percentage", cgpa);
                    params.put("universityname", uni);
                    params.put("email", email);
                    params.put("interest", getCheckedRadioButton(visa_study_rd_grp));
                    params.put("coursename", course);
                    params.put("city", visa_study_city.getSelectedItem().toString());
                    params.put("description", description);
                    params.put("country", visa_country_id);
                    params.put("mobilenumber", contact);
                    params.put("docs", attach_docs);
                    if (attach_files == null) params.put("userfile[]", "");
                    else params.put("userfile[]", attach_files);
                    new SubmitPackageForm(VisaStudyPackagesFragment.this, params, SubmitPackageForm.STUDY_VISA).execute();

                }
            }
        });
    }

    private void manageRadioButtons(LinearLayout parent_layout, View checked_view) {
        LinearLayout ll1 = (LinearLayout) parent_layout.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) parent_layout.getChildAt(1);
        for (int i = 0; i < ll1.getChildCount(); i++)
            ((RadioButton) ll1.getChildAt(i)).setChecked(false);
        for (int i = 0; i < ll2.getChildCount(); i++)
            ((RadioButton) ll2.getChildAt(i)).setChecked(false);
        ((RadioButton) checked_view).setChecked(true);
    }

    private String getCheckedRadioButton(LinearLayout parent_layout) {
        LinearLayout ll1 = (LinearLayout) parent_layout.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) parent_layout.getChildAt(1);
        for (int i = 0; i < ll1.getChildCount(); i++) {
            if (((RadioButton) ll1.getChildAt(i)).isChecked())
                return ((RadioButton) ll1.getChildAt(i)).getText().toString();
        }
        for (int i = 0; i < ll2.getChildCount(); i++) {
            if (((RadioButton) ll2.getChildAt(i)).isChecked())
                return ((RadioButton) ll2.getChildAt(i)).getText().toString();
        }
        return "";
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                attach_files = (ArrayList<File>) data.getSerializableExtra(FileSelectionActivity.FILES_TO_UPLOAD);
                if (attach_files.size() == 0)
                    attach_doc_names.setText(getString(R.string.no_file_selected));
                else if (attach_files.size() == 1)
                    attach_doc_names.setText(attach_files.get(0).getName() + " selected");
                else if (attach_files.size() > 0)
                    attach_doc_names.setText(attach_files.size() + " files selected");
            }
        }
    }
}
