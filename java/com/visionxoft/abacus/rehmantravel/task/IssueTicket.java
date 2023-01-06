package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Client;
import com.visionxoft.abacus.rehmantravel.model.ReceivableClient;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatParameters;
import com.visionxoft.abacus.rehmantravel.utils.IdGenerator;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;

import java.util.Hashtable;

/**
 * AsyncTask that Issue ticket from Server
 */
public class IssueTicket extends AsyncTask<Void, Void, Integer> {
    private MainActivity mainActivity;
    private FlightReservationFragment fragment;
    private AgentSession agent;
    private String PNR, ValidatingCarrier, percentage, receivable_amount;
    private Dialog dialog;
    private ReceivableClient receivableClient;

    /**
     * Issue Ticket Process
     *
     * @param fragment          FlightBookTicketFragment parent class
     * @param PNR               Personal Name Record
     * @param ValidatingCarrier Validating Carrier
     * @param agent             Agent Session
     * @param receivableClient  Form of Payment ID
     * @param percentage        Markup Percentage
     * @param receivable_amount Receivable Amount (including markup)
     */
    public IssueTicket(FlightReservationFragment fragment, String PNR, String ValidatingCarrier,
                       AgentSession agent, ReceivableClient receivableClient, String percentage,
                       String receivable_amount) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.PNR = PNR;
        this.ValidatingCarrier = ValidatingCarrier;
        this.agent = agent;
        this.receivableClient = receivableClient;
        this.percentage = percentage;
        this.receivable_amount = receivable_amount;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fragment.selected_btn.setEnabled(false);
        dialog = DialogHelper.createProgressDialog(mainActivity, R.string.ticket_issue_process,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!IssueTicket.this.isCancelled()) IssueTicket.this.cancel(true);
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
            params.put("VCarrier", ValidatingCarrier);
            params.put("FormOfPayment", receivableClient.value);
            params.put("receivableId", receivableClient.accountId);
            params.put("UPsf", receivable_amount);
            params.put("uniqueId", PNR);

            OKHttp http = new OKHttp();
            String response = http.postCall(mainActivity.getString(R.string.get_issue_ticket), params);

            if (response != null && http.responseCode == 200) {
                if (response.equals("")) return 2;
                //Pattern pattern = Pattern.compile("\\{([^]\\n]+)\\}");
                //Matcher matcher = pattern.matcher(response);
                //ticket = JsonConverter.parseJsonToTicket("{" + matcher.group(1) + "}", ticket);
                //PreferenceHelper.setTicketRecord(mainActivity, ticket);

                // TODO Check Ticket Issuance response

                Object client_obj = IntentHelper.getObjectForKey("client_info");

                // Sms to Client
                if (client_obj != null) {
                    if (PreferenceHelper.getInt(mainActivity, PreferenceHelper.GRANT_SEND_SMS) == 1) {
                        Client client = (Client) client_obj;
                        String header = mainActivity.getString(R.string.ticket_purchased);
                        String footer = "*This message is sent from " + fragment.agentSession.AGENT_NAME +
                                " (" + fragment.agentSession.AGENT_EMAIL + ")";
                        String textMessage = FormatParameters.prepareMessage(mainActivity, header, PNR,
                                (String) IntentHelper.getObjectForKey("total_travelers"),
                                (String) IntentHelper.getObjectForKey("first_sector"),
                                (String) IntentHelper.getObjectForKey("departure_date"), footer);
                        PhoneFunctionality.sendMessage(client.resident_no, textMessage);
                    } else {
                        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.no_sms_permission));
                    }
                } else {
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.client_contact_not_found));
                }
                return 1;
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
                    // Ticket Issued Successfully
                    fragment.flag_ticket_issued = true;

                    fragment.btn_pnr_ticket.setVisibility(View.GONE);
                    PhoneFunctionality.vibrateMobile(mainActivity);
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.ticket_issued));
                    PhoneFunctionality.showNotification(mainActivity.getClass(), mainActivity,
                            IdGenerator.generateViewId(), mainActivity.getString(R.string.ticket_issued_title),
                            mainActivity.getString(R.string.ticket_issued_msg) + " Ref# " + PNR, true);

                    // Display Ticket Receipt
                    new DisplayPNR(fragment, PNR, true, true).execute();
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
