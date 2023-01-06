package com.visionxoft.abacus.rehmantravel.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Client;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.Country;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.OriginDestinationOption;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItineraryPricingInfo.PTC_FareBreakdowns.PTC_FareBreakdown;
import com.visionxoft.abacus.rehmantravel.model.Traveler;
import com.visionxoft.abacus.rehmantravel.task.SecureWebService;

import java.util.Hashtable;
import java.util.List;

public class TicketHelper {

    /**
     * Get required information and setup post parameters for generating ticket ticket
     *
     * @param fragment         Parent fragment class
     * @param _PricedItinerary PricedItinerary object
     * @param client           Client object
     * @param travelers        Array of Traveler object
     * @param countries        List of Country object
     */
    public static int preparePnrParams(Fragment fragment, PricedItinerary _PricedItinerary,
                                       Client client, Traveler[] travelers, List<Country> countries) {

        Activity activity = fragment.getActivity();
        AgentSession agentSession = PreferenceHelper.getAgentSession(activity);

        // Get required objects

        final Object obj7 = IntentHelper.getObjectForKey("tkt_noOfAdults");
        final Object obj8 = IntentHelper.getObjectForKey("tkt_noOfChilds");
        final Object obj9 = IntentHelper.getObjectForKey("tkt_noOfInfants");

        if (obj7 != null && obj8 != null && obj9 != null) {

            String tkt_noOfAdults = (String) obj7;
            String tkt_noOfChilds = (String) obj8;
            String tkt_noOfInfants = (String) obj9;

            // region Prepare Post Parameters for ticket
            Hashtable<String, Object> params = FormatParameters.setConnectApiParams(true);
            params = FormatParameters.setBusinessTypeParams(activity, params);
            params.put("isActive", "1");

            if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
                params.put("FR", activity.getString(R.string.rt_email_address));
                params.put("TO", client.resident_email);
                params.put("CC", activity.getString(R.string.rt_email_address));
                params.put("RECEIVED_FROM", "GUEST");
            } else {
                params.put("FR", agentSession.AGENT_EMAIL);
                params.put("TO", client.resident_email);
                params.put("CC", activity.getString(R.string.rt_email_address));
                params.put("RECEIVED_FROM", agentSession.AGENT_NAME);
            }

            params.put("AgencyName", "Rehman Group Of Travels");
            params.put("StreetNmbr", "Office No. Aally Plaza Fazal e Haq Road Blue Area");
            params.put("PostalCode", "44000");
            params.put("CountryName", "Pakistan");
            params.put("NoOfAdult", tkt_noOfAdults);
            params.put("NoOfChild", tkt_noOfChilds);
            params.put("NoOfInf", tkt_noOfInfants);
            params.put("TravelDate", _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption.get(0)._attr.DepartureDateTime.split("T")[0]);
            params.put("PhoneNumber", client.resident_no);

            params.put("rspnseType", "0");

            List<OriginDestinationOption> list_odo = _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption;
            for (int i = 0; i < list_odo.size(); i++) {
                String index = String.valueOf(i);
                OriginDestinationOption odo = list_odo.get(i);
                params.put("AirBookRQ[" + index + "][DepartureDate]", odo._attr.DepartureDateTime.split("T")[0]);
                params.put("AirBookRQ[" + index + "][DepartureTime]", odo._attr.DepartureDateTime.split("T")[1]);
                params.put("AirBookRQ[" + index + "][ArrivalDate]", odo._attr.ArrivalDateTime.split("T")[0]);
                params.put("AirBookRQ[" + index + "][ArrivalTime]", odo._attr.ArrivalDateTime.split("T")[1]);
                params.put("AirBookRQ[" + index + "][FlightNumber]", odo._attr.FlightNumber);
                params.put("AirBookRQ[" + index + "][NumberInParty]", String.valueOf(travelers.length));
                params.put("AirBookRQ[" + index + "][ResBookDesigCode]", odo._attr.ResBookDesigCode);
                params.put("AirBookRQ[" + index + "][Status]", "NN");
                params.put("AirBookRQ[" + index + "][AirEquipType]", odo._Equipment.AirEquipType);
                params.put("AirBookRQ[" + index + "][DestinationLocation]", odo._ArrivalAirport.LocationCode);
                params.put("AirBookRQ[" + index + "][MarketingAirlineCode]", odo._MarketingAirline.Code);
                params.put("AirBookRQ[" + index + "][OperatingAirlineCode]", odo._OperatingAirline.Code);
                params.put("AirBookRQ[" + index + "][OriginLocation]", odo._DepartureAirport.LocationCode);
            }

            List<PTC_FareBreakdown> _PTC_FareBreakdown_list = _PricedItinerary._AirItineraryPricingInfo.get(0)._PTC_FareBreakdowns._PTC_FareBreakdown;
            PTC_FareBreakdown _PTC_FareBreakdown_0 = _PTC_FareBreakdown_list.get(0);
            if (_PTC_FareBreakdown_0._PassengerFare._Vendor.Type != null) {
                params.put("RPLLSRQ[Vendor][ADT][AirLineType]", _PTC_FareBreakdown_0._PassengerFare._Vendor.AirLineType);
                params.put("RPLLSRQ[Vendor][ADT][AirLineTypeMask]", _PTC_FareBreakdown_0._PassengerFare._Vendor.AirLineTypeMask);
            }
            if (params.get("WBS_ACTION").equals("B")) {
                if (_PTC_FareBreakdown_0._PassengerFare._Parent.Type != null) {
                    params.put("RPLLSRQ[Parent][ADT][AirLineType]", _PTC_FareBreakdown_0._PassengerFare._Parent.AirLineType);
                    params.put("RPLLSRQ[Parent][ADT][AirLineTypeMask]", _PTC_FareBreakdown_0._PassengerFare._Parent.AirLineTypeMask);
                }
                if (_PTC_FareBreakdown_0._PassengerFare._P_Parent.Type != null) {
                    params.put("RPLLSRQ[P_Parent][ADT][AirLineType]", _PTC_FareBreakdown_0._PassengerFare._P_Parent.AirLineType);
                    params.put("RPLLSRQ[P_Parent][ADT][AirLineTypeMask]", _PTC_FareBreakdown_0._PassengerFare._P_Parent.AirLineTypeMask);
                }
            }

            if (_PTC_FareBreakdown_list.size() > 1) {
                PTC_FareBreakdown _PTC_FareBreakdown_1 = _PTC_FareBreakdown_list.get(1);
                if (_PTC_FareBreakdown_1._PassengerFare._Vendor.Type != null) {
                    String PSG_TYPE = _PTC_FareBreakdown_1._PassengerFare._Vendor.Type;
                    params.put("RPLLSRQ[Vendor][" + PSG_TYPE + "][AirLineType]", _PTC_FareBreakdown_1._PassengerFare._Vendor.AirLineType);
                    params.put("RPLLSRQ[Vendor][" + PSG_TYPE + "][AirLineTypeMask]", _PTC_FareBreakdown_1._PassengerFare._Vendor.AirLineTypeMask);
                }
                if (params.get("WBS_ACTION").equals("B")) {
                    if (_PTC_FareBreakdown_1._PassengerFare._Parent.Type != null) {
                        String PSG_TYPE = _PTC_FareBreakdown_1._PassengerFare._Parent.Type;
                        params.put("RPLLSRQ[Parent][" + PSG_TYPE + "][AirLineType]", _PTC_FareBreakdown_1._PassengerFare._Parent.AirLineType);
                        params.put("RPLLSRQ[Parent][" + PSG_TYPE + "][AirLineTypeMask]", _PTC_FareBreakdown_1._PassengerFare._Parent.AirLineTypeMask);
                    }
                    if (_PTC_FareBreakdown_1._PassengerFare._P_Parent.Type != null) {
                        String PSG_TYPE = _PTC_FareBreakdown_1._PassengerFare._P_Parent.Type;
                        params.put("RPLLSRQ[P_Parent][" + PSG_TYPE + "][AirLineType]", _PTC_FareBreakdown_1._PassengerFare._P_Parent.AirLineType);
                        params.put("RPLLSRQ[P_Parent][" + PSG_TYPE + "][AirLineTypeMask]", _PTC_FareBreakdown_1._PassengerFare._P_Parent.AirLineTypeMask);
                    }
                }
            }
            if (_PTC_FareBreakdown_list.size() > 2) {
                PTC_FareBreakdown _PTC_FareBreakdown_2 = _PTC_FareBreakdown_list.get(2);
                if (_PTC_FareBreakdown_2._PassengerFare._Vendor.Type != null) {
                    String PSG_TYPE = _PTC_FareBreakdown_2._PassengerFare._Vendor.Type;
                    params.put("RPLLSRQ[Vendor][" + PSG_TYPE + "][AirLineType]", _PTC_FareBreakdown_2._PassengerFare._Vendor.AirLineType);
                    params.put("RPLLSRQ[Vendor][" + PSG_TYPE + "][AirLineTypeMask]", _PTC_FareBreakdown_2._PassengerFare._Vendor.AirLineTypeMask);
                }
                if (params.get("WBS_ACTION").equals("B")) {
                    if (_PTC_FareBreakdown_2._PassengerFare._Parent.Type != null) {
                        String PSG_TYPE = _PTC_FareBreakdown_2._PassengerFare._Parent.Type;
                        params.put("RPLLSRQ[Parent][" + PSG_TYPE + "][AirLineType]", _PTC_FareBreakdown_2._PassengerFare._Parent.AirLineType);
                        params.put("RPLLSRQ[Parent][" + PSG_TYPE + "][AirLineTypeMask]", _PTC_FareBreakdown_2._PassengerFare._Parent.AirLineTypeMask);
                    }
                    if (_PTC_FareBreakdown_2._PassengerFare._P_Parent.Type != null) {
                        String PSG_TYPE = _PTC_FareBreakdown_2._PassengerFare._P_Parent.Type;
                        params.put("RPLLSRQ[P_Parent][" + PSG_TYPE + "][AirLineType]", _PTC_FareBreakdown_2._PassengerFare._P_Parent.AirLineType);
                        params.put("RPLLSRQ[P_Parent][" + PSG_TYPE + "][AirLineTypeMask]", _PTC_FareBreakdown_2._PassengerFare._P_Parent.AirLineTypeMask);
                    }
                }
            }

            for (int i = 0; i < travelers.length; i++) {
                Traveler traveler = travelers[i];
                params.put("PsgInfo[" + String.valueOf(i) + "][Prefix]", FormatParameters.prefixType(activity, traveler.type, traveler.prefix));
                params.put("PsgInfo[" + String.valueOf(i) + "][Type]", traveler.type);
                params.put("PsgInfo[" + String.valueOf(i) + "][Dob]", traveler.dob);
                params.put("PsgInfo[" + String.valueOf(i) + "][GivenName]", traveler.fname);
                params.put("PsgInfo[" + String.valueOf(i) + "][Surname]", traveler.lname);
                params.put("PsgInfo[" + String.valueOf(i) + "][Country]", countries.get(traveler.pob).countryCode);
                params.put("PsgInfo[" + String.valueOf(i) + "][TypeOfDocument]", FormatParameters.documentType(activity, traveler.doc_type));
                params.put("PsgInfo[" + String.valueOf(i) + "][TypeOfDocumentNo]", traveler.doc_id);
                params.put("PsgInfo[" + String.valueOf(i) + "][IssuingCountry]", countries.get(traveler.visa_issue).countryCode);
                params.put("PsgInfo[" + String.valueOf(i) + "][ContactNo]", countries.get(traveler.pob).countryCode);
                params.put("PsgInfo[" + String.valueOf(i) + "][Gender]", FormatParameters.genderType(traveler.gender));
                params.put("PsgInfo[" + String.valueOf(i) + "][PFexpdate]", traveler.doc_expiry);
                params.put("PsgInfo[" + String.valueOf(i) + "][CountryAreaCode]", countries.get(traveler.addr_country).countryCode);
                params.put("PsgInfo[" + String.valueOf(i) + "][CityAreaCode]", traveler.addr_zip);
            }

            // Access web service
            SecureWebService.accessAPI(fragment, params);
            return 1;
        } else return 0;
    }
}
