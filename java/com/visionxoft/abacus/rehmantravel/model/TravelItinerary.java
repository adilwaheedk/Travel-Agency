package com.visionxoft.abacus.rehmantravel.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Model class to store Travel Itinerary details
 */
public class TravelItinerary {

    public ItineraryRef _ItineraryRef = new ItineraryRef();
    public CustomerInfo _CustomerInfo = new CustomerInfo();
    public ItineraryInfo _ItineraryInfo = new ItineraryInfo();
    public ArrayList<SpecialServices> _SpecialServicesList = new ArrayList<>();

    public static class ItineraryRef {
        public String ID;
        public String InhibitCode;
        public String PartitionID;
        public String PrimeHostID;
        public Source _Source = new Source();

        public static class Source {
            public String AAAPseudoCityCode;
            public String CreateDateTime;
            public String CreationAgent;
            public String HomePseudoCityCode;
            public String PseudoCityCode;
            public String ReceivedFrom;
        }
    }

    public static class CustomerInfo {
        public ArrayList<PersonName> _PersonNameList = new ArrayList<>();
        public Telephone _Telephone = new Telephone();
        public ArrayList<String> _AddressLineList = new ArrayList<>();

        public static class PersonName {
            public String NameNumber;
            @Nullable
            public String NameReference;
            public String PassengerType;
            public String GivenName;
            public String Surname;
            @Nullable
            public ArrayList<String> _EmailList = new ArrayList<>();
        }

        public static class Telephone {
            public String AreaCityCode;
            public String PhoneNumber;
            public String RPH;
        }
    }

    public static class ItineraryInfo {
        public ReservationItems _ReservationItems = new ReservationItems();
        public Ticketing _Ticketing = new Ticketing();
        public ItineraryPricing _ItineraryPricing = new ItineraryPricing();

        public static class ReservationItems {
            public String Item_RPH;
            public Air _Air = new Air();
            public String DepartureAirport_Code;
            public String ArrivalAirport_Code;
            public String Equipment_AirEquipType;
            public String MarketingAirline_Code;
            public String UpdatedDepartureTime;
            public String UpdatedArrivalTime;
            public String SupplierRef_ID;
            public String Meal_Code;

            public static class Air {
                public String AirMilesFlown;
                public String ArrivalDateTime;
                public String DepartureDateTime;
                public String ElapsedTime;
                public String FlightNumber;
                public String NumberInParty;
                public String ResBookDesigCode;
                public String SegmentNumber;
                public String SmokingAllowed;
                public String SpecialMeal;
                public String Status;
                public String StopQuantity;
                public String eTicket;
            }
        }

        public static class Ticketing {
            public String RPH;
            public String TicketTimeLimit;
        }

        public static class ItineraryPricing {
            public ArrayList<PriceQuote> _PriceQuoteList = new ArrayList<>();

            public static class PriceQuote {
                public ItinTotalFare _ItinTotalFare = new ItinTotalFare();
                public PTC_FareBreakdown _PTC_FareBreakdown = new PTC_FareBreakdown();

                public static class ItinTotalFare {
                    public BaseFare _BaseFare = new BaseFare();
                    public Taxes _Taxes = new Taxes();
                    public String TotalFare_Amount;
                    public String TotalFare_CurrencyCode;

                    public static class BaseFare {
                        public String Amount;
                        public String CurrencyCode;
                    }

                    public static class Taxes {
                        public String Tax_Amount;
                        public String Tax_TaxCode;
                        public ArrayList<String> _TaxBreakdownCodeList = new ArrayList<>();
                    }
                }

                public static class PTC_FareBreakdown {
                    public String PassengerTypeQuantity_Code;
                    public String PassengerTypeQuantity_Quantity;
                    public String FareBasis_Code;
                    public String FareCalculation_value;
                    public String Endorsements_value;
                    public ArrayList<FlightSegment> _FlightSegmentList = new ArrayList<>();

                    public static class FlightSegment {
                        public String Status;
                        public String ConnectionInd;
                        public String DepartureDateTime;
                        public String FlightNumber;
                        public String SegmentNumber;
                        public String ResBookDesigCode;
                        public String DepartureAirport_LocationCode;
                        public String MarketingAirline_Code;
                        public String MarketingAirline_FlightNumber;
                        public String FareBasis_Code;
                        public String ValidityDates_NotValidBefore;
                        public String ValidityDates_NotValidAfter;
                        public String BaggageAllowance_Number;
                    }
                }
            }
        }
    }

    public static class SpecialServices {
        public String ItemRPH;
        public String Type;
        public String Service_SSRCode;
        public String Service_SSRType;
        public String Name_value;
        public String Name_Number;
        public String Airline_Code;
        public String Text_value;
    }
}
