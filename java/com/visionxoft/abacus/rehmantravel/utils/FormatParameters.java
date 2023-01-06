package com.visionxoft.abacus.rehmantravel.utils;

import android.content.Context;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;

import java.util.Hashtable;

/**
 * Formatter class to format or map values required in Project
 */
public class FormatParameters {

    /**
     * Prepare Post Parameters for API to connect
     *
     * @return Post Parameters
     */
    public static Hashtable<String, Object> setConnectApiParams(boolean includeAction) {
        Hashtable<String, Object> post_params = new Hashtable<>();
        post_params.put("WebConnectEmail", Constants.WEB_CONNECT_EMAIL);
        post_params.put("WebConnectPwd", Constants.WEB_CONNECT_PWD);
        post_params.put("WebConnectUrl", Constants.WEB_CONNECT_URL);
        post_params.put("IP", Constants.WEB_CONNECT_IP);
        if (includeAction) post_params.put("Acttion", Constants.WEB_ACTION);
        return post_params;
    }

    public static Hashtable<String, Object> setBusinessTypeParams(Context context, Hashtable<String, Object> post_params) {
        AgentSession agentSession = PreferenceHelper.getAgentSession(context);
        String WBS_ACTION, WBS_VENDOR_ID, WBS_AGENT_ID, WBS_PARENT_ID, WBS_USER_ID;
        if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
            WBS_ACTION = "C";
            WBS_VENDOR_ID = "1";
            WBS_AGENT_ID = WBS_PARENT_ID = WBS_USER_ID = "0";
        }
        //else if (!agentSession.AGENT_LOGED_IN_TYPE.equals("RT_BK_WBS_AGENT")) {
        //WBS_ACTION = "B";
        //WBS_VENDOR_ID = agentSession.AGENT_ID;
        //WBS_AGENT_ID = agentSession.AGENT_ID;
        //WBS_PARENT_ID = agentSession.AGENT_PARENT_ID;
        //WBS_USER_ID = agentSession.AGENT_USER_ID;
        //}
        else {
            WBS_ACTION = "B";
            WBS_VENDOR_ID = agentSession.AGENT_AGENCY_USER;
            WBS_AGENT_ID = agentSession.AGENT_ID;
            WBS_PARENT_ID = agentSession.AGENT_PARENT_ID;
            WBS_USER_ID = agentSession.AGENT_USER_ID;
        }
        post_params.put("WBS_ACTION", WBS_ACTION);
        post_params.put("WBS_VENDOR_ID", WBS_VENDOR_ID);
        post_params.put("WBS_AGENT_ID", WBS_AGENT_ID);
        post_params.put("WBS_PARENT_ID", WBS_PARENT_ID);
        post_params.put("WBS_USER_ID", WBS_USER_ID);

        return post_params;
    }

    /**
     * Insert required values in Xml for request of flight booking
     *
     * @param dep_datetime     Departure date time
     * @param arr_datetime     Arrival date time
     * @param flight_no        Flight phone
     * @param party_no         Number of passengers
     * @param resBookDesigCode Booking design code
     * @param dest_loc_code    Destination/Arrival location code
     * @param air_quip_type    Equipment type
     * @param mkt_airline_code Marketing airline code
     * @param mkt_flight_no    Marketing flight phone
     * @param opr_airline_code Operating airline code
     * @param orig_loc_code    Origin/Departure location code
     * @return Flight segment in Xml format
     */
    static String flightSegmentToXml(String dep_datetime, String arr_datetime, String flight_no,
                                     String party_no, String resBookDesigCode, String dest_loc_code,
                                     String air_quip_type, String mkt_airline_code, String mkt_flight_no,
                                     String opr_airline_code, String orig_loc_code) {
        return "<FlightSegment DepartureDateTime=\"" + dep_datetime + "\"" +
                " ArrivalDateTime=\"" + arr_datetime + "\" FlightNumber=\"" + flight_no + "\"" +
                " NumberInParty=\"" + party_no + "\" ResBookDesigCode=\"" + resBookDesigCode + "\"" +
                " Status=\"NN\">\n" + " <DestinationLocation LocationCode=\"" + dest_loc_code
                + "\"" + "/>\n" + " <Equipment AirEquipType=\"" + air_quip_type + "\"/>\n" +
                " <MarketingAirline Code=\"" + mkt_airline_code + "\" FlightNumber=\"" + mkt_flight_no + "\"/>\n" +
                " <OperatingAirline Code=\"" + opr_airline_code + "\"/>\n" +
                " <OriginLocation LocationCode=\"" + orig_loc_code + "\"/>\n" + "</FlightSegment>";
    }

    /**
     * Return mapping of gender type value
     *
     * @param gender_type 0=M (Male), 1=F (Female)
     * @return Mapped value
     */
    static String genderType(int gender_type) {
        switch (gender_type) {
            case 0:
                return "M";
            case 1:
                return "F";
            default:
                return "M";
        }
    }

    /**
     * Return mapping of contact type value
     *
     * @param contact_type Mobile=M, Home=H, Business=W, Other=0
     * @return Mapped value
     */
    public static String contactType(String contact_type) {
        switch (contact_type) {
            case "Mobile":
                return "M";
            case "Home":
                return "H";
            case "Business":
                return "W";
            default:
                return "O";
        }
    }

    /**
     * Return type of segment of passenger
     *
     * @param context       Context
     * @param traveler_type Type of traveler (ADULT/CHILD/INFANT)
     * @param prefix_loc    Position of value in array
     * @return Mapped value
     */
    static String prefixType(Context context, String traveler_type, int prefix_loc) {
        if (!traveler_type.equals("ADULT")) {
            if (prefix_loc == 0) return "MSTR";
            else
                return context.getResources().getStringArray(R.array.prefixNonAdultValues)[prefix_loc];
        } else return context.getResources().getStringArray(R.array.prefixAdultValues)[prefix_loc];

    }

    /**
     * Return type of document
     *
     * @param context      Context
     * @param doc_type_loc Position of value in array
     * @return Mapped value
     */
    static String documentType(Context context, int doc_type_loc) {
        String doc_type = context.getResources().getStringArray(R.array.documentTypeValues)[doc_type_loc];
        if (doc_type.equals("Passport")) return "P";
        else return "F";
    }

    public static String prepareMessage(Context context, String header, String PNR, String total_travelers,
                                        String departure, String book_date, String footer) {
        String message = "";
        if (header != null) message += header + "\n";
        message += "Booking Reference: " + PNR;
        message += "\nNumber of Passengers: " + total_travelers;
        message += "\nDeparture from: " + departure;
        message += "\nDeparture date: " + book_date;
        message += "\nUAN: " + context.getString(R.string.rt_phone_number) + "\n";
        if (footer == null) message += context.getString(R.string.rt_sms_sent);
        else message += footer;
        return message;
    }

    public static String getDeviceInfo(Context context) {
        String str = "<br>------------ DEVICE INFORMATION ------------";
        String ipAddress = ConnectionHelper.getIPAddress(true);
        if (ipAddress != null && !ipAddress.equals("")) str += "<br>IP Address: " + ipAddress;
        String macAddress = ConnectionHelper.getMACAddress(null);
        if (!macAddress.equals("")) str += "<br>MAC Address: " + macAddress;
        String cur_loc = PreferenceHelper.getString(context, PreferenceHelper.CURRENT_LOCATION);
        if (!cur_loc.equals("")) str += "<br>Location: " + cur_loc;
        str += "<br>------------------------------------------------------------<br>";
        return str;
    }
}
