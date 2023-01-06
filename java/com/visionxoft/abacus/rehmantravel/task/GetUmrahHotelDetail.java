package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.UmrahDesignFragment;
import com.visionxoft.abacus.rehmantravel.model.MadinaHotel;
import com.visionxoft.abacus.rehmantravel.model.MakkahHotel;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask to request umrah hotel details from term used
 */
public class GetUmrahHotelDetail extends AsyncTask<String, Void, Integer> {
    private MainActivity activity;
    private UmrahDesignFragment fragment;
    private List<MakkahHotel> makkahHotels;
    private List<MadinaHotel> madinaHotels;
    private List<String> list;
    private MultiAutoCompleteTextView mactv;
    private int hotelType;

    /**
     * Get Umrah Hotel Details
     *
     * @param fragment  Parent Fragment Class
     * @param mactv     MultiAutoCompleteTextView to get input and display results
     * @param hotelType Type of hotel; 1=1st Makkah hotel, 2=Madina Hotel, 3=2nd Makkah Hotel
     */
    public GetUmrahHotelDetail(UmrahDesignFragment fragment, MultiAutoCompleteTextView mactv, int hotelType) {
        this.fragment = fragment;
        this.activity = (MainActivity) fragment.getActivity();
        this.mactv = mactv;
        this.hotelType = hotelType;
        this.list = new ArrayList<>();
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
            OKHttp sh = new OKHttp();
            String url;
            if (hotelType == fragment.MADINA) url = activity.getString(R.string.get_madina_hotels);
            else url = activity.getString(R.string.get_makkah_hotels);
            String responseContent = sh.getCall(url + params[0]);
            if (responseContent != null && sh.responseCode == 200) {
                if (hotelType == fragment.MADINA) {
                    madinaHotels = JsonConverter.parseJsonToMadinaHotel(responseContent);
                    for (MadinaHotel hotel : madinaHotels) list.add(hotel.label);
                } else {
                    makkahHotels = JsonConverter.parseJsonToMakkahHotel(responseContent);
                    for (MakkahHotel hotel : makkahHotels) list.add(hotel.label);
                }
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
            else if (feedback == 1 && list != null && list.size() > 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_dropdown_item_1line, list);
                mactv.setAdapter(adapter);
                mactv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                mactv.animate();
                mactv.showDropDown();

                if (hotelType == fragment.MAKKAH1) fragment.makkah1Hotels = makkahHotels;
                else if (hotelType == fragment.MADINA) fragment.madinaHotels = madinaHotels;
                else fragment.makkah2Hotels = makkahHotels;
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}