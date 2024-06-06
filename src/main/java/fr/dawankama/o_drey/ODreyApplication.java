package fr.dawankama.o_drey;

import fr.dawankama.o_drey.discord.events.DiscordEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ODreyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ODreyApplication.class, args);
    }

    @EventListener(DiscordEvent.class)
    public void reactToEvent(DiscordEvent discordEvent) {
        discordEvent.getEvent().reply("Hello").queue();
    }

}
