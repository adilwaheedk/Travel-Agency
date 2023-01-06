package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.SearchFragment;
import com.visionxoft.abacus.rehmantravel.model.AirlineName;
import com.visionxoft.abacus.rehmantravel.model.AirportLocation;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatParameters;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * AsyncTask to collect required information like locations, airlines and country names
 * from codes included in Itinerary
 */
class GetFlightExtraInfo extends AsyncTask<Void, Void, Integer> {
    private SearchFragment fragment;
    private MainActivity mainActivity;
    private PricedItinerary _PricedItinerary;
    private HashMap<String, AirportLocation> airport_names;
    private HashMap<String, String> airline_names;
    private Dialog dialog;
    private Object position;

    /**
     * Get Extra information of specific Itinerary
     *
     * @param fragment         Parent Fragment Class
     * @param _PricedItinerary Priced Itinerary Object
     * @param position         Reference code, specify which fragment you want to go
     */
    GetFlightExtraInfo(SearchFragment fragment, PricedItinerary _PricedItinerary, Object position) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this._PricedItinerary = _PricedItinerary;
        this.airport_names = new HashMap<>();
        this.airline_names = new HashMap<>();
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(mainActivity, R.string.fetch_flight_details, R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!GetFlightExtraInfo.this.isCancelled()) GetFlightExtraInfo.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            List<String> loc_codes = new ArrayList<>();
            List<String> airline_codes = new ArrayList<>();

            // First get Location and Airline Codes from Itinerary
            for (int i = 0; i < _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption.size(); i++) {
                PricedItinerary.AirItinerary.OriginDestinationOptions.OriginDestinationOption odOption =
                        _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption.get(i);
                loc_codes.add(odOption._DepartureAirport.LocationCode);
                if (!loc_codes.contains(odOption._ArrivalAirport.LocationCode))
                    loc_codes.add(odOption._ArrivalAirport.LocationCode);
                if (!airline_codes.contains(odOption._OperatingAirline.Code))
                    airline_codes.add(odOption._OperatingAirline.Code);
            }

            // Collect Location Names from Location Codes
            OKHttp http = new OKHttp();
            String responseContent;
            Hashtable<String, Object> get_airport_post_params = FormatParameters.setConnectApiParams(false);
            for (String loc_code : loc_codes) {
                get_airport_post_params.put("keyword", loc_code);
                responseContent = http.postCall(mainActivity.getString(R.string.get_airport_name), get_airport_post_params);
                if (responseContent != null && http.responseCode == 200) {
                    List<AirportLocation> airportLocations = JsonConverter.parseJsonToAirportLocation(responseContent);
                    if (airportLocations != null) {
                        for (AirportLocation airportLocation : airportLocations) {
                            if (airportLocation.iataCode.equals(loc_code))
                                airport_names.put(loc_code, airportLocation);
                        }
                    }
                } else return 0;
            }

            // Collect Airline Names from Airline Codes
            for (String airline_code : airline_codes) {
                responseContent = http.getCall(mainActivity.getString(R.string.get_airline_name) + airline_code);
                if (responseContent != null && http.responseCode == 200) {
                    List<AirlineName> airlineNames = JsonConverter.parseJsonToAirlineName(responseContent);
                    if (airlineNames != null) {
                        for (AirlineName airlineName : airlineNames) {
                            if (airlineName.countryCode.equals(airline_code)) {
                                airline_names.put(airline_code, airlineName.airLine);
                                break;
                            }
                        }
                    }
                } else return 0;
            }

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.fetch_detail_cancel));
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            dialog.dismiss();
            if (feedback == 1) {
                fragment.proceed(_PricedItinerary, airport_names, airline_names, position);
            } else if (feedback == 0) mainActivity.createNoNetworkDialog();
            else
                PhoneFunctionality.makeToast(mainActivity, fragment.getString(R.string.error));
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}
