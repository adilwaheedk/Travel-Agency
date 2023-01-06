package com.visionxoft.abacus.rehmantravel.fragment;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.model.FranchisePersonal;
import com.visionxoft.abacus.rehmantravel.model.FranchiseReference;
import com.visionxoft.abacus.rehmantravel.task.GetPackagesFromURL;
import com.visionxoft.abacus.rehmantravel.task.Test_GeneratePNR;
import com.visionxoft.abacus.rehmantravel.utils.FileHelper;
import com.visionxoft.abacus.rehmantravel.utils.FragmentHelper;
import com.visionxoft.abacus.rehmantravel.utils.IntentHelper;
import com.visionxoft.abacus.rehmantravel.utils.JsonConverter;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;
import com.visionxoft.abacus.rehmantravel.views.RippleView;

import org.json.JSONException;

import java.util.List;

/**
 * Home fragment shows all main features of application.
 */
public class HomeFragment extends Fragment {

    public TextView home_user_title, home_credit_title;
    public View home_credit_ll;
    private MainActivity mainActivity;
    private RippleView home_umrah_book, home_visa, home_europe;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();

        // Set Check Home button in drawer menu
        NavigationView navigationView = (NavigationView) mainActivity.findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        // region Find Views
        home_credit_ll = rootView.findViewById(R.id.home_credit_ll);
        home_user_title = (TextView) rootView.findViewById(R.id.home_user_title);
        home_credit_title = (TextView) rootView.findViewById(R.id.home_credit_title);
        RippleView home_flight_book = (RippleView) rootView.findViewById(R.id.home_flight_book);
        RippleView home_packages = (RippleView) rootView.findViewById(R.id.home_packages);
        RippleView home_franchise = (RippleView) rootView.findViewById(R.id.home_franchise);
        home_umrah_book = (RippleView) rootView.findViewById(R.id.home_umrah_book);
        home_visa = (RippleView) rootView.findViewById(R.id.home_visa);
        home_europe = (RippleView) rootView.findViewById(R.id.home_europe);
        // endregion

        registerForContextMenu(home_umrah_book);
        registerForContextMenu(home_visa);
        registerForContextMenu(home_europe);

        AgentSession agentSession = PreferenceHelper.getAgentSession(mainActivity);
        if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) {
            mainActivity.setToolbarTitle(getString(R.string.guest_mode));
            mainActivity.setToolbarSubTitle(null);
        } else {
            mainActivity.setToolbarTitle(agentSession.AGENT_NAME);
            mainActivity.setToolbarSubTitle(getString(R.string.credit_limit) + agentSession.CurrentCredit);
        }

        // region Button Click listeners
        home_flight_book.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                FragmentHelper.replaceFragment(HomeFragment.this, new FlightFragment(), getString(R.string.flight_tag));
            }
        });

        home_umrah_book.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mainActivity.openContextMenu(home_umrah_book);
            }
        });

        home_packages.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (Constants.APP_TEST_MODE)
                    new Test_GeneratePNR(HomeFragment.this).execute();
                else
                    new GetPackagesFromURL(HomeFragment.this, getString(R.string.all_package_url)).execute();
            }
        });

        home_visa.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mainActivity.openContextMenu(home_visa);
            }
        });

        home_europe.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mainActivity.openContextMenu(home_europe);
            }
        });

        home_franchise.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (Constants.APP_TEST_MODE) {

                    // Dummy Values for testing
                    final FranchisePersonal franPer = new FranchisePersonal();
                    final FranchiseReference[] franRefArray = new FranchiseReference[3];
                    franPer.name = "RT-IT FOR TESTING PURPOSE";
                    franPer.address = "RWP";
                    franPer.phone = "12345";
                    franPer.mobile = "12345";
                    franPer.email = "abc@mail.com";
                    franPer.nic = "123456-7890123-4";
                    franPer.age = "25";
                    franPer.qualification = "BCS";
                    franPer.nationality = "Pakistani";
                    franPer.marital_status = "Single";
                    franPer.no_of_kids = "";
                    for (int i = 0; i < franRefArray.length; i++) {
                        FranchiseReference franRef = new FranchiseReference();
                        franRef.name = "REF" + i;
                        franRef.relation = "XYZ" + i;
                        franRef.phone = "12345";
                        franRef.mobile = "12345";
                        franRef.email = "abc" + i + "@mail.com";
                        franRef.nic = "123456-7890123-" + i;
                        franRef.age = "25";
                        franRef.qualification = "BCS";
                        franRef.nationality = "Pakistani";
                        franRef.business = "BUSINESS" + i;
                        franRefArray[i] = franRef;
                    }

                    IntentHelper.addObjectForKey(franPer, "franchise_personal");
                    IntentHelper.addObjectForKey(franRefArray, "franchise_references");

                    FragmentHelper.replaceFragment(HomeFragment.this, new FranchiseRecordFragment(),
                            getString(R.string.franchise_rec_tag));
                } else {
                    //new GetCountriesInfo(HomeFragment.this).execute();
                    try {
                        List<String> countries = JsonConverter.parseJsonToCountries(FileHelper.readAssetFileToString
                                (mainActivity, "country_codes"));
                        IntentHelper.addObjectForKey(countries, "countries_names");
                        FragmentHelper.replaceFragment(HomeFragment.this, new FranchisePersonalFragment(),
                                mainActivity.getString(R.string.franchise_per_tag));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // endregion

        if (Constants.APP_TEST_MODE) {
            PhoneFunctionality.makeToast(mainActivity, getString(R.string.debug_warning));
        }

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == home_umrah_book) {
            menu.setHeaderTitle("HAJJ AND UMRAH PACKAGES");
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.hajj_packages);
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.umrah_packages);
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.umrah_design_package);
        } else if (v == home_visa) {
            menu.setHeaderTitle("SELECT VISA TYPE");
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.visa_visit);
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.visa_study);
        } else if (v == home_europe) {
            menu.setHeaderTitle("SELECT EUROPE TOUR");
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.eur_tour_1);
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.eur_tour_2);
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.eur_tour_3);
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.eur_tour_4);
            menu.add(Menu.NONE, v.getId(), Menu.NONE, R.string.eur_tour_5);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == getString(R.string.hajj_packages)) {
            new GetPackagesFromURL(this, getString(R.string.hajj_package_url)).execute();
        } else if (item.getTitle() == getString(R.string.umrah_packages)) {
            new GetPackagesFromURL(this, getString(R.string.umrah_package_url)).execute();
        } else if (item.getTitle() == getString(R.string.umrah_design_package)) {
            FragmentHelper.replaceFragment(this, new UmrahDesignFragment(), getString(R.string.umrah_tag));
        } else if (item.getTitle() == getString(R.string.visa_visit)) {
            new GetPackagesFromURL(this, getString(R.string.visa_visit_url)).execute();
        } else if (item.getTitle() == getString(R.string.visa_study)) {
            new GetPackagesFromURL(this, getString(R.string.visa_study_country_url)).execute();
        } else if (item.getTitle() == getString(R.string.eur_tour_1)) {
            new GetPackagesFromURL(this, getString(R.string.eur_tour_1_url)).execute();
        } else if (item.getTitle() == getString(R.string.eur_tour_2)) {
            new GetPackagesFromURL(this, getString(R.string.eur_tour_2_url)).execute();
        } else if (item.getTitle() == getString(R.string.eur_tour_3)) {
            new GetPackagesFromURL(this, getString(R.string.eur_tour_3_url)).execute();
        } else if (item.getTitle() == getString(R.string.eur_tour_4)) {
            new GetPackagesFromURL(this, getString(R.string.eur_tour_4_url)).execute();
        } else if (item.getTitle() == getString(R.string.eur_tour_5)) {
            new GetPackagesFromURL(this, getString(R.string.eur_tour_5_url)).execute();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity.action_log_in != null)
            mainActivity.action_log_in.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MainActivity.APP_EXIT = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mainActivity.action_log_in != null)
            mainActivity.action_log_in.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }
}
