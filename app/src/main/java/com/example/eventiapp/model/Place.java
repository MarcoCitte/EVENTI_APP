package com.example.eventiapp.model;

import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.Objects;

public class Place implements Serializable {

    @SerializedName("entity_id")
    private String id;
    private String name;
    private String type;
    @SerializedName("formatted_address")
    private String address;

    public Place(String id, String name, String type, String address) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(id, place.id) && Objects.equals(name, place.name) && Objects.equals(type, place.type) && Objects.equals(address, place.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, address);
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
