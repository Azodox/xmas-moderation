package fr.olten.moderation.logs;

import net.kyori.adventure.text.Component;

/**
 * Represents a chat-loggable {@link fr.olten.moderation.util.Moderator} or as called here <b>ChatLoggable</b>.<br>
 * <br>
 * This class is used to guarantee that an object (usually {@link fr.olten.moderation.util.Moderator}) can potentially
 * receive logs of moderation actions.<br>
 * <br>Although whether the object is able to receive logs is tell by {@link ChatLoggable#receiveLogs()}, logs are sent
 * by {@link ChatLogger#log(Component)}.
 * @since 0.1.1
 * @author Azodox_ (Luke)
 * 2/7/2022.
 */
public interface ChatLoggable {

    /**
     * Checks if the object is able to receive logs.<br>
     * @return <b>true</b> if the object is able to receive logs, <b>false</b> otherwise.
     */
    boolean receiveLogs();
}
