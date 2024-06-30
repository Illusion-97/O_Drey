package fr.dawankama.o_drey.management.session;

import fr.dawankama.o_drey.discord.listeners.Odrey;
import fr.dawankama.o_drey.management.user.User;
import fr.dawankama.o_drey.management.user.UserInfo;
import fr.dawankama.o_drey.management.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SessionListener {

    private final UserRepository repository;
    private final SimpMessagingTemplate template;
    private final Odrey odrey;
    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        repository.findByHost(event.getSessionId()).ifPresent(user -> notifyOnline(repository.save(user.host(null))));
    }

    @SubscribeMapping("/online")
    public List<UserInfo> getOnlines(@Header Map<String,List<String>> nativeHeaders, @Header String simpSessionId){
        odrey.findUser(nativeHeaders.get("user").get(0))
                .map(user -> repository.findById(user.getId()).orElseGet(() -> repository.save(new User(user.getId()).name(user.getName()))))
                .ifPresent(u -> notifyOnline(repository.save(u.host(simpSessionId))));
        return repository.findByHostNotNull();
    }

    private void notifyOnline(User user) {
        template.convertAndSend("/login/online", new UserInfo() {
            @Override
            public String getId() {
                return user.id();
            }

            @Override
            public String getName() {
                return user.name();
            }

            @Override
            public boolean isConnected() {
                return user.host() != null;
            }
        });
    }
}
