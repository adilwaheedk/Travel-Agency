package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.EuropeToursFragment;
import com.visionxoft.abacus.rehmantravel.model.ImageTag;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Hashtable;

public class GetEuropeTourInfo extends AsyncTask<Void, Void, Integer> {

    private EuropeToursFragment fragment;
    private Activity activity;
    private Dialog dialog;
    private String tourID, tourImg, data;
    private int tourType;

    public GetEuropeTourInfo(Fragment fragment, ImageTag imageTag, int tourType) {
        this.fragment = (EuropeToursFragment) fragment;
        this.activity = fragment.getActivity();
        this.tourID = imageTag.extra;
        this.tourImg = imageTag.src;
        this.tourType = tourType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogHelper.createProgressDialog(activity, R.string.fetch_package_detail,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!GetEuropeTourInfo.this.isCancelled())
                    GetEuropeTourInfo.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            Hashtable<String, Object> post_params = new Hashtable<>();
            OKHttp http = new OKHttp();
            post_params.put("tourID", tourID);
            String url = "";
            switch (tourType) {
                case 1:
                    url = activity.getString(R.string.eur_tour_1_details_url);
                    break;
                case 2:
                    url = activity.getString(R.string.eur_tour_2_details_url);
                    break;
                case 3:
                    url = activity.getString(R.string.eur_tour_3_details_url);
                    break;
                case 4:
                    url = activity.getString(R.string.eur_tour_4_details_url);
                    break;
                case 5:
                    url = activity.getString(R.string.eur_tour_5_details_url);
                    break;
            }
            String response = http.postCall(url, post_params);
            if (response != null && http.responseCode == 200) {
                data = getTourDetail(response);
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
            fragment.createTourDetailDialog(data, tourID, tourType);
        } else if (feedback == 2)
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));
        else
            PhoneFunctionality.makeToast(activity, activity.getString(R.string.no_response));
    }

    private String getTourDetail(String data) {
        Document doc = Jsoup.parse(data);
        doc.select("form").remove();
        doc.select("input").remove();
        doc.select("button").remove();
        doc.select("img[src='" + tourImg + "'").remove();
        return doc.toString();
    }
}
