package fr.dawankama.o_drey.management.personnage;

import fr.dawankama.o_drey.management.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Personnage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @ManyToOne
    private User proprietaire;

    private String nom;

    private int niveau;

    private short classe;

    private int genre;
}
