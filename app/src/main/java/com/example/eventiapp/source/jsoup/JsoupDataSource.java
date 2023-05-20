package com.example.eventiapp.source.jsoup;

import android.os.AsyncTask;
import android.util.Log;

import com.example.eventiapp.model.EventSource;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.source.events.BaseEventsRemoteDataSource;
import com.example.eventiapp.ui.main.AllEventsFragment;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.EventClassifier;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JsoupDataSource extends AsyncTask<Void, Void, EventsApiResponse> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected EventsApiResponse doInBackground(Void... voids) {

        List<Events> allEvents = new ArrayList<>();
        allEvents.addAll(eventsUciCinemas());
        allEvents.addAll(eventsPirelliHangar());
        allEvents.addAll(eventsUnimib());
        allEvents.addAll(eventsArcimboldi());
        return new EventsApiResponse(allEvents);
    }

    @Override
    protected void onPostExecute(EventsApiResponse eventsApiResponse) {
        BaseEventsRemoteDataSource.eventsCallback.onSuccessFromRemoteJsoup(eventsApiResponse);
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
                Element movieNote = e.getElementsByClass("mobile-showtimes__movie__notes-container").first();
                Element movie3D = movieNote.getElementsByClass("mobile-showtimes__movie__notes").first();
                String srcValue3D = null;
                if (movie3D != null) {
                    Element image3D = movie3D.select("img").first();
                    srcValue3D = image3D.attr("src");
                }
                Elements timetables = e.getElementsByClass("showtimes__movie__shows list-inline");
                Element href = movie.select("a").first();
                String name = href.text();
                String urlMovie = "https://www.ucicinemas.it" + href.attr("href");

                //ORARI FILM
                ArrayList<String> hours = new ArrayList<>();
                Elements times = timetables.select("a");
                for (Element o : times) {
                    String hour = o.text();
                    hours.add(hour);
                }

                //3D FILM
                if (srcValue3D != null && srcValue3D.contains("3d")) {
                    name = name + " 3D";
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
                Element movieImage = document2.getElementsByClass("main-carousel").first();
                Element imageElement = movieImage.select("img").first();
                String srcValue = imageElement.attr("src");

                Events event = new Events();
                event.setTitle(name);
                event.setEventSource(new EventSource(urlMovie, srcValue));
                event.setCategory("movies");
                event.setDescription(String.valueOf(description));
                event.setStart(DateUtils.currentDate());
                String[] hoursArray = new String[hours.size()];
                hoursArray = hours.toArray(hoursArray);
                event.setHours(hoursArray);
                event.setTimezone("Europe/Rome");
                double[] coordinates = {45.5220145, 9.2133497}; //COORDINATE UCI BICOCCA
                event.setCoordinates(coordinates);
                List<Place> placeList = new ArrayList<>();
                Place place = new Place("uci_bicocca", "UCI Cinemas Bicocca", "venue", "Via Chiese, 20126 Milan MI, Italy", coordinates);
                placeList.add(place);
                event.setPlaces(placeList);
                Log.i("UCI MOVIE: ", event.toString());
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
                String category = eventType.text();
                category = category.replace("Evento", "");
                category = category.replaceAll("\\s", " ");
                event.setCategory(category);
                Element imageElement = e.select("img").first();
                String srcValue = "https:" + imageElement.attr("src");
                event.setEventSource(new EventSource(href.attr("href"), srcValue));

                //DATA EVENTO
                Element dateElement = e.getElementsByClass("spot-date uppercase").first();
                String date = dateElement.text();
                Date date1 = DateUtils.parseDate(date, "it");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                if (date1 != null) {
                    String formattedDate = formatter.format(date1);
                    if (formattedDate.compareTo(DateUtils.currentDate()) >= 0) { //METTE SOLO EVENTI NUOVI A PARTIRE DALLA DATA CORRENTE
                        event.setStart(formattedDate);
                        event.setTimezone("Europe/Rome");
                        Element titleElement = e.select("h2").first();
                        event.setTitle(titleElement.text());
                        List<Place> placeList = new ArrayList<>();
                        double[] coordinates = {45.5203608, 9.2160497};
                        event.setCoordinates(coordinates);
                        Place place = new Place("pirelli_hangar", "Pirelli HangarBicocca", "venue", "Via Chiese, 2, 20126 Milan MI, Italy", coordinates);
                        placeList.add(place);
                        event.setPlaces(placeList);
                        Log.i("PIRELLI EVENT: ", event.toString());
                        events.add(event);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    private List<Events> eventsUnimib() {
        List<Events> events = new ArrayList<>();
        String baseUrl = "https://www.unimib.it/eventi"; // Inserisci l'URL di base del sito web degli eventi
        String urlTemplate = baseUrl + "?page=%d";

        int currentPage = 0;
        boolean hasNextPage = true;
        while (hasNextPage) {
            String url = String.format(urlTemplate, currentPage);

            try {
                Document document = Jsoup.connect(url).get();
                Elements eventElements = document.select(".anteprima--evento");

                // Se non ci sono più elementi degli eventi, esci dal loop
                if (eventElements.isEmpty()) {
                    hasNextPage = false;
                    continue;
                }

                for (Element e : eventElements) {
                    Events event = new Events();

                    String imageSrc = e.select("img").attr("src");
                    String title = e.select(".views-field-title a").text();
                    String dateRange = e.select(".views-field-field-data-evento").text();
                    String description = e.select(".views-field-field-sottotitolo").text();

                    String imageValue = "https://www.unimib.it" + imageSrc;
                    event.setEventSource(new EventSource("https://www.unimib.it" + e.select(".views-field-title a").first().attr("href"), imageValue));
                    String start = "";
                    String end = "";
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    if (dateRange.contains("da")) { //INIZIO E FINE EVENTO
                        String[] dates = dateRange.split(" a ");
                        start = dates[0];
                        end = dates[1];
                        Date startDate = DateUtils.parseDate(start, "it");
                        Date endDate = DateUtils.parseDate(end, "it");
                        start = outputFormat.format(startDate);
                        end = outputFormat.format(endDate);
                    } else {
                        Date startDate = DateUtils.parseDate(dateRange, "it");
                        start = outputFormat.format(startDate);
                        end = null;
                    }
                    event.setStart(start);
                    event.setEnd(end);
                    event.setTimezone("Europe/Rome");


                    //TITLE
                    event.setTitle(title);

                    //DESCRIPTION
                    event.setDescription(description);

                    //CATEGORY
                    EventClassifier eventClassifier = new EventClassifier();
                    String category = eventClassifier.classifyEvent(title, description);
                    event.setCategory(category);

                    //PLACE
                    List<Place> placeList = new ArrayList<>();
                    double[] coordinates = {45.5182898, 9.2111811};
                    event.setCoordinates(coordinates);
                    Place place = new Place("unimib", "Università degli Studi di Milano Bicocca", "venue", "Piazza dell'Ateneo Nuovo, 1, 20126 Milano MI, Italy", coordinates);
                    placeList.add(place);
                    event.setPlaces(placeList);

                    events.add(event);


                }
                currentPage++;

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return events;
    }

    private List<Events> eventsArcimboldi() {
        List<Events> events = new ArrayList<>();
        try {
            Document document = Jsoup.connect("https://www.teatroarcimboldi.it/all/").get();
            Element newEvents = document.getElementById("tab-programmazione");
            Elements eventItems = newEvents.select(".fat-event-item");
            for (Element eventItem : eventItems) {
                Events event = new Events();
                String title = eventItem.select(".fat-event-title a").text();
                String eventUrl = eventItem.select(".fat-event-tile a").attr("href");
                String imageUrl = eventItem.select(".fat-event-thumb img").attr("src");
                String description = eventItem.select(".riassunto").text();
                String date = eventItem.select(".fe-date").text();

                event.setTitle(title);
                event.setEventSource(new EventSource(eventUrl, imageUrl));
                event.setDescription(description);

                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = DateUtils.parseDate(date, "it");
                String start = outputFormat.format(startDate);
                event.setStart(start);
                event.setTimezone("Europe/Rome");

                //CATEGORY
                EventClassifier eventClassifier = new EventClassifier();
                String category = eventClassifier.classifyEvent(title, description);
                event.setCategory(category);

                //PLACE
                List<Place> placeList = new ArrayList<>();
                double[] coordinates = {45.514842, 9.2109728};
                event.setCoordinates(coordinates);
                Place place = new Place("QskKAMb7unj4usbvwV4fqC", "Teatro arcimboldi", "venue", "Viale dell'Innovazione, 20, 20126 Milano MI, Italy", coordinates);
                placeList.add(place);
                event.setPlaces(placeList);

                events.add(event);


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return events;
    }

}
