package br.com.moveacess.repository;

import br.com.moveacess.model.Place;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("select p from Place p where p.latitude between :minLat and :maxLat and p.longitude between :minLng and :maxLng")
    List<Place> findInBox(@Param("minLat") double minLat, @Param("maxLat") double maxLat,
                          @Param("minLng") double minLng, @Param("maxLng") double maxLng);
}
