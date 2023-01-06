package com.visionxoft.abacus.rehmantravel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.FranchisePersonal;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.FragmentHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.List;

public class FranchisePersonalFragment extends Fragment {

    private MainActivity mainActivity;
    private View rootView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.franchise_title));
        mainActivity.setToolbarSubTitle(getString(R.string.franchise_subtitle_per));

        if (rootView != null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_franchise_personal, container, false);

        List<String> countries = (List<String>) IntentHelper.getObjectForKey("countries_names");

        // region Find Views
        final EditText fran_per_name = (EditText) rootView.findViewById(R.id.fran_per_name);
        final EditText fran_per_addr = (EditText) rootView.findViewById(R.id.fran_per_addr);
        final EditText fran_per_phone = (EditText) rootView.findViewById(R.id.fran_per_phone);
        final EditText fran_per_mobile = (EditText) rootView.findViewById(R.id.fran_per_mobile);
        final EditText fran_per_email = (EditText) rootView.findViewById(R.id.fran_per_email);
        final EditText fran_per_nic = (EditText) rootView.findViewById(R.id.fran_per_nic);
        final EditText fran_per_age = (EditText) rootView.findViewById(R.id.fran_per_age);
        final EditText fran_per_qual = (EditText) rootView.findViewById(R.id.fran_per_qual);
        final Spinner fran_per_nationality = (Spinner) rootView.findViewById(R.id.fran_per_nationality);
        final Spinner fran_per_marital = (Spinner) rootView.findViewById(R.id.fran_per_marital);
        final EditText fran_per_kids = (EditText) rootView.findViewById(R.id.fran_per_kids);
        View fran_per_name_clear = rootView.findViewById(R.id.fran_per_name_clear);
        View fran_per_addr_clear = rootView.findViewById(R.id.fran_per_addr_clear);
        View fran_per_phone_clear = rootView.findViewById(R.id.fran_per_phone_clear);
        View fran_per_mobile_clear = rootView.findViewById(R.id.fran_per_mobile_clear);
        View fran_per_email_clear = rootView.findViewById(R.id.fran_per_email_clear);
        View fran_per_nic_clear = rootView.findViewById(R.id.fran_per_nic_clear);
        View fran_per_age_clear = rootView.findViewById(R.id.fran_per_age_clear);
        View fran_per_qual_clear = rootView.findViewById(R.id.fran_per_qual_clear);
        View btn_fran_per_continue = rootView.findViewById(R.id.btn_fran_per_continue);
        //endregion

        fran_per_nic.addTextChangedListener(new TextWatcher() {

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


        fran_per_nationality.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, countries));
        fran_per_marital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) fran_per_kids.setVisibility(View.GONE);
                else fran_per_kids.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // region Clear Edit Texts
        fran_per_name_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_per_name.setText("");
            }
        });

        fran_per_addr_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_per_addr.setText("");
            }
        });

        fran_per_phone_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_per_phone.setText("");
            }
        });

        fran_per_mobile_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_per_mobile.setText("");
            }
        });

        fran_per_email_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_per_email.setText("");
            }
        });

        fran_per_nic_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_per_nic.setText("");
            }
        });

        fran_per_age_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_per_age.setText("");
            }
        });

        fran_per_qual_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fran_per_qual.setText("");
            }
        });
        //endregion

        btn_fran_per_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = null;
                String name = fran_per_name.getText().toString();
                String address = fran_per_addr.getText().toString();
                String phone = fran_per_phone.getText().toString();
                String mobile = fran_per_mobile.getText().toString();
                String email = fran_per_email.getText().toString();
                String nic = fran_per_nic.getText().toString();
                String age = fran_per_age.getText().toString();
                String kids = fran_per_kids.getText().toString();
                String qualification = fran_per_qual.getText().toString();

                if (name.equals("")) editText = fran_per_name;
                else if (address.equals("")) editText = fran_per_addr;
                else if (phone.equals("")) editText = fran_per_phone;
                else if (mobile.equals("")) editText = fran_per_mobile;
                else if (email.equals("")) editText = fran_per_email;
                else if (nic.equals("")) editText = fran_per_nic;
                else if (age.equals("")) editText = fran_per_age;
                else if (fran_per_marital.getSelectedItemPosition() == 1 && kids.equals(""))
                    editText = fran_per_kids;
                else if (qualification.equals("")) editText = fran_per_qual;

                if (editText != null) {
                    editText.setError(getString(R.string.error_field_required));
                    editText.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }
                if (!FormatString.isNameValid(name)) {
                    fran_per_name.setError(getString(R.string.error_invalid_name));
                    fran_per_name.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }
                if (!FormatString.isEmailValid(email)) {
                    fran_per_email.setError(getString(R.string.error_invalid_email));
                    fran_per_email.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }
                if (!FormatString.isContactValid(phone)) {
                    fran_per_phone.setError(getString(R.string.error_invalid_number));
                    fran_per_phone.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }
                if (!FormatString.isContactValid(mobile)) {
                    fran_per_mobile.setError(getString(R.string.error_invalid_number));
                    fran_per_mobile.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }
                if (fran_per_nic.length() != 15) {
                    fran_per_nic.setError(getString(R.string.error_field_invalid));
                    fran_per_nic.requestFocus();
                    PhoneFunctionality.errorAnimation(v);
                    return;
                }

                FranchisePersonal franPer = new FranchisePersonal();
                franPer.name = name;
                franPer.address = address;
                franPer.phone = phone;
                franPer.mobile = mobile;
                franPer.email = email;
                franPer.nic = nic;
                franPer.age = age;
                franPer.qualification = qualification;
                franPer.nationality = fran_per_nationality.getSelectedItem().toString();
                franPer.marital_status = fran_per_marital.getSelectedItem().toString();
                franPer.no_of_kids = kids;

                // Go to Franchise References fragment
                IntentHelper.addObjectForKey(franPer, "franchise_personal");
                FragmentHelper.replaceFragment(FranchisePersonalFragment.this, new FranchiseReferenceFragment(),
                        mainActivity.getString(R.string.franchise_ref_tag));

            }
        });
        return rootView;
    }
}
