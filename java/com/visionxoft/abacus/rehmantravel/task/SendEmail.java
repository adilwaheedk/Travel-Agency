package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;

/**
 * AsyncTask that sends data/html to one or more mail addresses via server
 */
public class SendEmail extends AsyncTask<Void, Void, Integer> {

    private MainActivity mainActivity;
    private Fragment fragment;
    private String sender, receiver_addresses, cc, bcc, subject, message, info;
    private Dialog dialog;
    private int no_of_mail_sent;
    private boolean isTicket;

    /**
     * E-Mail Form
     *
     * @param fragment           Parent Fragment Class
     * @param sender             Sender Email Address
     * @param receiver_addresses Receiver Email Address
     * @param cc                 CC email receiver_addresses
     * @param bcc                BCC email receiver_addresses
     * @param subject            Email Subject
     * @param message            Email Message (html format)
     * @param info               Extra email info (html format)
     */
    public SendEmail(Fragment fragment, String sender, String receiver_addresses, String cc,
                     String bcc, String subject, String message, String info) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.sender = sender;
        this.receiver_addresses = receiver_addresses;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.message = message != null ? message : "";
        this.info = info != null ? info : "";
        this.no_of_mail_sent = 0;
        isTicket = false;
    }

    public SendEmail(Fragment fragment, String ePnr, String mFrom, String receiver_addresses, String htmlDataSet) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.sender = mFrom;
        this.receiver_addresses = receiver_addresses;
        this.message = htmlDataSet;
        this.info = ePnr;
        this.no_of_mail_sent = 0;
        isTicket = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(mainActivity, R.string.sending_email,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!SendEmail.this.isCancelled())
                    SendEmail.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;
            OKHttp http;
            Hashtable<String, Object> post_params = new Hashtable<>();
            String to_param, url;
            if(isTicket) {
                post_params.put("ePnr", info);
                post_params.put("mFrom", sender);
                post_params.put("htmlDataSet", message);
                to_param = "mTo";
                url = mainActivity.getString(R.string.send_email_pnr);
            } else {
                post_params.put("from", sender);
                post_params.put("cc", cc);
                post_params.put("bcc", bcc);
                post_params.put("subject", subject);
                post_params.put("message", message);
                post_params.put("info", info);
                to_param = "to";
                url = mainActivity.getString(R.string.send_email);
            }

            String[] addresses = receiver_addresses.split(";");
            for (String address : addresses) {
                if (FormatString.isEmailValid(address)) {
                    try {
                        post_params.put(to_param, address);
                        http = new OKHttp();
                        String response = http.postCall(url, post_params);
                        if (response != null && http.responseCode == 200) no_of_mail_sent++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.email_cancel));
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            dialog.dismiss();

            switch (feedback) {
                case 0:
                    mainActivity.createNoNetworkDialog();
                    break;
                case 1:
                    if (no_of_mail_sent == 0) {
                        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.mail_address_not_valid));
                    } else {
                        String toast_msg;
                        String[] addresses = receiver_addresses.split(";");
                        if (addresses.length == 1 && no_of_mail_sent == 1)
                            toast_msg = "Email sent to " + addresses[0];
                        else if (no_of_mail_sent == 1)
                            toast_msg = "Email sent";
                        else
                            toast_msg = "Email sent to " + no_of_mail_sent + " Addresses";
                        PhoneFunctionality.makeToast(mainActivity, toast_msg, true);
                        if (fragment instanceof FlightReservationFragment)
                            ((FlightReservationFragment) fragment).flag_pnr_emailed = true;
                    }
                    break;
                case 2:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
                    break;
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}
