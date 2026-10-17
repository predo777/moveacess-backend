package br.com.moveacess.service;

import br.com.moveacess.dto.Dtos.*;
import br.com.moveacess.model.Obstacle;
import br.com.moveacess.repository.ObstacleRepository;
import br.com.moveacess.util.GeoUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

/**
 * Pede a rota para cadeira de rodas ao OpenRouteService e cruza o trajeto
 * com os obstáculos ativos cadastrados pela comunidade.
 */
@Service
public class RouteService {
    private final ObstacleRepository obstacles;
    private final ObstacleService obstacleService;
    private final RestClient http;
    private final String apiKey;
    private final double radius;

    public RouteService(ObstacleRepository obstacles, ObstacleService obstacleService,
                        @Value("${app.ors.base-url}") String baseUrl,
                        @Value("${app.ors.api-key}") String apiKey,
                        @Value("${app.route.obstacle-radius-meters}") double radius) {
        this.obstacles = obstacles;
        this.obstacleService = obstacleService;
        this.http = RestClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.radius = radius;
    }

    public RouteResponse route(double fromLat, double fromLng, double toLat, double toLng) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "ORS_API_KEY não configurada");
        }
        Map<String, Object> body = Map.of("coordinates",
                List.of(List.of(fromLng, fromLat), List.of(toLng, toLat)));

        JsonNode json;
        try {
            json = http.post().uri("/v2/directions/wheelchair/geojson")
                    .header(HttpHeaders.AUTHORIZATION, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body).retrieve().body(JsonNode.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao consultar o serviço de rotas");
        }

        JsonNode feature = json.path("features").path(0);
        JsonNode summary = feature.path("properties").path("summary");
        List<double[]> geometry = new ArrayList<>();
        for (JsonNode c : feature.path("geometry").path("coordinates")) {
            geometry.add(new double[]{c.get(1).asDouble(), c.get(0).asDouble()}); // [lat, lng]
        }

        List<ObstacleResponse> found = obstaclesAlong(geometry);
        return new RouteResponse(summary.path("distance").asDouble(), summary.path("duration").asDouble(),
                geometry, found, !found.isEmpty());
    }

    private List<ObstacleResponse> obstaclesAlong(List<double[]> geometry) {
        if (geometry.isEmpty()) return List.of();
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE, minLng = Double.MAX_VALUE, maxLng = -Double.MAX_VALUE;
        for (double[] p : geometry) {
            minLat = Math.min(minLat, p[0]); maxLat = Math.max(maxLat, p[0]);
            minLng = Math.min(minLng, p[1]); maxLng = Math.max(maxLng, p[1]);
        }
        double dLat = GeoUtils.metersToLatDegrees(radius);
        double dLng = GeoUtils.metersToLngDegrees(radius, minLat);
        List<Obstacle> candidates = obstacles.findActiveInBox(minLat - dLat, maxLat + dLat, minLng - dLng, maxLng + dLng);

        List<ObstacleResponse> result = new ArrayList<>();
        for (Obstacle o : candidates) {
            for (double[] p : geometry) {
                if (GeoUtils.distanceMeters(p[0], p[1], o.getLatitude(), o.getLongitude()) <= radius) {
                    result.add(obstacleService.toResponse(o));
                    break;
                }
            }
        }
        return result;
    }
}
