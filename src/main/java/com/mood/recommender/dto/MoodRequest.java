package com.mood.recommender.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body containing the user's current mood")
public class MoodRequest {

    @Schema(description = "The user's current mood", example = "sad", required = true)
    private String mood;
}
