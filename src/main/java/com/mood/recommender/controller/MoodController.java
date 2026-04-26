package com.mood.recommender.controller;

import com.mood.recommender.dto.MoodRequest;
import com.mood.recommender.dto.MoodResponse;
import com.mood.recommender.service.MoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mood")
@CrossOrigin(origins = "*")   // Allows the HTML frontend to call this API
@Tag(name = "Mood Recommender", description = "Get AI-powered music, movie & activity suggestions based on your mood")
public class MoodController {

    private final MoodService moodService;

    // Constructor injection (cleaner than @Autowired on field)
    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }


    @PostMapping("/analyze")
    @Operation(
        summary = "Analyze mood and get recommendations",
        description = "Send your current mood and receive a personalized music track, movie, and activity suggestion powered by Google Gemini AI.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The user's mood",
            required = true,
            content = @Content(
                schema = @Schema(implementation = MoodRequest.class),
                examples = @ExampleObject(value = "{ \"mood\": \"sad\" }")
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully generated suggestions",
                content = @Content(schema = @Schema(implementation = MoodResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request - mood is missing or empty")
        }
    )
    public ResponseEntity<MoodResponse> analyzeMood(@RequestBody MoodRequest request) {
        MoodResponse response = moodService.getSuggestion(request);
        return ResponseEntity.ok(response);
    }
}
