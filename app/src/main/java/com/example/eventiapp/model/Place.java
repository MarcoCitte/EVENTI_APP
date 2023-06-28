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
import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
public class Place implements Serializable, Parcelable {

    @PrimaryKey
    @SerializedName("entity_id")
    @NonNull
    private String id;
    private String name;
    private String type;
    @SerializedName("formatted_address")
    private String address;
    private String idGoogle;
    private List<Double> coordinates;
    private String phoneNumber;
    @Exclude
    private List<PhotoMetadata> images;
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;
    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;
    @ColumnInfo(name = "creator_email")
    private String creatorEmail;
    private String urlUserImage;

    @Ignore
    public Place() {
    }

    @Ignore
    public Place(@NonNull String id, String name, String type, String address, List<Double> coordinates) {
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

    @Ignore
    public Place(@NonNull String id, String name, String type, String address, String urlUserImage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.urlUserImage=urlUserImage;
        this.isFavorite = false;
        this.isSynchronized = false;
    }

    public Place(@NonNull String id, String name, String type, String address, String idGoogle, List<Double> coordinates, String phoneNumber, List<PhotoMetadata> images) {
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
        //coordinates = in.createDoubleArray();
        urlUserImage=in.readString();
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

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

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

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
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

    @Exclude
    public List<PhotoMetadata> getImages() {
        return images;
    }

    public void setImages(List<PhotoMetadata> images) {
        this.images = images;
    }

    public String getUrlUserImage() {
        return urlUserImage;
    }

    public void setUrlUserImage(String urlUserImage) {
        this.urlUserImage = urlUserImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(name, place.name) && Objects.equals(type, place.type) && Objects.equals(address, place.address) && Objects.equals(idGoogle, place.idGoogle) && Objects.equals(coordinates, place.coordinates) && Objects.equals(phoneNumber, place.phoneNumber) && Objects.equals(images, place.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, address, idGoogle, coordinates, phoneNumber);  //rimosso images per problea dell'hash diverso
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
                ", coordinates=" + coordinates +
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
        //dest.writeTypedList(coordinates);
        dest.writeString(urlUserImage);
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

