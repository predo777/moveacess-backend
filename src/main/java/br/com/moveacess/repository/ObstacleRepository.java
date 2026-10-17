package br.com.moveacess.repository;

import br.com.moveacess.model.Obstacle;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ObstacleRepository extends JpaRepository<Obstacle, Long> {
    @Query("select o from Obstacle o where o.active = true and o.latitude between :minLat and :maxLat and o.longitude between :minLng and :maxLng")
    List<Obstacle> findActiveInBox(@Param("minLat") double minLat, @Param("maxLat") double maxLat,
                                   @Param("minLng") double minLng, @Param("maxLng") double maxLng);
}
