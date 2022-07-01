package fr.olten.moderation.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;
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
 * 25/6/2022.
 */

public class ActionBar {

    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ActionBarEngine ENGINE = new ActionBarEngine();

    static {
        SERVICE.scheduleAtFixedRate(ENGINE, 0, 1, TimeUnit.SECONDS);
    }

    public static void sendConstantly(Player player, Component actionBarContent){
        sendConstantly(player.getUniqueId(), actionBarContent);
    }

    public static void sendConstantly(UUID uuid, Component actionBarContent){
        ENGINE.getActionBars().put(uuid, actionBarContent);
    }

    public static void stopSending(Player player){
        stopSending(player.getUniqueId());
    }

    public static void stopSending(UUID uuid) {
        ENGINE.getActionBars().remove(uuid);
    }

    public static void clear(){
        ENGINE.getActionBars().forEach((uuid, component) -> {
            var player = Bukkit.getPlayer(uuid);
            if(player != null){
                player.sendActionBar(Component.text(""));
            }
        });
        ENGINE.getActionBars().clear();
        SERVICE.shutdown();
    }

    private final static class ActionBarEngine implements Runnable {

        private @Getter final Map<UUID, Component> actionBars = new HashMap<>();

        @Override
        public void run() {
            actionBars.forEach(((uuid, component) -> {
                var player = Bukkit.getPlayer(uuid);
                if(player != null)
                    player.sendActionBar(component);
            }));
        }
    }
}
