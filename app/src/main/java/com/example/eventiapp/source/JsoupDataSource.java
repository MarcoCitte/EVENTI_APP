package com.example.eventiapp.source;

import android.os.AsyncTask;
import android.util.Log;

import com.example.eventiapp.model.EventSource;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.ui.main.AllEventsFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsoupDataSource extends AsyncTask<Void, Void, EventsApiResponse> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected EventsApiResponse doInBackground(Void... voids) {

        List<Events> allEvents=new ArrayList<>();
        allEvents.addAll(eventsUciCinemas());
        allEvents.addAll(eventsPirelliHangar());

        return new EventsApiResponse(allEvents);
    }

    @Override
    protected void onPostExecute(EventsApiResponse eventsApiResponse) {
        BaseEventsRemoteDataSource.eventsCallback.onSuccessFromRemote(eventsApiResponse, System.currentTimeMillis());
    }

    private List<Events> eventsUciCinemas() {
        List<Events> events = new ArrayList<>();
        //UCI CINEMAS BICOCCA (FILM TRASMESSI NEL GIORNO CORRENTE)
        try {
            Document document = Jsoup.connect("https://www.ucicinemas.it/cinema/lombardia/milano/uci-cinemas-bicocca-milano/").get();
            Element element = document.getElementById("showtimes-venue-container");
            Element element2 = element.getElementsByClass("showtimes__movie").first();
            Elements show = element2.getElementsByClass("showtimes__show");

            for (Element e : show) {
                Element movie = e.getElementsByClass("movie-name").first();
                Elements timetables = e.getElementsByClass("showtimes__movie__shows list-inline");
                Element href = movie.select("a").first();
                String name = href.text();
                String urlMovie = "https://www.ucicinemas.it" + href.attr("href");
                Log.i("NOME FILM: ", name);
                Log.i("URL FILM: ", urlMovie);

                //ORARI FILM
                ArrayList<String> hours = new ArrayList<>();
                Elements times = timetables.select("a");
                for (Element o : times) {
                    String hour = o.text();
                    hours.add(hour);
                    Log.i("ORARIO FILM", hour);
                }

                //DESCRIZIONE FILM
                StringBuilder description = new StringBuilder();
                Document document2 = Jsoup.connect(urlMovie).get();
                Element movieDetail = document2.getElementsByClass("movie-data__wrapper").first();
                Elements p = movieDetail.getAllElements();
                int count = 0;
                for (Element o : p) {
                    if (count < 2) {
                        description.append(o.text());
                        count++;
                    }
                }

                //FOTO FILM
                Element movieImage=document2.getElementsByClass("main-carousel").first();
                Element imageElement = movieImage.select("img").first();
                String srcValue = imageElement.attr("src");

                Events event = new Events();
                event.setTitle(name);
                event.setEventSource(new EventSource(urlMovie, srcValue));
                event.setCategory("movies");
                event.setDescription(String.valueOf(description));
                event.setStart(AllEventsFragment.currentDate());
                String[] hoursArray = new String[hours.size()];
                hoursArray = hours.toArray(hoursArray);
                event.setHours(hoursArray);
                event.setTimezone("Europe/Rome");
                double[] coordinates = {45.5220145, 9.2133497}; //COORDINATE UCI BICOCCA
                event.setCoordinates(coordinates);
                List<Place> placeList = new ArrayList<>();
                Place place = new Place("ChIJUQcYMFvHhkcR2bA0VH8rzJw", "UCI Cinemas Bicocca", "venue", "Via Chiese, 20126 Milan MI, Italy");
                placeList.add(place);
                event.setPlaces(placeList);
                events.add(event);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    private List<Events> eventsPirelliHangar() {
        List<Events> events = new ArrayList<>();
        try {
            Document document = Jsoup.connect("https://pirellihangarbicocca.org/evento/").get();
            Elements elements = document.getElementsByClass("fl-post-column");
            for (Element e : elements) {
                Events event = new Events();
                Element href = e.select("a").first();
                Element eventType = e.getElementsByClass("spot-info uppercase").first();
                String category=eventType.text();
                category=category.replace("Evento","");
                category=category.replaceAll("\\s", " ");
                event.setCategory(category);
                Element imageElement = e.select("img").first();
                String srcValue = "https:" + imageElement.attr("src");
                event.setEventSource(new EventSource(href.attr("href"), srcValue));
                Element dateElement=e.getElementsByClass("spot-date uppercase").first();
                event.setStart(dateElement.text());
                event.setTimezone("Europe/Rome");
                Element titleElement=e.select("h2").first();
                event.setTitle(titleElement.text());
                List<Place> placeList=new ArrayList<>();
                double[] coordinates={45.5203608,9.2160497};
                event.setCoordinates(coordinates);
                Place place=new Place("ChIJX19ryKPGhkcR5i34n6bQsyI","Pirelli HangarBicocca","venue","Via Chiese, 2, 20126 Milan MI, Italy");
                placeList.add(place);
                event.setPlaces(placeList);
                Log.i("PIRELLI EVENT: " , event.toString());
                events.add(event);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

}
