package fr.dawankama.o_drey.management.user;

import org.springframework.beans.factory.annotation.Value;

/**
 * Projection for {@link User}
 */
public interface UserInfo {
    String getId();
    String getName();
    @Value("#{target.host != null}")
    boolean isConnected();
}
