package com.visionxoft.abacus.rehmantravel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

public class ContactUsFragment extends Fragment {
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle("Contact Us");
        mainActivity.setToolbarSubTitle(null);

        final TextView tv_rt_full_address = (TextView) rootView.findViewById(R.id.tv_rt_full_address);
        final TextView tv_rt_phone_number = (TextView) rootView.findViewById(R.id.tv_rt_phone_number);
        final TextView tv_rt_email_address = (TextView) rootView.findViewById(R.id.tv_rt_email_address);
        final TextView tv_rt_addr_khi = (TextView) rootView.findViewById(R.id.tv_rt_addr_khi);
        final TextView tv_rt_ph_khi = (TextView) rootView.findViewById(R.id.tv_rt_ph_khi);
        final TextView tv_rt_addr_lhr = (TextView) rootView.findViewById(R.id.tv_rt_addr_lhr);
        final TextView tv_rt_ph_lhr = (TextView) rootView.findViewById(R.id.tv_rt_ph_lhr);
        final TextView tv_rt_addr_psh = (TextView) rootView.findViewById(R.id.tv_rt_addr_psh);
        final TextView tv_rt_ph_psh = (TextView) rootView.findViewById(R.id.tv_rt_ph_psh);
        final TextView tv_rt_addr_att = (TextView) rootView.findViewById(R.id.tv_rt_addr_att);
        final TextView tv_rt_ph_att = (TextView) rootView.findViewById(R.id.tv_rt_ph_att);
        final TextView tv_rt_addr_rwp = (TextView) rootView.findViewById(R.id.tv_rt_addr_rwp);
        final TextView tv_rt_ph_rwp = (TextView) rootView.findViewById(R.id.tv_rt_ph_rwp);
        final TextView tv_rt_addr_swt = (TextView) rootView.findViewById(R.id.tv_rt_addr_swt);
        final TextView tv_rt_ph_swt = (TextView) rootView.findViewById(R.id.tv_rt_ph_swt);
        final TextView tv_rt_addr_isb = (TextView) rootView.findViewById(R.id.tv_rt_addr_isb);
        final TextView tv_rt_ph_isb = (TextView) rootView.findViewById(R.id.tv_rt_ph_isb);

        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PhoneFunctionality.copyToClipboard(getContext(), "RT", ((TextView) v).getText().toString());
                PhoneFunctionality.makeToast(getActivity(), getString(R.string.copied_to_clipboard));
                return true;
            }
        };

        tv_rt_full_address.setOnLongClickListener(longClickListener);
        tv_rt_phone_number.setOnLongClickListener(longClickListener);
        tv_rt_email_address.setOnLongClickListener(longClickListener);
        tv_rt_addr_khi.setOnLongClickListener(longClickListener);
        tv_rt_ph_khi.setOnLongClickListener(longClickListener);
        tv_rt_addr_lhr.setOnLongClickListener(longClickListener);
        tv_rt_ph_lhr.setOnLongClickListener(longClickListener);
        tv_rt_addr_psh.setOnLongClickListener(longClickListener);
        tv_rt_ph_psh.setOnLongClickListener(longClickListener);
        tv_rt_addr_att.setOnLongClickListener(longClickListener);
        tv_rt_ph_att.setOnLongClickListener(longClickListener);
        tv_rt_addr_rwp.setOnLongClickListener(longClickListener);
        tv_rt_ph_rwp.setOnLongClickListener(longClickListener);
        tv_rt_addr_swt.setOnLongClickListener(longClickListener);
        tv_rt_ph_swt.setOnLongClickListener(longClickListener);
        tv_rt_addr_isb.setOnLongClickListener(longClickListener);
        tv_rt_ph_isb.setOnLongClickListener(longClickListener);

        return rootView;
    }
}
