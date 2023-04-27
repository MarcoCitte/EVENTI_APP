package com.example.eventiapp.model;

public class EventSource {

    private String url;
    private String urlPhoto;

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
    public String toString() {
        return "EventSource{" +
                "url='" + url + '\'' +
                ", urlPhoto='" + urlPhoto + '\'' +
                '}';
    }
}
