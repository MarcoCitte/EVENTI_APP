package com.example.eventiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Events implements Parcelable {

    private String id;
    //private EventsSource source;
    private String title;
    private String description;
    private Category category;
    private List<Label> labels;
    private int rank;
    @SerializedName("local_rank")
    private int localRank;
    @SerializedName("phq_attendance")
    private int attendance;
    @SerializedName("entities") //LUOGO E INDIRIZZO EVENTO
    private String place;
    private int duration;
    private String start;
    private String end;
    private String timezone;
    @SerializedName("location")
    private List<Coordinate> coordinates; //COORDINATE
    private String country;
    private String state; //ATTIVO o DISATTIVO
    @SerializedName("private")
    private boolean isPrivate;

    private boolean isFavorite;

    public Events(String id, String title, String description, Category category, List<Label> labels, int rank, int localRank, int attendance,
                  String place, int duration, String start, String end, String timezone, List<Coordinate> coordinates, String country, String state,
                  boolean isPrivate, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.labels = labels;
        this.rank = rank;
        this.localRank = localRank;
        this.attendance = attendance;
        this.place = place;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.timezone = timezone;
        this.coordinates = coordinates;
        this.country = country;
        this.state = state;
        this.isPrivate = isPrivate;
        this.isFavorite = isFavorite;
    }

    protected Events(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        rank = in.readInt();
        localRank = in.readInt();
        attendance = in.readInt();
        place = in.readString();
        duration = in.readInt();
        start = in.readString();
        end = in.readString();
        timezone = in.readString();
        country = in.readString();
        state = in.readString();
        isPrivate = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
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

    @Override
    public String toString() {
        return "Events{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", labels=" + labels +
                ", rank=" + rank +
                ", localRank=" + localRank +
                ", attendance=" + attendance +
                ", place='" + place + '\'' +
                ", duration=" + duration +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", timezone='" + timezone + '\'' +
                ", location=" + coordinates +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", isPrivate=" + isPrivate +
                ", isFavorite=" + isFavorite +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(rank);
        dest.writeInt(localRank);
        dest.writeInt(attendance);
        dest.writeString(place);
        dest.writeInt(duration);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(timezone);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
