package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.AllPackagesFragment;
import com.visionxoft.abacus.rehmantravel.fragment.VisaStudyPackagesFragment;
import com.visionxoft.abacus.rehmantravel.fragment.VisaVisitPackagesFragment;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.IdGenerator;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import java.util.Hashtable;

public class SubmitPackageForm extends AsyncTask<Void, Void, Integer> {

    private Fragment fragment;
    private Hashtable<String, Object> params;
    private Activity activity;
    private Dialog dialog;
    private int package_type;
    public static int OTHER = 0, VISIT_VISA = 1, STUDY_VISA = 2;

    public SubmitPackageForm(Fragment fragment, Hashtable<String, Object> params, int package_type) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.params = params;
        this.package_type = package_type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(activity, R.string.apply_visa_visit,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!SubmitPackageForm.this.isCancelled()) SubmitPackageForm.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            OKHttp http = new OKHttp();
            String response, url;
            if (package_type == STUDY_VISA) {
                url = activity.getString(R.string.visa_study_apply_url);
                response = http.multipartRequest(url, params);
            } else if (package_type == VISIT_VISA) {
                url = activity.getString(R.string.visa_visit_apply_url);
                response = http.postCall(url, params);
            } else {
                url = activity.getString(R.string.all_package_apply_url);
                response = http.postCall(url, params);
            }
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
            if (package_type == STUDY_VISA) {
                VisaStudyPackagesFragment frag = (VisaStudyPackagesFragment) fragment;
                frag.apply_dialog.dismiss();
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.study_visa_submit_msg));
                PhoneFunctionality.showNotification(activity.getClass(), activity, IdGenerator.generateViewId(),
                        activity.getString(R.string.study_visa_submit_title), activity.getString(R.string.study_visa_submit_msg), true);
            } else if (package_type == VISIT_VISA) {
                VisaVisitPackagesFragment frag = (VisaVisitPackagesFragment) fragment;
                frag.apply_dialog.dismiss();
                frag.detail_dialog.dismiss();
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.visit_visa_submit_msg));
                PhoneFunctionality.showNotification(activity.getClass(), activity, IdGenerator.generateViewId(),
                        activity.getString(R.string.visit_visa_submit_title), activity.getString(R.string.visit_visa_submit_msg), true);
            } else {
                AllPackagesFragment frag = (AllPackagesFragment) fragment;
                frag.apply_dialog.dismiss();
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.package_submit_msg));
                PhoneFunctionality.showNotification(activity.getClass(), activity, IdGenerator.generateViewId(),
                        activity.getString(R.string.package_submit_title), activity.getString(R.string.package_submit_msg), true);
            }
        } else if (feedback == 2)
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));
        else
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.no_response));
    }
}
