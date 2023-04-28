package com.example.eventiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class Place implements Serializable, Parcelable {

    @PrimaryKey
    @NonNull
    @SerializedName("entity_id")
    private String id;
    private String name;
    private String type;
    @SerializedName("formatted_address")
    private String address;
    private double[] coordinates;
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;
    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;

    public Place(@NonNull String id, String name, String type, String address, double[] coordinates) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.coordinates=coordinates;
        this.isFavorite=false;
        this.isSynchronized=false;
    }


    protected Place(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        address = in.readString();
        coordinates = in.createDoubleArray();
        isFavorite = in.readByte() != 0;
        isSynchronized = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(address);
        dest.writeDoubleArray(coordinates);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isSynchronized ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
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
        Place place = (Place) o;
        return isFavorite == place.isFavorite && isSynchronized == place.isSynchronized && id.equals(place.id) && Objects.equals(name, place.name) && Objects.equals(type, place.type) && Objects.equals(address, place.address) && Arrays.equals(coordinates, place.coordinates);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, type, address, isFavorite, isSynchronized);
        result = 31 * result + Arrays.hashCode(coordinates);
        return result;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", coordinates=" + Arrays.toString(coordinates) +
                ", isFavorite=" + isFavorite +
                ", isSynchronized=" + isSynchronized +
                '}';
    }
}
