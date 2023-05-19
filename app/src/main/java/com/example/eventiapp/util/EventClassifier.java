package com.example.eventiapp.util;

import java.util.HashMap;
import java.util.Map;

public class EventClassifier {
    private final Map<String, EventCategoryClassifier> categoryClassifiers;

    public EventClassifier() {
        categoryClassifiers = new HashMap<>();
        categoryClassifiers.put("Concert", new MusicEventCategoryClassifier());
        categoryClassifiers.put("Art", new ArtEventCategoryClassifier());
        categoryClassifiers.put("Show", new ShowEventCategoryClassifier());
    }

    public String classifyEvent(String title, String description) {
        for (Map.Entry<String, EventCategoryClassifier> entry : categoryClassifiers.entrySet()) {
            String category = entry.getKey();
            EventCategoryClassifier classifier = entry.getValue();
            if (classifier.isEvent(category, title, description)) {
                return category;
            }
        }

        // Se nessuna categoria corrisponde, restituisci un valore default
        return "Other";
    }

    private interface EventCategoryClassifier {
        boolean isEvent(String category, String title, String description);
    }

    private static class MusicEventCategoryClassifier implements EventCategoryClassifier {
        private final String[] MUSIC_KEYWORDS = {"concerto", "festival", "live", "concert","band","music","musica","album","tour","musical","danza","cantante","cantautrice","cantautore"};

        @Override
        public boolean isEvent(String category, String title, String description) {
            String text = StringUtils.lowerCase(title + " " + description);
            for (String keyword : MUSIC_KEYWORDS) {
                if (text.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class ArtEventCategoryClassifier implements EventCategoryClassifier {
        private final String[] ART_KEYWORDS = {"art", "arte"};

        @Override
        public boolean isEvent(String category, String title, String description) {
            String text = StringUtils.lowerCase(title + " " + description);
            for (String keyword : ART_KEYWORDS) {
                if (text.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class ShowEventCategoryClassifier implements EventCategoryClassifier {
        private final String[] SHOW_KEYWORDS = {"show", "spettacolo"};

        @Override
        public boolean isEvent(String category, String title, String description) {
            String text = StringUtils.lowerCase(title + " " + description);
            for (String keyword : SHOW_KEYWORDS) {
                if (text.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
    }
}

//
