package com.visionxoft.abacus.rehmantravel.model;

/**
 * Model class to store User/Agent attributes
 */
public class AgentSession {

    public String AGENT_USER_ID;
    public String AGENT_PARENT_ID;
    public String AGENT_PARENT_OF_PARENT_ID;
    public String AGENT_ID;
    public String AGENT_NAME;
    public String AGENT_EMAIL;
    public String AGENT_RIGHTS;
    public String AGENT_LOGED_IN_TYPE;
    public String AGENT_LOGGED_IN;
    public String AGENT_AGENCY_USER;
    public String AMS_AGENT_CODE;
    public String RT_AGENT_KEY;
    public String TotalCredit;
    public String CurrentCredit;
    public String UsedCredit;

    public AgentSession() {
        AGENT_NAME = Constants.GUEST_NAME;
        AGENT_ID = Constants.GUEST_ID;
        RT_AGENT_KEY = Constants.GUEST_KEY;
        TotalCredit = CurrentCredit = UsedCredit = "N/A";
    }
}
