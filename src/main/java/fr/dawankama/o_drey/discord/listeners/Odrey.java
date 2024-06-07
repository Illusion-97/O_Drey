package fr.dawankama.o_drey.discord.listeners;

import fr.dawankama.o_drey.discord.events.DiscordEvent;
import fr.dawankama.o_drey.discord.events.OtherEvent;
import fr.dawankama.o_drey.discord.events.TjEvent;
import fr.dawankama.o_drey.discord.models.MySlashCommand;
import fr.dawankama.o_drey.discord.models.SlashCommand;
import fr.dawankama.o_drey.management.gifs.GifService;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class Odrey extends ListenerAdapter {
    private final JDA jda;
    private final ApplicationEventPublisher publisher;
    private final ThreadPoolTaskScheduler scheduler;
    private Role banned;
    public Odrey(@Value("${discord.token}") String DISCORD_KEY, ApplicationEventPublisher publisher, ThreadPoolTaskScheduler scheduler) {
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
        this.scheduler = scheduler;
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        guild.getRolesByName("BANNED",false).stream().findAny().ifPresentOrElse(
                role -> banned = role,
                () -> guild.createRole().setName("BANNED").queue(role -> banned = role)
        );
        guild.updateCommands().addCommands(mesCommandes.stream().map(MySlashCommand::getData).toList()).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        mesCommandes.stream()
                .filter(mySlashCommand -> mySlashCommand.getCommand().name().equalsIgnoreCase(event.getName()))
                .findFirst()
                .ifPresent(mySlashCommand -> publisher.publishEvent(mySlashCommand.getDiscordEvent().apply(this,event)));
    }

    List<MySlashCommand> mesCommandes = List.of(
            new MySlashCommand(SlashCommand.TJ,
                    "Prêche la bonne nouvelle",
                    List.of(new OptionData(OptionType.STRING, "text", "Ce que dit la Bible.", true)),
                    TjEvent::new),

            new MySlashCommand(SlashCommand.OTHER,
                    "Prêche la mauvaise nouvelle",
                    List.of(),
                    (source,event) -> new OtherEvent(source,event, true))
    );

    @EventListener(OtherEvent.class)
    public void reactToOtherEvent(OtherEvent discordEvent) {
        discordEvent.getEvent().reply("Hi there").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        publisher.publishEvent(event);
        if(Objects.requireNonNull(event.getMember()).getRoles().stream().anyMatch(role -> role.getName().equals(banned.getName())))
            event.getMessage().delete().queue();
        if(event.getMessage().getContentRaw().contains("n word"))
            event.getChannel().sendMessage("https://tenor.com/view/4k-caught-gif-20353888")
                    .queue(v -> event.getGuild().addRoleToMember(event.getMember(), banned).queue());
    }
}
