package fr.olten.moderation.logs;

import fr.olten.moderation.util.Moderator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

/**
 * @author Azodox_ (Luke)
 * 2/7/2022.
 */
public class ChatLogger {

    private static final Logger LOGGER = Logger.getLogger("ChatLogger");
    private final Component prefix;

    public ChatLogger(Component prefix) {
        this.prefix = prefix;
    }

    public void log(Component message){
        Bukkit.getOnlinePlayers().stream().map(Moderator::new).filter(ChatLoggable::receiveLogs).forEach(moderator -> {
            moderator.getPlayer().sendMessage(this.prefix.append(message));
            LOGGER.info("Sent log to " + moderator.getPlayer().getName() + " : " + PlainTextComponentSerializer.plainText().serialize(message));
        });
    }
}
