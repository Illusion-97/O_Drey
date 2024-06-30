package fr.dawankama.o_drey.management.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public class User {
    @Id
    @NonNull
    private String id;
    private String name;
    private long exp;
    private String host;

}
