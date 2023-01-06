package com.visionxoft.abacus.rehmantravel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.FranchiseReference;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.FragmentHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.List;

public class FranchiseReferenceFragment extends Fragment {

    private int current_pos = 0;
    private FranchiseReference[] franRefArray;
    private View rootView, fran_ref_input, btn_fran_ref_next, btn_fran_ref_prev, btn_fran_ref_continue;
    private TextView fran_ref_no;
    private EditText fran_ref_name, fran_ref_relation, fran_ref_phone, fran_ref_mobile, fran_ref_email,
            fran_ref_nic, fran_ref_age, fran_ref_job, fran_ref_qual, focused_et;
    private Spinner fran_ref_nationality;
    private List<String> countries;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.franchise_title));
        mainActivity.setToolbarSubTitle(getString(R.string.franchise_subtitle_ref));

        if (rootView != null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_franchise_reference, container, false);
        PhoneFunctionality.makeToast(mainActivity, "Please provide three references", true);
        countries = (List<String>) IntentHelper.getObjectForKey("countries_names");

        // region Find Views
        fran_ref_no = (TextView) rootView.findViewById(R.id.fran_ref_no);
        fran_ref_name = (EditText) rootView.findViewById(R.id.fran_ref_name);
        fran_ref_relation = (EditText) rootView.findViewById(R.id.fran_ref_relation);
        fran_ref_phone = (EditText) rootView.findViewById(R.id.fran_ref_phone);
        fran_ref_mobile = (EditText) rootView.findViewById(R.id.fran_ref_mobile);
        fran_ref_email = (EditText) rootView.findViewById(R.id.fran_ref_email);
        fran_ref_nic = (EditText) rootView.findViewById(R.id.fran_ref_nic);
        fran_ref_age = (EditText) rootView.findViewById(R.id.fran_ref_age);
        fran_ref_job = (EditText) rootView.findViewById(R.id.fran_ref_job);
        fran_ref_qual = (EditText) rootView.findViewById(R.id.fran_ref_qual);
        fran_ref_nationality = (Spinner) rootView.findViewById(R.id.fran_ref_nationality);
        View fran_ref_name_clear = rootView.findViewById(R.id.fran_ref_name_clear);
        View fran_ref_relation_clear = rootView.findViewById(R.id.fran_ref_relation_clear);
        View fran_ref_phone_clear = rootView.findViewById(R.id.fran_ref_phone_clear);
        View fran_ref_mobile_clear = rootView.findViewById(R.id.fran_ref_mobile_clear);
        View fran_ref_email_clear = rootView.findViewById(R.id.fran_ref_email_clear);
        View fran_ref_nic_clear = rootView.findViewById(R.id.fran_ref_nic_clear);
        View fran_ref_age_clear = rootView.findViewById(R.id.fran_ref_age_clear);
        View fran_ref_job_clear = rootView.findViewById(R.id.fran_ref_job_clear);
        View fran_ref_qual_clear = rootView.findViewById(R.id.fran_ref_qual_clear);
        btn_fran_ref_prev = rootView.findViewById(R.id.btn_fran_ref_prev);
        btn_fran_ref_next = rootView.findViewById(R.id.btn_fran_ref_next);
        btn_fran_ref_continue = rootView.findViewById(R.id.btn_fran_ref_continue);
        fran_ref_input = rootView.findViewById(R.id.fran_ref_input);
        //endregion

        // Init Views
        franRefArray = new FranchiseReference[3];
        fran_ref_nationality.setAdapter(new ArrayAdapter<>(mainActivity,
                android.R.layout.simple_list_item_1, countries));
        btn_fran_ref_prev.setEnabled(false);
        btn_fran_ref_continue.setEnabled(false);
        fran_ref_nic.addTextChangedListener(new TextWatcher() {

            private int old_count = 0;
            private boolean appendText = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                appendText = count >= old_count;
                old_count = count;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (appendText && (editable.length() == 5 || editable.length() == 13))
                    editable.append("-");
            }
        });

        // region Clear Edit Texts
        fran_ref_name_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_name.setText("");
            }
        });

        fran_ref_relation_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_relation.setText("");
            }
        });

        fran_ref_phone_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_phone.setText("");
            }
        });

        fran_ref_mobile_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_mobile.setText("");
            }
        });

        fran_ref_email_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_email.setText("");
            }
        });

        fran_ref_nic_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_nic.setText("");
            }
        });

        fran_ref_age_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_age.setText("");
            }
        });

        fran_ref_job_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_job.setText("");
            }
        });

        fran_ref_qual_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_ref_qual.setText("");
            }
        });
        //endregion

        // region Next Button
        btn_fran_ref_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_pos < franRefArray.length) {
                    FranchiseReference franRef = getFranchiseRefInput(true);
                    if (franRef != null) {
                        // Animation
                        YoYo.with(Techniques.SlideInRight).duration(400).playOn(fran_ref_input);
                        franRefArray[current_pos] = franRef;
                        // Increment
                        current_pos++;
                        // Views state
                        if (current_pos > 0) btn_fran_ref_prev.setEnabled(true);
                        if (current_pos >= franRefArray.length - 1) {
                            btn_fran_ref_next.setEnabled(false);
                            btn_fran_ref_continue.setEnabled(true);
                        }
                        // Display next reference if available
                        if (current_pos <= franRefArray.length) {
                            setFranchiseRefTitle();
                            if (franRefArray[current_pos] != null)
                                setFranchiseRefInput(franRefArray[current_pos]);
                            else
                                resetFranchiseInput();
                        }
                    } else {
                        // Animation
                        PhoneFunctionality.errorAnimation(v);
                    }
                }
            }
        });
        //endregion

        // region Previous Button
        btn_fran_ref_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_pos > 0) {
                    // Animation
                    YoYo.with(Techniques.SlideInLeft).duration(400).playOn(fran_ref_input);
                    // Save current traveler
                    franRefArray[current_pos] = getFranchiseRefInput(false);
                    // Decrement
                    current_pos--;
                    // Views state
                    if (current_pos == 0) btn_fran_ref_prev.setEnabled(false);
                    if (current_pos < franRefArray.length - 1) {
                        btn_fran_ref_next.setEnabled(true);
                        btn_fran_ref_continue.setEnabled(false);
                    }
                    // Display Previous traveler
                    setFranchiseRefTitle();
                    if (franRefArray[current_pos] != null)
                        setFranchiseRefInput(franRefArray[current_pos]);
                }
            }
        });
        //endregion


        // region Continue Button
        btn_fran_ref_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FranchiseReference franRef = getFranchiseRefInput(true);
                if (franRef == null) {
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }
                // Save Last Franchise Reference
                franRefArray[franRefArray.length - 1] = franRef;

                // Go to Employment/Business Record fragment
                IntentHelper.addObjectForKey(franRefArray, "franchise_references");
                FragmentHelper.replaceFragment(FranchiseReferenceFragment.this, new FranchiseRecordFragment(),
                        mainActivity.getString(R.string.franchise_rec_tag));

            }
        });
        //endregion

        return rootView;
    }

    // region Set Franchise Reference Title
    private void setFranchiseRefTitle() {
        String ref_no_title;
        switch (current_pos) {
            case 0:
                ref_no_title = "1st Reference";
                break;
            case 1:
                ref_no_title = "2nd Reference";
                break;
            default:
                ref_no_title = "3rd Reference";
                break;
        }
        fran_ref_no.setText(ref_no_title);
    }
    // endregion

    // region Get Franchise Reference Values
    private FranchiseReference getFranchiseRefInput(boolean showError) {

        String name = fran_ref_name.getText().toString();
        String relation = fran_ref_relation.getText().toString();
        String phone = fran_ref_phone.getText().toString();
        String mobile = fran_ref_mobile.getText().toString();
        String email = fran_ref_email.getText().toString();
        String nic = fran_ref_nic.getText().toString();
        String age = fran_ref_age.getText().toString();
        String job = fran_ref_job.getText().toString();
        String qualification = fran_ref_qual.getText().toString();
        if (showError) {
            focused_et = null;
            if (name.equals("")) focused_et = fran_ref_name;
            else if (relation.equals("")) focused_et = fran_ref_relation;
            else if (phone.equals("")) focused_et = fran_ref_phone;
            else if (mobile.equals("")) focused_et = fran_ref_mobile;
            else if (email.equals("")) focused_et = fran_ref_email;
            else if (nic.equals("")) focused_et = fran_ref_nic;
            else if (age.equals("")) focused_et = fran_ref_age;
            else if (job.equals("")) focused_et = fran_ref_job;
            else if (qualification.equals("")) focused_et = fran_ref_qual;

            if (focused_et != null) {
                focused_et.setError(getString(R.string.error_field_required));
                focused_et.requestFocus();
                return null;
            }
            if (!FormatString.isNameValid(name)) {
                fran_ref_name.setError(getString(R.string.error_invalid_name));
                fran_ref_name.requestFocus();
                return null;
            }
            if (!FormatString.isEmailValid(email)) {
                fran_ref_email.setError(getString(R.string.error_invalid_email));
                fran_ref_email.requestFocus();
                return null;
            }
            if (!FormatString.isContactValid(phone)) {
                fran_ref_phone.setError(getString(R.string.error_invalid_number));
                fran_ref_phone.requestFocus();
                return null;
            }
            if (!FormatString.isContactValid(mobile)) {
                fran_ref_mobile.setError(getString(R.string.error_invalid_number));
                fran_ref_mobile.requestFocus();
                return null;
            }
            if (fran_ref_nic.length() != 15) {
                fran_ref_nic.setError(getString(R.string.error_field_invalid));
                fran_ref_nic.requestFocus();
                return null;
            }
        } else if (focused_et != null) focused_et.setError(null);
        FranchiseReference franRef = new FranchiseReference();
        franRef.name = name;
        franRef.relation = relation;
        franRef.phone = phone;
        franRef.mobile = mobile;
        franRef.email = email;
        franRef.nic = nic;
        franRef.age = age;
        franRef.business = job;
        franRef.qualification = qualification;
        franRef.nationality = fran_ref_nationality.getSelectedItem().toString();
        return franRef;

    }
    // endregion

    // region Set Franchise Reference Values
    private void setFranchiseRefInput(FranchiseReference franRef) {
        fran_ref_name.setText(franRef.name);
        fran_ref_relation.setText(franRef.relation);
        fran_ref_phone.setText(franRef.phone);
        fran_ref_mobile.setText(franRef.mobile);
        fran_ref_email.setText(franRef.email);
        fran_ref_nic.setText(franRef.nic);
        fran_ref_age.setText(franRef.age);
        fran_ref_job.setText(franRef.business);
        fran_ref_qual.setText(franRef.qualification);
        fran_ref_nationality.setSelection(countries.indexOf(franRef.nationality));
    }
    // endregion

    // region Reset Franchise Reference Values
    private void resetFranchiseInput() {
        fran_ref_name.setText("");
        fran_ref_relation.setText("");
        fran_ref_phone.setText("");
        fran_ref_mobile.setText("");
        fran_ref_email.setText("");
        fran_ref_nic.setText("");
        fran_ref_age.setText("");
        fran_ref_job.setText("");
        fran_ref_qual.setText("");
        fran_ref_nationality.setSelection(0);
    }
    // endregion
}
