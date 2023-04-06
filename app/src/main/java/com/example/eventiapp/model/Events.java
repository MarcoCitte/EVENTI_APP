package com.example.eventiapp.model;

import android.media.metrics.Event;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.eventiapp.util.Converters;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Events implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id_db;
    private String title;
    private String description;
    private String category;
    private String[] labels;
    private int rank;
    @SerializedName("local_rank")
    private int localRank;
    @SerializedName("phq_attendance")
    private int attendance;
    @SerializedName("entities") //LUOGO E INDIRIZZO EVENTO
    private List<Place> places;
    private int duration;
    @ColumnInfo(name = "startDate")
    private String start;
    private String end;
    private String timezone;
    @SerializedName("location")
    private double[] coordinates; //COORDINATE
    private String country;
    private String state; //ATTIVO o DISATTIVO
    @SerializedName("private")
    private boolean isPrivate;
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;
    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;

    public Events() {}

    public Events(String title, String description, String category, String[] labels, int rank, int localRank, int attendance, List<Place> places, int duration,
                  String start, String end, String timezone, double[] coordinates, String country, String state, boolean isPrivate, boolean isFavorite, boolean isSynchronized) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.labels = labels;
        this.rank = rank;
        this.localRank = localRank;
        this.attendance = attendance;
        this.places = places;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.timezone = timezone;
        this.coordinates = coordinates;
        this.country = country;
        this.state = state;
        this.isPrivate = isPrivate;
        this.isFavorite = isFavorite;
        this.isSynchronized = isSynchronized;
    }

    protected Events(Parcel in) {
        id_db = in.readLong();
        title = in.readString();
        description = in.readString();
        category = in.readString();
        labels = in.createStringArray();
        rank = in.readInt();
        localRank = in.readInt();
        attendance = in.readInt();
        duration = in.readInt();
        start = in.readString();
        end = in.readString();
        timezone = in.readString();
        coordinates = in.createDoubleArray();
        country = in.readString();
        state = in.readString();
        isPrivate = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
        isSynchronized = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id_db);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeStringArray(labels);
        dest.writeInt(rank);
        dest.writeInt(localRank);
        dest.writeInt(attendance);
        dest.writeInt(duration);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(timezone);
        dest.writeDoubleArray(coordinates);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isSynchronized ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Events> CREATOR = new Creator<Events>() {
        @Override
        public Events createFromParcel(Parcel in) {
            return new Events(in);
        }

        @Override
        public Events[] newArray(int size) {
            return new Events[size];
        }
    };

    public long getId_db() {
        return id_db;
    }

    public void setId_db(long id_db) {
        this.id_db = id_db;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getLocalRank() {
        return localRank;
    }

    public void setLocalRank(int localRank) {
        this.localRank = localRank;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Events events = (Events) o;
        return id_db == events.id_db && rank == events.rank && localRank == events.localRank && attendance == events.attendance && duration == events.duration && isPrivate == events.isPrivate && isFavorite == events.isFavorite && isSynchronized == events.isSynchronized && Objects.equals(title, events.title) && Objects.equals(description, events.description) && Objects.equals(category, events.category) && Arrays.equals(labels, events.labels) && Objects.equals(places, events.places) && Objects.equals(start, events.start) && Objects.equals(end, events.end) && Objects.equals(timezone, events.timezone) && Arrays.equals(coordinates, events.coordinates) && Objects.equals(country, events.country) && Objects.equals(state, events.state);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id_db, title, description, category, rank, localRank, attendance, places, duration, start, end, timezone, country, state, isPrivate, isFavorite, isSynchronized);
        result = 31 * result + Arrays.hashCode(labels);
        result = 31 * result + Arrays.hashCode(coordinates);
        return result;
    }

    @Override
    public String toString() {
        return "Events{" +
                "id_db=" + id_db +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", labels=" + Arrays.toString(labels) +
                ", rank=" + rank +
                ", localRank=" + localRank +
                ", attendance=" + attendance +
                ", places=" + places +
                ", duration=" + duration +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", timezone='" + timezone + '\'' +
                ", coordinates=" + Arrays.toString(coordinates) +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", isPrivate=" + isPrivate +
                ", isFavorite=" + isFavorite +
                ", isSynchronized=" + isSynchronized +
                '}';
    }
}
