package fr.dawankama.o_drey.management.user;

import fr.dawankama.o_drey.discord.events.LoginEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
            User user = repository.findById(event.getAuthor().getId())
                    .orElseGet(() -> repository.save(new User(event.getAuthor().getId())));
            user.setExp(user.getExp() + 1);
            repository.save(user);
        }
    }
}
