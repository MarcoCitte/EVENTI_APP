package com.example.eventiapp.model;

import android.os.Parcelable;

import java.util.List;

public class EventsApiResponse extends EventsResponse{
    private String status;
    private int totalResults;

    public EventsApiResponse() {super();}

    public EventsApiResponse(String status,int totalResults,List<Events> events){
        super(events);
        this.status=status;
        this.totalResults=totalResults;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public String toString() {
        return "EventsApiResponse{" +
                "status='" + status + '\'' +
                ", totalResults=" + totalResults +
                '}';
    }
}
