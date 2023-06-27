package com.example.eventiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@IgnoreExtraProperties
public class Events implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id_db;
    private long id_remoto;
    private EventSource eventSource;
    private String title;
    private String description;
    private String category;
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
    private List<String> hours;
    private String timezone;
    @SerializedName("location")
    private List<Double> coordinates; //COORDINATE
    private String country;
    private String state; //ATTIVO o DISATTIVO
    @SerializedName("private")
    private boolean isPrivate;
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;
    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;

    @ColumnInfo(name = "creator_email")
    private String creatorEmail;


    public Events() {
    }

    public Events(List<Place> places) { //SERVE PER SALVARE I POSTI PROVENIENTI DA JSOUP
        this.places = places;
    }

    protected Events(Parcel in) {
        id_db = in.readLong();
        id_remoto=in.readLong();
        title = in.readString();
        description = in.readString();
        category = in.readString();
        //labels = in.createStringArray();
        rank = in.readInt();
        localRank = in.readInt();
        attendance = in.readInt();
        duration = in.readInt();
        start = in.readString();
        end = in.readString();
        //hours = in.createStringArray();
        timezone = in.readString();
        //coordinates = in.createDoubleArray();
        country = in.readString();
        state = in.readString();
        isPrivate = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
        isSynchronized = in.readByte() != 0;
        creatorEmail = in.readString();

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


    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public EventSource getEventSource() {
        return eventSource;
    }

    public void setEventSource(EventSource eventSource) {
        this.eventSource = eventSource;
    }

    public long getId_db() {
        return id_db;
    }

    public void setId_db(long id_db) {
        this.id_db = id_db;
    }

    public long getId_remoto() {
        return id_remoto;
    }

    public void setId_remoto(long id_remoto) {
        this.id_remoto = id_remoto;
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

    public List<String> getHours() {
        return hours;
    }

    public void setHours(List<String> hours) {
        this.hours = hours;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
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

    @Exclude
    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id_db);
        dest.writeLong(id_remoto);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeInt(rank);
        dest.writeInt(localRank);
        dest.writeInt(attendance);
        dest.writeInt(duration);
        dest.writeString(start);
        dest.writeString(end);
        //dest.writeStringArray(hours);
        dest.writeString(timezone);
        //dest.writeDoubleArray(coordinates);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isSynchronized ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Events events = (Events) o;
        return rank == events.rank && localRank == events.localRank && attendance == events.attendance && duration == events.duration && Objects.equals(eventSource, events.eventSource) && Objects.equals(title, events.title) && Objects.equals(description, events.description) && Objects.equals(category, events.category) && Objects.equals(places, events.places) && Objects.equals(start, events.start) && Objects.equals(end, events.end) && Objects.equals(hours, events.hours) && Objects.equals(timezone, events.timezone) && Objects.equals(coordinates, events.coordinates) && Objects.equals(country, events.country) && Objects.equals(state, events.state) && ((creatorEmail != null) ? creatorEmail.equals(events.creatorEmail) : true);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventSource, title, description, category, rank, localRank, attendance, duration, start, end, timezone, country, state, isPrivate, creatorEmail);
    }

    @NonNull
    @Override
    public String toString() {
        return "Events{" +
                "id_db=" + id_db +
                ", title='" + title + '\'' +
                '}';
    }


    public static class SortByLeastRecent implements java.util.Comparator<Events> {
        public int compare(Events a, Events b) {
            if (a.getStart() != null && b.getStart() != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date dateA = format.parse(a.getStart());
                    Date dateB = format.parse(b.getStart());
                    return -Objects.requireNonNull(dateA).compareTo(dateB);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
    }

    public static class SortByMostRecent implements java.util.Comparator<Events> {
        public int compare(Events a, Events b) {
            if (a.getStart() != null && b.getStart() != null) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = formatter.parse(a.getStart());
                    Date date2 = formatter.parse(b.getStart());
                    return Objects.requireNonNull(date1).compareTo(date2);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
            return 0;
        }
    }

    public static class SortByAlphabetAZ implements java.util.Comparator<Events> {
        public int compare(Events a, Events b) {
            return a.getTitle().compareToIgnoreCase(b.getTitle());
        }
    }

    public static class SortByAlphabetZA implements java.util.Comparator<Events> {
        public int compare(Events a, Events b) {
            return -a.getTitle().compareToIgnoreCase(b.getTitle());
        }
    }

    public static class SortByRank implements java.util.Comparator<Events> {
        public int compare(Events a, Events b) {
            return Integer.compare(b.getRank(), a.getRank());
        }
    }
}

