package br.com.moveacess.dto;

import br.com.moveacess.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public final class Dtos {
    private Dtos() {}

    // ----- Auth -----
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email,
                                  @Size(min = 6) String password) {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, String name, String email) {}

    // ----- Places -----
    public record FeatureRequest(@NotNull FeatureType type, String description) {}
    public record PlaceRequest(@NotBlank String name, String address, String category,
                               @DecimalMin("-90") @DecimalMax("90") double latitude,
                               @DecimalMin("-180") @DecimalMax("180") double longitude,
                               @Valid List<FeatureRequest> features) {}
    public record FeatureResponse(Long id, FeatureType type, String description, int confirmations) {}
    public record PlaceResponse(Long id, String name, String address, String category,
                                double latitude, double longitude, List<FeatureResponse> features,
                                Double averageRating, long reviewCount) {}

    // ----- Reviews -----
    public record ReviewRequest(@Min(1) @Max(5) int rating, @Size(max = 1000) String comment) {}
    public record ReviewResponse(Long id, int rating, String comment, String author, LocalDateTime createdAt) {}

    // ----- Obstacles -----
    public record ObstacleRequest(@NotNull ObstacleType type, String description,
                                  @DecimalMin("-90") @DecimalMax("90") double latitude,
                                  @DecimalMin("-180") @DecimalMax("180") double longitude) {}
    public record ObstacleResponse(Long id, ObstacleType type, String description, double latitude,
                                   double longitude, int confirmations, boolean active, LocalDateTime createdAt) {}

    // ----- Route -----
    public record RouteResponse(double distanceMeters, double durationSeconds,
                                List<double[]> geometry, List<ObstacleResponse> obstaclesOnRoute,
                                boolean hasObstacles) {}
}
