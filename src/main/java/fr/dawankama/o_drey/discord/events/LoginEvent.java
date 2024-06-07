package fr.dawankama.o_drey.discord.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


public class LoginEvent extends ApplicationEvent {
    @Getter
    private final String username;

    public LoginEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
}
