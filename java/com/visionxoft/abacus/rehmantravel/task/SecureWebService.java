package com.visionxoft.abacus.rehmantravel.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.fragment.FlightBookFragment;
import com.visionxoft.abacus.rehmantravel.fragment.SearchFragment;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;

import java.util.Hashtable;

/**
 * SecureWebService class works like a security layer that checks user identity by comparing KEY
 * from Server
 */
public class SecureWebService {

    private static SearchFlightsTask searchFlights;

    /**
     * Access API when user key is not null
     *
     * @param fragment Parent Fragment
     * @param data     Any data you want to pass
     */

    public static void accessAPI(Fragment fragment, Object data) {
        accessAPI(fragment, data, null);
    }

    /**
     * Access API when user key is not null
     *
     * @param fragment Parent Fragment
     * @param data     Any data you want to pass
     * @param extra    Extra data you want to pass
     */
    public static void accessAPI(Fragment fragment, Object data, Object extra) {
        Context context = fragment.getContext();
        String key = PreferenceHelper.getAgentSession(context).RT_AGENT_KEY;
        if (key != null) {
            // Stop Flight Searching
            if (data instanceof PricedItinerary && extra != null) stopSearchFlightsAsyncTask();

            // Check User Status
            new CheckUserStatus(fragment, data, extra, key).execute();
        } else {
            logOutUser(fragment.getActivity());
        }
    }

    /**
     * Stop Flight
     */
    public static void stopSearchFlightsAsyncTask() {
        if (searchFlights != null) searchFlights.cancel(true);
    }

    /**
     * AsyncTask that request User Status by comparing user unique KEY and last API access
     * timestamp (i.e. 30 minutes timeout)
     */
    private static class CheckUserStatus extends AsyncTask<String, Void, Integer> {
        private Fragment fragment;
        private MainActivity mainActivity;
        private String agent_session_key;
        private Object data;
        private Object extra;

        /**
         * Check user status
         *
         * @param fragment          Parent Fragment
         * @param data              Data that you want to pass
         * @param extra             Extra data that you want to pass
         * @param agent_session_key Unique session key (auto-generated from server)
         */
        CheckUserStatus(Fragment fragment, Object data, Object extra, String agent_session_key) {
            this.fragment = fragment;
            this.mainActivity = (MainActivity) fragment.getActivity();
            this.data = data;
            this.extra = extra;
            this.agent_session_key = agent_session_key;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;
                if (agent_session_key.equals(Constants.GUEST_KEY)) return 1;
                Hashtable<String, Object> post_params = new Hashtable<>();
                post_params.put("RT_AGENT_KEY", agent_session_key);
                OKHttp sh = new OKHttp();
                String responseContent = sh.postCall(fragment.getString(R.string.get_user_status), post_params);
                if (responseContent != null && sh.responseCode == 200)
                    if (responseContent.equals("1")) return 1;
                    else return 2;
                else return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return 3;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Integer feedback) {
            super.onPostExecute(feedback);
            try {
                switch (feedback) {
                    case 0:
                        mainActivity.createNoNetworkDialog();
                        break;
                    case 1:
                        // User Authorized, go to desired task
                        if (fragment instanceof SearchFragment) {
                            if (data instanceof Hashtable<?, ?>) {
                                searchFlights = new SearchFlightsTask(
                                        (SearchFragment) fragment, (Hashtable<String, Object>) data);
                                searchFlights.execute();
                            } else if (data instanceof PricedItinerary) {
                                new GetFlightExtraInfo((SearchFragment) fragment, (PricedItinerary) data, extra).execute();
                            } else {
                                mainActivity.startActivity(new Intent(mainActivity, MainActivity.class));
                            }
                        } else if (fragment instanceof FlightBookFragment) {
                            new GeneratePNR((FlightBookFragment) fragment, (Hashtable<String, Object>) data).execute();
                        }
                        break;
                    case 2:
                        logOutUser(mainActivity);
                        break;
                    case 3:
                        PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.error));
                        break;
                }
            } catch (Exception ignored) {
                // Ignored because fragment is not visible anymore
            }
        }
    }

    /**
     * Logout active user or agent
     *
     * @param activity Context
     */
    private static void logOutUser(Activity activity) {
        PhoneFunctionality.makeToast(activity, activity.getString(R.string.please_login_again), true);
        PreferenceHelper.clearAgentSession(activity);
        PreferenceHelper.clearValues(activity, new String[]{"keep_logged", "keep_logged_agent_pass", "keep_logged_user_type"});
        activity.startActivity(new Intent(activity, MainActivity.class));
    }
}
