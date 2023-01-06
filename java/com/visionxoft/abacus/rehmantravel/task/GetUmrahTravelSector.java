package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;
import android.view.View;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.UmrahDesignFragment;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;

import java.util.Hashtable;

/**
 * AsyncTask to request umrah travel sector by vehicle, sector ids
 */
public class GetUmrahTravelSector extends AsyncTask<String, Void, Integer> {
    private MainActivity activity;
    private UmrahDesignFragment fragment;
    private String travelPrice;

    /**
     * Get Umrah traveling expenses
     *
     * @param fragment Parent Fragment Class
     */
    public GetUmrahTravelSector(UmrahDesignFragment fragment) {
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
            if (!ConnectionHelper.isConnectedToInternet(activity)) return 0;
            Hashtable<String, Object> post_params = new Hashtable<>();
            OKHttp http = new OKHttp();
            post_params.put("vehicleId", params[0]);
            post_params.put("sectorId", params[1]);
            String responseContent = http.postCall(activity.getString(R.string.get_vehicle_id), post_params);
            if (responseContent != null && http.responseCode == 200) {
                travelPrice = JsonConverter.getValueFromJSONByKey(responseContent, "Price");
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
            else if (feedback == 1 && travelPrice != null) fragment.setTravelerAmount(travelPrice);
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}