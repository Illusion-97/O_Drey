package fr.dawankama.o_drey.management.personnage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/character")
public class PersonnageController {

    private final PersonnageRepository repository;

    public PersonnageController(PersonnageRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public List<Personnage> findByProprietaire(@PathVariable String id) {
        return repository.findByProprietaire_Id(id);
    }
}
