package com.visionxoft.abacus.rehmantravel.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.fragment.AboutCompanyProfileFragment;
import com.visionxoft.abacus.rehmantravel.fragment.ContactUsFragment;
import com.visionxoft.abacus.rehmantravel.fragment.FlightFragment;
import com.visionxoft.abacus.rehmantravel.fragment.FlightReservationFragment;
import com.visionxoft.abacus.rehmantravel.fragment.HomeFragment;
import com.visionxoft.abacus.rehmantravel.fragment.LocationFragment;
import com.visionxoft.abacus.rehmantravel.fragment.SearchFragment;
import com.visionxoft.abacus.rehmantravel.model.AgentSession;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.task.DisplayPNR;
import com.visionxoft.abacus.rehmantravel.task.SecureWebService;
import com.visionxoft.abacus.rehmantravel.task.UserCreditTask;
import com.visionxoft.abacus.rehmantravel.task.UserLoginTask;
import com.visionxoft.abacus.rehmantravel.utils.ConnectionHelper;
import com.visionxoft.abacus.rehmantravel.utils.DialogHelper;
import com.visionxoft.abacus.rehmantravel.utils.ExceptionHandler;
import com.visionxoft.abacus.rehmantravel.utils.FormatString;
import com.visionxoft.abacus.rehmantravel.utils.GoogleAPIHelper;
import com.visionxoft.abacus.rehmantravel.utils.PermissionHelper;
import com.visionxoft.abacus.rehmantravel.utils.PhoneFunctionality;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;
import com.visionxoft.abacus.rehmantravel.views.LayoutHack;

/**
 * Activity that provides main toolbar, drawer and location related functions.
 * It manages all fragments and its back stack behaviour.
 * For current location GoogleApiClient is used.
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks {

    // region Class member instances
    private MainActivity mainActivity;
    private static final int LOC_REQ_CODE = 1;
    private GoogleAPIHelper apiHelper;
    private GoogleApiClient apiClient;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Dialog dialog_login, dialog_no_network;
    private Toolbar toolbar;
    private boolean toggle_user_visible;
    private View btn_user_home, btn_user_logout, btn_user_pnr, btn_user_credit, toolbar_user;
    private ImageView toggle_user;

    public ActionBarDrawerToggle toggle;
    public View action_search, action_submit, action_progress, main_loader;
    public static boolean APP_EXIT = false;
    public MenuItem action_get_location, action_refresh_pnr, action_call_rt, action_log_in;
    // endregion

    // region Activity Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainActivity = MainActivity.this;

        // Initialize Google Client
        apiHelper = new GoogleAPIHelper();
        getCurrentLocation(false);

        // region Setup Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        action_search = findViewById(R.id.action_search);
        action_submit = findViewById(R.id.action_submit);
        action_progress = findViewById(R.id.action_progress);

        toolbar_user = findViewById(R.id.toolbar_user);
        toggle_user = (ImageView) findViewById(R.id.toggle_user);
        btn_user_home = findViewById(R.id.btn_user_home);
        btn_user_credit = findViewById(R.id.btn_user_credit);
        btn_user_pnr = findViewById(R.id.btn_user_pnr);
        btn_user_logout = findViewById(R.id.btn_user_logout);
        toolbar_user.setVisibility(View.GONE);
        toggle_user.setVisibility(View.GONE);

        main_loader = findViewById(R.id.main_loader);
        setSearchActionVisible(false);
        action_submit.setVisibility(View.GONE);
        action_progress.setVisibility(View.GONE);
        main_loader.setVisibility(View.GONE);
        // endregion

        // Slide up area to display bottom bar
        //View layout_drag_area = findViewById(R.id.layout_drag_area);
        //layout_drag_area


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                final HomeFragment homeFragment = (HomeFragment)
                        fragmentManager.findFragmentByTag(getString(R.string.home_tag));
                if (homeFragment != null && homeFragment.isVisible()) {
                    toggle.setDrawerIndicatorEnabled(true);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    toggle.setDrawerIndicatorEnabled(false);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }
        });
        // endregion

        // When fragment starts first time
        if (savedInstanceState == null) {

            // Display Home fragment
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment(), getString(R.string.home_tag));
            transaction.commit();

            // Auto User Log-In
            if (PreferenceHelper.getInt(this, PreferenceHelper.KEEP_LOGGED) == 1)
                new UserLoginTask(this, PreferenceHelper.getString(this, PreferenceHelper.LOGGED_EMAIL),
                        PreferenceHelper.getString(this, PreferenceHelper.LOGGED_PASS),
                        PreferenceHelper.getString(this, PreferenceHelper.LOGGED_USER_TYPE), 1).execute();

            PermissionHelper.checkPermissions(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient = apiHelper.getGoogleClient(this, this);
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (apiClient != null) {
            apiClient.disconnect();
        }
    }
    // endregion

    //region Toolbar methods
    public void setToolbarTitle(String msg) {
        toolbar.setTitle(msg);
    }

    public void setToolbarSubTitle(String msg) {
        toolbar.setSubtitle(msg);
    }
    // endregion

    // region Back Press Logic
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else {
            PhoneFunctionality.hideKeyboard(this);

            // Control HomeFragment
            final HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag(getString(R.string.home_tag));
            if (homeFragment != null && homeFragment.isVisible()) {
                if (!APP_EXIT) {
                    // App Exit Warning
                    PhoneFunctionality.makeToast(this, getString(R.string.press_again_to_exit));
                    APP_EXIT = true;
                } else {
                    // Exit from Application
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } else {
                // Control Search Fragment
                final SearchFragment searchFragment = (SearchFragment) fragmentManager.findFragmentByTag(getString(R.string.search_tag));
                if (searchFragment != null && searchFragment.isVisible()) {
                    SecureWebService.stopSearchFlightsAsyncTask();
                }

                // Control Flight Book Ticket Fragment
                final FlightReservationFragment bookTicketFragment = (FlightReservationFragment)
                        fragmentManager.findFragmentByTag(getString(R.string.flight_book_ticket_tag));
                if (bookTicketFragment != null && bookTicketFragment.isVisible()) {
                    if (!bookTicketFragment.flag_pnr_downloaded && !bookTicketFragment.flag_pnr_emailed) {
                        DialogInterface.OnClickListener positive_listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bookTicketFragment.btn_pnr_save.performClick();
                            }
                        };

                        DialogInterface.OnClickListener negative_listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PhoneFunctionality.makeToast(mainActivity, getString(R.string.pnr_not_saved));
                                startActivity(new Intent(mainActivity, MainActivity.class));
                            }
                        };

                        DialogHelper.createConfirmDialog(bookTicketFragment.getContext(),
                                getString(R.string.ticket_warning_title),
                                getString(R.string.ticket_warning_msg), positive_listener, negative_listener);
                    } else {
                        if (homeFragment == null)
                            startActivity(new Intent(mainActivity, MainActivity.class));
                        else while (!homeFragment.isVisible())
                            fragmentManager.popBackStackImmediate();
                    }
                } else super.onBackPressed();
            }
        }
    }
    // endregion

    // region Toolbar initialization
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        action_refresh_pnr = menu.findItem(R.id.action_refresh_pnr);
        action_get_location = menu.findItem(R.id.action_get_location);
        action_log_in = menu.findItem(R.id.action_log_in);
        action_call_rt = menu.findItem(R.id.action_call_rt);
        AgentSession agentSession = PreferenceHelper.getAgentSession(this);
        if (agentSession.RT_AGENT_KEY.equals(Constants.GUEST_KEY)) action_log_in.setVisible(true);
        else action_log_in.setVisible(false);

        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag(getString(R.string.home_tag));
        if (homeFragment != null && homeFragment.isVisible())
            action_log_in.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        else
            action_log_in.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    //  Toolbar Options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_call_rt:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.contact_number))));
                break;
            case R.id.action_log_in:
                createLoginDialog();
                break;
            case R.id.action_get_location:
                getCurrentLocation(true);
                break;
            case R.id.action_refresh_pnr:
                refreshPNR();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // endregion

    // region Logout confirmation dialog
    private void confirmLogout() {
        DialogInterface.OnClickListener positive_listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceHelper.clearAgentSession(mainActivity);
                PreferenceHelper.clearValues(mainActivity, new String[]{"keep_logged",
                        "keep_logged_agent_pass", "keep_logged_user_type"});
                startActivity(new Intent(mainActivity, MainActivity.class));
            }
        };

        DialogHelper.createConfirmDialog(this, getString(R.string.logout_title),
                getString(R.string.logout_msg), positive_listener, null);
    }
    // endregion

    // Refresh ticket
    private void refreshPNR() {
        FlightReservationFragment flightResFragment = (FlightReservationFragment)
                fragmentManager.findFragmentByTag(getString(R.string.flight_book_ticket_tag));
        if (flightResFragment != null && flightResFragment.isVisible() && flightResFragment.PNR != null) {
            new DisplayPNR(flightResFragment, flightResFragment.PNR, false, false).execute();
        } else action_refresh_pnr.setVisible(false);
    }

    // region Login related methods
    public void createLoginDialog() {
        dialog_login = DialogHelper.createCustomDialog(this, R.layout.dialog_login, Gravity.CENTER);
        dialog_login.show();

        // region Find views of dialog
        final AutoCompleteTextView login_email = (AutoCompleteTextView) dialog_login.findViewById(R.id.login_email);
        final EditText login_pass = (EditText) dialog_login.findViewById(R.id.login_pass);
        final Spinner login_sp_user_type = (Spinner) dialog_login.findViewById(R.id.login_sp_user_type);
        final CheckBox login_cb_remember = (CheckBox) dialog_login.findViewById(R.id.login_cb_remember);
        final View login_email_clear = dialog_login.findViewById(R.id.login_email_clear);
        final View login_pass_clear = dialog_login.findViewById(R.id.login_pass_clear);
        final View login_pass_toggle = dialog_login.findViewById(R.id.login_pass_toggle);
        final View btn_login_login = dialog_login.findViewById(R.id.btn_login_login);
        final View btn_login_back = dialog_login.findViewById(R.id.btn_login_back);
        // endregion

        // Get last login email address
        Object obj = PreferenceHelper.getString(this, PreferenceHelper.LOGGED_EMAIL);
        if (obj != null) login_email.setText((String) obj);

        // Button Click listeners to clear textViews
        login_email_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_email.setText("");
                login_email.requestFocus();
            }
        });

        login_pass_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_pass.setText("");
                login_pass.requestFocus();
            }
        });

        // Toggle password visibility
        login_pass_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneFunctionality.toggleVisibility(login_pass, login_pass_toggle);
            }
        });

        // Login when Enter pressed on Keyboard
        login_pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(login_email, login_pass, login_sp_user_type, login_cb_remember);
                    return true;
                }
                return false;
            }
        });

        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(login_email, login_pass, login_sp_user_type, login_cb_remember);
            }
        });

        btn_login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_login.dismiss();
            }
        });
    }

    private void attemptLogin(EditText login_email, EditText login_password,
                              Spinner login_user_type, CheckBox login_cb_keep_logged) {
        String email = login_email.getText().toString().trim();
        String password = login_password.getText().toString().trim();
        String user_type = login_user_type.getSelectedItem().toString().trim();

        boolean cancel = false;

        // Check for valid Email address
        if (TextUtils.isEmpty(email)) {
            login_email.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!FormatString.isEmailValid(email)) {
            login_email.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }

        // Check for valid password
        else if (TextUtils.isEmpty(password)) {
            login_password.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!FormatString.isPasswordValid(password)) {
            login_password.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        // Check if user type is selected
        else if (user_type.equals(getString(R.string.select_user_type))) {
            TextView spinnerTextView = (TextView) login_user_type.getSelectedView();
            spinnerTextView.setError(getString(R.string.select_user_type));
            cancel = true;
        } else {
            if (user_type.equals("Agent")) user_type = "1";
            else user_type = "0";
        }

        // Login when required fields are provided
        if (!cancel) {
            UserLoginTask userLoginTask = new UserLoginTask(this, email, password,
                    user_type, login_cb_keep_logged.isChecked() ? 1 : 0);
            userLoginTask.execute();
        }
    }

    public void loginSuccess() {
        final HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag(getString(R.string.home_tag));
        if (homeFragment != null) {
            toolbar_user.setVisibility(View.VISIBLE);
            toggle_user.setVisibility(View.VISIBLE);
            toggle_user_visible = true;

            toggle_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (toggle_user_visible) {
                        LayoutHack.toggleAnimation(toolbar_user, false);
                        LayoutHack.toggleAnimation(toggle_user, false);
                        toggle_user.setImageResource(android.R.drawable.arrow_up_float);
                        toggle_user_visible = false;
                    } else {
                        LayoutHack.toggleAnimation(toolbar_user, true);
                        LayoutHack.toggleAnimation(toggle_user, true);
                        toggle_user.setImageResource(android.R.drawable.arrow_down_float);
                        toggle_user_visible = true;
                    }
                }
            });

            btn_user_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!homeFragment.isVisible()) {
                        while (!homeFragment.isVisible()) fragmentManager.popBackStackImmediate();
                    }
                }
            });

            btn_user_credit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UserCreditTask(mainActivity).execute();
                }
            });

            btn_user_pnr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = DialogHelper.createCustomDialog(mainActivity, R.layout.dialog_user_pnr, Gravity.CENTER, false);
                    final EditText pnr_search_et = (EditText) dialog.findViewById(R.id.pnr_search_et);
                    View btn_search_user_pnr = dialog.findViewById(R.id.btn_search_user_pnr);
                    View btn_back_user_pnr = dialog.findViewById(R.id.btn_back_user_pnr);
                    btn_search_user_pnr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String PNR = pnr_search_et.getText().toString();
                            if (PNR.length() == 6) {
                                new DisplayPNR(homeFragment, PNR, true, false).execute();
                                dialog.dismiss();
                            } else PhoneFunctionality.makeToast(mainActivity, "PNR must have 6 letters");
                        }
                    });
                    btn_back_user_pnr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            btn_user_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmLogout();
                }
            });

            action_log_in.setVisible(false);
            AgentSession agentSession = PreferenceHelper.getAgentSession(this);
            mainActivity.setToolbarTitle(agentSession.AGENT_NAME);
            mainActivity.setToolbarSubTitle(getString(R.string.credit_limit) + agentSession.CurrentCredit);

            while (!homeFragment.isVisible()) fragmentManager.popBackStackImmediate();
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        dialog_login.dismiss();
    }
    // endregion

    // region No network dialog
    public void createNoNetworkDialog() {
        dialog_no_network = DialogHelper.createCustomDialog(this, R.layout.dialog_no_network, Gravity.CENTER, false);
        dialog_no_network.show();

        final View no_network_ll = dialog_no_network.findViewById(R.id.no_network_ll);
        no_network_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionHelper.isConnectedToInternet(mainActivity))
                    dialog_no_network.dismiss();
                else PhoneFunctionality.makeToast(mainActivity, getString(R.string.network_failed));
            }
        });
    }
    // endregion

    // region Drawer Navigation
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment newFragment = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case R.id.nav_home:
                final HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag(getString(R.string.home_tag));
                if (homeFragment == null) newFragment = new HomeFragment();
                else if (homeFragment.isVisible()) return false;
                else while (!homeFragment.isVisible()) fragmentManager.popBackStackImmediate();
                break;
            case R.id.nav_contact_us:
                newFragment = new ContactUsFragment();
                break;
            case R.id.nav_profile:
                newFragment = new AboutCompanyProfileFragment();
                break;
            case R.id.nav_feedback:
                PhoneFunctionality.sendEmail(this, getString(R.string.rt_email_address), getString(R.string.feedback), "");
                break;
            case R.id.nav_map:
                startActivity(new Intent(this, LocationFragment.class));
                break;
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (newFragment != null) {
            transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null).commit();
            return true;
        }
        return false;
    }
    // endregion

    // region Location related functions
    @Override
    public void onConnected(Bundle bundle) {
        apiHelper.getCurrentAirportLocation(this, apiClient, false);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private void getCurrentLocation(boolean showLocSrcDialog) {
        if (!isProviderEnabled()) {
            //String curr_loc = PreferenceHelper.getString(this, PreferenceHelper.CURRENT_AIRPORTS);
            //if (curr_loc == null || showLocSrcDialog) gotoLocSrcSettings();
            if (showLocSrcDialog) gotoLocSrcSettings();
        }
        if (apiClient != null && apiClient.isConnected()) apiHelper.getCurrentAirportLocation(this, apiClient, true);
        else {
            apiClient = apiHelper.getGoogleClient(this, this);
            apiClient.connect();
        }
    }

    private boolean isProviderEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = false, wifi = false;
        try {
            gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.getCause().getStackTrace();
        }

        try {
            wifi = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.getCause().getStackTrace();
        }
        return gps || wifi;
    }

    private void gotoLocSrcSettings() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.location_source_title))
                .setMessage(getString(R.string.location_source_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOC_REQ_CODE);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    // endregion

    // region Request Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_ACCESS_COARSE_LOCATION:
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_ACCESS_FINE_LOCATION);
                break;
            case Constants.REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    apiHelper.getCurrentAirportLocation(this, apiClient, true);
                } else {
                    PhoneFunctionality.makeToast(this, getString(R.string.no_loc_permission));
                }
                break;
            case Constants.REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PreferenceHelper.setInt(mainActivity, PreferenceHelper.GRANT_SEND_SMS, 1);
                } else {
                    PreferenceHelper.setInt(mainActivity, PreferenceHelper.GRANT_SEND_SMS, 0);
                }
                break;
            case Constants.REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PreferenceHelper.setInt(mainActivity, PreferenceHelper.GRANT_READ_CONTACTS, 1);
                } else {
                    PreferenceHelper.setInt(mainActivity, PreferenceHelper.GRANT_READ_CONTACTS, 0);
                }
                break;
            default:
                if (grantResults.length == 0) {
                    PhoneFunctionality.makeToast(this, getString(R.string.note_permission));
                }
                break;
        }
    }
    // endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOC_REQ_CODE) {
            if (!isProviderEnabled()) PhoneFunctionality.makeToast(this, getString(R.string.provider_not_available));
            else {
                apiClient = apiHelper.getGoogleClient(this, this);
                apiClient.connect();
            }
        }
    }

    public void setSearchActionVisible(boolean visible) {
        if (visible) {
            final FlightFragment flightFragment = (FlightFragment) fragmentManager.findFragmentByTag(getString(R.string.flight_tag));
            if (flightFragment != null && flightFragment.isVisible()) {
                action_search.setVisibility(View.VISIBLE);
                return;
            }
        }
        action_search.setVisibility(View.GONE);
    }

}
