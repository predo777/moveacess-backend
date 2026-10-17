package br.com.moveacess.controller;

import br.com.moveacess.dto.Dtos.*;
import br.com.moveacess.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/places") @RequiredArgsConstructor
public class PlaceController {
    private final PlaceService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PlaceResponse create(@Valid @RequestBody PlaceRequest req, Authentication auth) {
        return service.create(req, auth.getName());
    }

    @GetMapping("/{id}")
    public PlaceResponse get(@PathVariable Long id) { return service.get(id); }

    @GetMapping("/nearby")
    public List<PlaceResponse> nearby(@RequestParam double lat, @RequestParam double lng,
                                      @RequestParam(defaultValue = "500") double radius) {
        return service.nearby(lat, lng, radius);
    }

    @PostMapping("/{id}/features") @ResponseStatus(HttpStatus.CREATED)
    public PlaceResponse addFeature(@PathVariable Long id, @Valid @RequestBody FeatureRequest req) {
        return service.addFeature(id, req);
    }

    @PostMapping("/features/{featureId}/confirm")
    public FeatureResponse confirmFeature(@PathVariable Long featureId) {
        return service.confirmFeature(featureId);
    }

    @PostMapping("/{id}/reviews") @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse addReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest req, Authentication auth) {
        return service.addReview(id, req, auth.getName());
    }

    @GetMapping("/{id}/reviews")
    public List<ReviewResponse> reviews(@PathVariable Long id) { return service.listReviews(id); }
}
