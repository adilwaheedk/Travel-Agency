package com.visionxoft.abacus.rehmantravel.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

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
public class UserCreditTask extends AsyncTask<Void, Void, Integer> {

    private MainActivity mainActivity;
    private AgentSession agentSession;
    private Dialog dialog;

    /**
     * Login User
     *
     * @param activity MainActivity class
     */
    public UserCreditTask(final MainActivity activity) {
        this.mainActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog = DialogHelper.createProgressDialog(mainActivity, R.string.fetch_user_credit,
                R.string.please_wait, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!UserCreditTask.this.isCancelled()) UserCreditTask.this.cancel(true);
            }
        });
    }

    @Override
    protected Integer doInBackground(Void... aVoid) {
        try {
            if (!ConnectionHelper.isConnectedToInternet(mainActivity)) return 0;

            agentSession = PreferenceHelper.getAgentSession(mainActivity);
            OKHttp http = new OKHttp();
            Hashtable<String, Object> post_params = new Hashtable<>();
            post_params.put("RT_WBS_AGENT_ID", agentSession.AGENT_ID);
            post_params.put("RT_WBS_PARENT_AGENT_ID", agentSession.AGENT_PARENT_ID);
            String responseContent = http.postCall(mainActivity.getString(R.string.login_user_credit), post_params);
            agentSession = JsonConverter.parseJsonToAgentCredit(agentSession, responseContent);
            return 1;

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
                    final Dialog dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_user_credit, Gravity.CENTER);

                    TextView user_current_credit = (TextView) dialog.findViewById(R.id.user_current_credit);
                    TextView user_total_credit = (TextView) dialog.findViewById(R.id.user_total_credit);
                    TextView user_used_credit = (TextView) dialog.findViewById(R.id.user_used_credit);
                    View btn_back_user_credit = dialog.findViewById(R.id.btn_back_user_credit);

                    user_current_credit.setText(agentSession.CurrentCredit);
                    user_total_credit.setText(agentSession.TotalCredit);
                    user_used_credit.setText(agentSession.UsedCredit);

                    btn_back_user_credit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    return;
                default:
                    msg = mainActivity.getString(R.string.error);
                    break;
            }
            PhoneFunctionality.makeToast(mainActivity, msg);
        } catch (Exception ignored) {
            // Ignored because Activity is not visible anymore
        }
    }
}
