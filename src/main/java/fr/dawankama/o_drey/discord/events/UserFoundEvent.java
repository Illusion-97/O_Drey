package fr.dawankama.o_drey.discord.events;

import lombok.Getter;
import net.dv8tion.jda.api.entities.User;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserFoundEvent extends ApplicationEvent {
    private final User user;
    public UserFoundEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
