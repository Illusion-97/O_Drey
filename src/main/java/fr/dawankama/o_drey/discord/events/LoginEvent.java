package fr.dawankama.o_drey.discord.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class LoginEvent extends ApplicationEvent {
    private final String username;

    public LoginEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
}
