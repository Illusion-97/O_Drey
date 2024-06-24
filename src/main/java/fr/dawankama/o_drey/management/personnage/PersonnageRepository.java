package fr.dawankama.o_drey.management.personnage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonnageRepository extends JpaRepository<Personnage, String> {
    List<Personnage> findByProprietaire_Id(String id);
}
