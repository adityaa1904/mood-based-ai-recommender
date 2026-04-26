package com.mood.recommender.service;

import com.mood.recommender.dto.MoodRequest;
import com.mood.recommender.dto.MoodResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
@Service
public class MoodService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public MoodService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MoodResponse getSuggestion(MoodRequest request) {

        // ── Step 1: Input Validation ──────────────────────────────────────────
        String mood = request.getMood();

        if (mood == null || mood.trim().isEmpty()) {
            return new MoodResponse("Please provide a valid mood. Example: happy, sad, anxious, excited.");
        }

        mood = mood.trim().toLowerCase();
        try {
            String suggestion = callGeminiApi(mood);
            return new MoodResponse(suggestion);

        } catch (Exception e) {
            System.err.println("Gemini API call failed: " + e.getMessage());
            return new MoodResponse(getFallbackSuggestion(mood));
        }
    }

    private String callGeminiApi(String mood) {

        String url = apiUrl + "?key=" + apiKey;


        String prompt = buildPrompt(mood);

        // Gemini expects: { "contents": [ { "parts": [ { "text": "..." } ] } ] }
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return parseGeminiResponse(response.getBody());
    }


    private String buildPrompt(String mood) {
        return String.format(
            "I am feeling %s right now. Please recommend exactly ONE of each:\n" +
            "1. A music track or song (with artist name)\n" +
            "2. A movie to watch\n" +
            "3. A simple activity or thing to do\n\n" +
            "Format your response EXACTLY like this (no extra text, no asterisks):\n" +
            "Music: [song name] by [artist]\n" +
            "Movie: [movie title] - [one sentence why]\n" +
            "Activity: [what to do]",
            mood
        );
    }


    @SuppressWarnings("unchecked")
    private String parseGeminiResponse(Map responseBody) {
        try {
            List<Map> candidates = (List<Map>) responseBody.get("candidates");
            Map firstCandidate = candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            String text = (String) parts.get(0).get("text");
            return text.trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
        }
    }


    private String getFallbackSuggestion(String mood) {
        return switch (mood) {
            case "sad", "unhappy", "depressed" ->
                "Music: 'Fix You' by Coldplay\nMovie: The Pursuit of Happyness - An inspiring story about resilience.\nActivity: Write down 3 things you are grateful for today.";

            case "happy", "joyful", "excited" ->
                "Music: 'Happy' by Pharrell Williams\nMovie: The Secret Life of Walter Mitty - A joyful adventure awaits.\nActivity: Go for a walk and take photos of beautiful things around you.";

            case "anxious", "stressed", "nervous" ->
                "Music: 'Weightless' by Marconi Union\nMovie: Chef - A feel-good story about following your passion.\nActivity: Try 5 minutes of box breathing: inhale 4s, hold 4s, exhale 4s.";

            case "angry", "frustrated" ->
                "Music: 'Let It Go' by James Bay\nMovie: Inside Out - Helps understand and accept your emotions.\nActivity: Go for a brisk walk or do 10 minutes of exercise to release tension.";

            case "bored" ->
                "Music: 'Blinding Lights' by The Weeknd\nMovie: Everything Everywhere All at Once - Will definitely surprise you!\nActivity: Learn to draw a simple object or try a new recipe.";

            default ->
                "Music: 'Here Comes the Sun' by The Beatles\nMovie: Forrest Gump - A timeless classic for any mood.\nActivity: Step outside for 10 minutes of fresh air and sunlight.";
        };
    }
}
