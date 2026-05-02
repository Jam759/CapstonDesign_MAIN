package com.Hoseo.CapstoneDesign.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Schema(description = "User profile update request")
public record UserProfileUpdateRequest(
        @Schema(description = "Service nickname", example = "commit-master")
        @Pattern(regexp = ".*\\S.*", message = "userServiceNickname must not be blank")
        String userServiceNickname,

        @Schema(description = "User bio", example = "Backend developer interested in distributed systems.")
        @Pattern(regexp = ".*\\S.*", message = "bio must not be blank")
        String bio,

        @Schema(description = "User goal common code", example = "Job")
        @Pattern(regexp = ".*\\S.*", message = "goal must not be blank")
        String goal,

        @Schema(description = "User main position common code", example = "Backend")
        @Pattern(regexp = ".*\\S.*", message = "position must not be blank")
        String position,

        @Schema(description = "User tech stack common detail ids", example = "[\"Java\", \"Spring\", \"React\"]")
        List<@Pattern(regexp = ".*\\S.*", message = "techStacks must not contain blank values") String> techStacks
) {

    public String resolveUserServiceNickname(String currentUserServiceNickname) {
        return userServiceNickname != null ? userServiceNickname : currentUserServiceNickname;
    }

    public String resolveBio(String currentBio) {
        return bio != null ? bio : currentBio;
    }

    public String resolveGoal(String currentGoal) {
        return goal != null ? goal : currentGoal;
    }

    public String resolvePosition(String currentPosition) {
        return position != null ? position : currentPosition;
    }

    public List<String> resolveTechStacks(List<String> currentTechStacks) {
        return techStacks != null ? techStacks : currentTechStacks;
    }
}
