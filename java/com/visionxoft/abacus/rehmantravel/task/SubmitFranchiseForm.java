package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.FranchiseRecordFragment;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;

public class SubmitFranchiseForm extends AsyncTask<Void, Void, Integer> {

    private FranchiseRecordFragment fragment;
    private Hashtable<String, Object> params;
    private Activity activity;
    private Dialog dialog;
    private String form_html;

    public SubmitFranchiseForm(FranchiseRecordFragment fragment, Hashtable<String, Object> params) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.params = params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(activity, R.string.apply_franchise,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!SubmitFranchiseForm.this.isCancelled()) SubmitFranchiseForm.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            OKHttp http = new OKHttp();
            String response = http.multipartRequest(activity.getString(R.string.reg_franchise_url), params);
            if (response != null && http.responseCode == 200) {
                form_html = response;
                return 1;
            }
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
            fragment.showFranchiseFormDialog(form_html);
        } else if (feedback == 2)
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));
        else
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.no_response));
    }
}
