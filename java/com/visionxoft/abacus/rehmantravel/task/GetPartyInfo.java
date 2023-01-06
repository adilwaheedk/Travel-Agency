package com.visionxoft.abacus.rehmantravel.task;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.model.ReceivableClient;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * AsyncTask to request list of receivable clients from term used
 */
public class GetPartyInfo extends AsyncTask<Void, Void, Integer> {
    private FlightReservationFragment fragment;
    private MainActivity activity;
    private MultiAutoCompleteTextView mactv;
    private String AGENT_ID, AGENT_USER_ID;

    /**
     * Get List of Receivable Clients
     *
     * @param fragment Parent Fragment Class
     * @param mactv    MultiAutoCompleteTextView to get input and display results
     */
    public GetPartyInfo(FlightReservationFragment fragment, MultiAutoCompleteTextView mactv, String AGENT_ID, String AGENT_USER_ID) {
        this.fragment = fragment;
        this.activity = (MainActivity) fragment.getActivity();
        this.mactv = mactv;
        this.AGENT_ID = AGENT_ID;
        this.AGENT_USER_ID = AGENT_USER_ID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        OKHttp sh = new OKHttp();
        try {
            Hashtable<String, Object> params = new Hashtable<>();
            params.put("RT_WBS_AGENT_ID", AGENT_ID);
            params.put("RT_WBS_AGENT_USER_ID", AGENT_USER_ID);
            String responseContent = sh.postCall(activity.getString(R.string.get_receivable_client), params);
            if (responseContent != null && sh.responseCode == 200) {
                fragment.receivableClientList = JsonConverter.parseJsonToReceivableClient(responseContent);
                return 1;
            } else return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mactv.dismissDropDown();
    }

    @Override
    protected void onPostExecute(Integer feedback) {
        super.onPostExecute(feedback);
        try {
            if (feedback == 1 && fragment.receivableClientList != null && mactv != null && mactv.hasFocus()) {
                List<String> titles = new ArrayList<>();
                for (ReceivableClient client : fragment.receivableClientList)
                    titles.add(client.accountTitle);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_dropdown_item_1line, titles);
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