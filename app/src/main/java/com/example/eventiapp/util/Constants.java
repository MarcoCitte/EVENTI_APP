package com.example.eventiapp.util;

public class Constants {

    // Constants for EventsApi (https://api.predicthq.com)
    public static final String EVENTS_API_BASE_URL = "https://api.predicthq.com/v1/";
    public static final String EVENTS_ENDPOINT = "events";
    public static final String PLACES_ENDPOINT="places";
    public static final String CLIENT_ID = "Client-ID";
    public static final String EVENTS_COUNTRY="country";
    public static final String EVENTS_LIMIT="limit";
    public static final String EVENTS_START="start.gte";
    public static final String EVENTS_WITHIN="whitin";
    public static final String EVENTS_END="end.lte";
    public static final String CLIENT_ID_VALUE = "JY2hDOGTKiMtHH_VdBulMoKQai3uJq49AazyMAfJDDuzOqiJN4uq6A";
    public static final String TOKEN_API_VALUE = "Bearer kTBSbQX6vl4mXWDKxB9fOsyQvSyb4VqtItnnuATN"; //DA METTERE IN LOCAL PROPERTIES
    public static final String TOKEN_API = "Authorization";
    public static final String CONTENT_TYPE = "Accept";
    public static final String CONTENT_TYPE_VALUE = "application/json";

    public static final String LAST_UPDATE = "last_update";
    public static final int FRESH_TIMEOUT = 60*60*1000; // 1 hour in milliseconds
    public static final int EVENTS_PAGE_SIZE_VALUE = 10;

    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";


    // Constants for Room database
    public static final String EVENTS_DATABASE_NAME = "EVENTS_DB";

    //SHARED PREFERENCES
    public static final String SHARED_PREFERENCES_FILE_NAME = "preferences";

    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "encrypted_preferences";

    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "encrypted_file.txt";



}
