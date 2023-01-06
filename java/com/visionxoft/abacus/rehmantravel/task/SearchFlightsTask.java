package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.SearchFragment;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;
import java.util.List;

/**
 * AsyncTask to Search Lowest Fare Flights on basis of given inputs i.e. Origin Locations,
 * Destination Location, Departing dates, Returning dates, no of passengers etc.
 */
class SearchFlightsTask extends AsyncTask<Void, Integer, Integer> {

    private SearchFragment fragment;
    private MainActivity mainActivity;
    private Hashtable<String, Object> post_params;
    private List<PricedItinerary> pricedItineraryList;

    /**
     * @param fragment    Parent Fragment Class
     * @param post_params Valid Post Parameters
     */
    SearchFlightsTask(SearchFragment fragment, Hashtable<String, Object> post_params) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.post_params = post_params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;
            OKHttp http = new OKHttp();
            String responseContent = http.postCall(fragment.getString(R.string.get_lowfare_flights), post_params);

            if (responseContent != null && http.responseCode == 200) {
                if (!responseContent.equals("not SessionCreate")) {
                    pricedItineraryList = JsonConverter.parseJsonToPricedItinerary(responseContent);
                    return pricedItineraryList != null ? 1 : 3;
                } else return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 4;
        }
        return 0;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            String message = "";
            switch (feedback) {
                case 1:
                    fragment.execSearchFilter(pricedItineraryList);
                    return;
                case 0:
                    mainActivity.createNoNetworkDialog();
                    break;
                case 2:
                    message = mainActivity.getString(R.string.session_error);
                    break;
                case 3:
                    message = mainActivity.getString(R.string.no_flights);
                    break;
                case 4:
                    message = mainActivity.getString(R.string.error);
                    break;
            }
            PhoneFunctionality.makeToast(mainActivity, message);
        } catch (Exception ignored) {
            if (fragment != null && fragment.isVisible())
                PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
            // Ignored because search fragment is not visible anymore
        }
        if (fragment != null && fragment.isVisible()) mainActivity.onBackPressed();
    }
}