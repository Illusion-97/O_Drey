package fr.dawankama.o_drey.discord.listeners;

import fr.dawankama.o_drey.discord.events.DiscordEvent;
import fr.dawankama.o_drey.discord.events.LoginEvent;
import fr.dawankama.o_drey.discord.events.OtherEvent;
import fr.dawankama.o_drey.discord.events.TjEvent;
import fr.dawankama.o_drey.discord.models.MySlashCommand;
import fr.dawankama.o_drey.discord.models.SlashCommand;
import fr.dawankama.o_drey.management.gifs.GifService;
import fr.dawankama.o_drey.management.user.AuthResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class Odrey extends ListenerAdapter {

    private final SimpMessagingTemplate messagingTemplate;
    private final JDA jda;
    private final ApplicationEventPublisher publisher;
    private final ThreadPoolTaskScheduler scheduler;
    private Role banned;
    public Odrey(SimpMessagingTemplate messagingTemplate, @Value("${discord.token}") String DISCORD_KEY, ApplicationEventPublisher publisher, ThreadPoolTaskScheduler scheduler) {
        this.messagingTemplate = messagingTemplate;
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
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        publisher.publishEvent(event);
        Member target = event.getMember();
        Message message = event.getMessage();
        if(target != null && !target.getUser().isBot() && target.getRoles().stream().anyMatch(role -> role.getName().equals(banned.getName())))
            message.delete().queue();
        if(message.getContentRaw().contains("n word")) {
            message.reply("https://tenor.com/view/4k-caught-gif-20353888")
                    .queue(v -> event.getGuild().addRoleToMember(target, banned).queue(r -> scheduler.schedule(
                            () -> event.getGuild().removeRoleFromMember(target, banned).queue(f -> message.reply("https://tenor.com/view/out-of-jail-jail-prison-out-of-prison-im-out-gif-12660099").queue()
                            ),
                            LocalDateTime.now().plusSeconds(30).atZone(ZoneId.systemDefault()).toInstant()
                    )));
        }
    }

    @EventListener(LoginEvent.class)
    public void onLoginEvent(LoginEvent event) {
        jda.getGuilds().stream()
                .map(guild -> guild.getMembersByName(event.getUsername(), true))
                .filter(list -> !list.isEmpty())
                .findFirst()
                .map(list -> list.get(0).getUser().openPrivateChannel())
                .ifPresent(action -> action.queue(privateChannel ->
                        privateChannel.sendMessageEmbeds(new EmbedBuilder().setDescription("Tentative de connexion à votre compte sur DawAnkama").build())
                                .setActionRow(
                                        Button.success("OK", "Valider"),
                                        Button.danger("KO", "Bloquer")
                                )
                                .queue(message -> scheduler.schedule(
                                        () -> message.delete().queue(),
                                        LocalDateTime.now().plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant()))));
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        User user = event.getUser();
        String destination = "/login/"+ user.getName();
        event.reply("Décision prise en compte !").queue(r ->
                {
                    event.getMessage().delete().queue();
                    messagingTemplate.convertAndSend(
                            destination,
                            new AuthResponse(user.getId(), user.getName(), "TOKEN")
                    );
                }
        );
    }
}
