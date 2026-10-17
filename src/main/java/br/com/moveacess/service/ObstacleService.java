package br.com.moveacess.service;

import br.com.moveacess.dto.Dtos.*;
import br.com.moveacess.model.Obstacle;
import br.com.moveacess.repository.*;
import br.com.moveacess.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class ObstacleService {
    private static final int VOTES_TO_RESOLVE = 3;
    private final ObstacleRepository obstacles;
    private final UserRepository users;

    public ObstacleResponse report(ObstacleRequest req, String userEmail) {
        Obstacle o = new Obstacle();
        o.setType(req.type());
        o.setDescription(req.description());
        o.setLatitude(req.latitude());
        o.setLongitude(req.longitude());
        o.setReportedBy(users.findByEmail(userEmail).orElseThrow());
        return toResponse(obstacles.save(o));
    }

    public ObstacleResponse confirm(Long id) {
        Obstacle o = find(id);
        o.setConfirmations(o.getConfirmations() + 1);
        return toResponse(obstacles.save(o));
    }

    /** Validação colaborativa: com N votos de "resolvido", o obstáculo deixa de ser ativo. */
    public ObstacleResponse voteResolved(Long id) {
        Obstacle o = find(id);
        o.setResolvedVotes(o.getResolvedVotes() + 1);
        if (o.getResolvedVotes() >= VOTES_TO_RESOLVE) o.setActive(false);
        return toResponse(obstacles.save(o));
    }

    @Transactional(readOnly = true)
    public List<ObstacleResponse> nearby(double lat, double lng, double radiusMeters) {
        double dLat = GeoUtils.metersToLatDegrees(radiusMeters);
        double dLng = GeoUtils.metersToLngDegrees(radiusMeters, lat);
        return obstacles.findActiveInBox(lat - dLat, lat + dLat, lng - dLng, lng + dLng).stream()
                .filter(o -> GeoUtils.distanceMeters(lat, lng, o.getLatitude(), o.getLongitude()) <= radiusMeters)
                .map(this::toResponse).toList();
    }

    private Obstacle find(Long id) {
        return obstacles.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obstáculo não encontrado"));
    }

    public ObstacleResponse toResponse(Obstacle o) {
        return new ObstacleResponse(o.getId(), o.getType(), o.getDescription(), o.getLatitude(),
                o.getLongitude(), o.getConfirmations(), o.isActive(), o.getCreatedAt());
    }
}
