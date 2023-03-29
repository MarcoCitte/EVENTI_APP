package com.example.eventiapp.model;

import android.media.metrics.Event;
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
    private String category;
    //private List<Label> labels;
    private int rank;
    @SerializedName("local_rank")
    private int localRank;
    @SerializedName("phq_attendance")
    private int attendance;
    //@SerializedName("entities") //LUOGO E INDIRIZZO EVENTO
    //private String place;
    private int duration;
    private String start;
    private String end;
    private String timezone;
   // @SerializedName("location")
    //private List<Coordinate> coordinates; //COORDINATE
    private String country;
    private String state; //ATTIVO o DISATTIVO
    @SerializedName("private")
    private boolean isPrivate;

    //private boolean isFavorite;

    public Events(){};

    public Events(String id, String title, String description, String category, int rank, int localRank, int attendance, int duration,
                  String start, String end, String timezone, String country, String state, boolean isPrivate) {
        this.id = id;
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
    }

    @Override
    public String toString() {
        return "Events{" +
                "id='" + id + '\'' +
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
                '}';
    }

    protected Events(Parcel in) {
        id = in.readString();
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
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
}
