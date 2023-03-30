package com.example.eventiapp.util;


import static com.example.eventiapp.util.Constants.API_KEY_ERROR;
import static com.example.eventiapp.util.Constants.RETROFIT_ERROR;


import android.app.Application;

import com.example.eventiapp.R;


/**
 * Utility class to get the proper message to be show
 * to the user when an error occurs.
 */
public class ErrorMessageUtil {

    private Application application;

    public ErrorMessageUtil(Application application) {
        this.application = application;
    }

    /**
     * Returns a message to inform the user about the error.
     * @param errorType The type of error.
     * @return The message to be shown to the user.
     */
    public String getErrorMessage(String errorType) {
        switch(errorType) {
            case RETROFIT_ERROR:
                return application.getString(R.string.error_retrieving_events);
            case API_KEY_ERROR:
                return application.getString(R.string.events_api_key);
            default:
                return application.getString(R.string.unexpected_error);
        }
    }
}
