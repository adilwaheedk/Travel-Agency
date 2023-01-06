package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightBookFragment;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.OriginDestinationOption;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatParameters;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;
import java.util.List;

/**
 * AsyncTask to request list of status of all flights before booking
 */
public class GetFlightStatus extends AsyncTask<Void, Void, Integer> {
    private FlightBookFragment fragment;
    private MainActivity mainActivity;
    private PricedItinerary pricedItinerary;
    private int total_travelers;
    private Dialog dialog;

    /**
     * Get Flight Status
     *
     * @param fragment Parent Fragment Class
     */
    public GetFlightStatus(FlightBookFragment fragment, PricedItinerary pricedItinerary, int total_travelers) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.pricedItinerary = pricedItinerary;
        this.total_travelers = total_travelers;
    }

    @Override
    protected void onPreExecute() {
        dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_progress, Gravity.CENTER);
        ((TextView) dialog.findViewById(R.id.progress_title)).setText(R.string.check_flight_status);
        dialog.findViewById(R.id.progress_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GetFlightStatus.this.isCancelled()) GetFlightStatus.this.cancel(true);
            }
        });
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;
            OKHttp sh = new OKHttp();
            Hashtable<String, Object> params = FormatParameters.setConnectApiParams(true);
            params = FormatParameters.setBusinessTypeParams(mainActivity, params);
            List<OriginDestinationOption> list_odo = pricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption;
            for (int i = 0; i < list_odo.size(); i++) {
                String index = String.valueOf(i);
                OriginDestinationOption odo = list_odo.get(i);
                params.put("AirBookRQ[" + index + "][DepartureDate]", odo._attr.DepartureDateTime.split("T")[0]);
                params.put("AirBookRQ[" + index + "][DepartureTime]", odo._attr.DepartureDateTime.split("T")[1]);
                params.put("AirBookRQ[" + index + "][ArrivalDate]", odo._attr.ArrivalDateTime.split("T")[0]);
                params.put("AirBookRQ[" + index + "][ArrivalTime]", odo._attr.ArrivalDateTime.split("T")[1]);
                params.put("AirBookRQ[" + index + "][FlightNumber]", odo._attr.FlightNumber);
                params.put("AirBookRQ[" + index + "][NumberInParty]", String.valueOf(total_travelers));
                params.put("AirBookRQ[" + index + "][ResBookDesigCode]", odo._attr.ResBookDesigCode);
                params.put("AirBookRQ[" + index + "][Status]", "NN");
                params.put("AirBookRQ[" + index + "][AirEquipType]", odo._Equipment.AirEquipType);
                params.put("AirBookRQ[" + index + "][DestinationLocation]", odo._ArrivalAirport.LocationCode);
                params.put("AirBookRQ[" + index + "][MarketingAirlineCode]", odo._MarketingAirline.Code);
                params.put("AirBookRQ[" + index + "][OperatingAirlineCode]", odo._OperatingAirline.Code);
                params.put("AirBookRQ[" + index + "][OriginLocation]", odo._DepartureAirport.LocationCode);
            }
            String responseContent = sh.postCall(mainActivity.getString(R.string.get_flight_status), params);
            if (responseContent != null && sh.responseCode == 200) {
                if (responseContent.contains("OTA_AirBookRQ")) {
                    return responseContent.split("</OTA_AirBookRQ>")[1].equals("OK") ? 1 : 2;
                } else return responseContent.equals("OK") ? 1 : 2;
            } else return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        dialog.dismiss();
        try {
            switch (feedback) {
                case 0:
                    mainActivity.createNoNetworkDialog();
                    return;
                case 1:
                    fragment.proceedGeneratePNR();
                    return;
                case 2:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error_flight_status));
                    break;
                default:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
                    break;
            }

        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}