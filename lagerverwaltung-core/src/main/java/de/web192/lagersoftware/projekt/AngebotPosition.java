package de.web192.lagersoftware.projekt;

import de.web192.lagersoftware.lager.Material;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "angebot_position")
@Getter
@Setter
@NoArgsConstructor
public class AngebotPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "angebot_id", nullable = false)
    private Angebot angebot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(nullable = false)
    private double mengeSoll;
}
