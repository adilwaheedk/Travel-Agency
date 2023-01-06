package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;
import android.view.View;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.UmrahDesignFragment;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;

import java.util.Hashtable;

/**
 * AsyncTask to request umrah visa price by check in and out dates
 */
public class GetUmrahVisaPrice extends AsyncTask<String, Void, Integer> {
    private MainActivity activity;
    private UmrahDesignFragment fragment;
    private String visaPriceStr;

    /**
     * Get Umrah Visa Prices
     *
     * @param fragment Parent Fragment Class
     */
    public GetUmrahVisaPrice(UmrahDesignFragment fragment) {
        this.fragment = fragment;
        this.activity = (MainActivity) fragment.getActivity();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.action_progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            Hashtable<String, Object> post_params = new Hashtable<>();
            OKHttp sh = new OKHttp();
            post_params.put("mkcheckIn", params[0]);
            post_params.put("mkcheckOut", params[1]);
            String responseContent = sh.postCall(activity.getString(R.string.get_visa_price), post_params);
            if (responseContent != null && sh.responseCode == 200) {
                visaPriceStr = JsonConverter.getValueFromJSON(responseContent, "visa_price");
                return 1;
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
            if (feedback == 1 && visaPriceStr != null) {
                fragment.visaPrice = Integer.parseInt(visaPriceStr);
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}