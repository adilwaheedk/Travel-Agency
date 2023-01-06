package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightBookFragment;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.fragment.HomeFragment;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Client;
import com.visionxoft.abacus.rehmantravel.model.TravelItinerary;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatParameters;
import com.visionxoft.abacus.rehmantravel.utils.FragmentHelper;
import com.visionxoft.abacus.rehmantravel.utils.IdGenerator;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;

import java.util.Hashtable;

/**
 * AsyncTask that get ticket receipt html from PNR
 */
public class DisplayPNR extends AsyncTask<Void, Void, Integer> {
    private MainActivity mainActivity;
    private FlightReservationFragment frf;
    private HomeFragment hf;
    private FlightBookFragment fbf;
    private Dialog dialog;
    private String PNR, webview_ticket_html;
    private boolean sendMail, getTravelItinerary;

    public DisplayPNR(Fragment fragment, String PNR, boolean getTravelItinerary, boolean sendMail) {
        if (fragment instanceof FlightReservationFragment) {
            frf = (FlightReservationFragment) fragment;
        } else if (fragment instanceof HomeFragment) {
            hf = (HomeFragment) fragment;
        } else if (fragment instanceof FlightBookFragment) {
            fbf = (FlightBookFragment) fragment;
        }
        mainActivity = (MainActivity) fragment.getActivity();
        this.PNR = PNR;
        this.sendMail = sendMail;
        this.getTravelItinerary = getTravelItinerary;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(mainActivity, R.string.refresh_ticket, R.string.please_wait, false);
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;
            OKHttp http = new OKHttp();
            String responseContent = http.getCall(mainActivity.getString(R.string.get_ticket_html) + PNR);
            if (responseContent != null && http.responseCode == 200) {
                if (responseContent.equals("")) return 4;
                webview_ticket_html = responseContent;

                // Get TravelItinerary
                if (getTravelItinerary) {
                    Hashtable<String, Object> post_params = FormatParameters.setConnectApiParams(true);
                    post_params = FormatParameters.setBusinessTypeParams(mainActivity, post_params);
                    post_params.put("uniqueId", PNR);
                    responseContent = http.postCall(mainActivity.getString(R.string.get_ticket_details), post_params);
                    TravelItinerary travelItinerary = JsonConverter.parseJsonToTravelItinerary(responseContent);
                    IntentHelper.addObjectForKey(travelItinerary, "TravelItinerary");
                }

                if (sendMail) {
                    // Get Detail Information
                    //String infoDevice = FormatParameters.getDeviceInfo(mainActivity);
                    //String infoAgent = FormatParameters.getAgentInfo(fragment.agentSession);
                    AgentSession agentSession = PreferenceHelper.getAgentSession(mainActivity);
                    Hashtable<String, Object> post_params = new Hashtable<>();
                    post_params.put("ePnr", PNR);
                    post_params.put("mFrom", agentSession.AGENT_EMAIL);
                    post_params.put("htmlDataSet", webview_ticket_html);

                    Object client_obj = IntentHelper.getObjectForKey("client_info");
                    // Send Email To Client from Agent
                    if (client_obj != null) {
                        Client client = (Client) client_obj;
                        post_params.put("mTo", client.resident_email);
                        http.postCall(mainActivity.getString(R.string.send_email_pnr), post_params);
                    }
                    // Send Email To Management from Agent
                    post_params.put("mTo", mainActivity.getString(R.string.rt_email_address));
                    //post_params.put("info", infoDevice + infoAgent + infoExtra);
                    http.postCall(mainActivity.getString(R.string.send_email_pnr), post_params);
                }
                return 1;
            } else
                return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 5;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (dialog != null) dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            if (dialog != null) dialog.dismiss();
            switch (feedback) {
                case 0:
                    mainActivity.createNoNetworkDialog();
                    break;
                case 1:
                    if (frf != null) {
                        frf.webview_ticket_html = webview_ticket_html;
                        frf.refreshWebView(webview_ticket_html);
                    } else if (hf != null) {
                        IntentHelper.addObjectForKey(webview_ticket_html, "webView_ticket_html");
                        IntentHelper.addObjectForKey(PNR, "ticket_pnr");
                        IntentHelper.addObjectForKey(true, "view_pnr");
                        FragmentHelper.replaceFragment(hf, new FlightReservationFragment(),
                                mainActivity.getString(R.string.flight_book_ticket_tag));
                    } else if (fbf != null) {
                        IntentHelper.addObjectForKey(webview_ticket_html, "webView_ticket_html");
                        IntentHelper.addObjectForKey(PNR, "ticket_pnr");
                        IntentHelper.addObjectForKey(null, "view_pnr");
                        FragmentHelper.replaceFragment(fbf, new FlightReservationFragment(),
                                mainActivity.getString(R.string.flight_book_ticket_tag));
                        PhoneFunctionality.showNotification(mainActivity.getClass(), mainActivity,
                                IdGenerator.generateViewId(), mainActivity.getString(R.string.pnr_generate_title),
                                mainActivity.getString(R.string.pnr_generate_msg) + " Ref# " + PNR, true);
                    }
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.ticket_updated), true);
                    break;
                case 4:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.empty_response));
                    break;
                default:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
                    break;
            }
        } catch (Exception e) {
            // Ignored because fragment is not visible anymore
        }
    }
}
