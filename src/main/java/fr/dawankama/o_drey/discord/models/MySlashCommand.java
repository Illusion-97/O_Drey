package fr.dawankama.o_drey.discord.models;

import fr.dawankama.o_drey.discord.events.DiscordEvent;
import lombok.Data;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collection;

@Data
public class MySlashCommand {
    private SlashCommand command;
    private CommandData data;

    public MySlashCommand(SlashCommand command, String description, Collection<OptionData> options) {
        this.command = command;
        data = Commands.slash(command.name().toLowerCase(), description).addOptions(options);
    }

    public DiscordEvent getDiscordEvent(Object source, SlashCommandInteractionEvent event) {
        return new DiscordEvent(source, event);
    }
}
