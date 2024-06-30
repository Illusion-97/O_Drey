package fr.dawankama.o_drey.discord.events;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.context.ApplicationEvent;


@Getter
public abstract class DiscordEvent extends ApplicationEvent {
    private final SlashCommandInteractionEvent event;
    protected DiscordEvent(Object source, SlashCommandInteractionEvent event) {
        super(source);
        this.event = event;
    }
}
