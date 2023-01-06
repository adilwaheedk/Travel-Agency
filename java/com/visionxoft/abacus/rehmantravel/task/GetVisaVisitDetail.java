package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.VisaVisitPackagesFragment;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class GetVisaVisitDetail extends AsyncTask<Void, Void, Integer> {

    private VisaVisitPackagesFragment fragment;
    private Activity activity;
    private Dialog dialog;
    private String visaId, data;
    private List<String> visa_visits;

    public GetVisaVisitDetail(Fragment fragment, ImageTag imageTag) {
        this.fragment = (VisaVisitPackagesFragment) fragment;
        this.activity = fragment.getActivity();
        this.visaId = imageTag.extra;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(activity, R.string.fetch_package_detail,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!GetVisaVisitDetail.this.isCancelled()) GetVisaVisitDetail.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            Hashtable<String, Object> post_params = new Hashtable<>();
            OKHttp http = new OKHttp();
            post_params.put("visaId", visaId);
            String response = http.postCall(activity.getString(R.string.visa_visit_details_url), post_params);
            if (response != null && http.responseCode == 200) {
                data = getVisaDetails(response);
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
        PhoneFunctionality.makeToast(activity, activity.getString(R.string.fetch_cancel));
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        dialog.dismiss();
        if (feedback == 1) {
            fragment.createVisaDetailsDialog(visaId, data, visa_visits);
        } else if (feedback == 2)
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));
        else
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.no_response));
    }

    private String getVisaDetails(String data) {
        visa_visits = new ArrayList<>();
        Document doc = Jsoup.parse(data);
        Elements visa_broucher = doc.select("div[class$=visa-broucher]");
        Elements visa_fees = visa_broucher.select("ul[class='visa-fees']");
        for (Element el : visa_fees.select("li")) {
            if (el.text().contains("Visit Visa") || el.text().contains("Tourist Visa")) {
                String text = el.text();
                text = text.substring(text.indexOf("(") + 1);
                text = text.substring(0, text.indexOf(")"));
                visa_visits.add(text.trim());
            }

        }
        return visa_broucher.toString();
    }
}
