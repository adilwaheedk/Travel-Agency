package com.visionxoft.abacus.rehmantravel.model;

/**
 * Model class to store Project related Constants
 */
public final class Constants {

    // Set Debug mode to 'false' when publishing to server
    public static final boolean APP_TEST_MODE = true;

    // Google App Indexing API
    public static final String GAI_TITLE = "Rehman Travel";
    public static final String GAI_URL = "http://rehmantravel.com/";
    public static final String GAI_APP_LINK = "android-app://com.visionxoft.abacus.rehmantravel/http/rehmantravel.com/";

    // Phone Functionality
    public static final long VIBRATE_TIME = (long) 500;

    public static final double my_latitude = 33.717853;
    public static final double my_longitude = 73.073229;

    // Guest Account Credentials
    public static final String GUEST_NAME = "Guest";
    public static final String GUEST_ID = "1";
    public static final String GUEST_KEY = "guestKey";

    // App Request Codes
    public static final int REQUEST_PERMISSIONS = 999;
    public static final int REQUEST_PLAY_SERVICES_RESOLUTION = 1000;
    public static final int REQUEST_ACCESS_FINE_LOCATION = 1001;
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1002;
    public static final int REQUEST_SEND_SMS = 1003;
    public static final int REQUEST_READ_CONTACTS = 1004;

    // API related
    public static final String WEB_CONNECT_URL = "http://exaltedsys.com/";
    public static final String WEB_CONNECT_EMAIL = "info@exalted.pk";
    public static final String WEB_CONNECT_PWD = "7e2e70ebe6ccb2b9264eb305a36c09df";
    public static final String WEB_CONNECT_IP = "110.36.223.178";
    public static final String WEB_ACTION = "M";
}