package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.Contact;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatParameters;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.HashMap;

/**
 * AsyncTask that sends ticket and other info in message to multiple contacts
 */
public class SendMessages extends AsyncTask<Void, Void, Integer> {
    private Activity activity;
    private FlightReservationFragment fragment;
    private AgentSession agentSession;
    private HashMap<String, Contact> contactList;
    private String number_list, PNR;
    private boolean flag_ticket_issued;
    private int no_of_sms_sent;
    private Dialog dialog;

    /**
     * Sends messages to multiple contacts
     *
     * @param fragment    Parent fragment class
     * @param contactList HashMap of contacts from phone book
     * @param number_list Manually entered list of numbers
     * @param PNR         ticket number
     */
    public SendMessages(FlightReservationFragment fragment, AgentSession agentSession, HashMap<String, Contact> contactList,
                        String number_list, String PNR, boolean flag_ticket_issued) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.agentSession = agentSession;
        this.contactList = contactList;
        this.number_list = number_list;
        this.PNR = PNR;
        this.flag_ticket_issued = flag_ticket_issued;
        this.no_of_sms_sent = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fragment.selected_btn.setEnabled(false);
        dialog = DialogHelper.createProgressDialog(activity, R.string.sending_sms,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!SendMessages.this.isCancelled())
                    SendMessages.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... params) {
        String header = null, footer = null;
        if (flag_ticket_issued) header = activity.getString(R.string.ticket_purchased);
        if (!agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY))
            footer = "*This message is sent from " + agentSession.AGENT_NAME + " (" + agentSession.AGENT_EMAIL + ")";
        String textMessage = FormatParameters.prepareMessage(activity, header, PNR,
                (String) IntentHelper.getObjectForKey("total_travelers"),
                (String) IntentHelper.getObjectForKey("first_sector"),
                (String) IntentHelper.getObjectForKey("departure_date"), footer);
        if (contactList != null) {
            for (Contact contact : contactList.values()) {
                if (FormatString.isContactValid(contact.phone)) {
                    try {
                        PhoneFunctionality.sendMessage(contact.phone, textMessage);
                        no_of_sms_sent++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (number_list != null && !number_list.equals("")) {
            String[] numbers = number_list.split(";");
            for (String number : numbers) {
                if (FormatString.isContactValid(number)) {
                    try {
                        PhoneFunctionality.sendMessage(number, textMessage);
                        no_of_sms_sent++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return 1;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        fragment.selected_btn.setEnabled(true);
        PhoneFunctionality.makeToast(activity, activity.getString(R.string.sms_cancel));
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            fragment.selected_btn.setEnabled(true);
            dialog.dismiss();
            if (no_of_sms_sent > 0) {
                String toast_msg;
                if (no_of_sms_sent == 1) toast_msg = "SMS Sent";
                else toast_msg = "SMS Sent to " + no_of_sms_sent + " Contacts";
                PhoneFunctionality.makeToast(activity, toast_msg, true);
            } else {
                PhoneFunctionality.makeToast(activity, "SMS failed to Sent");
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}
