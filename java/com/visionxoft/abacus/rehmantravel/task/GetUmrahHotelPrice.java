package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;
import android.view.View;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.UmrahDesignFragment;
import com.visionxoft.abacus.rehmantravel.model.HotelPrice;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;

import java.util.Hashtable;

/**
 * AsyncTask to request umrah hotel price by check in and out dates
 */
public class GetUmrahHotelPrice extends AsyncTask<String, Void, Integer> {
    private MainActivity activity;
    private UmrahDesignFragment fragment;
    private int hotelType;
    private HotelPrice hotelPrice;

    /**
     * Get Umrah Hotel Prices
     *
     * @param fragment  Parent Fragment Class
     * @param hotelType Type of hotel; 1=1st Makkah hotel, 2=Madina Hotel, 3=2nd Makkah Hotel
     */
    public GetUmrahHotelPrice(UmrahDesignFragment fragment, int hotelType) {
        this.fragment = fragment;
        this.activity = (MainActivity) fragment.getActivity();
        this.hotelType = hotelType;
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
            String url;
            if (hotelType != fragment.MADINA) {
                post_params.put("mkHotelId", params[0]);
                post_params.put("mkcheckIn", params[1]);
                post_params.put("mkcheckOut", params[2]);
                url = activity.getString(R.string.get_makkah_price);
            } else {
                post_params.put("mdHotelId", params[0]);
                post_params.put("mdcheckIn", params[1]);
                post_params.put("mdcheckOut", params[2]);
                url = activity.getString(R.string.get_madina_price);
            }

            String responseContent = sh.postCall(url, post_params);
            if (responseContent != null && sh.responseCode == 200) {
                hotelPrice = JsonConverter.parseJsonToHotelPrice(responseContent);
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
            if (feedback == 0) activity.createNoNetworkDialog();
            else if (feedback == 1 && hotelPrice != null) {
                if (hotelType == fragment.MAKKAH1) fragment.makkah1Price = hotelPrice;
                else if (hotelType == fragment.MADINA) fragment.madinaPrice = hotelPrice;
                else fragment.makkah2Price = hotelPrice;
                fragment.calculateRoomsAndPrices(hotelType);
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}