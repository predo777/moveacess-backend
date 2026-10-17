package br.com.moveacess.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class AccessibilityFeature {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Place place;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private FeatureType type;
    private String description;
    private int confirmations = 0;
}
