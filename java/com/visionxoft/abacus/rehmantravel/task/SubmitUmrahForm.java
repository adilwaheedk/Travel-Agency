package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;
import android.view.View;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.UmrahDesignFragment;
import com.visionxoft.abacus.rehmantravel.model.UmrahFormDetail;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;

/**
 * AsyncTask to submit umrah booking details
 */
public class SubmitUmrahForm extends AsyncTask<String, Void, Integer> {

    private MainActivity activity;
    private UmrahDesignFragment fragment;
    private UmrahFormDetail umrahFormDetail;
    private String form_html;

    /**
     * Submit Umrah Booking
     *
     * @param fragment        Parent Fragment Class
     * @param umrahFormDetail Umrah Form detail model
     */
    public SubmitUmrahForm(UmrahDesignFragment fragment, UmrahFormDetail umrahFormDetail) {
        this.fragment = fragment;
        this.activity = (MainActivity) fragment.getActivity();
        this.umrahFormDetail = umrahFormDetail;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.action_progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(activity)) return 0;
            Hashtable<String, Object> post_params = new Hashtable<>();
            OKHttp sh = new OKHttp();
            post_params.put("user_name", umrahFormDetail.user_name);
            post_params.put("user_email", umrahFormDetail.user_email);
            post_params.put("user_contact_no", umrahFormDetail.user_contact_no);
            post_params.put("city_name", umrahFormDetail.city_name);
            post_params.put("mkHotelId", umrahFormDetail.mkHotelId);
            post_params.put("mkRoBB", umrahFormDetail.mkRoBB);
            post_params.put("mdRoBB", umrahFormDetail.mdRoBB);
            post_params.put("mk_RoBB", umrahFormDetail.mk_RoBB);
            post_params.put("jdRoBB", umrahFormDetail.jdRoBB);
            post_params.put("jdAdd", umrahFormDetail.jdAdd);
            post_params.put("mkhotelTypeId", umrahFormDetail.mkhotelTypeId);
            post_params.put("MDhotelType", umrahFormDetail.MDhotelType);
            post_params.put("MK_hotelType", umrahFormDetail.MK_hotelType);
            post_params.put("JDhotelType", umrahFormDetail.JDhotelType);
            post_params.put("adults", umrahFormDetail.adults);
            post_params.put("children", umrahFormDetail.children);
            post_params.put("infant", umrahFormDetail.infant);
            post_params.put("mkInputId", umrahFormDetail.mkInputId);
            post_params.put("mdInputId", umrahFormDetail.mdInputId);
            post_params.put("mk_InputId", umrahFormDetail.mk_InputId);
            post_params.put("jdInputId", umrahFormDetail.jdInputId);
            post_params.put("mkcheckIn", umrahFormDetail.mkcheckIn);
            post_params.put("mkcheckOut", umrahFormDetail.mkcheckOut);
            post_params.put("md_checkIn", umrahFormDetail.md_checkIn);
            post_params.put("md_checkOut", umrahFormDetail.md_checkOut);
            post_params.put("mk_checkIn", umrahFormDetail.mk_checkIn);
            post_params.put("mk_checkOut", umrahFormDetail.mk_checkOut);
            post_params.put("jd_checkIn", umrahFormDetail.jd_checkIn);
            post_params.put("jd_checkOut", umrahFormDetail.jd_checkOut);
            post_params.put("mkDoubleroom", umrahFormDetail.mkDoubleroom);
            post_params.put("mkTripleroom", umrahFormDetail.mkTripleroom);
            post_params.put("mkQuadroom", umrahFormDetail.mkQuadroom);
            post_params.put("mktotal_nights", umrahFormDetail.mktotal_nights);
            post_params.put("remarks", umrahFormDetail.remarks);
            post_params.put("mdDoubleroom", umrahFormDetail.mdDoubleroom);
            post_params.put("mdTripleroom", umrahFormDetail.mdTripleroom);
            post_params.put("mdQuadroom", umrahFormDetail.mdQuadroom);
            post_params.put("md_total_nights", umrahFormDetail.md_total_nights);
            post_params.put("mk_Double_room", umrahFormDetail.mk_Double_room);
            post_params.put("mk_Triple_room", umrahFormDetail.mk_Triple_room);
            post_params.put("mk_Quad_room", umrahFormDetail.mk_Quad_room);
            post_params.put("mk_total_nights", umrahFormDetail.mk_total_nights);
            post_params.put("jd_Doubleroom", umrahFormDetail.jd_Doubleroom);
            post_params.put("jd_Tripleroom", umrahFormDetail.jd_Tripleroom);
            post_params.put("jd_Quadroom", umrahFormDetail.jd_Quadroom);
            post_params.put("jd_total_nights", umrahFormDetail.jd_total_nights);
            post_params.put("TotalNRoom", umrahFormDetail.TotalNRoom);
            post_params.put("total_amount", umrahFormDetail.total_amount);
            post_params.put("total_nights", umrahFormDetail.total_nights);
            post_params.put("sector", umrahFormDetail.sector);
            post_params.put("vehicleId", umrahFormDetail.vehicleId);
            post_params.put("MKHootelTypeId", umrahFormDetail.MKHootelTypeId);
            post_params.put("mdHotelId", umrahFormDetail.mdHotelId);
            post_params.put("mk_HotelId", umrahFormDetail.mk_HotelId);
            post_params.put("jdHotelId", umrahFormDetail.jdHotelId);
            post_params.put("visa", umrahFormDetail.visa);
            post_params.put("db_visa_price", umrahFormDetail.db_visa_price);
            post_params.put("mktotal_grant_price", umrahFormDetail.mktotal_grant_price);
            post_params.put("mdtotal_grant_price", umrahFormDetail.mdtotal_grant_price);
            post_params.put("mk_total_grant_price", umrahFormDetail.mk_total_grant_price);
            post_params.put("total_grant_vehicle_price", umrahFormDetail.total_grant_vehicle_price);
            String responseContent = sh.postCall(activity.getString(R.string.submit_umrah_form), post_params);
            if (responseContent != null && sh.responseCode == 200) {
                if (responseContent.length() > 0) {
                    form_html = responseContent;
                    return 1;
                } else return 3;
            } else return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        activity.action_progress.setVisibility(View.GONE);
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            activity.action_progress.setVisibility(View.GONE);
            if (feedback == 0) activity.createNoNetworkDialog();
            else if (feedback == 1 && form_html != null) {
                PhoneFunctionality.vibrateMobile(activity);
                fragment.showUmrahDialog(form_html);
            } else if (feedback == 3)
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.bad_response));
            else if (feedback == 2)
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));

        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}