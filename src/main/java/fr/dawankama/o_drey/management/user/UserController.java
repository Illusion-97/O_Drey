package fr.dawankama.o_drey.management.user;

import fr.dawankama.o_drey.discord.events.LoginEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final ApplicationEventPublisher publisher;

    @GetMapping("/login/{name}")
    public void login(@PathVariable String name) {
        publisher.publishEvent(new LoginEvent(this, name));
    }
}
