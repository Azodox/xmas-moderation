package fr.olten.moderation.listener;

import fr.olten.moderation.Moderation;
import fr.olten.moderation.util.Moderator;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class PlayerChatListener implements Listener {

    private final Moderation moderation;

    public PlayerChatListener(final Moderation moderation) {
        this.moderation = moderation;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        var player  = event.getPlayer();
        var moderator = new Moderator(player);
        var message = event.message();
        var content = PlainTextComponentSerializer.plainText().serialize(message);
        var censored = (Component) Component.empty();
        var moderatorMessage = (Component) Component.empty();

        if(!Moderation.STAFF_CHAT_TOGGLE.contains(player.getUniqueId())) {
            boolean sensitive = false;
            for (String sSplit : content.split(" ")) {
                String s = StringUtils.stripAccents(sSplit).replaceAll("\\p{Punct}", "");
                if (Arrays.stream(Moderation.SENSITIVE_WORDS).map(StringUtils::stripAccents).anyMatch(s::equalsIgnoreCase)) {
                    sensitive = true;
                    message = message.replaceText(builder -> builder.matchLiteral(sSplit).replacement(Component.text(sSplit).color(TextColor.color(0xf74343)).decorate(TextDecoration.BOLD)));
                    moderatorMessage = message.replaceText(builder -> builder.matchLiteral(sSplit).replacement(Component.text("*" + sSplit + "*").decorate(TextDecoration.UNDERLINED)));
                    var text = Component.text();
                    for (char c : sSplit.toCharArray()) {
                        text.append(Component.text('*'));
                    }
                    censored = message.replaceText(builder -> builder.matchLiteral(sSplit).replacement(text.build().decorate(TextDecoration.OBFUSCATED)));
                }
            }

            if (sensitive) {
                Component finalCensored = censored;
                Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasPermission("xmas.moderation.warning.sensitive")).forEach(p -> event.renderer().render(player, player.displayName(), finalCensored, p));
                Component finalModeratorMessage = moderatorMessage;
                Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("xmas.moderation.warning.sensitive")).forEach(p -> event.renderer().render(player, player.displayName(), finalModeratorMessage, p));
            }
        }else{
            event.setCancelled(true);
            moderator.sendInStaffChat(message);
        }

        if(content.startsWith(Moderation.STAFF_CHAT_PREFIX) && !Moderation.STAFF_CHAT_TOGGLE.contains(player.getUniqueId()) && player.hasPermission("xmas.moderation.staffchat")) {
            event.setCancelled(true);
            moderator.sendInStaffChat(message.replaceText(builder -> builder.matchLiteral(Moderation.STAFF_CHAT_PREFIX).replacement(Component.text(Moderation.STAFF_CHAT_PREFIX).toBuilder().color(NamedTextColor.AQUA).build())));
        }
    }
}
