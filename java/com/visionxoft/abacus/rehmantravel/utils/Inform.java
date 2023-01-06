package com.visionxoft.abacus.rehmantravel.utils;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.AgentParent;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;

import java.util.Hashtable;

public class Inform {

    public static void sendTextToClient(MainActivity mainActivity, AgentSession agentSession, String PNR, String client_number) {
        if (PreferenceHelper.getInt(mainActivity, PreferenceHelper.GRANT_SEND_SMS) == 1) {
            String footer, header;
            header = mainActivity.getString(R.string.pnr_book);
            if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY))
                footer = "*This message is sent from " + mainActivity.getString(R.string.text_pnr_guest);
            else
                footer = "*This message is sent from " + agentSession.AGENT_NAME + " (" + agentSession.AGENT_EMAIL + ")";

            String textMessage = FormatParameters.prepareMessage(mainActivity, header, PNR,
                    (String) IntentHelper.getObjectForKey("total_travelers"),
                    (String) IntentHelper.getObjectForKey("first_sector"),
                    (String) IntentHelper.getObjectForKey("departure_date"), footer);
            PhoneFunctionality.sendMessage(client_number, textMessage);
        }
    }

    public static void sendEmailToManagement(MainActivity mainActivity, String ePnr, String webview_ticket_html,
                                             AgentSession agentSession) throws Exception {
        //String info = FormatParameters.getAgentInfo(agentSession);
        //info += "<br>Client Contact: " + client.resident_no;
        //info += "<br>Client Email: " + client.resident_email;
        //info += FormatParameters.getDeviceInfo(mainActivity);

        Hashtable<String, Object> post_params = new Hashtable<>();
        post_params.put("ePnr", ePnr);
        post_params.put("mFrom", agentSession.AGENT_EMAIL);
        post_params.put("htmlDataSet", webview_ticket_html);
        post_params.put("mTo", mainActivity.getString(R.string.rt_email_address));
        OKHttp http = new OKHttp();
        http.postCall(mainActivity.getString(R.string.send_email_pnr), post_params);
    }

    public static void sendEmailAndTextToParentAgent(MainActivity mainActivity, String ePnr, String webview_ticket_html,
                                                     String PNR, AgentSession agentSession) throws Exception {
        if (!agentSession.AGENT_ID.equals(Constants.GUEST_ID) && !agentSession.AGENT_PARENT_ID.equals("")
                && !agentSession.AGENT_PARENT_ID.equals("0") && !agentSession.AGENT_PARENT_ID.equals("1")) {
            OKHttp http = new OKHttp();
            String responseContent = http.getCall(mainActivity.getString(R.string.get_api_encode) + agentSession.AGENT_PARENT_ID);
            if (responseContent != null && http.responseCode == 200) {
                Hashtable<String, Object> post_params = new Hashtable<>();
                post_params.put("agentId", responseContent);
                responseContent = http.postCall(mainActivity.getString(R.string.login_user_detail), post_params);
                if (!responseContent.equals("0")) {

                    AgentParent agentParent = JsonConverter.parseJsonToParentAgentDetails(responseContent);
                    IntentHelper.addObjectForKey(agentParent, "agentParent");

                    //String info = FormatParameters.getAgentInfo(agentSession);
                    //info += "<br>Client Contact: " + client.resident_no;
                    //info += "<br>Client Email: " + client.resident_email;
                    post_params = new Hashtable<>();
                    post_params.put("ePnr", ePnr);
                    post_params.put("mFrom", agentSession.AGENT_EMAIL);
                    post_params.put("htmlDataSet", webview_ticket_html);
                    post_params.put("mTo", agentParent.email);
                    http.postCall(mainActivity.getString(R.string.send_email_pnr), post_params);

                    if (PreferenceHelper.getInt(mainActivity, PreferenceHelper.GRANT_SEND_SMS) == 1) {
                        String footer, header;
                        header = mainActivity.getString(R.string.pnr_book);
                        footer = "*This message is sent from " + agentSession.AGENT_NAME + " (" + agentSession.AGENT_EMAIL + ")";
                        String textMessage = FormatParameters.prepareMessage(mainActivity, header, PNR,
                                (String) IntentHelper.getObjectForKey("total_travelers"),
                                (String) IntentHelper.getObjectForKey("first_sector"),
                                (String) IntentHelper.getObjectForKey("departure_date"), footer);
                        PhoneFunctionality.sendMessage(agentParent.phoneNo, textMessage);
                    }
                }
            } else throw new Exception();
        }
    }

    private void sendEmailToClient(MainActivity mainActivity, String ePnr, String webview_ticket_html, AgentSession agentSession, String client_mail) throws Exception {
        Hashtable<String, Object> post_params = new Hashtable<>();
        post_params.put("ePnr", ePnr);
        post_params.put("mFrom", agentSession.AGENT_EMAIL);
        post_params.put("htmlDataSet", webview_ticket_html);
        post_params.put("mTo", client_mail);
        OKHttp http = new OKHttp();
        String response = http.postCall(mainActivity.getString(R.string.send_email_pnr), post_params);
        if (response == null || http.responseCode != 200) throw new Exception();
    }
}
