package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.OKHttp;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;

import java.util.Hashtable;

/**
 * AsyncTask to Authenticate User from Server
 */
public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

    private MainActivity mainActivity;
    private AgentSession agentSession;
    private Dialog dialog;
    private int keep_logged;
    private String email, pass, user_type;

    /**
     * Login User
     *
     * @param activity    MainActivity class
     * @param email       Valid email address
     * @param pass        Valid password
     * @param user_type   0 = user and 1 = agent
     * @param keep_logged Set 'true' to login automatically on application startup
     */
    public UserLoginTask(final MainActivity activity, String email, String pass, String user_type, int keep_logged) {
        this.mainActivity = activity;
        this.keep_logged = keep_logged;
        this.email = email;
        this.pass = pass;
        this.user_type = user_type;
    }

    @Override
    protected void onPreExecute() {
        PreferenceHelper.setString(mainActivity, PreferenceHelper.LOGGED_EMAIL, email);
        dialog = DialogHelper.createProgressDialog(mainActivity, R.string.logging_in,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!UserLoginTask.this.isCancelled()) UserLoginTask.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;
            Hashtable<String, Object> post_params = new Hashtable<>();
            post_params.put("rt_agent_email", email);
            post_params.put("rt_agent_pwd", pass);
            post_params.put("user_type", user_type);
            OKHttp http = new OKHttp();
            String responseContent = http.postCall(mainActivity.getString(R.string.login_user), post_params);
            if (responseContent != null && http.responseCode == 200) {
                switch (responseContent) {
                    case "":
                        return 5;
                    case "2":
                        return 2;
                    case "3":
                        return 3;
                    case "4":
                        return 4;
                    default:
                        agentSession = JsonConverter.parseJsonToAgentSession(responseContent);
                        post_params = new Hashtable<>();
                        post_params.put("RT_WBS_AGENT_ID", agentSession.AGENT_ID);
                        post_params.put("RT_WBS_PARENT_AGENT_ID", agentSession.AGENT_PARENT_ID);
                        responseContent = http.postCall(mainActivity.getString(R.string.login_user_credit), post_params);
                        agentSession = JsonConverter.parseJsonToAgentCredit(agentSession, responseContent);
                        return 1;
                }
            } else return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 6;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        dialog.dismiss();
    }

    @Override
    protected void onPostExecute(final Integer feedback) {
        super.onPostExecute(feedback);
        try {
            dialog.dismiss();
            String msg;
            switch (feedback) {
                case 0:
                    mainActivity.createNoNetworkDialog();
                    return;
                case 1:
                    // Save User Preferences
                    PreferenceHelper.setAgentSession(mainActivity, agentSession);
                    PreferenceHelper.setString(mainActivity, PreferenceHelper.LOGGED_PASS, pass);
                    PreferenceHelper.setString(mainActivity, PreferenceHelper.LOGGED_USER_TYPE, user_type);
                    PreferenceHelper.setInt(mainActivity, PreferenceHelper.KEEP_LOGGED, keep_logged);

                    PhoneFunctionality.vibrateMobile(mainActivity);
                    PhoneFunctionality.makeToast(mainActivity, mainActivity.getString(R.string.login_success));

                    // Login Success
                    mainActivity.loginSuccess();
                    return;
                case 2:
                    msg = mainActivity.getString(R.string.account_pending);
                    break;
                case 3:
                    msg = mainActivity.getString(R.string.account_deactivate);
                    break;
                case 4:
                    msg = mainActivity.getString(R.string.account_trashed);
                    break;
                case 5:
                    msg = mainActivity.getString(R.string.account_wrong_cred);
                    break;
                default:
                    msg = mainActivity.getString(R.string.error);
                    break;
            }
            PhoneFunctionality.makeToast(mainActivity, msg);
            PreferenceHelper.setInt(mainActivity, PreferenceHelper.KEEP_LOGGED, 0);
        } catch (Exception ignored) {
            // Ignored because Activity is not visible anymore
        }
    }
}
