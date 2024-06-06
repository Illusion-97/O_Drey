package fr.dawankama.o_drey.management.gifs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GifRepository extends JpaRepository<Gif, String> {


    @Query("SELECT g.url FROM Gif g WHERE :message LIKE CONCAT('%',g.gifKey,'%')")
    List<String> findUrlByKeyLike(String message);
}
