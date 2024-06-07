package fr.dawankama.o_drey.management.gifs;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GifService {
    private final GifRepository repository;

    public Optional<String> findGifUrl(String message) {
        List<String> urlByKeyLike = repository.findUrlByKeyLike(message);
        Collections.shuffle(urlByKeyLike);
        return urlByKeyLike.stream().findAny();
    }

    @EventListener(MessageReceivedEvent.class)
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot())
            findGifUrl(event.getMessage().getContentRaw()).ifPresent(url -> event.getChannel().sendMessage(url).queue());
    }
}
