package fr.dawankama.o_drey.management.user;

import fr.dawankama.o_drey.discord.events.LoginEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    @EventListener(MessageReceivedEvent.class)
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            User user = getUser(event.getAuthor());
            repository.save(user.exp(user.exp() + 1));
        }
    }

    @NotNull
    private User getUser(net.dv8tion.jda.api.entities.User author) {
        return repository.findById(author.getId())
                .orElseGet(() -> repository.save(new User(author.getId()).name(author.getName())));
    }


}
