package com.example.eventiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class EventsSource implements Parcelable {
    private String name;
    public EventsSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventsSource that = (EventsSource) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "EventsSource{" +
                "name='" + name + '\'' +
                '}';
    }

    protected EventsSource(Parcel in) {
    }

    public static final Creator<EventsSource> CREATOR = new Creator<EventsSource>() {
        @Override
        public EventsSource createFromParcel(Parcel in) {
            return new EventsSource(in);
        }

        @Override
        public EventsSource[] newArray(int size) {
            return new EventsSource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
    }
}
