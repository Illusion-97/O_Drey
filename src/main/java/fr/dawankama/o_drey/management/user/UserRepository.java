package fr.dawankama.o_drey.management.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {


    List<UserInfo> findByHostNotNull();

    Optional<User> findByHost(String host);
}
