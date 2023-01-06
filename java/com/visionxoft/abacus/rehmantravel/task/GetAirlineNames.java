package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;

import java.util.List;

/**
 * AsyncTask to request list of airline names from term or airline code used
 */
public class GetAirlineNames extends AsyncTask<String, Void, Integer> {
    private MainActivity activity;
    private List<String> list;
    private MultiAutoCompleteTextView mactv;

    /**
     * Get list of Airlines
     *
     * @param activity Parent Activity Class
     * @param mactv    MultiAutoCompleteTextView to get input and display results
     */
    public GetAirlineNames(MainActivity activity, MultiAutoCompleteTextView mactv) {
        this.activity = activity;
        this.mactv = mactv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.main_loader.setVisibility(View.VISIBLE);
        activity.setSearchActionVisible(false);
    }

    @Override
    protected Integer doInBackground(String... params) {
        OKHttp sh = new OKHttp();
        try {
            String responseContent = sh.getCall(activity.getString(R.string.get_airline_name) + params[0]);
            if (responseContent != null && sh.responseCode == 200) {
                list = JsonConverter.parseJsonToAirlines(responseContent);
                return 1;
            } else
                return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        activity.main_loader.setVisibility(View.GONE);
        activity.setSearchActionVisible(true);
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            activity.main_loader.setVisibility(View.GONE);
            activity.setSearchActionVisible(true);

            if (feedback == 1 && list != null && mactv != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_dropdown_item_1line, list);
                mactv.setAdapter(adapter);
                mactv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                mactv.animate();
                mactv.showDropDown();
            }
        } catch (Exception ignored) {
            // Ignored because fragment is not visible anymore
        }
    }
}