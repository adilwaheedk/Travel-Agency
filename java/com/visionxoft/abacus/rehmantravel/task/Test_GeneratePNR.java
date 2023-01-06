package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.fragment.HomeFragment;
import com.visionxoft.abacus.rehmantravel.model.Client;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.FragmentHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

/**
 * AsyncTask that creates ticket receipt from Old ticket 'for testing purpose only'
 */
public class Test_GeneratePNR extends AsyncTask<Void, Void, Integer> {
    private Activity activity;
    private HomeFragment fragment;
    private String webview_ticket_html;
    private Dialog dialog;

    // Dummy Data
    private String carrier_code = "PK",
            fare_amount = "6584",
            total_travelers = "1",
            tkt_noOfChilds = "0",
            tkt_noOfInfants = "0",
            first_sector = "ISB,Islamabad,Pakistan",
            departure_date = "19-08-2017",
            test_client_number = "12345678901",
            test_client_email = "abc@mail.com",
            PNR = "BOLWJI",
            AGENT_ID = "280",
            AGENT_PARENT_ID = "false",
            AGENT_USER_ID = "false",
            ePnrType = "ePnr";

    /**
     * @param fragment Parent Fragment Class
     */
    public Test_GeneratePNR(HomeFragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(fragment.getContext(), R.string.refresh_ticket,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!Test_GeneratePNR.this.isCancelled())
                    Test_GeneratePNR.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            OKHttp http = new OKHttp();


            // Generate ticket
            String responseContent = http.getCall(activity.getString(R.string.get_ticket_html) + PNR
                    + "/" + AGENT_ID + "/" + AGENT_PARENT_ID + "/" + AGENT_USER_ID + "/" + ePnrType);

            if (responseContent != null && http.responseCode == 200) {
                webview_ticket_html = responseContent;
                return 1;
            } else return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 5;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        PhoneFunctionality.makeToast(activity, "Testing Cancelled");
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        dialog.dismiss();
        switch (feedback) {
            case 0:
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.no_response));
                break;
            case 1:

                // Enter Dummy Data for testing purpose
                IntentHelper.addObjectForKey(tkt_noOfChilds, "tkt_noOfChilds");
                IntentHelper.addObjectForKey(tkt_noOfInfants, "tkt_noOfInfants");
                Client c = new Client();
                c.resident_no = test_client_number;
                c.resident_email = test_client_email;
                IntentHelper.addObjectForKey(c, "client_info");
                IntentHelper.addObjectForKey(total_travelers, "total_travelers");
                IntentHelper.addObjectForKey(FormatString.getLongDate(activity, departure_date), "departure_date");
                IntentHelper.addObjectForKey(first_sector, "first_sector");
                IntentHelper.addObjectForKey(webview_ticket_html, "webView_ticket_html");
                IntentHelper.addObjectForKey(PNR, "ticket_pnr");
                PricedItinerary pricedItinerary = new PricedItinerary();
                pricedItinerary._AirItineraryPricingInfo.add(new PricedItinerary.AirItineraryPricingInfo());
                pricedItinerary._AirItineraryPricingInfo.get(0)._ItinTotalFare._TotalFare.Amount = fare_amount;
                pricedItinerary._TPA_Extensions._ValidatingCarrier.Code = carrier_code;
                IntentHelper.addObjectForKey(pricedItinerary, "PricedItinerary");

                FragmentHelper.replaceFragment(fragment, new FlightReservationFragment(),
                        activity.getString(R.string.flight_book_ticket_tag));
                break;
            case 4:
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.storage_not_available));
                break;
            default:
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));
                break;
        }
    }
}
