package fr.olten.moderation.listener;

import fr.olten.moderation.Moderation;
import fr.olten.xmas.Core;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import net.valneas.account.rank.RankUnit;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;

public class PlayerChatListener implements Listener {

    private final Moderation moderation;

    public PlayerChatListener(final Moderation moderation) {
        this.moderation = moderation;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        var player  = event.getPlayer();
        var message = event.getMessage();
        var censoredMessage = event.getMessage();

        if(!moderation.getStaffChatToggle().contains(player.getUniqueId())) {
            boolean sensitive = false;
            for (String sSplit : message.split(" ")) {
                String s = StringUtils.stripAccents(sSplit).replaceAll("\\p{Punct}", "");
                if (Arrays.stream(Moderation.SENSITIVE_WORDS).map(StringUtils::stripAccents).anyMatch(s::equalsIgnoreCase)) {
                    sensitive = true;
                    message = message.replace(sSplit, net.md_5.bungee.api.ChatColor.of("#2EFF00") + "" + ChatColor.BOLD + sSplit + ChatColor.RESET);
                    censoredMessage = censoredMessage.replace(sSplit, ChatColor.MAGIC + sSplit + ChatColor.RESET);
                }
            }

            if (sensitive) {
                event.setCancelled(true);
                String finalMessage = message;
                String finalCensoredMessage = censoredMessage;

                var accountSystem = (AccountSystem) Bukkit.getPluginManager().getPlugin("AccountSystem");
                var accountManager = new AccountManager(accountSystem, player);
                var rank = accountManager.newRankManager().getMajorRank();
                Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(p -> p.sendMessage(String.format(Core.CHAT_FORMAT.replace("%rank%", rank.getPrefix()), player.getName(), finalMessage)));
                Bukkit.getOnlinePlayers().stream().filter(p -> !p.isOp()).forEach(p -> p.sendMessage(String.format(Core.CHAT_FORMAT.replace("%rank%", rank.getPrefix()), player.getName(), event.getMessage())));
                Bukkit.getConsoleSender().sendMessage(String.format(Core.CHAT_FORMAT.replace("%rank%", rank.getPrefix()), player.getName(), finalCensoredMessage));
            }
        }else{
            event.setCancelled(true);
            moderation.sendInStaffChat(player, message);
        }

        if(message.startsWith(Moderation.STAFF_CHAT_PREFIX)){
            event.setCancelled(true);
            moderation.sendInStaffChat(player, message.replace(Moderation.STAFF_CHAT_PREFIX, "§b" + Moderation.STAFF_CHAT_PREFIX + "§r"));
        }
    }
}
