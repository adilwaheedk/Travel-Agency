package com.visionxoft.abacus.rehmantravel.task;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightFragment;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AsyncTask to request airport location from x/y coordinates
 */
public class GetCurrentAirportLocation extends AsyncTask<String, Void, Integer> {
    private MainActivity activity;
    private List<String> list;
    private String current_loc;
    private Location location;
    private boolean showFeedback;

    /**
     * Get Current Airport Location
     *
     * @param activity     Parent Activity Class
     * @param location     Current Location (X/Y coordinates)
     * @param showFeedback Boolean value, true to display feedback to user
     */
    public GetCurrentAirportLocation(MainActivity activity, Location location, boolean showFeedback) {
        this.activity = activity;
        this.location = location;
        this.showFeedback = showFeedback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showFeedback) activity.action_progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            String addr_locality = null;
            OKHttp http = new OKHttp();
            if (Geocoder.isPresent()) {
                try {
                    Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addressList != null && addressList.size() > 0)
                        addr_locality = addressList.get(0).getLocality().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (addr_locality == null) {
                String param_str = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
                String responseContent = http.getCall(activity.getString(R.string.get_latlng_geocode) + param_str);
                if (responseContent != null && http.responseCode == 200) {
                    addr_locality = JsonConverter.parseJsonFromAddressToLocality(responseContent).trim();
                }
            }
            if (addr_locality != null) {
                String responseContent = http.getCall(activity.getString(R.string.get_airport_name) + addr_locality);
                if (responseContent != null && http.responseCode == 200) {
                    current_loc = addr_locality;
                    list = JsonConverter.parseJsonToLabelStrings(responseContent);
                    return 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (showFeedback) activity.action_progress.setVisibility(View.GONE);
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            if (showFeedback) activity.action_progress.setVisibility(View.GONE);
            // PreferenceHelper.clearValue(activity, PreferenceHelper.CURRENT_AIRPORTS);
            // PreferenceHelper.clearValue(activity, PreferenceHelper.CURRENT_LOCATION);
            if (list != null && current_loc != null) {
                PreferenceHelper.setString(activity, PreferenceHelper.CURRENT_LOCATION, current_loc);
                if (list.size() == 0) {
                    if (showFeedback)
                        PhoneFunctionality.makeToast(activity, activity.getString(R.string.airport_not_found));
                } else if (list.size() == 1 && list.get(0).contains(current_loc)) {

                    PreferenceHelper.setString(activity, PreferenceHelper.CURRENT_AIRPORTS, list.get(0));

                    if (showFeedback)
                        PhoneFunctionality.makeToast(activity, activity.getString(R.string.location_udpated));

                    if (FlightFragment.autoflyfrom != null && FlightFragment.autoflyfrom1 != null) {
                        FlightFragment.autoflyfrom.setText(list.get(0));
                        FlightFragment.autoflyfrom1.setText(list.get(0));
                    } //else if (showFeedback)
                    //PhoneFunctionality.makeToast(activity, activity.getString(R.string.airport_not_found));
                } else {
                    List<String> airports = new ArrayList<>();
                    for (String airport_label : list) {
                        if (airport_label.contains(current_loc)) airports.add(airport_label);
                    }

                    PreferenceHelper.setListValues(activity, PreferenceHelper.CURRENT_AIRPORTS, airports);

                    if (showFeedback)
                        PhoneFunctionality.makeToast(activity, activity.getString(R.string.location_udpated));

                    if (FlightFragment.autoflyfrom != null && FlightFragment.autoflyfrom1 != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                                android.R.layout.simple_dropdown_item_1line, airports);
                        FlightFragment.autoflyfrom.setAdapter(adapter);
                        FlightFragment.autoflyfrom1.setAdapter(adapter);
                        FlightFragment.autoflyfrom.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                        FlightFragment.autoflyfrom1.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                        FlightFragment.autoflyfrom.showDropDown();
                        FlightFragment.autoflyfrom1.showDropDown();
                    }
                }
            } else if (showFeedback)
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.location_not_found));
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}

