package com.visionxoft.abacus.rehmantravel.fragment;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.FlightSearchAdapter;
import com.visionxoft.abacus.rehmantravel.adapter.FlightSearchDetailAdapter;
import com.visionxoft.abacus.rehmantravel.model.AirportLocation;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.task.GetSearchFilterInfo;
import com.visionxoft.abacus.rehmantravel.task.SecureWebService;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.FragmentHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Display list of Low Fare Available Flights to the user.
 * Click Detail button on each row to view flight details.
 * Click Select Flight button to start booking process
 */
public class SearchFragment extends Fragment {

    // Temporary storage of Trip details
    public HashMap<Integer, PricedItinerary> detail_PricedItinerary;
    public HashMap<Integer, HashMap<String, AirportLocation>> detail_airport_names;
    public HashMap<Integer, HashMap<String, String>> detail_airline_names;

    private List<PricedItinerary> pricedItineraryListOrig, pricedItineraryList;
    private MainActivity mainActivity;
    private RecyclerView search_flights_list;
    private View rootView, rel_layout_search, search_flight_header, search_progress;
    private Button btn_search_filter;
    private FlightSearchAdapter adapter;
    private Dialog filterDialog;
    private LinearLayout layout_airlines;
    private RadioGroup layout_stops;
    private BiMap airlines_inverse, stops_inverse;
    private Integer checkedAirline = 0;
    private Integer checkedStop = 0;
    private HashBiMap<String, String> airlines;
    private HashBiMap<String, String> low_fare;
    private HashBiMap<Integer, String> stops_str;
    private HashMap<Integer, List<Object>> listMap;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        String flight_type;
        switch ((String) IntentHelper.getObjectForKey("trip_type")) {
            case "one-way":
                flight_type = "One Way Flights";
                break;
            case "round-trip":
                flight_type = "Round Trips";
                break;
            default:
                flight_type = "Multi Destination";
                break;
        }

        setToolbarSubtitle();

        if (rootView == null) {
            mainActivity.setToolbarTitle("Searching " + flight_type);
            rootView = inflater.inflate(R.layout.fragment_search, container, false);
            rel_layout_search = rootView.findViewById(R.id.rel_layout_search);
            search_flights_list = (RecyclerView) rootView.findViewById(R.id.LVFlights);
            search_progress = rootView.findViewById(R.id.search_progress);
            search_flight_header = rootView.findViewById(R.id.search_flight_header);
            btn_search_filter = (Button) rootView.findViewById(R.id.btn_search_filter);

            // Flights Search Filter Button
            btn_search_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (airlines != null && stops_str != null && listMap != null)
                        searchFilterDialog();
                }
            });

            // Initialize Views Visibility
            btn_search_filter.setVisibility(View.GONE);
            search_flight_header.setVisibility(View.GONE);
            search_flights_list.setVisibility(View.GONE);
            search_progress.setVisibility(View.VISIBLE);

            SecureWebService.accessAPI(SearchFragment.this, IntentHelper.getObjectForKey("search_flights_params"));
        } else
            mainActivity.setToolbarTitle(flight_type);
        return rootView;
    }

    // Setup Toolbar Values

    private void setToolbarTitle(int list_size) {
        if (isVisible() && list_size > 0) {
            if (list_size == 1) mainActivity.setToolbarTitle("Single Trip Found");
            else mainActivity.setToolbarTitle(String.valueOf(list_size) + " Trips Found");
        }
    }

    // Set Toolbar Subtitle
    private void setToolbarSubtitle() {
        String total_travelers = (String) IntentHelper.getObjectForKey("total_travelers");
        String departure_date = (String) IntentHelper.getObjectForKey("departure_date");
        if (total_travelers != null && departure_date != null) {
            if (total_travelers.equals("1")) total_travelers += " Passenger";
            else total_travelers += " Passengers";
            mainActivity.setToolbarSubTitle(total_travelers + ", " + departure_date);
        }
    }

    public void execSearchFilter(List<PricedItinerary> pricedItineraryList) {
        new GetSearchFilterInfo(this, pricedItineraryList).execute();
    }

    public void setupSearchFilter(List<PricedItinerary> pricedItineraryList,
                                  final HashBiMap<String, String> airlines,
                                  final HashBiMap<String, String> low_fare,
                                  final HashBiMap<Integer, String> stops_str,
                                  final HashMap<Integer, List<Object>> listMap) {
        this.pricedItineraryListOrig = pricedItineraryList;
        this.pricedItineraryList = pricedItineraryList;
        this.airlines = airlines;
        this.low_fare = low_fare;
        this.stops_str = stops_str;
        this.listMap = listMap;

        // Inverse mapping for search
        airlines_inverse = airlines.inverse();
        stops_inverse = stops_str.inverse();

        detail_PricedItinerary = new HashMap<>();
        detail_airport_names = new HashMap<>();
        detail_airline_names = new HashMap<>();

        searchFilterDialog();
        setupFlightSearchAdapter();
    }

    // Setup Views for Flight Search
    public void setupFlightSearchAdapter() {
        search_progress.setVisibility(View.GONE);

        setToolbarTitle(pricedItineraryList.size());
        adapter = new FlightSearchAdapter(this, pricedItineraryList);
        search_flights_list.setLayoutManager(new LinearLayoutManager(mainActivity));
        search_flights_list.setItemAnimator(new DefaultItemAnimator());
        search_flights_list.addItemDecoration(new SimpleDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL, 4f));
        search_flights_list.setAdapter(adapter);
        search_flights_list.setVisibility(View.VISIBLE);

        rel_layout_search.setBackgroundResource(R.color.transparent);
        search_flight_header.setVisibility(View.VISIBLE);
        btn_search_filter.setVisibility(View.VISIBLE);
    }

    /**
     * Setup and display flights filtering dialog
     */
    private void searchFilterDialog() {
        if (filterDialog == null) {
            filterDialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_flight_search_filter, Gravity.BOTTOM);

            layout_airlines = (LinearLayout) filterDialog.findViewById(R.id.layout_airlines);
            layout_stops = (RadioGroup) filterDialog.findViewById(R.id.layout_stops);
            Button btn_filter_back = (Button) filterDialog.findViewById(R.id.btn_filter_back);

            btn_filter_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterDialog.hide();
                }
            });

            LinearLayout linearLayout = new LinearLayout(mainActivity);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < layout_airlines.getChildCount(); i++)
                        ((RadioButton) ((LinearLayout) layout_airlines.getChildAt(i)).getChildAt(1)).setChecked(false);
                    ((RadioButton) ((LinearLayout) v).getChildAt(1)).setChecked(true);
                    filterFlights();
                }
            });

            ImageView iv = new ImageView(mainActivity);
            iv.setImageResource(R.drawable.home_plane);
            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageView_height);
            iv.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageView_width);

            RadioButton rb = new RadioButton(mainActivity);
            rb.setClickable(false);
            rb.setText(R.string.all_airlines);

            TextView tv = new TextView(mainActivity);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
            else tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tv.setPadding(16, 0, 0, 0);
            float lowest_fare = Float.MAX_VALUE;
            for (String fare : low_fare.values()) {
                if (Float.parseFloat(fare) < lowest_fare) lowest_fare = Float.parseFloat(fare);
            }
            tv.setText("(" + String.valueOf(lowest_fare) + ")");

            linearLayout.addView(iv);
            linearLayout.addView(rb);
            linearLayout.addView(tv);
            layout_airlines.addView(linearLayout);


            // Populate Airlines
            for (String airline : airlines.keySet()) {

                linearLayout = new LinearLayout(mainActivity);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < layout_airlines.getChildCount(); i++)
                            ((RadioButton) ((LinearLayout) layout_airlines.getChildAt(i)).getChildAt(1)).setChecked(false);
                        ((RadioButton) ((LinearLayout) v).getChildAt(1)).setChecked(true);
                        filterFlights();
                    }
                });

                iv = new ImageView(mainActivity);
                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageView_height);
                iv.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageView_width);
                iv.setImageResource(getResources().getIdentifier("_" +
                        FormatString.filterString(airline), "drawable", mainActivity.getPackageName()));

                rb = new RadioButton(mainActivity);
                rb.setClickable(false);
                rb.setText(airlines.get(airline));

                tv = new TextView(mainActivity);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
                } else tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv.setPadding(16, 0, 0, 0);
                tv.setText("(" + low_fare.get(airline) + ")");

                // Add views to layout
                linearLayout.addView(iv);
                linearLayout.addView(rb);
                linearLayout.addView(tv);
                layout_airlines.addView(linearLayout);
            }

            // Populate Flight Stops
            rb = new RadioButton(mainActivity);
            rb.setText(R.string.all_stops);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterFlights();
                }
            });
            layout_stops.addView(rb);

            for (String stop : stops_str.values()) {
                rb = new RadioButton(mainActivity);
                rb.setText(stop);
                rb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterFlights();
                    }
                });
                layout_stops.addView(rb);
            }
        } else {
            for (int i = 0; i < layout_airlines.getChildCount(); i++)
                ((RadioButton) ((LinearLayout) layout_airlines.getChildAt(i)).getChildAt(1)).setChecked(false);
        }
        ((RadioButton) ((LinearLayout) layout_airlines.getChildAt(checkedAirline)).getChildAt(1)).setChecked(true);
        ((RadioButton) layout_stops.getChildAt(checkedStop)).setChecked(true);
        filterDialog.show();
    }

    /**
     * Filter Flights
     */
    private void filterFlights() {

        // Filtered list contains original list positions to display
        RadioButton rb;
        ArrayList<Integer> stopfilteredList = new ArrayList<>();
        checkedAirline = 0;
        checkedStop = 0;

        // Loop until checked stop to get filtered list positions
        rb = (RadioButton) layout_stops.getChildAt(0);
        if (rb.isChecked()) checkedStop = 0;
        else {
            for (int i = 1; i < layout_stops.getChildCount(); i++) {
                rb = (RadioButton) layout_stops.getChildAt(i);
                if (rb.isChecked()) {
                    checkedStop = i;
                    Integer stops = (Integer) stops_inverse.get(rb.getText());

                    // Collect rows which contain required stops
                    for (Integer rowNo : listMap.keySet()) {
                        if (listMap.get(rowNo).contains(stops) && !stopfilteredList.contains(rowNo))
                            stopfilteredList.add(rowNo);
                    }
                    break;
                }
            }
        }

        // Loop until checked airline to get list positions

        ArrayList<Integer> airlinefilteredList = null;
        rb = (RadioButton) ((LinearLayout) layout_airlines.getChildAt(0)).getChildAt(1);
        if (rb.isChecked()) checkedAirline = 0;
        else {
            airlinefilteredList = new ArrayList<>();
            for (int i = 1; i < layout_airlines.getChildCount(); i++) {
                rb = (RadioButton) ((LinearLayout) layout_airlines.getChildAt(i)).getChildAt(1);
                if (rb.isChecked()) {
                    checkedAirline = i;
                    String airline_code = (String) airlines_inverse.get(rb.getText());

                    ArrayList<Integer> listToLoop;
                    if (stopfilteredList.size() > 0) listToLoop = stopfilteredList;
                    else listToLoop = new ArrayList<>(listMap.keySet());

                    for (Integer rowNo : listToLoop) {
                        // Collect rows which contain required no of stops and airline code
                        if (listMap.get(rowNo).contains(airline_code) && !airlinefilteredList.contains(rowNo))
                            airlinefilteredList.add(rowNo);
                    }
                    break;
                }
            }
        }

        if (checkedStop == 0 && checkedAirline == 0) {
            pricedItineraryList = pricedItineraryListOrig;
        } else if (checkedAirline == 0) {
            List<PricedItinerary> items = new ArrayList<>();
            for (Integer child : stopfilteredList) items.add(pricedItineraryListOrig.get(child));
            pricedItineraryList = items;
        } else if (airlinefilteredList.size() > 0) {
            List<PricedItinerary> items = new ArrayList<>();
            for (Integer child : airlinefilteredList) items.add(pricedItineraryListOrig.get(child));
            pricedItineraryList = items;
        } else {
            PhoneFunctionality.makeToast(getActivity(), mainActivity.getString(R.string.no_trip_found));
            return;
        }
        setToolbarTitle(pricedItineraryList.size());
        adapter = new FlightSearchAdapter(this, pricedItineraryList);
        search_flights_list.setAdapter(adapter);
        filterDialog.hide();
    }

    /**
     * Go to specific fragment
     *
     * @param _PricedItinerary Single Itinerary object
     * @param airport_names    List of airport names included in Itinerary
     * @param airline_names    List of airline names included in Itinerary
     * @param position         Selected Trip position
     */
    public void proceed(PricedItinerary _PricedItinerary, HashMap<String, AirportLocation> airport_names,
                        HashMap<String, String> airline_names, Object position) {
        if (position == null) {
            proceedBookFragment(_PricedItinerary, airport_names, airline_names);
        } else {
            int key = (int) position;
            detail_PricedItinerary.put(key, _PricedItinerary);
            detail_airport_names.put(key, airport_names);
            detail_airline_names.put(key, airline_names);
            showTripDetailDialog(_PricedItinerary, airport_names, airline_names);
        }
    }

    private void proceedBookFragment(PricedItinerary _PricedItinerary, HashMap<String, AirportLocation> airport_names,
                                     HashMap<String, String> airline_names) {
        IntentHelper.addObjectForKey(_PricedItinerary, "PricedItinerary");
        IntentHelper.addObjectForKey(airport_names, "Airport_names");
        IntentHelper.addObjectForKey(airline_names, "Airline_names");

        FragmentHelper.replaceFragment(this, new FlightBookFragment());
    }


    public void showTripDetailDialog(final PricedItinerary _PricedItinerary, final HashMap<String, AirportLocation> airport_names,
                                     final HashMap<String, String> airline_names) {

        if (_PricedItinerary._AirItineraryPricingInfo.size() == 0) {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.itinerary_not_found));
            return;
        }

        final Dialog dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_flight_detail, Gravity.CENTER);
        dialog.show();

        // region Find Views in dialog
        TextView afcurrency = (TextView) dialog.findViewById(R.id.afcurrency);
        TextView cfcurrency = (TextView) dialog.findViewById(R.id.cfcurrency);
        TextView ifcurrency = (TextView) dialog.findViewById(R.id.ifcurrency);
        TextView afamount = (TextView) dialog.findViewById(R.id.afamount);
        TextView cfamount = (TextView) dialog.findViewById(R.id.cfamount);
        TextView ifamount = (TextView) dialog.findViewById(R.id.ifamount);
        View cnn_fare_layout = dialog.findViewById(R.id.cnn_fare_layout);
        View inf_fare_layout = dialog.findViewById(R.id.inf_fare_layout);

        TextView bfcurrency = (TextView) dialog.findViewById(R.id.bfcurrency);
        TextView fntcurrency = (TextView) dialog.findViewById(R.id.fntcurrency);
        TextView totalcurrency = (TextView) dialog.findViewById(R.id.totalcurrency);
        TextView bfamount = (TextView) dialog.findViewById(R.id.bfamount);
        TextView fntamount = (TextView) dialog.findViewById(R.id.fntamount);
        TextView totalamount = (TextView) dialog.findViewById(R.id.totalamount);

        View btn_detail_continue = dialog.findViewById(R.id.btn_detail_continue);
        View btn_detail_back = dialog.findViewById(R.id.btn_detail_back);
        // endregion

        cnn_fare_layout.setVisibility(View.GONE);
        inf_fare_layout.setVisibility(View.GONE);

        PricedItinerary.AirItineraryPricingInfo _AirItineraryPricingInfo = _PricedItinerary._AirItineraryPricingInfo.get(0);

        // Calculate each Passenger Fare
        for (int i = 0; i < _AirItineraryPricingInfo._PTC_FareBreakdowns._PTC_FareBreakdown.size(); i++) {
            PricedItinerary.AirItineraryPricingInfo.PTC_FareBreakdowns.PTC_FareBreakdown obj =
                    _AirItineraryPricingInfo._PTC_FareBreakdowns._PTC_FareBreakdown.get(i);
            String currency = obj._PassengerFare._EquivFare.CurrencyCode;
            String base_fare = obj._PassengerFare._EquivFare.Amount;
            switch (obj._PassengerTypeQuantity.Code) {
                case "ADT":
                    afcurrency.setText(currency);
                    afamount.setText(base_fare);
                    break;
                case "CNN":
                    cfcurrency.setText(currency);
                    cfamount.setText(base_fare);
                    cnn_fare_layout.setVisibility(View.VISIBLE);
                    break;
                case "INF":
                    ifcurrency.setText(currency);
                    ifamount.setText(base_fare);
                    inf_fare_layout.setVisibility(View.VISIBLE);
                    break;
            }
        }

        // Basic Fare
        bfamount.setText(_AirItineraryPricingInfo._ItinTotalFare._EquivFare.Amount);
        bfcurrency.setText(_AirItineraryPricingInfo._ItinTotalFare._EquivFare.CurrencyCode);

        // Fees and Taxes
        if (_AirItineraryPricingInfo._ItinTotalFare._Taxes._Tax.get(0).TaxCode.equals("TOTALTAX")) {
            fntamount.setText(_AirItineraryPricingInfo._ItinTotalFare._Taxes._Tax.get(0).Amount);
            fntcurrency.setText(_AirItineraryPricingInfo._ItinTotalFare._Taxes._Tax.get(0).CurrencyCode);
        }

        // Total Amount
        totalamount.setText(_AirItineraryPricingInfo._ItinTotalFare._TotalFare.Amount);
        totalcurrency.setText(_AirItineraryPricingInfo._ItinTotalFare._TotalFare.CurrencyCode);

        if (_PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption.size() > 0) {
            FlightSearchDetailAdapter adapter = new FlightSearchDetailAdapter(dialog.getContext(),
                    _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption, airport_names,
                    airline_names, _AirItineraryPricingInfo._FareInfos._FareInfo);
            ListView listView_row = (ListView) dialog.findViewById(R.id.listView_detail_row);
            //listView_row = LayoutHack.setListViewHeightBasedOnChildren(adapter, listView_row);
            listView_row.setAdapter(adapter);
        } else
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.no_flights_found));

        // CONTINUE BUTTON
        btn_detail_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                proceedBookFragment(_PricedItinerary, airport_names, airline_names);
            }
        });

        // BACK BUTTON
        btn_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}