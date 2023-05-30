package com.example.eventiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.example.eventiapp.util.StringUtils;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
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
    private String idGoogle;
    private double[] coordinates;
    private String phoneNumber;
    private List<PhotoMetadata> images;
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;
    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;


    @Ignore
    public Place() {
    }

    @Ignore
    public Place(@NonNull String id, String name, String type, String address, double[] coordinates) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.coordinates = coordinates;
        this.isFavorite = false;
        this.isSynchronized = false;
    }

    @Ignore
    public Place(@NonNull String id, String name, String type, String address) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.isFavorite = false;
        this.isSynchronized = false;
    }

    public Place(@NonNull String id, String name, String type, String address, String idGoogle, double[] coordinates, String phoneNumber, List<PhotoMetadata> images) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.idGoogle = idGoogle;
        this.coordinates = coordinates;
        this.phoneNumber = phoneNumber;
        this.images = images;
        this.isFavorite = false;
        this.isSynchronized = false;
    }


    protected Place(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        address = in.readString();
        idGoogle = in.readString();
        coordinates = in.createDoubleArray();
        phoneNumber = in.readString();
        isFavorite = in.readByte() != 0;
        isSynchronized = in.readByte() != 0;
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

    @NonNull
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
        this.name = StringUtils.capitalizeFirstLetter(name);
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

    public String getIdGoogle() {
        return idGoogle;
    }

    public void setIdGoogle(String idGoogle) {
        this.idGoogle = idGoogle;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<PhotoMetadata> getImages() {
        return images;
    }

    public void setImages(List<PhotoMetadata> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(name, place.name) && Objects.equals(address, place.address) && Objects.equals(idGoogle, place.idGoogle) && Objects.equals(phoneNumber, place.phoneNumber) && Objects.equals(images, place.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, idGoogle, phoneNumber, images);
    }

    @NonNull
    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", idGoogle='" + idGoogle + '\'' +
                ", coordinates=" + Arrays.toString(coordinates) +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", images=" + images +
                ", isFavorite=" + isFavorite +
                ", isSynchronized=" + isSynchronized +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(address);
        dest.writeString(idGoogle);
        dest.writeDoubleArray(coordinates);
        dest.writeString(phoneNumber);
        dest.writeTypedList(images);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isSynchronized ? 1 : 0));
    }

    public static class SortByAlphabetAZ implements java.util.Comparator<Place> {
        public int compare(Place a, Place b) {
            return a.getName().compareTo(b.getName());
        }
    }

    public static class SortByAlphabetZA implements java.util.Comparator<Place> {
        public int compare(Place a, Place b) {
            return -a.getName().compareTo(b.getName());
        }
    }

}

