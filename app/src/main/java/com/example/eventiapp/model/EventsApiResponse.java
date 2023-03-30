package com.example.eventiapp.model;

import android.os.Parcelable;

import java.util.List;

public class EventsApiResponse extends EventsResponse{
    private int count;
    private boolean overflow;
    private String next;
    private String previous;

    public EventsApiResponse() {super();}

    public EventsApiResponse(List<Events> eventsList, int count, boolean overflow, String next, String previous) {
        super(eventsList);
        this.count = count;
        this.overflow = overflow;
        this.next = next;
        this.previous = previous;
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

    @Override
    public String toString() {
        return "EventsApiResponse{" +
                "count='" + count + '\'' +
                ", overflow=" + overflow +
                ", next='" + next + '\'' +
                ", previous='" + previous + '\'' +
                '}';
    }
}
