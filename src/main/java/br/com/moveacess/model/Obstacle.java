package br.com.moveacess.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class Obstacle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ObstacleType type;
    private String description;
    @Column(nullable = false) private double latitude;
    @Column(nullable = false) private double longitude;
    private int confirmations = 1;
    private int resolvedVotes = 0;
    private boolean active = true;
    @ManyToOne(fetch = FetchType.LAZY) private User reportedBy;
    private LocalDateTime createdAt = LocalDateTime.now();
}
