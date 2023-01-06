package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.EuropeToursFragment;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.IdGenerator;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;

public class SubmitEuropeTourForm extends AsyncTask<Void, Void, Integer> {

    private EuropeToursFragment fragment;
    private Hashtable<String, Object> params;
    private Activity activity;
    private Dialog dialog;
    private int tourType;

    public SubmitEuropeTourForm(EuropeToursFragment fragment, Hashtable<String, Object> params, int tourType) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.params = params;
        this.tourType = tourType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(activity, R.string.apply_tour,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!SubmitEuropeTourForm.this.isCancelled()) SubmitEuropeTourForm.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            OKHttp http = new OKHttp();
            String response, url = "";
            switch (tourType) {
                case EuropeToursFragment.SWISS_STD_CAMP:
                    url = activity.getString(R.string.eur_tour_1_apply_url);
                    break;
                case EuropeToursFragment.POLAND_TOUR:
                    url = activity.getString(R.string.eur_tour_2_apply_url);
                    break;
                case EuropeToursFragment.POLAND_TOUR_PKG:
                    url = activity.getString(R.string.eur_tour_3_apply_url);
                    break;
                case EuropeToursFragment.AUSTRIA_TOUR_PKG:
                    url = activity.getString(R.string.eur_tour_4_apply_url);
                    break;
                case EuropeToursFragment.GERMANY_TOUR:
                    url = activity.getString(R.string.eur_tour_5_apply_url);
                    break;
            }
            response = http.postCall(url, params);

            if (response != null && http.responseCode == 200) return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
        return 3;
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
        if (feedback == 1) {
            PhoneFunctionality.vibrateMobile(activity);
            fragment.apply_dialog.dismiss();
            fragment.detail_dialog.dismiss();
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.tour_submit_msg));
            PhoneFunctionality.showNotification(activity.getClass(), activity, IdGenerator.generateViewId(),
                    activity.getString(R.string.tour_submit_title), activity.getString(R.string.tour_submit_msg), true);
        } else if (feedback == 2)
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));
        else
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.no_response));
    }
}
