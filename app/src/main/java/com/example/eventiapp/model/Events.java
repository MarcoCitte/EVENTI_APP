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

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Events implements Parcelable {


    @PrimaryKey(autoGenerate = true)
    private long id_db;
    //private EventsSource source;
    private String title;
    private String description;
    private String category;
    //private String[] labels;
    private int rank;
    @SerializedName("local_rank")
    private int localRank;
    @SerializedName("phq_attendance")
    private int attendance;
    //@SerializedName("entities") //LUOGO E INDIRIZZO EVENTO
    //private String place;
    private int duration;
    @ColumnInfo(name = "startDate")
    private String start;
    private String end;
    private String timezone;
    // @SerializedName("location")
    //private List<Coordinate> coordinates; //COORDINATE
    private String country;
    private String state; //ATTIVO o DISATTIVO
    @SerializedName("private")
    private boolean isPrivate;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;

    public Events() {
    }

    ;

    public Events(String title, String description, String category, int rank, int localRank, int attendance, int duration,
                  String start, String end, String timezone, String country, String state, boolean isPrivate, boolean isFavorite, boolean isSynchronized) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.rank = rank;
        this.localRank = localRank;
        this.attendance = attendance;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.timezone = timezone;
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
        rank = in.readInt();
        localRank = in.readInt();
        attendance = in.readInt();
        duration = in.readInt();
        start = in.readString();
        end = in.readString();
        timezone = in.readString();
        country = in.readString();
        state = in.readString();
        isPrivate = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
        isSynchronized = in.readByte() != 0;
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
    public String toString() {
        return "Events{" +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", rank=" + rank +
                ", localRank=" + localRank +
                ", attendance=" + attendance +
                ", duration=" + duration +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", timezone='" + timezone + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", isPrivate=" + isPrivate +
                ", isFavorite=" + isFavorite +
                ", isSynchronized=" + isSynchronized +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Events events = (Events) o;
        return id_db == events.id_db && rank == events.rank && localRank == events.localRank && attendance == events.attendance && duration == events.duration && isPrivate == events.isPrivate && isFavorite == events.isFavorite && isSynchronized == events.isSynchronized && Objects.equals(title, events.title) && Objects.equals(description, events.description) && Objects.equals(category, events.category) && Objects.equals(start, events.start) && Objects.equals(end, events.end) && Objects.equals(timezone, events.timezone) && Objects.equals(country, events.country) && Objects.equals(state, events.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_db, title, description, category, rank, localRank, attendance, duration, start, end, timezone, country, state, isPrivate, isFavorite, isSynchronized);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id_db);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeInt(rank);
        dest.writeInt(localRank);
        dest.writeInt(attendance);
        dest.writeInt(duration);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(timezone);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isSynchronized ? 1 : 0));
    }
}
