package com.visionxoft.abacus.rehmantravel.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class to store PricedItinerary properties.
 * Schema of these nested classes are according to the GDS response in json format i.e.
 * 'OTA_AirLowFareSearchRS' > 'PricedItineraries' > 'PricedItinerary'
 */
public class PricedItinerary {
    public attr _attr = new attr();
    public AirItinerary _AirItinerary = new AirItinerary();
    public ArrayList<AirItineraryPricingInfo> _AirItineraryPricingInfo = new ArrayList<>();

    public void addAirItineraryPricingInf(AirItineraryPricingInfo obj) {
        _AirItineraryPricingInfo.add(obj);
    }

    public TicketingInfo _TicketingInfo = new TicketingInfo();
    public TPA_Extensions _TPA_Extensions = new TPA_Extensions();

    public static class attr {
        public String SequenceNumber;
    }

    public static class AirItinerary {
        public attr _attr = new attr();
        public OriginDestinationOptions _OriginDestinationOptions = new OriginDestinationOptions();

        public static class attr {
            public String DirectionInd;
        }

        public static class OriginDestinationOptions {

            public ArrayList<OriginDestinationOption> _OriginDestinationOption = new ArrayList<>();

            public void addOriginDestinationOption(OriginDestinationOption obj) {
                _OriginDestinationOption.add(obj);
            }

            public void addMultiOriginDestinationOption(List<OriginDestinationOption> list) {
                _OriginDestinationOption.addAll(list);
            }

            public static class OriginDestinationOption {
                public String segment = "0";
                public attr _attr = new attr();
                public DepartureAirport _DepartureAirport = new DepartureAirport();
                public ArrivalAirport _ArrivalAirport = new ArrivalAirport();
                public OperatingAirline _OperatingAirline = new OperatingAirline();
                public Equipment _Equipment = new Equipment();
                public MarketingAirline _MarketingAirline = new MarketingAirline();
                public MarriageGrp _MarriageGrp = new MarriageGrp();
                public DepartureTimeZone _DepartureTimeZone = new DepartureTimeZone();
                public ArrivalTimeZone _ArrivalTimeZone = new ArrivalTimeZone();
                public TPA_Extensions _TPA_Extensions = new TPA_Extensions();
                public LocationMap _LocationMap = new LocationMap();

                public static class attr {
                    public String DepartureDateTime;
                    public String ArrivalDateTime;
                    public String StopQuantity;
                    public String FlightNumber;
                    public String ResBookDesigCode;
                    public String ElapsedTime;
                }

                public static class DepartureAirport {
                    public String LocationCode;
                }

                public static class ArrivalAirport {
                    public String LocationCode;
                }

                public static class OperatingAirline {
                    public String Code;
                    public String FlightNumber;
                }

                public static class Equipment {
                    public String AirEquipType;
                }

                public static class MarketingAirline {
                    public String Code;
                }

                public static class MarriageGrp {
                    public String value;
                }

                public static class DepartureTimeZone {
                    public String GMTOffset;
                }

                public static class ArrivalTimeZone {
                    public String GMTOffset;
                }

                public static class TPA_Extensions {
                    public String eTicket;
                }

                public static class LocationMap {
                    @Nullable
                    public String Location;
                }

            }
        }
    }

    public static class AirItineraryPricingInfo {
        public attr _attr = new attr();
        public ItinTotalFare _ItinTotalFare = new ItinTotalFare();
        public PTC_FareBreakdowns _PTC_FareBreakdowns = new PTC_FareBreakdowns();
        public FareInfos _FareInfos = new FareInfos();
        public TPA_Extensions _TPA_Extensions = new TPA_Extensions();

        public static class attr {
            public String PricingSource;
            public String PricingSubSource;
        }

        public static class ItinTotalFare {
            public BaseFare _BaseFare = new BaseFare();
            public FareConstruction _FareConstruction = new FareConstruction();
            public EquivFare _EquivFare = new EquivFare();
            public Taxes _Taxes = new Taxes();
            public TotalFare _TotalFare = new TotalFare();

            public static class BaseFare {
                public String Amount;
                public String CurrencyCode;
                public String DecimalPlaces;
            }

            public static class FareConstruction {
                public String Amount;
                public String CurrencyCode;
                public String DecimalPlaces;
            }

            public static class EquivFare {
                public String Amount;
                public String CurrencyCode;
                public String DecimalPlaces;
            }

            public static class Taxes {
                public ArrayList<Tax> _Tax = new ArrayList<>();

                public void addTax(Tax obj) {
                    _Tax.add(obj);
                }

                public static class Tax {
                    public String TaxCode;
                    public String Amount;
                    public String CurrencyCode;
                    public String DecimalPlaces;
                }
            }

            public static class TotalFare {
                public String Amount;
                public String CurrencyCode;
                public String DecimalPlaces;
            }
        }

        public static class PTC_FareBreakdowns {
            public ArrayList<PTC_FareBreakdown> _PTC_FareBreakdown = new ArrayList<>();

            public void addPTC_FareBreakdown(PTC_FareBreakdown obj) {
                _PTC_FareBreakdown.add(obj);
            }

            public static class PTC_FareBreakdown {
                public PassengerTypeQuantity _PassengerTypeQuantity = new PassengerTypeQuantity();
                public FareBasisCodes _FareBasisCodes = new FareBasisCodes();
                public PassengerFare _PassengerFare = new PassengerFare();
                public Endorsements _Endorsements = new Endorsements();
                public TPA_Extensions _TPA_Extensions = new TPA_Extensions();

                public static class PassengerTypeQuantity {
                    public String Code;
                    public String Quantity;
                }

                public static class FareBasisCodes {
                    public ArrayList<FareBasisCode> _FareBasisCode = new ArrayList<>();

                    public void addFareBasisCode(FareBasisCode obj) {
                        _FareBasisCode.add(obj);
                    }

                    public static class FareBasisCode {
                        public String value;
                        public attr _attr = new attr();

                        public static class attr {
                            public String BookingCode;
                            public String AvailabilityBreak;
                            public String DepartureAirportCode;
                            public String ArrivalAirportCode;
                        }
                    }
                }

                public static class PassengerFare {
                    public BaseFare _BaseFare = new BaseFare();
                    public FareConstruction _FareConstruction = new FareConstruction();
                    public EquivFare _EquivFare = new EquivFare();
                    public Taxes _Taxes = new Taxes();
                    public TotalFare _TotalFare = new TotalFare();
                    public Vendor _Vendor = new Vendor();
                    public Parent _Parent = new Parent();
                    public P_Parent _P_Parent = new P_Parent();

                    public static class BaseFare {
                        public String Amount;
                        public String CurrencyCode;
                    }

                    public static class FareConstruction {
                        public String Amount;
                        public String CurrencyCode;
                        public String DecimalPlaces;
                    }

                    public static class EquivFare {
                        public String Amount;
                        public String CurrencyCode;
                        public String DecimalPlaces;
                    }

                    public static class Taxes {
                        public ArrayList<Tax> _Tax = new ArrayList<>();
                        public TotalTax _TotalTax = new TotalTax();

                        public static class Tax {
                            public String TaxCode;
                            public String Amount;
                            public String CurrencyCode;
                            public String DecimalPlaces;
                        }

                        public static class TotalTax {
                            public String Amount;
                            public String CurrencyCode;
                            public String DecimalPlaces;
                        }
                    }

                    public static class TotalFare {
                        public String Amount;
                        public String CurrencyCode;
                    }

                    public static class Vendor {
                        public String Type = null;
                        public String AirLineType;
                        public String AirLineTypeMask;
                    }

                    public static class Parent {
                        public String Type = null;
                        public String AirLineType;
                        public String AirLineTypeMask;
                    }

                    public static class P_Parent {
                        public String Type = null;
                        public String AirLineType;
                        public String AirLineTypeMask;
                    }

                }

                public static class Endorsements {
                    public String NonRefundableIndicator;
                }

                public static class TPA_Extensions {
                    public FareCalcLine _FareCalcLine = new FareCalcLine();

                    public static class FareCalcLine {
                        public String Info;
                    }
                }
            }
        }

        public static class FareInfos {
            public ArrayList<FareInfo> _FareInfo = new ArrayList<>();

            public void addFareInfo(FareInfo obj) {
                _FareInfo.add(obj);
            }

            public static class FareInfo {
                public FareReference _FareReference = new FareReference();
                public TPA_Extensions _TPA_Extensions = new TPA_Extensions();

                public static class FareReference {
                    public String value;
                }

                public static class TPA_Extensions {
                    public SeatsRemaining _SeatsRemaining = new SeatsRemaining();
                    public Cabin _Cabin = new Cabin();

                    public static class SeatsRemaining {
                        public String Number;
                        public String BelowMin;
                    }

                    public static class Cabin {
                        public String Cabin;
                    }
                }
            }
        }

        public static class TPA_Extensions {
            public DivideInParty _DivideInParty = new DivideInParty();

            public static class DivideInParty {
                public String Indicator;
            }
        }
    }

    public static class TicketingInfo {
        public String TicketType;
        public String ValidInterline;
    }

    public static class TPA_Extensions {

        public ValidatingCarrier _ValidatingCarrier = new ValidatingCarrier();

        public static class ValidatingCarrier {
            public String Code;
        }
    }
}







