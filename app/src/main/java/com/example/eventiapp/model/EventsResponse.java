package com.example.eventiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventsResponse implements Parcelable {

    private boolean isLoading;

    @SerializedName("results")
    private List<Events> eventsList;

    public EventsResponse() {}

    public EventsResponse(List<Events> eventsList){this.eventsList=eventsList;};

    protected EventsResponse(Parcel in) {
        isLoading = in.readByte() != 0;
        eventsList = in.createTypedArrayList(Events.CREATOR);
    }

    public static final Creator<EventsResponse> CREATOR = new Creator<EventsResponse>() {
        @Override
        public EventsResponse createFromParcel(Parcel in) {
            return new EventsResponse(in);
        }

        @Override
        public EventsResponse[] newArray(int size) {
            return new EventsResponse[size];
        }
    };

    public List<Events> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Events> eventsList) {
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public String toString() {
        return "EventsResponse{" +
                "eventsList=" + eventsList +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (isLoading ? 1 : 0));
        dest.writeTypedList(eventsList);
    }
}
