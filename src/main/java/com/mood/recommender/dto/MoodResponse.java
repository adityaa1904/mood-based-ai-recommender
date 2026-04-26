package com.mood.recommender.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing AI-generated mood-based suggestions")
public class MoodResponse {

    @Schema(description = "AI-generated suggestion including music, movie, and activity",
            example = "Music: 'Someone Like You' by Adele | Movie: 'The Pursuit of Happyness' | Activity: Take a warm bath and journal your thoughts.")
    private String suggestion;
}
