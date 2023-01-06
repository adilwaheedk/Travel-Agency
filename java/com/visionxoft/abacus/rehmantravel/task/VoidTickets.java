package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatParameters;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;

/**
 * AsyncTask that Void E-Tickets in PNR
 */
public class VoidTickets extends AsyncTask<Void, Void, Integer> {
    private MainActivity mainActivity;
    private FlightReservationFragment fragment;
    private String PNR;
    private AgentSession agent;
    private String ValidatingCarrier;
    private Dialog dialog;

    /**
     * Issue Ticket Process
     *
     * @param fragment FlightBookTicketFragment parent class
     * @param agent    Agent Session
     */
    public VoidTickets(FlightReservationFragment fragment, String PNR, AgentSession agent, String ValidatingCarrier) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.PNR = PNR;
        this.agent = agent;
        this.ValidatingCarrier = ValidatingCarrier;
        // TODO Get eTicketNumbers
        // eTicketNumbers (Multiple Tickets + RPH>1)
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fragment.selected_btn.setEnabled(false);
        dialog = DialogHelper.createProgressDialog(mainActivity, R.string.ticket_voiding,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!VoidTickets.this.isCancelled()) VoidTickets.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;

            Hashtable<String, Object> params = FormatParameters.setConnectApiParams(true);
            params = FormatParameters.setBusinessTypeParams(mainActivity, params);
            params.put("RECEIVED_FROM", agent.AGENT_NAME);
            params.put("uniqueId", PNR);
            params.put("VCarrier", ValidatingCarrier);
            OKHttp http = new OKHttp();
            String response = http.postCall(mainActivity.getString(R.string.void_tickets), params);
            if (response != null && http.responseCode == 200) {
                return response.equals("OK") ? 1 : 2;
            } else return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 3;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        fragment.selected_btn.setEnabled(true);
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            fragment.selected_btn.setEnabled(true);
            dialog.dismiss();
            switch (feedback) {
                case 0:
                    mainActivity.createNoNetworkDialog();
                    break;
                case 1:
                    fragment.flag_ticket_voided = true;

                    fragment.btn_pnr_ticket.setVisibility(View.GONE);
                    fragment.btn_pnr_book_cancel.setVisibility(View.GONE);
                    fragment.btn_pnr_ticket_void.setVisibility(View.GONE);
                    PhoneFunctionality.vibrateMobile(mainActivity);
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.tickets_voided));

                    // Refresh Ticket Receipt
                    new DisplayPNR(fragment, PNR, true, false).execute();
                    break;
                case 2:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.bad_response));
                    break;
                case 3:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
                    break;
                case 4:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.invalid_response));
                    break;
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}
