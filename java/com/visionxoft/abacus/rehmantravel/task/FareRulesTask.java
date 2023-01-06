package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightBookFragment;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.*;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatParameters;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * AsyncTask to Request Trip Fare Rules
 */
public class FareRulesTask extends AsyncTask<Void, Void, Integer> {

    private MainActivity mainActivity;
    private FlightBookFragment fragment;
    private Dialog dialog;
    private List<OriginDestinationOption> list;
    private String VCarrier;
    private HashMap<Integer, String> fare_rules_map;

    /**
     * Request Trip fare rules
     *
     * @param fragment Parent Fragment Class
     * @param list     List of Origin Destination Option
     */
    public FareRulesTask(FlightBookFragment fragment, List<OriginDestinationOption> list, String VCarrier) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.VCarrier = VCarrier;
        this.list = list;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fragment.selected_btn.setEnabled(false);
        dialog = DialogHelper.createProgressDialog(mainActivity, R.string.fare_rules_title,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!FareRulesTask.this.isCancelled()) FareRulesTask.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;

            fare_rules_map = new HashMap<>();
            OKHttp http = new OKHttp();
            Hashtable<String, Object> post_params = FormatParameters.setConnectApiParams(false);
            for (int i = 0; i < list.size(); i++) {
                OriginDestinationOption odo = list.get(i);
                post_params.put("OLocation", odo._DepartureAirport.LocationCode);
                post_params.put("DLocation", odo._ArrivalAirport.LocationCode);
                post_params.put("VCarrier", VCarrier);

                String responseContent = http.postCall(mainActivity.getString(R.string.get_fare_rules), post_params);
                if (responseContent != null && http.responseCode == 200) {
                    //fare_rules_map = JsonConverter.parseJsonToFareRules(responseContent);
                    fare_rules_map.put(i,responseContent);
                } else {
                    return 0;
                }
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 4;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        fragment.selected_btn.setEnabled(true);
        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.fare_rule_cancel));
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            fragment.selected_btn.setEnabled(true);
            dialog.dismiss();
            if (feedback == 0) mainActivity.createNoNetworkDialog();
            else if (feedback == 3)
                PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.fare_rules_not_found));
            else if (feedback == 4)
                PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
            else fragment.showFareRuleDialog(fare_rules_map);
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}
