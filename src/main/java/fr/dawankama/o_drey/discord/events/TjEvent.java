package fr.dawankama.o_drey.discord.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TjEvent extends DiscordEvent {
    public TjEvent(Object source, SlashCommandInteractionEvent event) {
        super(source, event);
    }
}
