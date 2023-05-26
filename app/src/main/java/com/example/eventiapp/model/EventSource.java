package com.example.eventiapp.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class EventSource {

    private String url;
    private String urlPhoto;

    public EventSource() {

    }

    public EventSource(String url, String urlPhoto) {
        this.url = url;
        this.urlPhoto = urlPhoto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventSource that = (EventSource) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(urlPhoto, that.urlPhoto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, urlPhoto);
    }

    @NonNull
    @Override
    public String toString() {
        return "EventSource{" +
                "url='" + url + '\'' +
                ", urlPhoto='" + urlPhoto + '\'' +
                '}';
    }
}
