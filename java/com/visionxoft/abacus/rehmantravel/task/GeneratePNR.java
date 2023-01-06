package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightBookFragment;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FileHelper;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * AsyncTask process of requesting and encoding reservation (Passenger name record)
 * and generates Ticket Receipt from Server
 */
class GeneratePNR extends AsyncTask<Void, Void, Integer> {
    private MainActivity mainActivity;
    private FlightBookFragment fragment;
    private Hashtable<String, Object> post_params;
    private Dialog dialog;
    private String PNR;

    /**
     * Generate ticket receipt
     *
     * @param fragment    Parent Fragment Class
     * @param post_params Post parameters
     */
    GeneratePNR(FlightBookFragment fragment, Hashtable<String, Object> post_params) {
        this.fragment = fragment;
        this.mainActivity = (MainActivity) fragment.getActivity();
        this.post_params = post_params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(fragment.getContext(), R.string.generate_ticket,
                R.string.please_wait, false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!GeneratePNR.this.isCancelled())
                    GeneratePNR.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;
            FileHelper.createMainDirectory(mainActivity);
            OKHttp http = new OKHttp();

            // Request PNR
            String responseContent = http.postCall(mainActivity.getString(R.string.get_ticket_pnr), post_params);
            if (responseContent != null && http.responseCode == 200) {
                if (!responseContent.equals("")) {
                    if (responseContent.contains("</OTA_AirPriceRQ>")) {
                        String str = responseContent.split("</OTA_AirPriceRQ>")[1];
                        if (str.length() == 6) PNR = str;
                    } else if (responseContent.length() == 6) PNR = responseContent;
                    else if (responseContent.startsWith("1")) {
                        String str = responseContent.replace("1", "");
                        if (str.length() == 6) PNR = str;
                    }
                    if (PNR == null) {
                        Calendar cal = Calendar.getInstance();
                        FileHelper.logData(mainActivity, FileHelper.getDirectory(mainActivity),
                                mainActivity.getString(R.string.log_filename) + cal.get(Calendar.MONTH) + "-"
                                        + cal.get(Calendar.YEAR) + ".txt", "Uncaught PNR Response: " + responseContent);
                        return 6;
                    }
                    try {
                        // Log reservation to file
                        String data = "\n" + DateFormat.getDateTimeInstance().format(new Date()) + " : " + PNR;
                        FileHelper.logData(mainActivity, FileHelper.getDirectory(mainActivity),
                                mainActivity.getString(R.string.log_pnr), data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 1;
                } else return 2;
            } else return 0;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return 9;
        } catch (Exception e) {
            e.printStackTrace();
            return 7;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.pnr_cancelled));
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
                    new DisplayPNR(fragment, PNR, true, true).execute();
                    break;
                case 2:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.empty_response));
                    break;
                case 6:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.invalid_response));
                    break;
                case 7:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
                    break;
                case 9:
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.timeout));
                    break;
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}
