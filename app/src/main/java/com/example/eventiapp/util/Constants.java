package com.example.eventiapp.util;

import com.google.android.gms.maps.model.LatLng;

public class Constants {

    // Constants for EventsApi (https://api.predicthq.com)
    public static final String EVENTS_API_BASE_URL = "https://api.predicthq.com/v1/";
    public static final String EVENTS_ENDPOINT = "events";
    public static final String PLACES_ENDPOINT = "places";
    public static final String CLIENT_ID = "Client-ID";
    public static final String EVENTS_COUNTRY = "country";
    public static final String EVENTS_CATEGORY = "category";
    public static final String EVENTS_LIMIT = "limit";
    public static final String EVENTS_START = "start.gte";
    public static final String EVENTS_WITHIN = "within";
    public static final String EVENTS_SORT = "sort";
    public static final String EVENTS_END = "end.lte";
    public static final String CLIENT_ID_VALUE = "JY2hDOGTKiMtHH_VdBulMoKQai3uJq49AazyMAfJDDuzOqiJN4uq6A";
    public static final String TOKEN_API = "Authorization";
    public static final String CONTENT_TYPE = "Accept";
    public static final String CONTENT_TYPE_VALUE = "application/json";
    public static final LatLng BICOCCA_LATLNG = new LatLng(45.51851, 9.2075123);


    //API FOURSQUARE
    public static final String FOURSQUARE_API_BASE_URL = "https://api.foursquare.com/v3/places/match";
    public static final String FOURSQUARE_ENDPOINT = "events";
    public static final String FOURSQUARE_NAME = "name";
    public static final String FOURSQUARE_ADDRESS = "address";
    public static final String FOURSQUARE_CITY = "city";
    public static final String FOURSQUARE_CC = "cc";


    public static final String LAST_UPDATE = "last_update";
    public static final int FRESH_TIMEOUT = 60 * 60 * 1000; // 1 hour in milliseconds
    public static final int EVENTS_PAGE_SIZE_VALUE = 10;

    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";

    public static final String FIREBASE_REALTIME_DATABASE = "https://accessfb-562e9-default-rtdb.europe-west1.firebasedatabase.app";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_FAVORITE_EVENTS_COLLECTION = "favorite_events";

    public static final String FIREBASE_FAVORITE_PLACES_COLLECTION = "favorite_places";

    public static final String FIREBASE_USERS_CREATED_EVENTS_COLLECTION = "users_created_events";



    public static final int REQUEST_CODE = 123;


    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";


    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    // Constants for Room database
    public static final String EVENTS_DATABASE_NAME = "EVENTS_DB";

    //SHARED PREFERENCES
    public static final String SHARED_PREFERENCES_FILE_NAME = "preferences";
    public static final String SHARED_PREFERENCES_FIRST_LOADING = "first_loading";
    public static final String SHARED_PREFERENCES_LANGUAGE = "IT";

    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "encrypted_preferences";

    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "encrypted_file.txt";

    //RECYCLER VIEW ADAPTER
    public static final int MAX_ITEMS = 10;
    public static final int EVENTS_VIEW_TYPE = 0;
    public static final int LOADING_VIEW_TYPE = 1;
    public static final int PLACES_VIEW_TYPE = 2;
    public static final int EVENTS2_VIEW_TYPE = 3;
    public static final int PLACES2_VIEW_TYPE = 4;
    public static final int EVENTS3_VIEW_TYPE = 5;
    public static final int PLACES3_VIEW_TYPE = 6;

    //IMAGE
    public static final int REQUEST_CODE_PICK_IMAGE = 1000;


    // Constants for EncryptedSharedPreferences
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";
    public static final String ID_TOKEN = "google_token";

}
