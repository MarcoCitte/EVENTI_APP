package com.example.eventiapp.model;

import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.List;

public class EventsApiResponse extends EventsResponse{
    private int count;
    private boolean overflow;
    private String next;
    private String previous;

    public EventsApiResponse() {super();}

    public EventsApiResponse(int count, boolean overflow, String next, String previous,List<Events> eventsList) {
        super(eventsList);
        this.count = count;
        this.overflow = overflow;
        this.next = next;
        this.previous = previous;
    }

    public EventsApiResponse(List<Events> eventsList){
        super(eventsList);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isOverflow() {
        return overflow;
    }

    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    @NonNull
    @Override
    public String toString() {
        return "EventsApiResponse{" +
                "count='" + count + '\'' +
                ", overflow=" + overflow +
                ", next='" + next + '\'' +
                ", previous='" + previous + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.count);
        dest.writeByte(this.overflow ? (byte) 1 : (byte) 0);
        dest.writeString(this.next);
        dest.writeString(this.previous);
    }

    public void readFromParcel(Parcel source) {
        this.count = source.readInt();
        this.overflow = source.readByte() != 0;
        this.next = source.readString();
        this.previous = source.readString();
    }

    protected EventsApiResponse(Parcel in) {
        super(in);
        this.count = in.readInt();
        this.overflow = in.readByte() != 0;
        this.next = in.readString();
        this.previous = in.readString();
    }

    public static final Creator<EventsApiResponse> CREATOR = new Creator<EventsApiResponse>() {
        @Override
        public EventsApiResponse createFromParcel(Parcel source) {
            return new EventsApiResponse(source);
        }

        @Override
        public EventsApiResponse[] newArray(int size) {
            return new EventsApiResponse[size];
        }
    };
}
