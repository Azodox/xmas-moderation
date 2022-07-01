package fr.olten.moderation.modes;

import fr.olten.moderation.Moderation;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Azodox_ (Luke)
 * 27/6/2022.
 */

public class ModesStatus {

    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ModesEngine ENGINE = new ModesEngine();

    static {
        SERVICE.scheduleAtFixedRate(ENGINE, 0, 1, TimeUnit.SECONDS);
    }

    public static void showStatus(Player player){
        update(player);
    }

    public static void hideStatus(UUID uuid){
        ENGINE.getStatus().remove(uuid);
    }

    public static void update(Player player){
        var provider = Bukkit.getServicesManager().getRegistration(AccountSystem.class);
        if(provider != null){
            var accountSystem = provider.getProvider();
            var accountManager = new AccountManager(accountSystem, player);

            var moderationMode = accountManager.getAccount().isModerationMode();
            var vanish = accountManager.getAccount().isVanish();
            var staffChat = Moderation.STAFF_CHAT_TOGGLE.contains(player.getUniqueId());

            var status = Component.text();
            status.append(Component.text("§6StaffChat §f- §e§l" + (staffChat ? "ACTIVÉ" : "DÉSACTIVÉ")));
            status.append(Component.text(" §8| §6Modération §f- §e§l" + (moderationMode ? "ACTIVÉ" : "DÉSACTIVÉ")));
            status.append(Component.text(" §8| §6Vanish §f- §e§l" + (vanish ? "ACTIVÉ" : "DÉSACTIVÉ")));

            if(ENGINE.getStatus().containsKey(player.getUniqueId()))
                ENGINE.getStatus().replace(player.getUniqueId(), status.build());
            else
                ENGINE.getStatus().put(player.getUniqueId(), status.build());

            player.sendActionBar(status.build());
            if(!moderationMode && !vanish && !staffChat)
                hideStatus(player.getUniqueId());
        }
    }

    public static void clear(){
        ENGINE.getStatus().forEach((uuid, component) -> {
            var player = Bukkit.getPlayer(uuid);
            if(player != null){
                player.sendActionBar(Component.text(""));
            }
        });
        ENGINE.getStatus().clear();
        SERVICE.shutdown();
    }

    private static class ModesEngine implements Runnable {

        private @Getter final Map<UUID, Component> status = new HashMap<>();

        @Override
        public void run() {
            status.forEach((uuid, component) -> {
                var player = Bukkit.getPlayer(uuid);
                if(player != null && player.isOnline()){
                    player.sendActionBar(component);
                }
            });
        }

    }
}
