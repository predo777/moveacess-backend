package br.com.moveacess.repository;

import br.com.moveacess.model.Review;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPlaceIdOrderByCreatedAtDesc(Long placeId);
    long countByPlaceId(Long placeId);
    @Query("select avg(r.rating) from Review r where r.place.id = :placeId")
    Double averageRating(@Param("placeId") Long placeId);
}
