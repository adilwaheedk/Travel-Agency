package com.visionxoft.abacus.rehmantravel.utils;

import com.visionxoft.abacus.rehmantravel.model.AgentParent;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.AirlineName;
import com.visionxoft.abacus.rehmantravel.model.AirportLocation;
import com.visionxoft.abacus.rehmantravel.model.Country;
import com.visionxoft.abacus.rehmantravel.model.HotelPrice;
import com.visionxoft.abacus.rehmantravel.model.MadinaHotel;
import com.visionxoft.abacus.rehmantravel.model.MakkahHotel;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.*;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItineraryPricingInfo.PTC_FareBreakdowns.*;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItineraryPricingInfo.PTC_FareBreakdowns.PTC_FareBreakdown.FareBasisCodes.*;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItinerary.OriginDestinationOptions.OriginDestinationOption;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItineraryPricingInfo.PTC_FareBreakdowns.PTC_FareBreakdown.PassengerFare.Taxes;
import com.visionxoft.abacus.rehmantravel.model.ReceivableClient;
import com.visionxoft.abacus.rehmantravel.model.TravelItinerary;
import com.visionxoft.abacus.rehmantravel.model.TravelItinerary.*;
import com.visionxoft.abacus.rehmantravel.model.TravelItinerary.CustomerInfo.PersonName;
import com.visionxoft.abacus.rehmantravel.model.TravelItinerary.ItineraryInfo.ItineraryPricing.PriceQuote;
import com.visionxoft.abacus.rehmantravel.model.TravelItinerary.ItineraryInfo.ItineraryPricing.PriceQuote.PTC_FareBreakdown.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class responsible of Converting JSON String to Objects by mapping required values.
 */
public class JsonConverter {

    /**
     * Get String value from Simple JSON Object
     *
     * @param jsonData Json object to get value from
     * @param name     Name of key mapped by required value
     * @return Value of type String
     * @throws JSONException
     */
    public static String getValueFromJSON(String jsonData, String name) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        return jsonObject.getString(name);
    }

    /**
     * Get String value from Simple JSON Object
     *
     * @param jsonData Json object to get value from
     * @param term     Term contain in name of key mapped by required value
     * @return Value of type String
     * @throws JSONException
     */
    public static String getValueFromJSONByKey(String jsonData, String term) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.contains(term)) return jsonObject.getString(key);
        }
        return null;
    }

    /**
     * Mapping of Json string to list of strings
     *
     * @param jsonData Json data to parse
     * @return List of string
     * @throws JSONException
     */
    public static List<String> parseJsonToLabelStrings(String jsonData) throws JSONException {
        List<String> list = new ArrayList<>();
        if (jsonData.startsWith("[")) {
            JSONArray jsonArr = new JSONArray(jsonData);
            for (int i = 0; i < jsonArr.length(); i++)
                list.add(jsonArr.getString(0).trim());
        } else {
            list.add(new JSONObject(jsonData).getString("label").trim());
        }
        return list;
    }

    /**
     * Mapping of Json string to airlines
     *
     * @param jsonData Json data to parse
     * @return List of string
     * @throws JSONException
     */
    public static List<String> parseJsonToAirlines(String jsonData) throws JSONException {
        List<String> list = new ArrayList<>();
        if (jsonData.startsWith("[")) {
            JSONArray jsonArr = new JSONArray(jsonData);
            for (int i = 0; i < jsonArr.length(); i++)
                list.add(jsonArr.getJSONObject(i).getString("label").trim());
        } else {
            list.add(new JSONObject(jsonData).getString("label").trim());
        }
        return list;
    }

    /**
     * Mapping of Json string to list of Receivable Clients
     *
     * @param jsonData Json data to parse
     * @return List of Receivable Clients
     * @throws JSONException
     */
    public static List<ReceivableClient> parseJsonToReceivableClient(String jsonData) throws JSONException {
        JSONArray jsonArr = new JSONArray(jsonData);
        List<ReceivableClient> list = new ArrayList<>();
        ReceivableClient client;
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObject = jsonArr.getJSONObject(i);
            client = new ReceivableClient();
            client.accountId = jsonObject.getString("accountId");
            client.accountTitle = jsonObject.getString("accountTitle");
            client.code = jsonObject.getString("code");
            client.value = jsonObject.getString("value");
            client.label = jsonObject.getString("label");
            list.add(client);
        }
        return list;
    }

    /**
     * Mapping of Json string to list of Makkah Hotels
     *
     * @param jsonData Json data to parse
     * @return List of Makkah Hotels
     * @throws JSONException
     */
    public static List<MakkahHotel> parseJsonToMakkahHotel(String jsonData) throws JSONException {
        JSONArray jsonArr = new JSONArray(jsonData);
        List<MakkahHotel> list = new ArrayList<>();
        MakkahHotel hotel;
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObject = jsonArr.getJSONObject(i);
            hotel = new MakkahHotel();
            hotel.hotelId = jsonObject.getString("hotelId");
            hotel.mkhotelName = jsonObject.getString("mkhotelName");
            hotel.mkhotelLocation = jsonObject.getString("mkhotelLocation");
            hotel.mkhotelTypeId = jsonObject.getString("mkhotelTypeId");
            hotel.mkro_bb = jsonObject.getString("mkro_bb");
            hotel.mkhotelImage = jsonObject.getString("mkhotelImage");
            hotel.mkdblImage = jsonObject.getString("mkdblImage");
            hotel.mktrplrImage = jsonObject.getString("mktrplrImage");
            hotel.mkqudrImage = jsonObject.getString("mkqudrImage");
            hotel.mkdistance = jsonObject.getString("mkdistance");
            hotel.mkhotelDesc = jsonObject.getString("mkhotelDesc");
            hotel.id = jsonObject.getString("id");
            hotel.value = jsonObject.getString("value");
            hotel.label = jsonObject.getString("label");
            list.add(hotel);
        }
        return list;
    }

    /**
     * Mapping of Json string to list of Madina Hotels
     *
     * @param jsonData Json data to parse
     * @return List of Madina Hotels
     * @throws JSONException
     */
    public static List<MadinaHotel> parseJsonToMadinaHotel(String jsonData) throws JSONException {
        JSONArray jsonArr = new JSONArray(jsonData);
        List<MadinaHotel> list = new ArrayList<>();
        MadinaHotel hotel;
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObject = jsonArr.getJSONObject(i);
            hotel = new MadinaHotel();
            hotel.hotelId = jsonObject.getString("hotelId");
            hotel.mdhotelName = jsonObject.getString("mdhotelName");
            hotel.mdhotelLocation = jsonObject.getString("mdhotelLocation");
            hotel.mdhotelTypeId = jsonObject.getString("mdhotelTypeId");
            hotel.mdro_bb = jsonObject.getString("mdro_bb");
            hotel.mdhotelImage = jsonObject.getString("mdhotelImage");
            hotel.mddblImage = jsonObject.getString("mddblImage");
            hotel.mdtrplImage = jsonObject.getString("mdtrplImage");
            hotel.mdqudImage = jsonObject.getString("mdqudImage");
            hotel.mddistance = jsonObject.getString("mddistance");
            hotel.mdhotelDesc = jsonObject.getString("mdhotelDesc");
            hotel.id = jsonObject.getString("id");
            hotel.value = jsonObject.getString("value");
            hotel.label = jsonObject.getString("label");
            list.add(hotel);
        }
        return list;
    }

    /**
     * Mapping of Json string to list of Makkah Hotels
     *
     * @param jsonData Json data to parse
     * @return List of Makkah Hotels
     * @throws JSONException
     */
    public static HotelPrice parseJsonToHotelPrice(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        HotelPrice price = new HotelPrice();
        price.mkhotel_rateId = jsonObject.getString("mkhotel_rateId");
        price.mkhotelId = jsonObject.getString("mkhotelId");
        price.mkprdFrom = jsonObject.getString("mkprdFrom");
        price.mkprdTo = jsonObject.getString("mkprdTo");
        price.mkdblrprice = jsonObject.getString("mkdblrprice");
        price.mkmarkdbprice = jsonObject.getString("mkmarkdbprice");
        price.mkdblImage = jsonObject.getString("mkdblImage");
        price.mktrplrprice = jsonObject.getString("mktrplrprice");
        price.mkmarktrplrprice = jsonObject.getString("mkmarktrplrprice");
        price.mkqudrprice = jsonObject.getString("mkqudrprice");
        price.mkmarkqudrprice = jsonObject.getString("mkmarkqudrprice");
        price.mdhotelId = jsonObject.getString("mdhotelId");
        price.mdprdFrom = jsonObject.getString("mdprdFrom");
        price.mdprdTo = jsonObject.getString("mdprdTo");
        price.mddblrprice = jsonObject.getString("mddblrprice");
        price.mdmarkdbprice = jsonObject.getString("mdmarkdbprice");
        price.mdtrplrprice = jsonObject.getString("mdtrplrprice");
        price.mdmarktrplrprice = jsonObject.getString("mdmarktrplrprice");
        price.mdqudrprice = jsonObject.getString("mdqudrprice");
        price.mdmarkqudrprice = jsonObject.getString("mdmarkqudrprice");
        price.jdhotelId = jsonObject.getString("jdhotelId");
        price.jdprdFrom = jsonObject.getString("jdprdFrom");
        price.jdprdTo = jsonObject.getString("jdprdTo");
        price.jddblrprice = jsonObject.getString("jddblrprice");
        price.jdmarkdblrprice = jsonObject.getString("jdmarkdblrprice");
        price.jdtrplrprice = jsonObject.getString("jdtrplrprice");
        price.jdmarktrplrprice = jsonObject.getString("jdmarktrplrprice");
        price.jdqudrprice = jsonObject.getString("jdqudrprice");
        price.jdmarkqudrprice = jsonObject.getString("jdmarkqudrprice");
        return price;
    }

    /**
     * Mapping of Json string to get locality from address
     *
     * @param jsonData Json data to parse
     * @return Address locality string
     * @throws JSONException
     */
    public static String parseJsonFromAddressToLocality(String jsonData) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonData);
        if (jsonObj.getString("status").equals("OK")) {
            JSONArray jsonArr = jsonObj.getJSONArray("results");
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject object = jsonArr.getJSONObject(i);
                JSONArray address_components = object.getJSONArray("address_components");
                for (int j = 0; i < address_components.length(); j++) {
                    JSONObject address_component = address_components.getJSONObject(j);
                    if (address_component.getJSONArray("types").optString(0).equals("locality"))
                        return address_component.getString("long_name");
                }
            }
        }
        return null;
    }

    /**
     * Mapping of Json string to get list of countries
     *
     * @param jsonData Json data to parse
     * @return List of Country object
     * @throws JSONException
     */
    public static List<Country> parseJsonToCountryCodes(String jsonData) throws JSONException {
        JSONArray jsonArr = new JSONArray(jsonData);
        List<Country> list = new ArrayList<>();
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject object = jsonArr.getJSONObject(i);
            Country m = new Country();
            m.tktctrycodeId = object.getString("tktctrycodeId").trim();
            m.countryName = object.getString("countryName").trim();
            m.countryCode = object.getString("countryCode").trim();
            m.countryaccessCode = object.getString("countryaccessCode").trim();
            list.add(m);
        }
        return list;
    }

    /**
     * Mapping of Json string to get list of countries
     *
     * @param jsonData Json data to parse
     * @return List of Country object
     * @throws JSONException
     */
    public static List<String> parseJsonToCountries(String jsonData) throws JSONException {
        JSONArray jsonArr = new JSONArray(jsonData);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArr.length(); i++) {
            list.add(jsonArr.getJSONObject(i).getString("countryName").trim());
        }
        return list;
    }

    /**
     * Mapping of Json string to get logged user/agent properties
     *
     * @param jsonData Json data to parse
     * @return AgentSession object
     * @throws JSONException
     */
    public static AgentSession parseJsonToAgentSession(String jsonData) throws JSONException {
        JSONObject outer_json = new JSONObject(jsonData);
        JSONObject object = outer_json.getJSONObject("RT_BK_WBS_SESSION");
        AgentSession obj = new AgentSession();
        obj.AGENT_USER_ID = object.getString("RT_BK_WBS_AGENT_USER_ID");
        obj.AGENT_PARENT_ID = object.getString("RT_BK_WBS_AGENT_PARENT_ID");
        obj.AGENT_PARENT_OF_PARENT_ID = object.getString("RT_BK_WBS_AGENT_PARENT_OF_PARENT_ID");
        obj.AGENT_ID = object.getString("RT_BK_WBS_AGENT_ID");
        obj.AGENT_NAME = object.getString("RT_BK_WBS_AGENT_NAME");
        obj.AGENT_EMAIL = object.getString("RT_BK_WBS_AGENT_EMAIL");
        obj.AGENT_RIGHTS = object.getString("RT_BK_WBS_AGENT_RIGHTS");
        obj.AGENT_LOGED_IN_TYPE = object.getString("RT_BK_WBS_AGENT_LOGED_IN_TYPE");
        obj.AGENT_LOGGED_IN = object.getString("RT_BK_WBS_AGENT_LOGGED_IN");
        obj.AGENT_AGENCY_USER = object.getString("RT_BK_WBS_AGENT_AGENCY_USER");
        obj.AMS_AGENT_CODE = object.getString("RT_AMS_AGENT_CODE");
        obj.RT_AGENT_KEY = object.getString("RT_AGENT_KEY");
        return obj;
    }

    public static AgentSession parseJsonToAgentCredit(final AgentSession agentSession, String jsonData) throws JSONException {
        JSONObject obj = new JSONObject(jsonData);
        agentSession.TotalCredit = obj.getString("TotalCredit");
        agentSession.CurrentCredit = obj.getString("CurrentCredit");
        agentSession.UsedCredit = obj.getString("UsedCredit");
        return agentSession;
    }

    /**
     * Mapping of Json string to parent agent details
     *
     * @param jsonData Json data to parse
     * @return AgentParent object
     * @throws JSONException
     */
    public static AgentParent parseJsonToParentAgentDetails(String jsonData) throws JSONException {
        JSONObject object = new JSONObject(jsonData);
        AgentParent agentParent = new AgentParent();
        agentParent.phoneNo = object.getString("phoneNo");
        agentParent.mobileNo = object.getString("mobileNo");
        agentParent.managermobile = object.getString("managermobile");
        agentParent.accountmobile = object.getString("accountmobile");
        agentParent.ownermobile = object.getString("ownermobile");
        agentParent.email = object.getString("email");
        return agentParent;
    }

    /**
     * Mapping of Json string to get list of airport locations
     *
     * @param jsonData Json data to parse
     * @return List of AirportLocation object
     * @throws JSONException
     */
    public static List<AirportLocation> parseJsonToAirportLocation(String jsonData) throws JSONException {
        List<AirportLocation> list = new ArrayList<>();
        JSONArray jsonArr = new JSONArray(jsonData);
        for (int i = 0; i < jsonArr.length(); i++)
            list.add(getAirportLocation(jsonArr.getString(0).trim()));
        return list;
    }

    private static AirportLocation getAirportLocation(String label) {
        String[] arr = label.split(",");
        AirportLocation obj = new AirportLocation();
        obj.label = label;
        if (arr.length == 3) {
            obj.iataCode = arr[0];
            obj.location = arr[1];
            obj.country = arr[2];
        } else {
            obj.iataCode = arr[0];
            obj.location = arr[1] + ", " + arr[2];
            obj.country = arr[3];
        }
        return obj;
    }

    /**
     * Mapping of Json string to get airline names
     *
     * @param jsonData Json data to parse
     * @return List of AirlineName object
     * @throws JSONException
     */
    public static List<AirlineName> parseJsonToAirlineName(String jsonData) throws JSONException {
        if (jsonData.startsWith("[")) {
            JSONArray jsonArr = new JSONArray(jsonData);
            List<AirlineName> list = new ArrayList<>();
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject object = jsonArr.getJSONObject(i);
                AirlineName obj = new AirlineName();
                obj.countryCode = object.getString("countryCode").trim();
                obj.airLine = object.getString("airLine").trim();
                obj.value = object.getString("value").trim();
                obj.label = object.getString("label".trim());
                list.add(obj);
            }
            return list;
        } else return null;
    }

    // region Travel Itinerary

    /**
     * Mapping of Json string to Travel Itinerary
     *
     * @param jsonData Json data to parse
     * @return TravelItinerary object
     * @throws Exception
     */
    public static TravelItinerary parseJsonToTravelItinerary(String jsonData) throws JSONException {
        TravelItinerary obj = new TravelItinerary();
        JSONObject jsonObject = new JSONObject(jsonData);
        if (jsonObject.optJSONObject("TravelItineraryReadRS").optJSONObject("TravelItinerary") == null) return null;
        JSONObject _JSON_TravelItinerary = jsonObject.getJSONObject("TravelItineraryReadRS").getJSONObject("TravelItinerary");

        JSONObject JSON_ItineraryRef = _JSON_TravelItinerary.getJSONObject("ItineraryRef");
        obj._ItineraryRef.ID = JSON_ItineraryRef.getJSONObject("attr").getString("ID");
        obj._ItineraryRef.InhibitCode = JSON_ItineraryRef.getJSONObject("attr").getString("InhibitCode");
        obj._ItineraryRef.PartitionID = JSON_ItineraryRef.getJSONObject("attr").getString("PartitionID");
        obj._ItineraryRef.PrimeHostID = JSON_ItineraryRef.getJSONObject("attr").getString("PrimeHostID");
        obj._ItineraryRef._Source.AAAPseudoCityCode = JSON_ItineraryRef.getJSONObject("Source").getJSONObject("attr").getString("AAAPseudoCityCode");
        obj._ItineraryRef._Source.CreateDateTime = JSON_ItineraryRef.getJSONObject("Source").getJSONObject("attr").getString("CreateDateTime");
        obj._ItineraryRef._Source.CreationAgent = JSON_ItineraryRef.getJSONObject("Source").getJSONObject("attr").getString("CreationAgent");
        obj._ItineraryRef._Source.HomePseudoCityCode = JSON_ItineraryRef.getJSONObject("Source").getJSONObject("attr").getString("HomePseudoCityCode");
        obj._ItineraryRef._Source.PseudoCityCode = JSON_ItineraryRef.getJSONObject("Source").getJSONObject("attr").getString("PseudoCityCode");
        obj._ItineraryRef._Source.ReceivedFrom = JSON_ItineraryRef.getJSONObject("Source").getJSONObject("attr").getString("ReceivedFrom");

        JSONObject JSON_CustomerInfo = _JSON_TravelItinerary.getJSONObject("CustomerInfo");
        JSONArray _PersonName_arr = JSON_CustomerInfo.optJSONArray("PersonName");

        if (_PersonName_arr != null) {
            for (int i = 0; i < _PersonName_arr.length(); i++)
                obj._CustomerInfo._PersonNameList.add(getPersonNameFromCustomerInfo(_PersonName_arr.getJSONObject(i)));
        } else {
            obj._CustomerInfo._PersonNameList.add(getPersonNameFromCustomerInfo(JSON_CustomerInfo.getJSONObject("PersonName")));
        }
        obj._CustomerInfo._Telephone.AreaCityCode = JSON_CustomerInfo.getJSONObject("Telephone").getJSONObject("attr").getString("AreaCityCode");
        obj._CustomerInfo._Telephone.PhoneNumber = JSON_CustomerInfo.getJSONObject("Telephone").getJSONObject("attr").getString("PhoneNumber");
        obj._CustomerInfo._Telephone.RPH = JSON_CustomerInfo.getJSONObject("Telephone").getJSONObject("attr").getString("RPH");

        JSONArray AddressLine_arr = JSON_CustomerInfo.getJSONObject("Address").getJSONArray("AddressLine");
        for (int i = 0; i < AddressLine_arr.length(); i++)
            obj._CustomerInfo._AddressLineList.add(AddressLine_arr.getString(i));

        JSONObject JSON_ItineraryInfo = _JSON_TravelItinerary.getJSONObject("ItineraryInfo");
        JSONObject JSON_ReservationItems = JSON_ItineraryInfo.getJSONObject("ReservationItems").getJSONObject("Item");
        obj._ItineraryInfo._ReservationItems.Item_RPH = JSON_ReservationItems.getJSONObject("attr").getString("RPH");
        JSONObject JSON_Air_attr = JSON_ReservationItems.getJSONObject("Air").getJSONObject("attr");
        obj._ItineraryInfo._ReservationItems._Air.AirMilesFlown = JSON_Air_attr.getString("AirMilesFlown");
        obj._ItineraryInfo._ReservationItems._Air.ArrivalDateTime = JSON_Air_attr.getString("ArrivalDateTime");
        obj._ItineraryInfo._ReservationItems._Air.DepartureDateTime = JSON_Air_attr.getString("DepartureDateTime");
        obj._ItineraryInfo._ReservationItems._Air.ElapsedTime = JSON_Air_attr.getString("ElapsedTime");
        obj._ItineraryInfo._ReservationItems._Air.FlightNumber = JSON_Air_attr.getString("FlightNumber");
        obj._ItineraryInfo._ReservationItems._Air.NumberInParty = JSON_Air_attr.getString("NumberInParty");
        obj._ItineraryInfo._ReservationItems._Air.ResBookDesigCode = JSON_Air_attr.getString("ResBookDesigCode");
        obj._ItineraryInfo._ReservationItems._Air.SegmentNumber = JSON_Air_attr.getString("SegmentNumber");
        obj._ItineraryInfo._ReservationItems._Air.SmokingAllowed = JSON_Air_attr.getString("SmokingAllowed");
        obj._ItineraryInfo._ReservationItems._Air.SpecialMeal = JSON_Air_attr.getString("SpecialMeal");
        obj._ItineraryInfo._ReservationItems._Air.Status = JSON_Air_attr.getString("Status");
        obj._ItineraryInfo._ReservationItems._Air.StopQuantity = JSON_Air_attr.getString("StopQuantity");
        obj._ItineraryInfo._ReservationItems._Air.eTicket = JSON_Air_attr.getString("eTicket");

        obj._ItineraryInfo._ReservationItems.DepartureAirport_Code = JSON_Air_attr.getJSONObject("DepartureAirport").getJSONObject("attr").getString("LocationCode");
        obj._ItineraryInfo._ReservationItems.ArrivalAirport_Code = JSON_Air_attr.getJSONObject("ArrivalAirport").getJSONObject("attr").getString("LocationCode");
        obj._ItineraryInfo._ReservationItems.Equipment_AirEquipType = JSON_Air_attr.getJSONObject("Equipment").getJSONObject("attr").getString("AirEquipType");
        obj._ItineraryInfo._ReservationItems.MarketingAirline_Code = JSON_Air_attr.getJSONObject("MarketingAirline").getJSONObject("attr").getString("Code");
        obj._ItineraryInfo._ReservationItems.UpdatedDepartureTime = JSON_Air_attr.getJSONObject("UpdatedDepartureTime").getString("value");
        obj._ItineraryInfo._ReservationItems.UpdatedArrivalTime = JSON_Air_attr.getJSONObject("UpdatedArrivalTime").getString("value");
        obj._ItineraryInfo._ReservationItems.SupplierRef_ID = JSON_Air_attr.getJSONObject("SupplierRef").getJSONObject("attr").getString("ID");
        obj._ItineraryInfo._ReservationItems.Meal_Code = JSON_Air_attr.getJSONObject("Meal").getJSONObject("attr").getString("Code");

        obj._ItineraryInfo._Ticketing.RPH = JSON_ItineraryInfo.getJSONObject("Ticketing").getJSONObject("attr").getString("RPH");
        obj._ItineraryInfo._Ticketing.TicketTimeLimit = JSON_ItineraryInfo.getJSONObject("Ticketing").getJSONObject("attr").getString("TicketTimeLimit");
        JSONArray PriceQuote_arr = JSON_ItineraryInfo.getJSONObject("ItineraryPricing").optJSONArray("PriceQuote");
        if (PriceQuote_arr != null) {
            for (int i = 0; i < PriceQuote_arr.length(); i++)
                obj._ItineraryInfo._ItineraryPricing._PriceQuoteList.add(getPriceQuoteFromItineraryPricing(PriceQuote_arr.getJSONObject(i)));
        } else {
            obj._ItineraryInfo._ItineraryPricing._PriceQuoteList.add(getPriceQuoteFromItineraryPricing(JSON_ItineraryInfo.getJSONObject("ItineraryPricing").getJSONObject("PriceQuote")));
        }

        JSONArray SpecialServices_arr = _JSON_TravelItinerary.optJSONArray("SpecialServices");
        if (SpecialServices_arr != null) {
            for (int i = 0; i < SpecialServices_arr.length(); i++)
                obj._SpecialServicesList.add(getSpecialServicesFromTravelItinerary(SpecialServices_arr.getJSONObject(i)));
        } else {
            obj._SpecialServicesList.add(getSpecialServicesFromTravelItinerary(_JSON_TravelItinerary.getJSONObject("SpecialServices")));
        }

        return obj;
    }

    private static PersonName getPersonNameFromCustomerInfo(JSONObject JSON_PersonName) throws JSONException {
        PersonName obj = new PersonName();
        obj.NameNumber = JSON_PersonName.getJSONObject("attr").getString("NameNumber");
        obj.NameReference = JSON_PersonName.optJSONObject("attr").getString("NameReference");
        obj.PassengerType = JSON_PersonName.getJSONObject("attr").getString("PassengerType");
        obj.GivenName = JSON_PersonName.getJSONObject("GivenName").getString("value");
        obj.Surname = JSON_PersonName.getJSONObject("Surname").getString("value");
        JSONArray _Email_arr = JSON_PersonName.optJSONArray("Email");
        if (_Email_arr != null) {
            for (int i = 0; i < _Email_arr.length(); i++) {
                obj._EmailList.add(_Email_arr.getString(i));
            }
        }
        return obj;
    }

    private static PriceQuote getPriceQuoteFromItineraryPricing(JSONObject JSON_PriceQuote) throws JSONException {
        PriceQuote obj = new PriceQuote();
        JSONObject JSON_ItinTotalFare = JSON_PriceQuote.getJSONObject("ItinTotalFare");
        obj._ItinTotalFare._BaseFare.Amount = JSON_ItinTotalFare.getJSONObject("BaseFare").getJSONObject("attr").getString("Amount");
        obj._ItinTotalFare._BaseFare.CurrencyCode = JSON_ItinTotalFare.getJSONObject("BaseFare").getJSONObject("attr").getString("CurrencyCode");
        obj._ItinTotalFare._Taxes.Tax_Amount = JSON_ItinTotalFare.getJSONObject("Taxes").getJSONObject("Tax").getJSONObject("attr").getString("Amount");
        obj._ItinTotalFare._Taxes.Tax_TaxCode = JSON_ItinTotalFare.getJSONObject("Taxes").getJSONObject("Tax").getJSONObject("attr").getString("TaxCode");
        JSONArray TaxBreakdownCode_arr = JSON_ItinTotalFare.getJSONObject("Taxes").getJSONArray("TaxBreakdownCode");
        for (int i = 0; i < TaxBreakdownCode_arr.length(); i++)
            obj._ItinTotalFare._Taxes._TaxBreakdownCodeList.add(TaxBreakdownCode_arr.getString(i));
        obj._ItinTotalFare.TotalFare_Amount = JSON_ItinTotalFare.getJSONObject("TotalFare").getJSONObject("attr").getString("Amount");
        obj._ItinTotalFare.TotalFare_CurrencyCode = JSON_ItinTotalFare.getJSONObject("TotalFare").getJSONObject("attr").getString("CurrencyCode");


        JSONObject JSON_PTC_FareBreakdown = JSON_PriceQuote.getJSONObject("PTC_FareBreakdown");
        obj._PTC_FareBreakdown.PassengerTypeQuantity_Code = JSON_PTC_FareBreakdown.getJSONObject("PassengerTypeQuantity").getJSONObject("attr").getString("Code");
        obj._PTC_FareBreakdown.PassengerTypeQuantity_Quantity = JSON_PTC_FareBreakdown.getJSONObject("PassengerTypeQuantity").getJSONObject("attr").getString("Quantity");
        obj._PTC_FareBreakdown.FareBasis_Code = JSON_PTC_FareBreakdown.getJSONObject("FareBasis").getJSONObject("attr").getString("Code");
        obj._PTC_FareBreakdown.FareCalculation_value = JSON_PTC_FareBreakdown.getJSONObject("FareCalculation").getJSONObject("Text").getString("value");
        obj._PTC_FareBreakdown.Endorsements_value = JSON_PTC_FareBreakdown.getJSONObject("Endorsements").getJSONObject("Text").getString("value");
        JSONArray FlightSegment_arr = JSON_PTC_FareBreakdown.getJSONArray("FlightSegment");
        for (int i = 0; i < FlightSegment_arr.length(); i++)
            obj._PTC_FareBreakdown._FlightSegmentList.add(getFlightSegmentFromPriceQuote(FlightSegment_arr.getJSONObject(i)));
        return obj;
    }

    private static FlightSegment getFlightSegmentFromPriceQuote(JSONObject JSON_FlightSegment) throws JSONException {
        FlightSegment obj = new FlightSegment();

        obj.DepartureAirport_LocationCode = JSON_FlightSegment.getJSONObject("DepartureAirport").getJSONObject("attr").getString("LocationCode");
        JSONObject JSON_Flight_attr = JSON_FlightSegment.optJSONObject("attr");
        if (JSON_Flight_attr != null) {
            obj.Status = JSON_FlightSegment.getJSONObject("attr").getString("Status");
            obj.ConnectionInd = JSON_FlightSegment.getJSONObject("attr").getString("ConnectionInd");
            obj.DepartureDateTime = JSON_FlightSegment.getJSONObject("attr").getString("DepartureDateTime");
            obj.FlightNumber = JSON_FlightSegment.getJSONObject("attr").getString("FlightNumber");
            obj.SegmentNumber = JSON_FlightSegment.getJSONObject("attr").getString("SegmentNumber");
            obj.ResBookDesigCode = JSON_FlightSegment.getJSONObject("attr").getString("ResBookDesigCode");
            obj.MarketingAirline_Code = JSON_FlightSegment.getJSONObject("MarketingAirline").getJSONObject("attr").getString("Code");
            obj.MarketingAirline_FlightNumber = JSON_FlightSegment.getJSONObject("MarketingAirline").getJSONObject("attr").getString("FlightNumber");
            obj.FareBasis_Code = JSON_FlightSegment.getJSONObject("FareBasis").getJSONObject("attr").getString("Code");
            obj.ValidityDates_NotValidBefore = JSON_FlightSegment.getJSONObject("ValidityDates").getJSONObject("NotValidBefore").getString("value");
            obj.ValidityDates_NotValidAfter = JSON_FlightSegment.getJSONObject("ValidityDates").getJSONObject("NotValidAfter").getString("value");
            obj.BaggageAllowance_Number = JSON_FlightSegment.getJSONObject("BaggageAllowance").getJSONObject("attr").getString("Number");
        }
        return obj;
    }

    private static SpecialServices getSpecialServicesFromTravelItinerary(JSONObject JSON_SpecialServices) throws JSONException {
        SpecialServices obj = new SpecialServices();
        obj.ItemRPH = JSON_SpecialServices.getJSONObject("attr").getString("ItemRPH");
        obj.Type = JSON_SpecialServices.getJSONObject("attr").getString("Type");
        obj.Service_SSRCode = JSON_SpecialServices.getJSONObject("Service").getJSONObject("attr").getString("SSRCode");
        obj.Service_SSRType = JSON_SpecialServices.getJSONObject("Service").getJSONObject("attr").getString("SSRType");
        obj.Name_value = JSON_SpecialServices.getJSONObject("Name").getString("value");
        obj.Name_Number = JSON_SpecialServices.getJSONObject("Name").getJSONObject("attr").getString("Number");
        obj.Airline_Code = JSON_SpecialServices.getJSONObject("Airline").getJSONObject("attr").getString("Code");
        obj.Text_value = JSON_SpecialServices.getJSONObject("Text").getString("value");
        return obj;
    }

    // endregion

    // region Priced Itineraries

    /**
     * Mapping of Json string to get list of Priced Itineraries
     *
     * @param jsonData Json data to parse
     * @return List of PricedItinerary object
     * @throws Exception
     */
    public static List<PricedItinerary> parseJsonToPricedItinerary(String jsonData) throws Exception {
        if (jsonData.contains("\"PricedItineraries\":{\"PricedItinerary\":null}")) return null;
        List<PricedItinerary> list_PricedItinerary = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonData);
        if (jsonObject.optJSONObject("OTA_AirLowFareSearchRS").optJSONObject("PricedItineraries") == null)
            return null;

        JSONArray _PricedItinerary_arr = jsonObject.getJSONObject("OTA_AirLowFareSearchRS")
                .getJSONObject("PricedItineraries").getJSONArray("PricedItinerary");
        for (int i = 0; i < _PricedItinerary_arr.length(); i++) {
            PricedItinerary _PricedItinerary = new PricedItinerary();

            JSONObject _PricedItinerary_JSON = _PricedItinerary_arr.getJSONObject(i);
            JSONObject PricedItinerary_attr_JSON = _PricedItinerary_JSON.getJSONObject("attr");
            JSONObject _AirItinerary = _PricedItinerary_JSON.getJSONObject("AirItinerary");
            JSONArray _AirItineraryPricingInfo_arr = _PricedItinerary_JSON.optJSONArray("AirItineraryPricingInfo");
            JSONObject _TicketingInfo = _PricedItinerary_JSON.getJSONObject("TicketingInfo");
            JSONObject _TPA_Extensions = _PricedItinerary_JSON.getJSONObject("TPA_Extensions");

            _PricedItinerary._attr.SequenceNumber = PricedItinerary_attr_JSON.getString("SequenceNumber");
            _PricedItinerary._AirItinerary._attr.DirectionInd = _AirItinerary.getJSONObject("attr").getString("DirectionInd");
            JSONArray _OriginDestinationOption_arr = _AirItinerary.getJSONObject("OriginDestinationOptions").optJSONArray("OriginDestinationOption");
            if (_OriginDestinationOption_arr != null) {
                for (int j = 0; j < _OriginDestinationOption_arr.length(); j++) {
                    JSONObject JSON_odo = _OriginDestinationOption_arr.getJSONObject(j);
                    if (JSON_odo.optJSONObject("0") == null) {
                        _PricedItinerary._AirItinerary._OriginDestinationOptions.addOriginDestinationOption
                                (setSingleOriginDestinationOption(JSON_odo, false));
                    } else {
                        _PricedItinerary._AirItinerary._OriginDestinationOptions.addMultiOriginDestinationOption
                                (setMultiOriginDestinationOption(JSON_odo));
                    }
                }
            } else {
                _PricedItinerary._AirItinerary._OriginDestinationOptions.addOriginDestinationOption
                        (setSingleOriginDestinationOption(_AirItinerary.getJSONObject("OriginDestinationOptions")
                                .getJSONObject("OriginDestinationOption"), true));
            }

            if (_AirItineraryPricingInfo_arr != null) {
                for (int k = 0; k < _AirItineraryPricingInfo_arr.length(); k++)
                    _PricedItinerary.addAirItineraryPricingInf(setAirItineraryPricingInfoValues(_AirItineraryPricingInfo_arr.getJSONObject(k)));
            } else {
                _PricedItinerary.addAirItineraryPricingInf(setAirItineraryPricingInfoValues(_PricedItinerary_JSON.getJSONObject("AirItineraryPricingInfo")));
            }

            _PricedItinerary._TicketingInfo.TicketType = _TicketingInfo.getJSONObject("attr").getString("TicketType");
            _PricedItinerary._TicketingInfo.ValidInterline = _TicketingInfo.getJSONObject("attr").getString("ValidInterline");

            _PricedItinerary._TPA_Extensions._ValidatingCarrier.Code = _TPA_Extensions.getJSONObject("ValidatingCarrier").getJSONObject("attr").getString("Code");

            // Add PricedItinerary
            list_PricedItinerary.add(_PricedItinerary);
        }
        return list_PricedItinerary;
    }

    private static OriginDestinationOption setSingleOriginDestinationOption(JSONObject _OriginDestinationOption, boolean isOneWay) throws JSONException {
        OriginDestinationOption obj;
        if (isOneWay) {
            obj = setOriginDestinationOptionValues(_OriginDestinationOption.getJSONObject("FlightSegment"));
        } else {
            obj = setOriginDestinationOptionValues(_OriginDestinationOption);
        }
        JSONObject JSON_LocationMap = _OriginDestinationOption.optJSONObject("LocationMap");
        if (JSON_LocationMap != null) {
            obj._LocationMap.Location = JSON_LocationMap.getJSONObject("attr").getString("Location");
        }
        return obj;
    }

    private static List<OriginDestinationOption> setMultiOriginDestinationOption(JSONObject _OriginDestinationOption) throws JSONException {

        List<OriginDestinationOption> list_odo = new ArrayList<>();
        OriginDestinationOption odo;
        odo = setOriginDestinationOptionValues(_OriginDestinationOption.optJSONObject("0"));
        odo.segment = "1";
        JSONObject JSON_LocationMap = _OriginDestinationOption.optJSONObject("LocationMap");
        if (JSON_LocationMap != null) {
            odo._LocationMap.Location = JSON_LocationMap.getJSONObject("attr").getString("Location");
        }
        list_odo.add(odo);
        if (_OriginDestinationOption.optJSONObject("1") != null) {
            odo = setOriginDestinationOptionValues(_OriginDestinationOption.optJSONObject("1"));
            odo.segment = "2";
            list_odo.add(odo);
        }
        if (_OriginDestinationOption.optJSONObject("2") != null) {
            odo = setOriginDestinationOptionValues(_OriginDestinationOption.optJSONObject("2"));
            odo.segment = "3";
            list_odo.add(odo);
        }
        return list_odo;
    }

    private static OriginDestinationOption setOriginDestinationOptionValues(JSONObject _JSON_OriginDestinationOption) throws JSONException {
        OriginDestinationOption obj = new OriginDestinationOption();
        JSONObject _attr_OriginDestinationOption = _JSON_OriginDestinationOption.getJSONObject("attr");
        obj._attr.DepartureDateTime = _attr_OriginDestinationOption.getString("DepartureDateTime");
        obj._attr.ArrivalDateTime = _attr_OriginDestinationOption.getString("ArrivalDateTime");
        obj._attr.StopQuantity = _attr_OriginDestinationOption.getString("StopQuantity");
        obj._attr.FlightNumber = _attr_OriginDestinationOption.getString("FlightNumber");
        obj._attr.ResBookDesigCode = _attr_OriginDestinationOption.getString("ResBookDesigCode");
        obj._attr.ElapsedTime = _attr_OriginDestinationOption.getString("ElapsedTime");

        obj._DepartureAirport.LocationCode = _JSON_OriginDestinationOption.getJSONObject("DepartureAirport").getJSONObject("attr").getString("LocationCode");
        obj._ArrivalAirport.LocationCode = _JSON_OriginDestinationOption.getJSONObject("ArrivalAirport").getJSONObject("attr").getString("LocationCode");
        obj._OperatingAirline.Code = _JSON_OriginDestinationOption.getJSONObject("OperatingAirline").getJSONObject("attr").getString("Code");
        obj._OperatingAirline.FlightNumber = _JSON_OriginDestinationOption.getJSONObject("OperatingAirline").getJSONObject("attr").getString("FlightNumber");
        obj._Equipment.AirEquipType = _JSON_OriginDestinationOption.getJSONObject("Equipment").getJSONObject("attr").getString("AirEquipType");
        obj._MarketingAirline.Code = _JSON_OriginDestinationOption.getJSONObject("MarketingAirline").getJSONObject("attr").getString("Code");
        obj._MarriageGrp.value = _JSON_OriginDestinationOption.getJSONObject("MarriageGrp").getString("value");
        obj._DepartureTimeZone.GMTOffset = _JSON_OriginDestinationOption.getJSONObject("DepartureTimeZone").getJSONObject("attr").getString("GMTOffset");
        obj._ArrivalTimeZone.GMTOffset = _JSON_OriginDestinationOption.getJSONObject("ArrivalTimeZone").getJSONObject("attr").getString("GMTOffset");
        obj._TPA_Extensions.eTicket = _JSON_OriginDestinationOption.getJSONObject("TPA_Extensions").getJSONObject("eTicket").getJSONObject("attr").getString("Ind");
        return obj;
    }

    private static AirItineraryPricingInfo setAirItineraryPricingInfoValues(JSONObject _AirItineraryPricingInfo_JSON) throws JSONException {

        JSONObject _AirItineraryPricingInfo_attr = _AirItineraryPricingInfo_JSON.getJSONObject("attr");
        JSONObject _ItinTotalFare = _AirItineraryPricingInfo_JSON.getJSONObject("ItinTotalFare");
        JSONObject _PTC_FareBreakdowns = _AirItineraryPricingInfo_JSON.getJSONObject("PTC_FareBreakdowns");
        JSONObject _FareInfos = _AirItineraryPricingInfo_JSON.getJSONObject("FareInfos");
        JSONObject _TPA_Extensions = _AirItineraryPricingInfo_JSON.getJSONObject("TPA_Extensions");

        AirItineraryPricingInfo _AirItineraryPricingInfo = new AirItineraryPricingInfo();
        _AirItineraryPricingInfo._attr.PricingSource = _AirItineraryPricingInfo_attr.getString("PricingSource");
        _AirItineraryPricingInfo._attr.PricingSubSource = _AirItineraryPricingInfo_attr.getString("PricingSubSource");

        // region ItinTotalFare
        _AirItineraryPricingInfo._ItinTotalFare._BaseFare.Amount = _ItinTotalFare.getJSONObject("BaseFare").getJSONObject("attr").getString("Amount");
        _AirItineraryPricingInfo._ItinTotalFare._BaseFare.CurrencyCode = _ItinTotalFare.getJSONObject("BaseFare").getJSONObject("attr").getString("CurrencyCode");
        _AirItineraryPricingInfo._ItinTotalFare._BaseFare.DecimalPlaces = _ItinTotalFare.getJSONObject("BaseFare").getJSONObject("attr").getString("DecimalPlaces");

        _AirItineraryPricingInfo._ItinTotalFare._FareConstruction.Amount = _ItinTotalFare.getJSONObject("FareConstruction").getJSONObject("attr").getString("Amount");
        _AirItineraryPricingInfo._ItinTotalFare._FareConstruction.CurrencyCode = _ItinTotalFare.getJSONObject("FareConstruction").getJSONObject("attr").getString("CurrencyCode");
        _AirItineraryPricingInfo._ItinTotalFare._FareConstruction.DecimalPlaces = _ItinTotalFare.getJSONObject("FareConstruction").getJSONObject("attr").getString("DecimalPlaces");

        _AirItineraryPricingInfo._ItinTotalFare._EquivFare.Amount = _ItinTotalFare.getJSONObject("EquivFare").getJSONObject("attr").getString("Amount");
        _AirItineraryPricingInfo._ItinTotalFare._EquivFare.CurrencyCode = _ItinTotalFare.getJSONObject("EquivFare").getJSONObject("attr").getString("CurrencyCode");
        _AirItineraryPricingInfo._ItinTotalFare._EquivFare.DecimalPlaces = _ItinTotalFare.getJSONObject("EquivFare").getJSONObject("attr").getString("DecimalPlaces");

        JSONArray _Tax_JSON_arr = _ItinTotalFare.getJSONObject("Taxes").optJSONArray("Tax");
        if (_Tax_JSON_arr != null) {
            for (int i = 0; i < _Tax_JSON_arr.length(); i++) {
                JSONObject _Tax_JSON = _Tax_JSON_arr.getJSONObject(i);
                AirItineraryPricingInfo.ItinTotalFare.Taxes.Tax _TAX = new AirItineraryPricingInfo.ItinTotalFare.Taxes.Tax();
                _TAX.TaxCode = _Tax_JSON.getJSONObject("attr").getString("TaxCode");
                _TAX.Amount = _Tax_JSON.getJSONObject("attr").getString("Amount");
                _TAX.CurrencyCode = _Tax_JSON.getJSONObject("attr").getString("CurrencyCode");
                _TAX.DecimalPlaces = _Tax_JSON.getJSONObject("attr").getString("DecimalPlaces");
                _AirItineraryPricingInfo._ItinTotalFare._Taxes.addTax(_TAX);
            }
        } else {
            JSONObject _Tax_JSON = _ItinTotalFare.getJSONObject("Taxes").getJSONObject("Tax");
            AirItineraryPricingInfo.ItinTotalFare.Taxes.Tax _TAX = new AirItineraryPricingInfo.ItinTotalFare.Taxes.Tax();
            _TAX.TaxCode = _Tax_JSON.getJSONObject("attr").getString("TaxCode");
            _TAX.Amount = _Tax_JSON.getJSONObject("attr").getString("Amount");
            _TAX.CurrencyCode = _Tax_JSON.getJSONObject("attr").getString("CurrencyCode");
            _TAX.DecimalPlaces = _Tax_JSON.getJSONObject("attr").getString("DecimalPlaces");
            _AirItineraryPricingInfo._ItinTotalFare._Taxes.addTax(_TAX);
        }

        JSONObject _TotalFare_attr = _ItinTotalFare.getJSONObject("TotalFare").getJSONObject("attr");
        _AirItineraryPricingInfo._ItinTotalFare._TotalFare.Amount = _TotalFare_attr.getString("Amount");
        _AirItineraryPricingInfo._ItinTotalFare._TotalFare.CurrencyCode = _TotalFare_attr.getString("CurrencyCode");
        _AirItineraryPricingInfo._ItinTotalFare._TotalFare.DecimalPlaces = _TotalFare_attr.getString("DecimalPlaces");
        // endregion

        // region PTC_FareBreakdown
        JSONArray _PTC_FareBreakdown_arr = _PTC_FareBreakdowns.optJSONArray("PTC_FareBreakdown");
        if (_PTC_FareBreakdown_arr != null) {
            for (int i = 0; i < _PTC_FareBreakdown_arr.length(); i++)
                _AirItineraryPricingInfo._PTC_FareBreakdowns.addPTC_FareBreakdown(
                        setPTC_FareBreakdown(_PTC_FareBreakdown_arr.getJSONObject(i)));
        } else {
            _AirItineraryPricingInfo._PTC_FareBreakdowns.addPTC_FareBreakdown(
                    setPTC_FareBreakdown(_PTC_FareBreakdowns.getJSONObject("PTC_FareBreakdown")));
        }
        // endregion

        // region FareInfo
        JSONArray _FareInfo_arr = _FareInfos.optJSONArray("FareInfo");
        AirItineraryPricingInfo.FareInfos.FareInfo _FareInfo;
        if (_FareInfo_arr != null) {
            for (int i = 0; i < _FareInfo_arr.length(); i++) {
                _FareInfo = new AirItineraryPricingInfo.FareInfos.FareInfo();
                JSONObject _FareInfo_JSON = _FareInfo_arr.getJSONObject(i);
                _FareInfo._FareReference.value = _FareInfo_JSON.getJSONObject("FareReference").getString("value");
                _FareInfo._TPA_Extensions._SeatsRemaining.Number = _FareInfo_JSON.getJSONObject("TPA_Extensions").getJSONObject("SeatsRemaining").getJSONObject("attr").getString("Number");
                _FareInfo._TPA_Extensions._SeatsRemaining.BelowMin = _FareInfo_JSON.getJSONObject("TPA_Extensions").getJSONObject("SeatsRemaining").getJSONObject("attr").getString("BelowMin");
                _FareInfo._TPA_Extensions._Cabin.Cabin = _FareInfo_JSON.getJSONObject("TPA_Extensions").getJSONObject("Cabin").getJSONObject("attr").getString("Cabin");
                _AirItineraryPricingInfo._FareInfos.addFareInfo(_FareInfo);
            }
        } else {
            _FareInfo = new AirItineraryPricingInfo.FareInfos.FareInfo();
            JSONObject _FareInfo_JSON = _FareInfos.getJSONObject("FareInfo");
            _FareInfo._FareReference.value = _FareInfo_JSON.getJSONObject("FareReference").getString("value");
            _FareInfo._TPA_Extensions._SeatsRemaining.Number = _FareInfo_JSON.getJSONObject("TPA_Extensions").getJSONObject("SeatsRemaining").getJSONObject("attr").getString("Number");
            _FareInfo._TPA_Extensions._SeatsRemaining.BelowMin = _FareInfo_JSON.getJSONObject("TPA_Extensions").getJSONObject("SeatsRemaining").getJSONObject("attr").getString("BelowMin");
            _FareInfo._TPA_Extensions._Cabin.Cabin = _FareInfo_JSON.getJSONObject("TPA_Extensions").getJSONObject("Cabin").getJSONObject("attr").getString("Cabin");
            _AirItineraryPricingInfo._FareInfos.addFareInfo(_FareInfo);
        }
        // endregion

        _AirItineraryPricingInfo._TPA_Extensions._DivideInParty.Indicator =
                _TPA_Extensions.getJSONObject("DivideInParty").getJSONObject("attr").getString("Indicator");

        return _AirItineraryPricingInfo;
    }

    private static PTC_FareBreakdown setPTC_FareBreakdown(JSONObject _PTC_FareBreakdown_JSON) throws JSONException {
        PTC_FareBreakdown obj = new PTC_FareBreakdown();
        obj._PassengerTypeQuantity.Code = _PTC_FareBreakdown_JSON.getJSONObject("PassengerTypeQuantity").getJSONObject("attr").getString("Code");
        obj._PassengerTypeQuantity.Quantity = _PTC_FareBreakdown_JSON.getJSONObject("PassengerTypeQuantity").getJSONObject("attr").getString("Quantity");

        // region FareBasisCode
        JSONArray _FareBasisCode_arr = _PTC_FareBreakdown_JSON.getJSONObject("FareBasisCodes").optJSONArray("FareBasisCode");
        if (_FareBasisCode_arr != null) {
            for (int i = 0; i < _FareBasisCode_arr.length(); i++)
                obj._FareBasisCodes.addFareBasisCode(setFareBasisCode(_FareBasisCode_arr.getJSONObject(i)));
        } else {
            obj._FareBasisCodes.addFareBasisCode(setFareBasisCode(_PTC_FareBreakdown_JSON.getJSONObject("FareBasisCodes").getJSONObject("FareBasisCode")));
        }
        // endregion

        // region PassengerFare
        JSONObject _PassengerFare_JSON = _PTC_FareBreakdown_JSON.getJSONObject("PassengerFare");
        obj._PassengerFare._BaseFare.Amount = _PassengerFare_JSON.getJSONObject("BaseFare").getJSONObject("attr").getString("Amount");
        obj._PassengerFare._BaseFare.CurrencyCode = _PassengerFare_JSON.getJSONObject("BaseFare").getJSONObject("attr").getString("CurrencyCode");
        obj._PassengerFare._FareConstruction.Amount = _PassengerFare_JSON.getJSONObject("FareConstruction").getJSONObject("attr").getString("Amount");
        obj._PassengerFare._FareConstruction.CurrencyCode = _PassengerFare_JSON.getJSONObject("FareConstruction").getJSONObject("attr").getString("CurrencyCode");
        obj._PassengerFare._FareConstruction.DecimalPlaces = _PassengerFare_JSON.getJSONObject("FareConstruction").getJSONObject("attr").getString("DecimalPlaces");
        obj._PassengerFare._EquivFare.Amount = _PassengerFare_JSON.getJSONObject("EquivFare").getJSONObject("attr").getString("Amount");
        obj._PassengerFare._EquivFare.CurrencyCode = _PassengerFare_JSON.getJSONObject("EquivFare").getJSONObject("attr").getString("CurrencyCode");
        obj._PassengerFare._EquivFare.DecimalPlaces = _PassengerFare_JSON.getJSONObject("EquivFare").getJSONObject("attr").getString("DecimalPlaces");

        if (_PassengerFare_JSON.optJSONObject("Vendor") != null) {
            JSONObject JSON_Vendor = _PassengerFare_JSON.getJSONObject("Vendor");
            if (JSON_Vendor.has("ADT")) {
                obj._PassengerFare._Vendor.Type = "ADT";
                obj._PassengerFare._Vendor.AirLineType = JSON_Vendor.getJSONObject("ADT")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._Vendor.AirLineTypeMask = JSON_Vendor.getJSONObject("ADT")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            } else if (JSON_Vendor.has("CNN")) {
                obj._PassengerFare._Vendor.Type = "CNN";
                obj._PassengerFare._Vendor.AirLineType = JSON_Vendor.getJSONObject("CNN")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._Vendor.AirLineTypeMask = JSON_Vendor.getJSONObject("CNN")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            } else {
                obj._PassengerFare._Vendor.Type = "INF";
                obj._PassengerFare._Vendor.AirLineType = JSON_Vendor.getJSONObject("INF")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._Vendor.AirLineTypeMask = JSON_Vendor.getJSONObject("INF")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            }
        }

        if (_PassengerFare_JSON.optJSONObject("Parent") != null) {
            JSONObject JSON_Parent = _PassengerFare_JSON.getJSONObject("Parent");
            if (JSON_Parent.has("ADT")) {
                obj._PassengerFare._Parent.Type = "ADT";
                obj._PassengerFare._Parent.AirLineType = JSON_Parent.getJSONObject("ADT")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._Parent.AirLineTypeMask = JSON_Parent.getJSONObject("ADT")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            } else if (JSON_Parent.has("CNN")) {
                obj._PassengerFare._Parent.Type = "CNN";
                obj._PassengerFare._Parent.AirLineType = JSON_Parent.getJSONObject("CNN")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._Parent.AirLineTypeMask = JSON_Parent.getJSONObject("CNN")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            } else {
                obj._PassengerFare._Parent.Type = "INF";
                obj._PassengerFare._Parent.AirLineType = JSON_Parent.getJSONObject("INF")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._Parent.AirLineTypeMask = JSON_Parent.getJSONObject("INF")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            }
        }

        if (_PassengerFare_JSON.optJSONObject("P_Parent") != null) {
            JSONObject JSON_P_Parent = _PassengerFare_JSON.getJSONObject("P_Parent");
            if (JSON_P_Parent.has("ADT")) {
                obj._PassengerFare._P_Parent.Type = "ADT";
                obj._PassengerFare._P_Parent.AirLineType = JSON_P_Parent.getJSONObject("ADT")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._P_Parent.AirLineTypeMask = JSON_P_Parent.getJSONObject("ADT")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            } else if (JSON_P_Parent.has("CNN")) {
                obj._PassengerFare._P_Parent.Type = "CNN";
                obj._PassengerFare._P_Parent.AirLineType = JSON_P_Parent.getJSONObject("CNN")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._P_Parent.AirLineTypeMask = JSON_P_Parent.getJSONObject("CNN")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            } else {
                obj._PassengerFare._P_Parent.Type = "INF";
                obj._PassengerFare._P_Parent.AirLineType = JSON_P_Parent.getJSONObject("INF")
                        .getJSONObject("attr").getString("AirLineType");
                obj._PassengerFare._P_Parent.AirLineTypeMask = JSON_P_Parent.getJSONObject("INF")
                        .getJSONObject("attr").getString("AirLineTypeMask");
            }
        }

        JSONArray _Tax_arr = _PassengerFare_JSON.getJSONObject("Taxes").optJSONArray("Tax");
        Taxes.Tax _Tax;
        if (_Tax_arr != null) {
            for (int i = 0; i < _Tax_arr.length(); i++) {
                _Tax = new Taxes.Tax();
                JSONObject _Tax_JSON_attr = _Tax_arr.getJSONObject(i).getJSONObject("attr");
                _Tax.TaxCode = _Tax_JSON_attr.getString("TaxCode");
                _Tax.Amount = _Tax_JSON_attr.getString("Amount");
                _Tax.CurrencyCode = _Tax_JSON_attr.getString("CurrencyCode");
                _Tax.DecimalPlaces = _Tax_JSON_attr.getString("DecimalPlaces");
                obj._PassengerFare._Taxes._Tax.add(_Tax);
            }
        } else {
            JSONObject _Tax_JSON_attr = _PassengerFare_JSON.getJSONObject("Taxes").getJSONObject("Tax").getJSONObject("attr");
            _Tax = new Taxes.Tax();
            _Tax.TaxCode = _Tax_JSON_attr.getString("TaxCode");
            _Tax.Amount = _Tax_JSON_attr.getString("Amount");
            _Tax.CurrencyCode = _Tax_JSON_attr.getString("CurrencyCode");
            _Tax.DecimalPlaces = _Tax_JSON_attr.getString("DecimalPlaces");
            obj._PassengerFare._Taxes._Tax.add(_Tax);
        }

        JSONObject _TotalTax_attr = _PassengerFare_JSON.getJSONObject("Taxes").getJSONObject("TotalTax").getJSONObject("attr");
        obj._PassengerFare._Taxes._TotalTax.Amount = _TotalTax_attr.getString("Amount");
        obj._PassengerFare._Taxes._TotalTax.CurrencyCode = _TotalTax_attr.getString("CurrencyCode");
        obj._PassengerFare._Taxes._TotalTax.DecimalPlaces = _TotalTax_attr.getString("DecimalPlaces");
        obj._PassengerFare._Taxes._TotalTax.DecimalPlaces = _TotalTax_attr.getString("DecimalPlaces");

        obj._PassengerFare._TotalFare.Amount = _PassengerFare_JSON.getJSONObject("TotalFare").getJSONObject("attr").getString("Amount");
        obj._PassengerFare._TotalFare.CurrencyCode = _PassengerFare_JSON.getJSONObject("TotalFare").getJSONObject("attr").getString("CurrencyCode");
        // endregion

        obj._Endorsements.NonRefundableIndicator =
                _PTC_FareBreakdown_JSON.getJSONObject("Endorsements").getJSONObject("attr").getString("NonRefundableIndicator");

        obj._TPA_Extensions._FareCalcLine.Info =
                _PTC_FareBreakdown_JSON.getJSONObject("TPA_Extensions").getJSONObject("FareCalcLine").getJSONObject("attr").getString("Info");

        return obj;
    }

    private static FareBasisCode setFareBasisCode(JSONObject _FareBasisCode_JSON) throws JSONException {
        FareBasisCode obj = new FareBasisCode();
        obj.value = _FareBasisCode_JSON.getString("value");
        JSONObject _FareBasisCode_attr = _FareBasisCode_JSON.getJSONObject("attr");
        obj._attr.BookingCode = _FareBasisCode_attr.getString("BookingCode");
        obj._attr.AvailabilityBreak = _FareBasisCode_attr.optString("AvailabilityBreak");
        obj._attr.DepartureAirportCode = _FareBasisCode_attr.getString("DepartureAirportCode");
        obj._attr.ArrivalAirportCode = _FareBasisCode_attr.getString("ArrivalAirportCode");
        return obj;
    }

    // endregion
}
