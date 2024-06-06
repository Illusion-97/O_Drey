package fr.dawankama.o_drey.discord.listeners;

import fr.dawankama.o_drey.discord.models.MySlashCommand;
import fr.dawankama.o_drey.discord.models.SlashCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class Odrey extends ListenerAdapter {
    private final JDA jda;
    private final ApplicationEventPublisher publisher;
    public Odrey(@Value("${discord.token}") String DISCORD_KEY, ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        this.jda = JDABuilder.createDefault(DISCORD_KEY)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .setChunkingFilter(ChunkingFilter.ALL)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.playing("Wakfu"))
                .addEventListeners(this)
                .build();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        guild.updateCommands().addCommands(mesCommandes.stream().map(MySlashCommand::getData).toList()).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        mesCommandes.stream()
                .filter(mySlashCommand -> mySlashCommand.getCommand().name().equalsIgnoreCase(event.getName()))
                .findFirst()
                .ifPresent(mySlashCommand -> publisher.publishEvent(mySlashCommand.getDiscordEvent(this,event)));
    }

    List<MySlashCommand> mesCommandes = List.of(
            new MySlashCommand(SlashCommand.TJ,
                    "Prêche la bonne nouvelle",
                    List.of(new OptionData(OptionType.STRING, "text", "Ce que dit la Bible.", true)))
    );
}
