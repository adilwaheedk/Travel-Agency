package com.visionxoft.abacus.rehmantravel.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.Country;
import com.visionxoft.abacus.rehmantravel.model.Currency;
import com.visionxoft.abacus.rehmantravel.task.GetAirlineNames;
import com.visionxoft.abacus.rehmantravel.task.GetAirportLocation;
import com.visionxoft.abacus.rehmantravel.utils.FileHelper;
import com.visionxoft.abacus.rehmantravel.utils.FormatParameters;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.FragmentHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;
import com.visionxoft.abacus.rehmantravel.utils.XmlConverter;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/**
 * Display round-trip, one-way or multi destinations forms to input desired flights
 * You can set departure and arrival locations and dates. Specify phone and types of passengers.
 * Choose preferred airline (optionally) and select business or economic class.
 */
public class FlightFragment extends Fragment {

    // region CLASS INSTANCES
    private List<Country> countries;
    private GetAirportLocation getAirportLocation;
    private GetAirlineNames getAirlineNames;
    private TextView tvdepart, tvreturn, tvdepart1, tvdepart2, tvdepart3, tvdepart4, tvdepart5,
            tvdepart6;
    private RadioButton radiort, radioow, radiomd;
    private MultiAutoCompleteTextView autoflyto, autoflyto1,
            autoflyfrom2, autoflyto2, autoflyfrom3, autoflyto3, autoflyfrom4, autoflyto4,
            autoflyfrom5, autoflyto5, autoflyfrom6, autoflyto6, autoprefairline;
    private View rootView, layout_single_dest, layout_multi_dest, ll3_loc, ll3_dep, ll4_loc, ll4_dep,
            ll5_loc, ll5_dep, ll6_loc, ll6_dep, imgreturn, layout_advance_options;
    private ImageView img_advance_options;
    private Calendar current_date, max_depart_date;
    private MainActivity mainActivity;
    public static MultiAutoCompleteTextView autoflyfrom, autoflyfrom1;
    private boolean button_clicked = false;
    private int no_of_dests;
    // endregion

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        AgentSession agentSession = PreferenceHelper.getAgentSession(mainActivity);
        if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
            mainActivity.setToolbarTitle(getString(R.string.guest_mode));
            mainActivity.setToolbarSubTitle(null);
        } else {
            mainActivity.setToolbarTitle(agentSession.AGENT_NAME);
            mainActivity.setToolbarSubTitle(getString(R.string.credit_limit) + agentSession.CurrentCredit);
        }

        if (rootView != null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_flight, container, false);

        // Initialize Dates
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
        current_date = Calendar.getInstance(Locale.getDefault());
        max_depart_date = Calendar.getInstance(Locale.getDefault());
        max_depart_date.add(Calendar.YEAR, 1);

        // region Find Views
        layout_single_dest = rootView.findViewById(R.id.layout_single_dest);
        layout_multi_dest = rootView.findViewById(R.id.layout_multi_dest);
        ll3_loc = rootView.findViewById(R.id.ll3_loc);
        ll3_dep = rootView.findViewById(R.id.ll3_dep);
        ll4_loc = rootView.findViewById(R.id.ll4_loc);
        ll4_dep = rootView.findViewById(R.id.ll4_dep);
        ll5_loc = rootView.findViewById(R.id.ll5_loc);
        ll5_dep = rootView.findViewById(R.id.ll5_dep);
        ll6_loc = rootView.findViewById(R.id.ll6_loc);
        ll6_dep = rootView.findViewById(R.id.ll6_dep);
        autoflyfrom = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyfrom);
        autoflyto = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyto);
        autoflyfrom1 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyfrom1);
        autoflyto1 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyto1);
        autoflyfrom2 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyfrom2);
        autoflyto2 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyto2);
        autoflyfrom3 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyfrom3);
        autoflyto3 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyto3);
        autoflyfrom4 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyfrom4);
        autoflyto4 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyto4);
        autoflyfrom5 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyfrom5);
        autoflyto5 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyto5);
        autoflyfrom6 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyfrom6);
        autoflyto6 = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoflyto6);
        autoprefairline = (MultiAutoCompleteTextView) rootView.findViewById(R.id.autoprefairline);
        radiort = (RadioButton) rootView.findViewById(R.id.radiort);
        radioow = (RadioButton) rootView.findViewById(R.id.radioow);
        radiomd = (RadioButton) rootView.findViewById(R.id.radiomd);
        tvdepart = (TextView) rootView.findViewById(R.id.tvdepart);
        tvreturn = (TextView) rootView.findViewById(R.id.tvreturn);
        tvdepart1 = (TextView) rootView.findViewById(R.id.tvdepart1);
        tvdepart2 = (TextView) rootView.findViewById(R.id.tvdepart2);
        tvdepart3 = (TextView) rootView.findViewById(R.id.tvdepart3);
        tvdepart4 = (TextView) rootView.findViewById(R.id.tvdepart4);
        tvdepart5 = (TextView) rootView.findViewById(R.id.tvdepart5);
        tvdepart6 = (TextView) rootView.findViewById(R.id.tvdepart6);

        layout_advance_options = rootView.findViewById(R.id.layout_advance_options);
        img_advance_options = (ImageView) rootView.findViewById(R.id.img_advance_options);

        final TextView btn_add_destination = (TextView) rootView.findViewById(R.id.btn_add_destination);
        final View toggle_advance_options = rootView.findViewById(R.id.toggle_advance_options);
        final Spinner spadult = (Spinner) rootView.findViewById(R.id.spadult);
        final Spinner spchild = (Spinner) rootView.findViewById(R.id.spchild);
        final Spinner spinfant = (Spinner) rootView.findViewById(R.id.spinfant);
        final Spinner spclass = (Spinner) rootView.findViewById(R.id.spclass);
        final Spinner spcurrency = (Spinner) rootView.findViewById(R.id.spcurrency);

        View clearflyfrom = rootView.findViewById(R.id.clearflyfrom);
        View clearflyto = rootView.findViewById(R.id.clearflyto);
        View clearflyfrom1 = rootView.findViewById(R.id.clearflyfrom1);
        View clearflyto1 = rootView.findViewById(R.id.clearflyto1);
        View clearflyfrom2 = rootView.findViewById(R.id.clearflyfrom2);
        View clearflyto2 = rootView.findViewById(R.id.clearflyto2);
        View clearflyfrom3 = rootView.findViewById(R.id.clearflyfrom3);
        View clearflyto3 = rootView.findViewById(R.id.clearflyto3);
        View clearflyfrom4 = rootView.findViewById(R.id.clearflyfrom4);
        View clearflyto4 = rootView.findViewById(R.id.clearflyto4);
        View clearflyfrom5 = rootView.findViewById(R.id.clearflyfrom5);
        View clearflyto5 = rootView.findViewById(R.id.clearflyto5);
        View clearflyfrom6 = rootView.findViewById(R.id.clearflyfrom6);
        View clearflyto6 = rootView.findViewById(R.id.clearflyto6);
        View clearprefairline = rootView.findViewById(R.id.clearprefairline);
        View imgswaplocs = rootView.findViewById(R.id.imgswaplocs);
        imgreturn = rootView.findViewById(R.id.imgreturn);

        layout_single_dest.setVisibility(View.VISIBLE);
        layout_advance_options.setVisibility(View.GONE);
        layout_multi_dest.setVisibility(View.GONE);
        ll3_loc.setVisibility(View.GONE);
        ll3_dep.setVisibility(View.GONE);
        ll4_loc.setVisibility(View.GONE);
        ll4_dep.setVisibility(View.GONE);
        ll5_loc.setVisibility(View.GONE);
        ll5_dep.setVisibility(View.GONE);
        ll6_loc.setVisibility(View.GONE);
        ll6_dep.setVisibility(View.GONE);
        // endregion

        getAirportLocation = new GetAirportLocation(mainActivity, autoflyfrom);
        getAirlineNames = new GetAirlineNames(mainActivity, autoprefairline);
        spclass.setSelection(0);

        // region Toggle Advance Button
        toggle_advance_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean lao_shown = layout_advance_options.isShown();
                Techniques techs = lao_shown ? Techniques.FadeOutUp : Techniques.FadeInDown;
                YoYo.with(techs).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (lao_shown) {
                            layout_advance_options.setVisibility(View.GONE);
                            img_advance_options.setImageResource(R.drawable.ic_add_white_18dp);
                        } else {
                            layout_advance_options.setVisibility(View.VISIBLE);
                            img_advance_options.setImageResource(R.drawable.ic_remove_white_18dp);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                }).duration(200).playOn(layout_advance_options);
            }
        });
        // endregion

        // Convert currencies
        XmlConverter converter = new XmlConverter();
        final List<Currency> currencyList = converter.getCurrencies(converter.getXmlParser(mainActivity, "currencies.xml"));

        // Get List of Countries from JSON file
        try {
            countries = JsonConverter.parseJsonToCountryCodes(FileHelper.readAssetFileToString(mainActivity, "country_codes"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get Currency values
        List<String> currencyValues = new ArrayList<>();
        for (Currency currency : currencyList)
            currencyValues.add(currency.currency_name + " (" + currency.currency_code + ")");
        spcurrency.setAdapter(new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, currencyValues));

        // region Radio Button Listeners
        radiort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layout_single_dest.setVisibility(View.VISIBLE);
                    layout_multi_dest.setVisibility(View.GONE);
                }
            }
        });

        radioow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!layout_single_dest.isShown()) {
                        layout_single_dest.setVisibility(View.VISIBLE);
                        layout_multi_dest.setVisibility(View.GONE);
                    }
                    tvreturn.setVisibility(View.GONE);
                    imgreturn.setVisibility(View.GONE);
                } else {
                    tvreturn.setVisibility(View.VISIBLE);
                    imgreturn.setVisibility(View.VISIBLE);
                }
            }
        });

        radiomd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layout_multi_dest.requestFocus();
                    layout_single_dest.setVisibility(View.GONE);
                    layout_multi_dest.setVisibility(View.VISIBLE);
                }
            }
        });
        // endregion

        //region OnClick Listeners
        imgswaplocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = autoflyfrom.getText().toString();
                autoflyfrom.setText(autoflyto.getText().toString());
                autoflyto.setText(temp);
            }
        });

        autoflyfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyfrom);
            }
        });

        autoflyto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyto);
            }
        });

        autoflyfrom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyfrom1);
            }
        });

        autoflyto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyto1);
            }
        });

        autoflyfrom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyfrom2);
            }
        });

        autoflyto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyto2);
            }
        });

        autoflyfrom3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyfrom3);
            }
        });

        autoflyto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyto3);
            }
        });

        autoflyfrom4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyfrom4);
            }
        });

        autoflyto4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyto4);
            }
        });

        autoflyfrom5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyfrom5);
            }
        });

        autoflyto5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyto5);
            }
        });

        autoflyfrom6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyfrom6);
            }
        });

        autoflyto6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirportLocation(autoflyto6);
            }
        });

        autoprefairline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execAirlineName(autoprefairline);
            }
        });
        //endregion

        // region OnItemClick Listeners
        autoflyfrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyfrom.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyto.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyfrom1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyfrom1.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyto1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyto1.setText(parent.getItemAtPosition(position).toString());
                autoflyfrom2.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyfrom2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyfrom2.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyto2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyto2.setText(parent.getItemAtPosition(position).toString());
                autoflyfrom3.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyfrom3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyfrom3.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyto3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyto3.setText(parent.getItemAtPosition(position).toString());
                autoflyfrom4.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyfrom4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyfrom4.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyto4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyto4.setText(parent.getItemAtPosition(position).toString());
                autoflyfrom5.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyfrom5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyfrom5.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyto5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyto5.setText(parent.getItemAtPosition(position).toString());
                autoflyfrom6.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyfrom6.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyfrom6.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoflyto6.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoflyto6.setText(parent.getItemAtPosition(position).toString());
            }
        });

        autoprefairline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoprefairline.setText(parent.getItemAtPosition(position).toString());
            }
        });
        // endregion

        //region OnClick Listeners to clear textViews
        clearflyfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyfrom.setText("");
                autoflyfrom.requestFocus();
            }
        });

        clearflyto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyto.setText("");
                autoflyto.requestFocus();
            }
        });

        clearflyfrom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyfrom1.setText("");
                autoflyfrom1.requestFocus();
            }
        });

        clearflyto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyto1.setText("");
                autoflyto1.requestFocus();
            }
        });

        clearflyfrom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyfrom2.setText("");
                autoflyfrom2.requestFocus();
            }
        });

        clearflyto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyto2.setText("");
                autoflyto2.requestFocus();
            }
        });

        clearflyfrom3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyfrom3.setText("");
                autoflyfrom3.requestFocus();
            }
        });

        clearflyto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyto3.setText("");
                autoflyto3.requestFocus();
            }
        });

        clearflyfrom4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyfrom4.setText("");
                autoflyfrom4.requestFocus();
            }
        });

        clearflyto4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyto4.setText("");
                autoflyto4.requestFocus();
            }
        });

        clearflyfrom5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyfrom5.setText("");
                autoflyfrom5.requestFocus();
            }
        });

        clearflyto5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyto5.setText("");
                autoflyto5.requestFocus();
            }
        });

        clearflyfrom6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyfrom6.setText("");
                autoflyfrom6.requestFocus();
            }
        });

        clearflyto6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoflyto6.setText("");
                autoflyto6.requestFocus();
            }
        });

        clearprefairline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoprefairline.setText("");
                autoprefairline.requestFocus();
            }
        });
        //endregion

        // region Add Destination
        btn_add_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (no_of_dests) {
                    case 0:
                        no_of_dests = 1;
                        ll3_loc.setVisibility(View.VISIBLE);
                        ll3_dep.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        no_of_dests = 2;
                        ll4_loc.setVisibility(View.VISIBLE);
                        ll4_dep.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        no_of_dests = 3;
                        ll5_loc.setVisibility(View.VISIBLE);
                        ll5_dep.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        no_of_dests = 4;
                        ll6_loc.setVisibility(View.VISIBLE);
                        ll6_dep.setVisibility(View.VISIBLE);
                        break;
                    default:
                        btn_add_destination.setVisibility(View.GONE);
                }
            }
        });
        //endregion

        // region TextViews TextChangedListeners
        autoflyfrom.addTextChangedListener(new AirportLocationTextWatcher(autoflyfrom));
        autoflyto.addTextChangedListener(new AirportLocationTextWatcher(autoflyto));
        autoflyfrom1.addTextChangedListener(new AirportLocationTextWatcher(autoflyfrom1));
        autoflyto1.addTextChangedListener(new AirportLocationTextWatcher(autoflyto1));
        autoflyfrom2.addTextChangedListener(new AirportLocationTextWatcher(autoflyfrom2));
        autoflyto2.addTextChangedListener(new AirportLocationTextWatcher(autoflyto2));
        autoflyfrom3.addTextChangedListener(new AirportLocationTextWatcher(autoflyfrom3));
        autoflyto3.addTextChangedListener(new AirportLocationTextWatcher(autoflyto3));
        autoflyfrom4.addTextChangedListener(new AirportLocationTextWatcher(autoflyfrom4));
        autoflyto4.addTextChangedListener(new AirportLocationTextWatcher(autoflyto4));
        autoflyfrom5.addTextChangedListener(new AirportLocationTextWatcher(autoflyfrom5));
        autoflyto5.addTextChangedListener(new AirportLocationTextWatcher(autoflyto5));
        autoflyfrom6.addTextChangedListener(new AirportLocationTextWatcher(autoflyfrom6));
        autoflyto6.addTextChangedListener(new AirportLocationTextWatcher(autoflyto6));
        autoprefairline.addTextChangedListener(new AirlineNameTextWatcher(autoprefairline));
        // endregion

        // region SEARCH BUTTON
        mainActivity.action_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean empty_fields = false;
                //String OriginLocation, DLocation;
                // region CHECK FIELDS
                if (radiort.isChecked()) {
                    if (autoflyfrom.getText().toString().equals("") || autoflyto.getText().toString().equals("") ||
                            tvdepart.getText().toString().equals("") || tvreturn.getText().toString().equals(""))
                        empty_fields = true;
                    //OriginLocation = autoflyfrom.getText().toString();
                    //DLocation = autoflyto.getText().toString();
                } else if (radioow.isChecked()) {
                    if (autoflyfrom.getText().toString().equals("") || autoflyto.getText().toString().equals("") ||
                            tvdepart.getText().toString().equals(""))
                        empty_fields = true;
                    //OriginLocation = autoflyfrom.getText().toString();
                    //DLocation = autoflyto.getText().toString();
                } else {
                    if (autoflyfrom1.getText().toString().equals("") || autoflyto1.getText().toString().equals("")
                            || autoflyfrom2.getText().toString().equals("") || autoflyto2.getText().toString().equals("")
                            || tvdepart1.getText().toString().equals("") || tvdepart2.getText().toString().equals(""))
                        empty_fields = true;
                    //OriginLocation = autoflyfrom1.getText().toString();
                    switch (no_of_dests) {
                        case 1:
                            if (autoflyfrom3.getText().toString().equals("") || autoflyto3.getText().toString().equals("")
                                    || tvdepart3.getText().toString().equals(""))
                                empty_fields = true;
                            //DLocation = autoflyto.getText().toString();
                            break;
                        case 2:
                            if (autoflyfrom4.getText().toString().equals("") || autoflyto4.getText().toString().equals("")
                                    || tvdepart4.getText().toString().equals(""))
                                empty_fields = true;
                            break;
                        case 3:
                            if (autoflyfrom5.getText().toString().equals("") || autoflyto5.getText().toString().equals("")
                                    || tvdepart5.getText().toString().equals(""))
                                empty_fields = true;
                            break;
                        case 4:
                            if (autoflyfrom6.getText().toString().equals("") || autoflyto6.getText().toString().equals("")
                                    || tvdepart6.getText().toString().equals(""))
                                empty_fields = true;
                            break;
                    }
                }

                if (empty_fields) {
                    PhoneFunctionality.errorAnimation(view);
                    PhoneFunctionality.makeToast(getActivity(), getString(R.string.error_all_field_required));
                    return;
                }

                if (!radiomd.isChecked()) {
                    EditText et = null;
                    if (autoflyfrom.getText().toString().trim().length() < 4) et = autoflyfrom;
                    else if (autoflyto.getText().toString().trim().length() < 4) et = autoflyto;
                    if (et != null) {
                        PhoneFunctionality.makeToast(getActivity(), getString(R.string.error_field_invalid));
                        et.selectAll();
                        et.requestFocus();
                        return;
                    }

                    if (autoflyfrom.getText().toString().trim().equals(autoflyto.getText().toString().trim())) {
                        PhoneFunctionality.errorAnimation(view);
                        PhoneFunctionality.makeToast(getActivity(), getString(R.string.error_same_loc));
                        return;
                    }
                }

                String no_of_adults = spadult.getSelectedItem().toString();
                String no_of_childs = spchild.getSelectedItem().toString();
                String no_of_infants = spinfant.getSelectedItem().toString();
                if (no_of_childs.equals("None")) no_of_childs = "0";
                if (no_of_infants.equals("None")) no_of_infants = "0";

                if ((Integer.parseInt(no_of_adults) + Integer.parseInt(no_of_childs)) > 9) {
                    PhoneFunctionality.errorAnimation(view);
                    PhoneFunctionality.makeToast(getActivity(), getString(R.string.error_psg_limit));
                    return;
                }
                // endregion

                // region Prepare Parameters
                Hashtable<String, Object> params = FormatParameters.setConnectApiParams(true);
                params = FormatParameters.setBusinessTypeParams(mainActivity, params);

                String trip_type;
                if (radiort.isChecked()) {
                    trip_type = "round-trip";
                    params.put("srchType", "Return");
                    params.put("OriginLocation", autoflyfrom.getText().toString().split(",")[0]);
                    params.put("DestinationLocation", autoflyto.getText().toString().split(",")[0]);
                    params.put("DepartureDateTime", tvdepart.getText().toString() + "T00:00:00");
                    params.put("ReturnDateTime", tvreturn.getText().toString() + "T00:00:00");

                } else if (radioow.isChecked()) {
                    trip_type = "one-way";
                    params.put("srchType", "OneWay");
                    params.put("OriginLocation", autoflyfrom.getText().toString().split(",")[0]);
                    params.put("DestinationLocation", autoflyto.getText().toString().split(",")[0]);
                    params.put("DepartureDateTime", tvdepart.getText().toString() + "T00:00:00");
                } else {
                    trip_type = "multiple";
                    params.put("srchType", "Circle");

                    params.put("locations[0][OriginLocation]", autoflyfrom1.getText().toString().split(",")[0]);
                    params.put("locations[1][OriginLocation]", autoflyfrom2.getText().toString().split(",")[0]);
                    if (!autoflyfrom3.getText().toString().equals(""))
                        params.put("locations[2][OriginLocation]", autoflyfrom3.getText().toString().split(",")[0]);
                    if (!autoflyfrom4.getText().toString().equals(""))
                        params.put("locations[3][OriginLocation]", autoflyfrom4.getText().toString().split(",")[0]);
                    if (!autoflyfrom5.getText().toString().equals(""))
                        params.put("locations[4][OriginLocation]", autoflyfrom5.getText().toString().split(",")[0]);
                    if (!autoflyfrom6.getText().toString().equals(""))
                        params.put("locations[5][OriginLocation]", autoflyfrom6.getText().toString().split(",")[0]);

                    params.put("locations[0][DestinationLocation]", autoflyto1.getText().toString().split(",")[0]);
                    params.put("locations[1][DestinationLocation]", autoflyto2.getText().toString().split(",")[0]);
                    if (!autoflyto3.getText().toString().equals(""))
                        params.put("locations[2][DestinationLocation]", autoflyto3.getText().toString().split(",")[0]);
                    if (!autoflyto4.getText().toString().equals(""))
                        params.put("locations[3][DestinationLocation]", autoflyto4.getText().toString().split(",")[0]);
                    if (!autoflyto5.getText().toString().equals(""))
                        params.put("locations[4][DestinationLocation]", autoflyto5.getText().toString().split(",")[0]);
                    if (!autoflyto6.getText().toString().equals(""))
                        params.put("locations[5][DestinationLocation]", autoflyto6.getText().toString().split(",")[0]);

                    params.put("locations[0][DepartureDateTime]", tvdepart1.getText().toString() + "T00:00:00");
                    params.put("locations[1][DepartureDateTime]", tvdepart2.getText().toString() + "T00:00:00");
                    if (!tvdepart3.getText().toString().equals(""))
                        params.put("locations[2][DepartureDateTime]", tvdepart3.getText().toString() + "T00:00:00");
                    if (!tvdepart4.getText().toString().equals(""))
                        params.put("locations[3][DepartureDateTime]", tvdepart4.getText().toString() + "T00:00:00");
                    if (!tvdepart5.getText().toString().equals(""))
                        params.put("locations[4][DepartureDateTime]", tvdepart5.getText().toString() + "T00:00:00");
                    if (!tvdepart6.getText().toString().equals(""))
                        params.put("locations[5][DepartureDateTime]", tvdepart6.getText().toString() + "T00:00:00");
                }

                String str_class = spclass.getSelectedItem().toString();
                switch (str_class) {
                    case "Economy":
                        params.put("BookingCabin", "Y");
                        break;
                    case "Premium Economy":
                        params.put("BookingCabin", "S");
                        break;
                    case "Business":
                        params.put("BookingCabin", "C");
                        break;
                    case "First":
                        params.put("BookingCabin", "F");
                        break;
                    default:
                        params.put("BookingCabin", "Y");
                }

                params.put("currencyCode", currencyList.get(spcurrency.getSelectedItemPosition()).currency_code);
                if (autoprefairline.getText().toString().equals(""))
                    params.put("prdredAirline", "0");
                else
                    params.put("prdredAirline", autoprefairline.getText().toString().split(",")[0]);

                params.put("stopsQuantity", "");
                params.put("blackListAirlineCode", "");
                params.put("rspnseType", "0");
                params.put("RequestType", "50");

                params.put("PsgType[0][Type]", "ADT");
                params.put("PsgType[0][Value]", no_of_adults);
                params.put("PsgType[1][Type]", "CNN");
                params.put("PsgType[1][Value]", no_of_childs);
                params.put("PsgType[2][Type]", "INF");
                params.put("PsgType[2][Value]", no_of_infants);
                // endregion

                // region Save values for E Ticket
                //IntentHelper.addObjectForKey(trip_info, "tkt_tripInfo");
                IntentHelper.addObjectForKey(currencyList.get(spcurrency.getSelectedItemPosition()).currency_code, "tkt_currencyCode");
                IntentHelper.addObjectForKey(no_of_adults, "tkt_noOfAdults");
                IntentHelper.addObjectForKey(no_of_childs, "tkt_noOfChilds");
                IntentHelper.addObjectForKey(no_of_infants, "tkt_noOfInfants");
                IntentHelper.addObjectForKey(autoflyfrom.getText().toString(), "tkt_OriginLocation");
                IntentHelper.addObjectForKey(autoflyto.getText().toString(), "tkt_DLocation");

                IntentHelper.addObjectForKey(str_class, "tkt_bkClass");
                String departure_date;
                if (radiomd.isChecked()) departure_date = tvdepart1.getText().toString();
                else departure_date = tvdepart.getText().toString();
                IntentHelper.addObjectForKey(departure_date, "tkt_oneWayDeparting");
                // endregion

                // Extra objects
                IntentHelper.addObjectForKey(countries, "Country_codes");
                IntentHelper.addObjectForKey(trip_type, "trip_type");
                String total_travelers = String.valueOf(Integer.parseInt(no_of_childs) + Integer.parseInt(no_of_infants) +
                        Integer.parseInt(no_of_adults));
                IntentHelper.addObjectForKey(total_travelers, "total_travelers");
                IntentHelper.addObjectForKey(FormatString.getLongDate(mainActivity, departure_date), "departure_date");
                String first_sector;
                if (!radiomd.isChecked()) first_sector = autoflyfrom.getText().toString();
                else first_sector = autoflyfrom1.getText().toString();
                IntentHelper.addObjectForKey(first_sector, "first_sector");

                // Go to Search Flight Fragment
                onFragmentHide();
                IntentHelper.addObjectForKey(params, "search_flights_params");
                FragmentHelper.replaceFragment(FlightFragment.this, new SearchFragment(), getString(R.string.search_tag));
            }
        });
        // endregion

        // region OnClickListeners to show Calendars
        tvdepart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    Calendar min_date = Calendar.getInstance(Locale.getDefault());
                    PhoneFunctionality.showCalendar(FlightFragment.this, v, current_date, min_date, max_depart_date);
                    button_clicked = false;
                }
            }
        });

        tvdepart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    try {
                        Calendar return_date = Calendar.getInstance(Locale.getDefault());
                        return_date.setTime(simpleDateFormat.parse(tvdepart.getText().toString()));
                        return_date.add(Calendar.DAY_OF_MONTH, 3);
                        tvreturn.setText(simpleDateFormat.format(return_date.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    try {
                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(tvdepart.getText().toString()));
                        min_date.add(Calendar.DAY_OF_MONTH, 1);
                        PhoneFunctionality.showCalendar(FlightFragment.this, v, current_date, min_date, max_depart_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.previous_date_error));
                    }
                    button_clicked = false;
                }
            }
        });

        tvdepart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    Calendar min_date = Calendar.getInstance(Locale.getDefault());
                    PhoneFunctionality.showCalendar(FlightFragment.this, v, current_date, min_date, max_depart_date);
                    button_clicked = false;
                }
            }
        });

        tvdepart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    try {
                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(tvdepart1.getText().toString()));
                        min_date.add(Calendar.DAY_OF_MONTH, 1);
                        PhoneFunctionality.showCalendar(FlightFragment.this, v, current_date, min_date, max_depart_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.previous_date_error));
                    }
                    button_clicked = false;
                }
            }
        });

        tvdepart3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    try {
                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(tvdepart2.getText().toString()));
                        min_date.add(Calendar.DAY_OF_MONTH, 1);
                        PhoneFunctionality.showCalendar(FlightFragment.this, v, current_date, min_date, max_depart_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.previous_date_error));
                    }
                    button_clicked = false;
                }
            }
        });

        tvdepart4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    try {
                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(tvdepart3.getText().toString()));
                        min_date.add(Calendar.DAY_OF_MONTH, 1);
                        PhoneFunctionality.showCalendar(FlightFragment.this, v, current_date, min_date, max_depart_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.previous_date_error));
                    }
                    button_clicked = false;
                }
            }
        });

        tvdepart5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    try {
                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(tvdepart4.getText().toString()));
                        min_date.add(Calendar.DAY_OF_MONTH, 1);
                        PhoneFunctionality.showCalendar(FlightFragment.this, v, current_date, min_date, max_depart_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.previous_date_error));
                    }
                    button_clicked = false;
                }
            }
        });

        tvdepart6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button_clicked) {
                    button_clicked = true;
                    try {
                        Calendar min_date = Calendar.getInstance(Locale.getDefault());
                        min_date.setTime(simpleDateFormat.parse(tvdepart5.getText().toString()));
                        min_date.add(Calendar.DAY_OF_MONTH, 1);
                        PhoneFunctionality.showCalendar(FlightFragment.this, v, current_date, min_date, max_depart_date);
                    } catch (ParseException e) {
                        PhoneFunctionality.makeToast(mainActivity, getString(R.string.previous_date_error));
                    }
                    button_clicked = false;
                }
            }
        });
        //endregion

        getCurrentLocationPref(autoflyfrom, autoflyfrom1);

        return rootView;
    }

    /**
     * Call method when FlightFragment hides from user
     */
    private void onFragmentHide() {
        PhoneFunctionality.hideKeyboard(mainActivity);
        if (getAirportLocation != null && getAirportLocation.getStatus() == AsyncTask.Status.RUNNING)
            getAirportLocation.cancel(true);
        if (getAirlineNames != null && getAirlineNames.getStatus() == AsyncTask.Status.RUNNING)
            getAirlineNames.cancel(true);
    }

    /**
     * TextWatcher class to intercept when user types in textViews for Airport Location
     */
    private class AirportLocationTextWatcher implements TextWatcher {
        private MultiAutoCompleteTextView macTV;

        AirportLocationTextWatcher(MultiAutoCompleteTextView macTV) {
            this.macTV = macTV;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            execAirportLocation(macTV);
        }
    }

    /**
     * TextWatcher class to intercept when user types in textViews for Airlines
     */
    private class AirlineNameTextWatcher implements TextWatcher {
        private MultiAutoCompleteTextView macTV;

        AirlineNameTextWatcher(MultiAutoCompleteTextView macTV) {
            this.macTV = macTV;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            execAirlineName(macTV);
        }
    }

    /**
     * Cancel current getAirportLocation task and execute new instance
     *
     * @param macTV MultiAutoCompleteTextView to get input and display result in listAdapter
     */
    private void execAirportLocation(MultiAutoCompleteTextView macTV) {
        if (macTV.getText().toString().length() >= 3) {
            getAirportLocation.cancel(true);
            getAirportLocation = new GetAirportLocation(mainActivity, macTV);
            getAirportLocation.execute(macTV.getText().toString().trim());
        }
    }

    /**
     * Cancel current getAirlineNames task and execute new instance
     *
     * @param macTV MultiAutoCompleteTextView to get input and display result in listAdapter
     */
    private void execAirlineName(MultiAutoCompleteTextView macTV) {
        if (macTV.getText().toString().length() > 0) {
            getAirlineNames.cancel(true);
            getAirlineNames = new GetAirlineNames(mainActivity, macTV);
            getAirlineNames.execute(macTV.getText().toString().trim());
        }
    }

    /**
     * Get current location from preference if available
     *
     * @param autoflyfrom  View to display result
     * @param autoflyfrom1 View to display result
     */
    private void getCurrentLocationPref(MultiAutoCompleteTextView autoflyfrom,
                                        MultiAutoCompleteTextView autoflyfrom1) {
        String curr_loc = PreferenceHelper.getString(getContext(), PreferenceHelper.CURRENT_AIRPORTS);
        if (curr_loc != null) {
            autoflyfrom.setText(curr_loc);
            autoflyfrom1.setText(curr_loc);
        } else {
            List<String> curr_locs = PreferenceHelper.getListValues(getContext(), PreferenceHelper.CURRENT_AIRPORTS);
            if (curr_locs != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, curr_locs);
                autoflyfrom.setAdapter(adapter);
                autoflyfrom1.setAdapter(adapter);
                autoflyfrom.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                autoflyfrom1.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                autoflyfrom.showDropDown();
                autoflyfrom1.showDropDown();
            }
        }
    }

    private boolean isFlightUmrah(String label) {
        return label.equals("MED,Medina,Saudi Arabia") || label.equals("JED,Jeddah,Saudi Arabia");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity != null) {
            mainActivity.action_get_location.setVisible(true);
            mainActivity.setSearchActionVisible(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mainActivity != null) {
            mainActivity.action_get_location.setVisible(false);
            mainActivity.setSearchActionVisible(false);
        }
    }

    @Override
    public void onStop() {
        onFragmentHide();
        super.onStop();
    }
}
