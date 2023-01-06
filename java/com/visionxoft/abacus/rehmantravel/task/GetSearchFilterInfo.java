package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;

import com.google.common.collect.HashBiMap;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.SearchFragment;
import com.visionxoft.abacus.rehmantravel.model.AirlineName;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.*;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * AsyncTask to collect required information like airline codes, phone of stops
 * from all Itineraries for flights filtering
 */
public class GetSearchFilterInfo extends AsyncTask<Void, Void, Integer> {
    private SearchFragment fragment;
    private MainActivity mainActivity;
    private List<PricedItinerary> list;
    private HashBiMap<String, String> airlines;
    private HashBiMap<String, String> low_fare;
    private HashBiMap<Integer, String> stops_str;
    private HashMap<Integer, List<Object>> listMap;

    /**
     * Get required information from all Itineraries for flights filtering
     *
     * @param fragment Parent Fragment Class
     * @param list     List of all Itineraries
     */
    public GetSearchFilterInfo(SearchFragment fragment, List<PricedItinerary> list) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.list = list;
        this.airlines = HashBiMap.create();
        this.low_fare = HashBiMap.create();
        this.stops_str = HashBiMap.create();
        this.listMap = new HashMap<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;

            // Collect All Airline Codes
            List<String> airline_codes = new ArrayList<>();
            List<Object> listProperties;
            PricedItinerary _PricedItinerary;
            List<OriginDestinationOption> odOptions;
            OriginDestinationOption odOption;

            for (int i = 0; i < list.size(); i++) {
                _PricedItinerary = list.get(i);

                // Single Object contains Airline Codes and No of Stops of each flight segment
                listProperties = new ArrayList<>();

                odOptions = _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption;
                for (int j = 0; j < odOptions.size(); j++) {
                    odOption = odOptions.get(j);
                    int stops = Integer.valueOf(odOption._attr.StopQuantity);
                    if (!listProperties.contains(stops)) listProperties.add(stops);
                }
                listMap.put(i, listProperties);

                String first_airline_code = _PricedItinerary._AirItinerary._OriginDestinationOptions
                        ._OriginDestinationOption.get(0)._OperatingAirline.Code;

                if (!airline_codes.contains(first_airline_code))
                    airline_codes.add(first_airline_code);
                if (!listProperties.contains(first_airline_code))
                    listProperties.add(first_airline_code);

                String total_fare = _PricedItinerary._AirItineraryPricingInfo.get(0)._ItinTotalFare._TotalFare.Amount;

                if (low_fare.containsKey(first_airline_code)) {
                    if (Float.parseFloat(total_fare) < Float.parseFloat(low_fare.get(first_airline_code)))
                        low_fare.put(first_airline_code, total_fare);
                } else {
                    if (!low_fare.containsValue(total_fare))
                        low_fare.put(first_airline_code, total_fare);
                }
            }

            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;
            OKHttp http = new OKHttp();
            String responseContent;

            // Get Airline Names from Airline Codes to Display
            List<AirlineName> airlineNames;
            for (String airline_code : airline_codes) {
                responseContent = http.getCall(fragment.getString(R.string.get_airline_name) + airline_code);
                if (responseContent != null && http.responseCode == 200) {
                    airlineNames = JsonConverter.parseJsonToAirlineName(responseContent);
                    if (airlineNames != null) {
                        for (AirlineName airlineName : airlineNames) {
                            if (airlineName.countryCode.equals(airline_code)) {
                                if (!airlines.containsValue(airlineName.airLine))
                                    airlines.put(airline_code, airlineName.airLine);
                                break;
                            }
                        }
                    }
                } else return 0;
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            //e.getCause().getStackTrace();
            return 2;
        }
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            if (feedback == 1) {
                fragment.setupSearchFilter(list, airlines, low_fare, stops_str, listMap);
                return;
            } else if (feedback == 0) mainActivity.createNoNetworkDialog();
        } catch (Exception ignored) {
            if (fragment != null && fragment.isVisible())
                PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
            // Ignored because search fragment is not visible anymore
        }
        if (fragment != null && fragment.isVisible()) mainActivity.onBackPressed();
    }
}
