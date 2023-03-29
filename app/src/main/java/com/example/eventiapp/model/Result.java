package com.example.eventiapp.model;

/**
 * Class that represents the result of an action that requires
 * the use of a Web Service or a local database.
 */
public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        if (this instanceof EventsResponseSuccess) {
            return true;
        } else {
            return false;
        }
    }

    public static final class EventsResponseSuccess extends Result {
        private final EventsResponse eventsResponse;
        public EventsResponseSuccess(EventsResponse eventsResponse) {
            this.eventsResponse = eventsResponse;
        }
        public EventsResponse getData() {
            return eventsResponse;
        }
    }


    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}
