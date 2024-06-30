package fr.dawankama.o_drey.management.personnage;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
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
