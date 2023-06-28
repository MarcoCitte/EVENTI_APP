package com.example.eventiapp.model;

import java.util.List;

/**
 * Class that represents the result of an action that requires
 * the use of a Web Service or a local database.
 */
public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        return this instanceof EventsResponseSuccess || this instanceof UserResponseSuccess || this instanceof ResetPasswordSuccess || this instanceof PlacesResponseSuccess || this instanceof ChangePasswordSuccess;
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

    public static final class PlacesResponseSuccess extends Result {
        private final List<Place> places;
        public PlacesResponseSuccess(List<Place> places) {
            this.places = places;
        }
        public List<Place> getData() {
            return places;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class UserResponseSuccess extends Result {
        private final User user;
        public UserResponseSuccess(User user) {
            this.user = user;
        }
        public User getData() {
            return user;
        }
    }

    public static final class ResetPasswordSuccess extends Result {
        private final String message;
        public ResetPasswordSuccess(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }

    public static final class ChangePasswordSuccess extends Result {
        private final String message;
        public ChangePasswordSuccess(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
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
