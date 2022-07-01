package fr.olten.moderation.listener;

import com.google.common.base.Preconditions;
import fr.olten.moderation.Moderation;
import fr.olten.moderation.util.Moderator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final Moderation moderation;

    public PlayerJoinListener(Moderation moderation) {
        this.moderation = moderation;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        var provider = Preconditions.checkNotNull(Bukkit.getServicesManager().getRegistration(AccountSystem.class));
        var player = event.getPlayer();
        var accountManager = new AccountManager(provider.getProvider(), player);
        var moderator = new Moderator(player);

        if(accountManager.getAccount().isModerationMode()){
            moderator.moderationMode(true, true);
            player.sendMessage(moderation.getPrefix().append(Component.text("Mode modération automatiquement activé.").color(NamedTextColor.YELLOW)));
        }else if(accountManager.getAccount().isVanish()){
            moderator.vanish(true, true);
            player.sendMessage(moderation.getPrefix().append(Component.text("Vanish automatiquement activé.").color(NamedTextColor.YELLOW)));
        }
    }
}
