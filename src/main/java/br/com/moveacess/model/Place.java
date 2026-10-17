package br.com.moveacess.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    private String address;
    private String category;
    @Column(nullable = false) private double latitude;
    @Column(nullable = false) private double longitude;
    @ManyToOne(fetch = FetchType.LAZY) private User createdBy;
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccessibilityFeature> features = new ArrayList<>();
}
