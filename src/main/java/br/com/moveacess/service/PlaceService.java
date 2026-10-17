package br.com.moveacess.service;

import br.com.moveacess.dto.Dtos.*;
import br.com.moveacess.model.*;
import br.com.moveacess.repository.*;
import br.com.moveacess.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class PlaceService {
    private final PlaceRepository places;
    private final AccessibilityFeatureRepository featureRepo;
    private final ReviewRepository reviews;
    private final UserRepository users;

    public PlaceResponse create(PlaceRequest req, String userEmail) {
        Place p = new Place();
        p.setName(req.name());
        p.setAddress(req.address());
        p.setCategory(req.category());
        p.setLatitude(req.latitude());
        p.setLongitude(req.longitude());
        p.setCreatedBy(users.findByEmail(userEmail).orElseThrow());
        if (req.features() != null) {
            for (FeatureRequest f : req.features()) p.getFeatures().add(newFeature(p, f));
        }
        return toResponse(places.save(p));
    }

    public PlaceResponse addFeature(Long placeId, FeatureRequest req) {
        Place p = find(placeId);
        p.getFeatures().add(newFeature(p, req));
        return toResponse(places.save(p));
    }

    public FeatureResponse confirmFeature(Long featureId) {
        AccessibilityFeature f = featureRepo.findById(featureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso não encontrado"));
        f.setConfirmations(f.getConfirmations() + 1);
        return toFeature(featureRepo.save(f));
    }

    @Transactional(readOnly = true)
    public PlaceResponse get(Long id) { return toResponse(find(id)); }

    @Transactional(readOnly = true)
    public List<PlaceResponse> nearby(double lat, double lng, double radiusMeters) {
        double dLat = GeoUtils.metersToLatDegrees(radiusMeters);
        double dLng = GeoUtils.metersToLngDegrees(radiusMeters, lat);
        return places.findInBox(lat - dLat, lat + dLat, lng - dLng, lng + dLng).stream()
                .filter(p -> GeoUtils.distanceMeters(lat, lng, p.getLatitude(), p.getLongitude()) <= radiusMeters)
                .map(this::toResponse).toList();
    }

    public ReviewResponse addReview(Long placeId, ReviewRequest req, String userEmail) {
        Review r = new Review();
        r.setPlace(find(placeId));
        User u = users.findByEmail(userEmail).orElseThrow();
        r.setUser(u);
        r.setRating(req.rating());
        r.setComment(req.comment());
        r = reviews.save(r);
        return new ReviewResponse(r.getId(), r.getRating(), r.getComment(), u.getName(), r.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listReviews(Long placeId) {
        return reviews.findByPlaceIdOrderByCreatedAtDesc(placeId).stream()
                .map(r -> new ReviewResponse(r.getId(), r.getRating(), r.getComment(),
                        r.getUser().getName(), r.getCreatedAt())).toList();
    }

    private Place find(Long id) {
        return places.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado"));
    }

    private AccessibilityFeature newFeature(Place p, FeatureRequest req) {
        AccessibilityFeature f = new AccessibilityFeature();
        f.setPlace(p);
        f.setType(req.type());
        f.setDescription(req.description());
        return f;
    }

    private FeatureResponse toFeature(AccessibilityFeature f) {
        return new FeatureResponse(f.getId(), f.getType(), f.getDescription(), f.getConfirmations());
    }

    private PlaceResponse toResponse(Place p) {
        Double avg = p.getId() == null ? null : reviews.averageRating(p.getId());
        long count = p.getId() == null ? 0 : reviews.countByPlaceId(p.getId());
        return new PlaceResponse(p.getId(), p.getName(), p.getAddress(), p.getCategory(),
                p.getLatitude(), p.getLongitude(),
                p.getFeatures().stream().map(this::toFeature).toList(), avg, count);
    }
}
