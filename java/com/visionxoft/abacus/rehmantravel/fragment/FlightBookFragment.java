package com.visionxoft.abacus.rehmantravel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.adapter.FlightBookFareRulesAdapter;
import com.visionxoft.abacus.rehmantravel.adapter.FlightBookFlightSummaryAdapter;
import com.visionxoft.abacus.rehmantravel.adapter.FlightBookTaxesAdapter;
import com.visionxoft.abacus.rehmantravel.model.AirportLocation;
import com.visionxoft.abacus.rehmantravel.model.Client;
import com.visionxoft.abacus.rehmantravel.model.Country;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItineraryPricingInfo;
import com.visionxoft.abacus.rehmantravel.model.PricedItinerary.AirItineraryPricingInfo.PTC_FareBreakdowns.PTC_FareBreakdown;
import com.visionxoft.abacus.rehmantravel.model.Traveler;
import com.visionxoft.abacus.rehmantravel.task.FareRulesTask;
import com.visionxoft.abacus.rehmantravel.task.GetFlightStatus;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.PermissionHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;
import com.visionxoft.abacus.rehmantravel.utils.TicketHelper;
import com.visionxoft.abacus.rehmantravel.views.RippleView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Display forms to input passenger info for flight booking.
 * Provides buttons to show flight summary, fare rules and fare summary dialogs.
 */
public class FlightBookFragment extends Fragment {

    private MainActivity mainActivity;
    private Context context;
    private PricedItinerary _PricedItinerary;
    private HashMap<String, AirportLocation> airport_names;
    private List<Country> countries;
    private List<String> total_travelers;
    private Client client;
    private Traveler[] travelers;
    private int current_pos = 0;
    private EditText travel_firstname, travel_lastname, travel_dob, travel_doc_id, travel_doc_expire,
            visa_number, visa_place, visa_date, addr_address, addr_city, addr_state, addr_zip;
    private TextView traveler_title, traveler_no;
    private Spinner sp_prefix, sp_gender, sp_place_birth, sp_doc_issuing, sp_doc_type, sp_visa_type, sp_visa_issue,
            sp_visa_travel, sp_addr_type, sp_addr_country;
    private Calendar dob_min_date, dob_max_date;
    private Date departure_date;
    private boolean is_location_usa = false, is_flight_domestic = true, button_clicked = false;
    private Dialog dialog_client, fare_rule_dialog;
    private View rootView;
    public View selected_btn;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolbarTitle(getString(R.string.enter_passenger_info));
        mainActivity.setToolbarSubTitle(null);

        if (rootView != null) return rootView;

        // Get Objects
        Object obj0 = IntentHelper.getObjectForKey("tkt_oneWayDeparting");
        Object obj1 = IntentHelper.getObjectForKey("PricedItinerary");
        Object obj2 = IntentHelper.getObjectForKey("Airport_names");
        Object obj3 = IntentHelper.getObjectForKey("Country_codes");

        if (obj0 != null && obj1 != null && obj2 != null && obj3 != null) {

            String str_departure_date = (String) obj0;
            _PricedItinerary = (PricedItinerary) obj1;
            airport_names = (HashMap<String, AirportLocation>) obj2;
            countries = (List<Country>) obj3;

            rootView = inflater.inflate(R.layout.fragment_flight_book, container, false);
            context = rootView.getContext();

            // region Find Views
            RippleView btn_flight_summary = (RippleView) rootView.findViewById(R.id.btn_flight_summary);
            RippleView btn_fare_summary = (RippleView) rootView.findViewById(R.id.btn_fare_summary);
            RippleView btn_fare_rules = (RippleView) rootView.findViewById(R.id.btn_fare_rules);
            travel_firstname = (EditText) rootView.findViewById(R.id.travel_firstname);
            travel_lastname = (EditText) rootView.findViewById(R.id.travel_lastname);
            traveler_title = (TextView) rootView.findViewById(R.id.traveler_title);
            traveler_no = (TextView) rootView.findViewById(R.id.traveler_no);
            travel_dob = (EditText) rootView.findViewById(R.id.travel_dob);
            travel_doc_id = (EditText) rootView.findViewById(R.id.travel_doc_id);
            travel_doc_expire = (EditText) rootView.findViewById(R.id.travel_doc_expire);
            sp_prefix = (Spinner) rootView.findViewById(R.id.sp_prefix);
            sp_gender = (Spinner) rootView.findViewById(R.id.sp_gender);
            sp_place_birth = (Spinner) rootView.findViewById(R.id.sp_place_birth);
            sp_doc_issuing = (Spinner) rootView.findViewById(R.id.sp_doc_issuing);
            sp_doc_type = (Spinner) rootView.findViewById(R.id.sp_doc_type);

            View layout_visa_details = rootView.findViewById(R.id.layout_visa_details);
            TextView lbl_visa = (TextView) rootView.findViewById(R.id.lbl_visa);
            sp_visa_type = (Spinner) rootView.findViewById(R.id.sp_visa_type);
            sp_visa_issue = (Spinner) rootView.findViewById(R.id.sp_visa_issue);
            sp_visa_travel = (Spinner) rootView.findViewById(R.id.sp_visa_travel);
            visa_number = (EditText) rootView.findViewById(R.id.visa_number);
            visa_place = (EditText) rootView.findViewById(R.id.visa_place);
            visa_date = (EditText) rootView.findViewById(R.id.visa_date);

            TextView lbl_addr = (TextView) rootView.findViewById(R.id.lbl_addr);
            sp_addr_type = (Spinner) rootView.findViewById(R.id.sp_addr_type);
            sp_addr_country = (Spinner) rootView.findViewById(R.id.sp_addr_country);
            addr_address = (EditText) rootView.findViewById(R.id.addr_address);
            addr_city = (EditText) rootView.findViewById(R.id.addr_city);
            addr_state = (EditText) rootView.findViewById(R.id.addr_state);
            addr_zip = (EditText) rootView.findViewById(R.id.addr_zip);

            final LinearLayout btn_next = (LinearLayout) rootView.findViewById(R.id.btn_next);
            final LinearLayout btn_prev = (LinearLayout) rootView.findViewById(R.id.btn_prev);
            final LinearLayout btn_continue = (LinearLayout) rootView.findViewById(R.id.btn_continue);
            final View traveler_input = rootView.findViewById(R.id.traveler_input);
            // endregion

            // region Flight Summary Button
            btn_flight_summary.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    if (!button_clicked) {
                        button_clicked = true;
                        flightSummaryDialog();
                        button_clicked = false;
                    }
                }
            });
            // endregion

            // region Fare Summary Button
            btn_fare_summary.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    if (!button_clicked) {
                        button_clicked = true;
                        fareSummaryDialog();
                        button_clicked = false;
                    }
                }
            });
            // endregion

            // region Fare Rule Button
            btn_fare_rules.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView v) {
                    if (!button_clicked) {
                        button_clicked = true;
                        selected_btn = v;
                        fareRulesDialog();
                        button_clicked = false;
                    }
                }
            });
            // endregion

            // Check domestic and USA flights
            for (AirportLocation location : airport_names.values()) {
                if (!location.country.equals("Pakistan")) {
                    is_flight_domestic = false;
                }
                if (location.country.equals("USA")) {
                    is_location_usa = true;
                    break;
                }
            }

            travel_doc_id.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            visa_number.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            sp_doc_type.setSelection(is_flight_domestic ? 1 : 0);

            if (is_flight_domestic) {
                layout_visa_details.setVisibility(View.GONE);
            }

            if (is_location_usa) {
                lbl_addr.setText("Address Details (Compulsory)");
                lbl_visa.setText("Visa Details (Compulsory)");
            } else {
                lbl_addr.setText("Address Details (Optional)");
                lbl_visa.setText("Visa Details (Optional)");
            }

            // Collect all Travelers information
            total_travelers = new ArrayList<>();
            List<PricedItinerary.AirItineraryPricingInfo.PTC_FareBreakdowns.PTC_FareBreakdown> _PTC_FareBreakdown =
                    _PricedItinerary._AirItineraryPricingInfo.get(0)._PTC_FareBreakdowns._PTC_FareBreakdown;
            for (int i = 0; i < _PTC_FareBreakdown.size(); i++) {
                int total_travelers_int = Integer.valueOf(_PTC_FareBreakdown.get(i)._PassengerTypeQuantity.Quantity);
                for (int j = 0; j < total_travelers_int; j++)
                    total_travelers.add(_PTC_FareBreakdown.get(i)._PassengerTypeQuantity.Code);
            }

            travelers = new Traveler[total_travelers.size()];

            departure_date = new Date();
            try {
                departure_date = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()).parse(str_departure_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Initialize Traveler name
            setTravelerTitle(departure_date, total_travelers.get(current_pos), current_pos);

            if (total_travelers.size() > 1) {
                btn_next.setVisibility(View.VISIBLE);
                btn_prev.setVisibility(View.INVISIBLE);
                btn_continue.setVisibility(View.INVISIBLE);
            } else {
                btn_next.setVisibility(View.GONE);
                btn_prev.setVisibility(View.GONE);
                btn_continue.setVisibility(View.VISIBLE);
            }

            // Collect Country Names from Countries object
            List<String> spinner_list = new ArrayList<>();
            for (Country country : countries)
                spinner_list.add(country.countryName);
            final ArrayAdapter<String> countries_adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, spinner_list);

            sp_place_birth.setAdapter(countries_adapter);
            sp_doc_issuing.setAdapter(countries_adapter);
            sp_visa_issue.setAdapter(countries_adapter);
            sp_visa_travel.setAdapter(countries_adapter);
            sp_addr_country.setAdapter(countries_adapter);

            travel_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar min_date = Calendar.getInstance(Locale.getDefault());
                    if (dob_min_date != null) min_date.setTime(dob_min_date.getTime());
                    else min_date.add(Calendar.YEAR, -100);
                    Calendar max_date = Calendar.getInstance(Locale.getDefault());
                    max_date.setTime(dob_max_date.getTime());
                    Calendar current_date = Calendar.getInstance(Locale.getDefault());
                    current_date.setTime(dob_max_date.getTime());
                    PhoneFunctionality.showCalendar(FlightBookFragment.this, v, current_date, min_date, max_date, true);
                }
            });

            travel_doc_expire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar min_date = Calendar.getInstance(Locale.getDefault());
                    Calendar max_date = Calendar.getInstance(Locale.getDefault());
                    max_date.add(Calendar.YEAR, 10);
                    Calendar current_date = Calendar.getInstance(Locale.getDefault());
                    current_date.setTime(dob_max_date.getTime());
                    PhoneFunctionality.showCalendar(FlightBookFragment.this, v, current_date, min_date, max_date);
                }
            });

            visa_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar min_date = Calendar.getInstance(Locale.getDefault());
                    Calendar max_date = Calendar.getInstance(Locale.getDefault());
                    min_date.add(Calendar.YEAR, -10);
                    max_date.add(Calendar.YEAR, 1);
                    Calendar current_date = Calendar.getInstance(Locale.getDefault());
                    PhoneFunctionality.showCalendar(FlightBookFragment.this, v, current_date, min_date, max_date);
                }
            });

            // region Next Button
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current_pos < total_travelers.size()) {

                        // Save current traveler
                        Traveler traveler = getTravelerInput();
                        if (traveler != null) {
                            // Animation
                            YoYo.with(Techniques.SlideInRight).duration(400).playOn(traveler_input);
                            travelers[current_pos] = traveler;
                            // Increment
                            current_pos++;
                            // View visibility
                            if (current_pos > 0) btn_prev.setVisibility(View.VISIBLE);
                            if (current_pos >= total_travelers.size() - 1) {
                                btn_next.setVisibility(View.INVISIBLE);
                                btn_continue.setVisibility(View.VISIBLE);
                            }
                            // Display next traveler if available
                            if (current_pos <= total_travelers.size()) {
                                setTravelerTitle(departure_date, total_travelers.get(current_pos), current_pos);
                                if (travelers[current_pos] != null) setTravelerInput(travelers[current_pos]);
                                else resetTravelerInput();
                            }
                        } else {

                            // Animation
                            PhoneFunctionality.errorAnimation(v);
                            PhoneFunctionality.makeToast(mainActivity, getString(R.string.error_all_field_required));
                        }
                    }
                }
            });
            // endregion

            // region Previous Button
            btn_prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current_pos > 0) {
                        // Animation
                        YoYo.with(Techniques.SlideInLeft).duration(400).playOn(traveler_input);
                        // Save current traveler
                        travelers[current_pos] = getTravelerInput();
                        // Decrement
                        current_pos--;
                        // View visibility
                        if (current_pos == 0) btn_prev.setVisibility(View.INVISIBLE);
                        if (current_pos < total_travelers.size() - 1) {
                            btn_next.setVisibility(View.VISIBLE);
                            btn_continue.setVisibility(View.INVISIBLE);
                        }
                        // Display Previous traveler
                        setTravelerTitle(departure_date, total_travelers.get(current_pos), current_pos);
                        if (travelers[current_pos] != null)
                            setTravelerInput(travelers[current_pos]);
                    }
                }
            });
            // endregion

            // region Continue Button
            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreferenceHelper.getInt(mainActivity, PreferenceHelper.GRANT_SEND_SMS) == -1)
                        PermissionHelper.checkSMSPermission(mainActivity);

                    // Save Last Traveler
                    Traveler traveler = getTravelerInput();
                    if (traveler == null) {
                        PhoneFunctionality.errorAnimation(v);
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.error_all_field_required));
                        return;
                    }

                    travelers[total_travelers.size() - 1] = traveler;

                    if (dialog_client == null) {
                        dialog_client = DialogHelper.createCustomDialog(mainActivity,
                                R.layout.dialog_flight_book_client, Gravity.CENTER);

                        // Find Views
                        final EditText resident_no = (EditText) dialog_client.findViewById(R.id.resident_no);
                        final EditText resident_email = (EditText) dialog_client.findViewById(R.id.resident_email);
                        final EditText destination_no = (EditText) dialog_client.findViewById(R.id.destination_no);
                        View resident_no_clear = dialog_client.findViewById(R.id.resident_no_clear);
                        View resident_email_clear = dialog_client.findViewById(R.id.resident_email_clear);
                        View destination_no_clear = dialog_client.findViewById(R.id.destination_no_clear);
                        View destination_label = dialog_client.findViewById(R.id.destination_label);
                        final CheckBox cb_terms_and_conditions = (CheckBox) dialog_client.findViewById(R.id.cb_terms_and_conditions);
                        final View lbl_imp_note = dialog_client.findViewById(R.id.lbl_imp_note);
                        final CheckBox cb_imp_note = (CheckBox) dialog_client.findViewById(R.id.cb_imp_note);
                        View btn_client_back = dialog_client.findViewById(R.id.btn_client_back);
                        final View btn_client_book = dialog_client.findViewById(R.id.btn_client_book);

                        if (is_flight_domestic) {
                            destination_label.setVisibility(View.GONE);
                            destination_no.setVisibility(View.GONE);
                            destination_no_clear.setVisibility(View.GONE);
                            lbl_imp_note.setVisibility(View.GONE);
                            cb_imp_note.setVisibility(View.GONE);
                        }

                        btn_client_book.setEnabled(false);

                        cb_terms_and_conditions.setText(Html.fromHtml(getString(R.string.terms_and_condtions)));
                        cb_imp_note.setText(Html.fromHtml(getString(R.string.imp_note)));

                        cb_terms_and_conditions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                if (isChecked && (is_flight_domestic || cb_imp_note.isChecked()))
                                    btn_client_book.setEnabled(true);
                                else btn_client_book.setEnabled(false);
                            }
                        });

                        cb_imp_note.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                if (isChecked && cb_terms_and_conditions.isChecked()) btn_client_book.setEnabled(true);
                                else btn_client_book.setEnabled(false);
                            }
                        });

                        // Clear phone text Button
                        resident_no_clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                resident_no.setText("");
                            }
                        });

                        // Clear email text Button
                        resident_email_clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                resident_email.setText("");
                            }
                        });

                        destination_no_clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                destination_no.setText("");
                            }
                        });

                        // Back Button
                        btn_client_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_client.hide();
                            }
                        });

                        // Book Button
                        btn_client_book.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                client = new Client();
                                client.resident_no = resident_no.getText().toString().trim();
                                client.resident_email = resident_email.getText().toString().trim();
                                client.destination_no = destination_no.getText().toString().trim();
                                if (!client.resident_no.equals("") && !client.resident_email.equals("") && (is_flight_domestic || !client.destination_no.equals(""))) {
                                    if (FormatString.isContactValid(client.resident_no) || FormatString.isContactValid(client.destination_no)) {
                                        if (FormatString.isEmailValid(client.resident_email)) {
                                            // Save client info for later use
                                            IntentHelper.addObjectForKey(client, "client_info");

                                            // Check Flight Status before booking
                                            new GetFlightStatus(FlightBookFragment.this, _PricedItinerary,
                                                    total_travelers.size()).execute();
                                        } else
                                            resident_email.setError(getString(R.string.error_invalid_email));
                                    } else
                                        resident_no.setError(getString(R.string.error_invalid_number));
                                } else
                                    PhoneFunctionality.makeToast(mainActivity, getString(R.string.error_all_field_required));
                            }
                        });
                    }
                    dialog_client.show();


                }
            });
            // endregion
        } else {
            mainActivity.finish();
        }

        return rootView;
    }

    /**
     * Set Traveler title
     *
     * @param departure_date Date of departure
     * @param traveler_type  Type of Traveler (ADT/CNN/INF)
     * @param traveler_no    Traveler serial number
     */
    private void setTravelerTitle(final Date departure_date, final String traveler_type,
                                  final int traveler_no) {
        this.traveler_no.setText("#" + String.valueOf(traveler_no + 1));
        dob_min_date = Calendar.getInstance(Locale.getDefault());
        dob_max_date = Calendar.getInstance(Locale.getDefault());
        switch (traveler_type) {
            case "ADT":
                traveler_title.setText(R.string.value_adult);
                sp_prefix.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                        context.getResources().getStringArray(R.array.prefixAdultValues)));
                dob_min_date = null;
                dob_max_date.setTime(departure_date);
                dob_max_date.add(Calendar.YEAR, -12);
                break;
            case "CNN":
                traveler_title.setText(R.string.value_child);
                sp_prefix.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                        context.getResources().getStringArray(R.array.prefixNonAdultValues)));
                dob_min_date.setTime(departure_date);
                dob_min_date.add(Calendar.YEAR, -12);
                dob_max_date.setTime(departure_date);
                dob_max_date.add(Calendar.YEAR, -2);
                break;
            case "INF":
                traveler_title.setText(R.string.value_infant);
                sp_prefix.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                        context.getResources().getStringArray(R.array.prefixNonAdultValues)));
                dob_min_date.setTime(departure_date);
                dob_min_date.add(Calendar.YEAR, -2);
                dob_max_date.setTime(departure_date);
                dob_max_date.add(Calendar.DAY_OF_YEAR, -2);
                break;
        }
    }

    // region Reset Values
    private void resetTravelerInput() {
        travel_firstname.setText("");
        travel_lastname.setText("");
        travel_dob.setText("");
        travel_doc_id.setText("");
        travel_doc_expire.setText("");
        visa_number.setText("");
        visa_place.setText("");
        visa_date.setText("");
        addr_address.setText("");
        addr_city.setText("");
        addr_state.setText("");
        addr_zip.setText("");
        sp_prefix.setSelection(0);
        sp_gender.setSelection(0);
        sp_place_birth.setSelection(0);
        sp_doc_type.setSelection(is_flight_domestic ? 1 : 0);
        sp_doc_issuing.setSelection(0);
        sp_visa_type.setSelection(0);
        sp_visa_issue.setSelection(0);
        sp_visa_travel.setSelection(0);
        sp_addr_type.setSelection(0);
        sp_addr_country.setSelection(0);
    }
    // endregion

    // region Set Traveler Values
    private void setTravelerInput(Traveler traveler) {
        travel_firstname.setText(traveler.fname);
        travel_lastname.setText(traveler.lname);
        travel_dob.setText(traveler.dob);
        travel_doc_id.setText(traveler.doc_id);
        travel_doc_expire.setText(traveler.doc_expiry);
        visa_number.setText(traveler.visa_number);
        visa_place.setText(traveler.visa_place);
        visa_date.setText(traveler.visa_date);
        addr_address.setText(traveler.addr_address);
        addr_city.setText(traveler.addr_city);
        addr_state.setText(traveler.addr_state);
        addr_zip.setText(traveler.addr_zip);
        sp_prefix.setSelection(traveler.prefix);
        sp_gender.setSelection(traveler.gender);
        sp_place_birth.setSelection(traveler.pob);
        sp_doc_type.setSelection(traveler.doc_type);
        sp_doc_issuing.setSelection(traveler.doc_issuing);
        sp_visa_type.setSelection(traveler.visa_type);
        sp_visa_issue.setSelection(traveler.visa_issue);
        sp_visa_travel.setSelection(traveler.visa_travel);
        sp_addr_type.setSelection(traveler.addr_type);
        sp_addr_country.setSelection(traveler.addr_country);
    }
    // endregion

    // region Get Traveler Input
    private Traveler getTravelerInput() {
        Traveler traveler = new Traveler();
        traveler.type = traveler_title.getText().toString();
        traveler.fname = travel_firstname.getText().toString();
        traveler.lname = travel_lastname.getText().toString();
        traveler.dob = travel_dob.getText().toString();
        traveler.doc_id = travel_doc_id.getText().toString();
        traveler.doc_expiry = travel_doc_expire.getText().toString();
        traveler.addr_address = addr_address.getText().toString();
        traveler.addr_city = addr_city.getText().toString();
        traveler.addr_state = addr_state.getText().toString();
        traveler.addr_zip = addr_zip.getText().toString();
        traveler.prefix = sp_prefix.getSelectedItemPosition();
        traveler.gender = sp_gender.getSelectedItemPosition();
        traveler.pob = sp_place_birth.getSelectedItemPosition();
        traveler.doc_type = sp_doc_type.getSelectedItemPosition();
        traveler.doc_issuing = sp_doc_issuing.getSelectedItemPosition();
        if (!traveler.fname.equals("") && !traveler.lname.equals("") && !traveler.dob.equals("")
                && !traveler.doc_id.equals("") && !traveler.doc_expiry.equals("")) {
            if (is_location_usa) {
                traveler.visa_number = visa_number.getText().toString();
                traveler.visa_place = visa_place.getText().toString();
                traveler.visa_date = visa_date.getText().toString();
                if (!traveler.visa_number.equals("") && !traveler.visa_place.equals("") && !traveler.visa_date.equals("") &&
                        !traveler.addr_address.equals("") && !traveler.addr_city.equals("") && !traveler.addr_state.equals("")) {
                    return traveler;
                } else return null;
            } else return traveler;
        } else return null;
    }
    // endregion

    // region Setup Flight Summary Dialog
    private void flightSummaryDialog() {
        final Dialog dialog = DialogHelper.createCustomDialog(context, R.layout.dialog_flight_book_flight_summary, Gravity.CENTER);
        ListView LVFlightSummary = (ListView) dialog.findViewById(R.id.LVFlightSummary);

        FlightBookFlightSummaryAdapter adapter = new FlightBookFlightSummaryAdapter(context,
                _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption, airport_names);
        LVFlightSummary.setAdapter(adapter);

        dialog.show();

        LinearLayout btn_back_flight_sum = (LinearLayout) dialog.findViewById(R.id.btn_back_flight_sum);
        btn_back_flight_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    // endregion

    // region Setup Fare Summary Dialog
    private void fareSummaryDialog() {

        final Dialog dialog = DialogHelper.createCustomDialog(context, R.layout.dialog_flight_book_fare_summary, Gravity.CENTER);
        LinearLayout btn_back_fare_sum = (LinearLayout) dialog.findViewById(R.id.btn_back_fare_sum);
        btn_back_fare_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final Fare_Sum_ViewHolder holder = new Fare_Sum_ViewHolder(dialog);
        holder.fare_child_ll.setVisibility(View.GONE);
        holder.fare_infant_ll.setVisibility(View.GONE);

        holder.show_fair_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.fare_detail_layout.isShown()) {
                    holder.fare_detail_layout.setVisibility(View.GONE);
                    holder.show_fair_detail.setImageResource(R.drawable.ic_add_white_18dp);
                } else {
                    holder.fare_detail_layout.setVisibility(View.VISIBLE);
                    holder.show_fair_detail.setImageResource(R.drawable.ic_remove_white_18dp);
                }
            }
        });

        holder.show_taxes_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.LVTaxDetails.isShown()) {
                    holder.LVTaxDetails.setVisibility(View.GONE);
                    holder.show_taxes_detail.setImageResource(R.drawable.ic_add_white_18dp);
                } else {
                    holder.LVTaxDetails.setVisibility(View.VISIBLE);
                    holder.show_taxes_detail.setImageResource(R.drawable.ic_remove_white_18dp);
                }
            }
        });

        List<PTC_FareBreakdown.PassengerFare.Taxes.Tax> tax_list = new ArrayList<>();

        // Collect All Taxes from PricedItinerary
        for (PricedItinerary.AirItineraryPricingInfo obj : _PricedItinerary._AirItineraryPricingInfo)
            for (PTC_FareBreakdown _PTC_FareBreakdown : obj._PTC_FareBreakdowns._PTC_FareBreakdown)
                for (PTC_FareBreakdown.PassengerFare.Taxes.Tax tax : _PTC_FareBreakdown._PassengerFare._Taxes._Tax)
                    tax_list.add(tax);

        FlightBookTaxesAdapter adapter = new FlightBookTaxesAdapter(context, tax_list);
        holder.LVTaxDetails.setAdapter(adapter);

        AirItineraryPricingInfo pricingInfo = _PricedItinerary._AirItineraryPricingInfo.get(0);

        // Calculate each Passenger fare
        List<PTC_FareBreakdown> _PTC_FareBreakdown = pricingInfo._PTC_FareBreakdowns._PTC_FareBreakdown;
        for (int i = 0; i < _PTC_FareBreakdown.size(); i++)
            setFaresAndTaxes(holder, _PTC_FareBreakdown.get(i));

        // BASIC FARE
        holder.fare_amount.setText(pricingInfo._ItinTotalFare._EquivFare.Amount);
        holder.fare_currency.setText(pricingInfo._ItinTotalFare._EquivFare.CurrencyCode);

        // FEES AND TAXES
        holder.taxes_amount.setText(pricingInfo._ItinTotalFare._Taxes._Tax.get(0).Amount);
        holder.taxes_currency.setText(pricingInfo._ItinTotalFare._Taxes._Tax.get(0).CurrencyCode);

        // TOTAL AMOUNT
        holder.total_amount.setText(pricingInfo._ItinTotalFare._TotalFare.Amount);
        holder.total_currency.setText(pricingInfo._ItinTotalFare._TotalFare.CurrencyCode);

        dialog.show();
    }

    private void setFaresAndTaxes(Fare_Sum_ViewHolder holder, PTC_FareBreakdown obj) {
        String currency = obj._PassengerFare._EquivFare.CurrencyCode;
        String amount = obj._PassengerFare._EquivFare.Amount;
        int quantity = Math.round(Float.parseFloat(obj._PassengerTypeQuantity.Quantity));
        switch (obj._PassengerTypeQuantity.Code) {
            case "ADT":
                holder.fare_adult.setText("Adult → " + String.valueOf(quantity));
                holder.fare_adult_currency.setText(currency);
                holder.fare_adult_amount.setText(amount);
                break;
            case "CNN":
                holder.fare_child.setText("Children → " + String.valueOf(quantity));
                holder.fare_child_currency.setText(currency);
                holder.fare_child_amount.setText(amount);
                holder.fare_child_ll.setVisibility(View.VISIBLE);
                break;
            case "INF":
                holder.fare_infant.setText("Infant → " + String.valueOf(quantity));
                holder.fare_infant_currency.setText(currency);
                holder.fare_infant_amount.setText(amount);
                holder.fare_infant_ll.setVisibility(View.VISIBLE);
                break;
        }
    }
    // endregion

    // region Setup Fare Rules Dialog
    private void fareRulesDialog() {
        if (fare_rule_dialog == null) {
            new FareRulesTask(this, _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption,
                    _PricedItinerary._TPA_Extensions._ValidatingCarrier.Code).execute();
        } else fare_rule_dialog.show();
    }

    public void showFareRuleDialog(HashMap<Integer, String> fare_rules) {
        fare_rule_dialog = DialogHelper.createCustomDialog(context, R.layout.dialog_flight_book_fare_rules, Gravity.CENTER);
        ListView LVFareRules = (ListView) fare_rule_dialog.findViewById(R.id.LVFareRules);

        FlightBookFareRulesAdapter adapter = new FlightBookFareRulesAdapter(context,
                _PricedItinerary._AirItinerary._OriginDestinationOptions._OriginDestinationOption,
                airport_names, fare_rules);
        LVFareRules.setAdapter(adapter);

        LinearLayout btn_back_fare_rules = (LinearLayout) fare_rule_dialog.findViewById(R.id.btn_back_fare_rules);
        btn_back_fare_rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fare_rule_dialog.hide();
            }
        });

        fare_rule_dialog.show();
    }
    // endregion

    // region Generate PNR
    public void proceedGeneratePNR() {
        if (TicketHelper.preparePnrParams(FlightBookFragment.this, _PricedItinerary, client, travelers, countries) == 1)
            dialog_client.hide();
        else
            mainActivity.finish();
    }
    // endregion

    // region Viewholder for Flight Summaries
    private class Fare_Sum_ViewHolder {
        ImageView show_fair_detail, show_taxes_detail;
        LinearLayout fare_detail_layout, fare_child_ll, fare_infant_ll;
        TextView fare_amount, fare_currency, fare_adult, fare_adult_amount,
                fare_adult_currency, fare_child, fare_child_amount, fare_child_currency,
                fare_infant, fare_infant_amount, fare_infant_currency, taxes_amount,
                taxes_currency, total_amount, total_currency;
        ListView LVTaxDetails;

        Fare_Sum_ViewHolder(Dialog v) {
            show_fair_detail = (ImageView) v.findViewById(R.id.show_fair_detail);
            show_taxes_detail = (ImageView) v.findViewById(R.id.show_taxes_detail);
            fare_detail_layout = (LinearLayout) v.findViewById(R.id.fare_detail_layout);
            fare_child_ll = (LinearLayout) v.findViewById(R.id.fare_child_ll);
            fare_infant_ll = (LinearLayout) v.findViewById(R.id.fare_infant_ll);
            fare_amount = (TextView) v.findViewById(R.id.fare_amount);
            fare_currency = (TextView) v.findViewById(R.id.fare_currency);
            fare_adult = (TextView) v.findViewById(R.id.fare_adult);
            fare_adult_amount = (TextView) v.findViewById(R.id.fare_adult_amount);
            fare_adult_currency = (TextView) v.findViewById(R.id.fare_adult_currency);
            fare_child = (TextView) v.findViewById(R.id.fare_child);
            fare_child_amount = (TextView) v.findViewById(R.id.fare_child_amount);
            fare_child_currency = (TextView) v.findViewById(R.id.fare_child_currency);
            fare_infant = (TextView) v.findViewById(R.id.fare_infant);
            fare_infant_amount = (TextView) v.findViewById(R.id.fare_infant_amount);
            fare_infant_currency = (TextView) v.findViewById(R.id.fare_infant_currency);
            taxes_amount = (TextView) v.findViewById(R.id.taxes_amount);
            taxes_currency = (TextView) v.findViewById(R.id.taxes_currency);
            total_amount = (TextView) v.findViewById(R.id.total_amount);
            total_currency = (TextView) v.findViewById(R.id.total_currency);
            LVTaxDetails = (ListView) v.findViewById(R.id.LVTaxDetails);
        }
    }
    // endregion
}


