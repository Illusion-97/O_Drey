package fr.dawankama.o_drey.discord.events;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Getter
public class OtherEvent extends DiscordEvent {
    private final boolean accept;
    public OtherEvent(Object source, SlashCommandInteractionEvent event, boolean accept) {
        super(source, event);
        this.accept = accept;
    }
}
