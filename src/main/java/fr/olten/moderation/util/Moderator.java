package fr.olten.moderation.util;

import com.google.common.base.Preconditions;
import dev.morphia.query.experimental.updates.UpdateOperators;
import fr.olten.moderation.Moderation;
import fr.olten.moderation.logs.ChatLoggable;
import fr.olten.moderation.modes.ModesStatus;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Moderator implements ChatLoggable {

    private @Getter final Player player;
    private final AccountManager accountManager;

    public Moderator(Player player) {
        this.player = player;

        var provider = Preconditions.checkNotNull(Bukkit.getServicesManager().getRegistration(AccountSystem.class));
        this.accountManager = new AccountManager(provider.getProvider(), player);
    }

    public void moderationMode(boolean mode, boolean force){
        if(mode && !accountManager.getAccount().isModerationMode()){
            this.databaseModerationMode(true);
            this.vanish(true, force);
            Moderation.getChatLogger().log(Component.text(player.getName() + " s'est mis en mode modération."));
        }else if(!mode && accountManager.getAccount().isModerationMode()){
            this.databaseModerationMode(false);
            this.vanish(false, force);
            Moderation.getChatLogger().log(Component.text(player.getName() + " n'est plus mode modération."));
        }
    }

    private void databaseModerationMode(boolean mode){
        accountManager.getAccountQuery().update(UpdateOperators.set("moderation-mod", mode)).execute();
    }

    public void vanish(boolean vanish, boolean force){
        if (force || (vanish && !accountManager.getAccount().isVanish())) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!p.hasPermission("moderation.vanish.see")){
                    p.hidePlayer(player);
                }
            });

            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

            Moderation.getVanishTeam().addEntry(player.getName());
            player.displayName(player.displayName().decorate(TextDecoration.OBFUSCATED));
            this.databaseVanish(true);
            ModesStatus.showStatus(player);
            Moderation.getChatLogger().log(Component.text(player.getName() + " a activé son vanish."));
        }else if(!vanish && accountManager.getAccount().isVanish()) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.GLOWING);

            Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(player));
            Moderation.getVanishTeam().removeEntry(player.getName());

            player.displayName(player.name());
            var rankManager = accountManager.newRankManager();
            var team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(String.valueOf(rankManager.getMajorRank().getPower()));
            if(team != null)
                team.addEntry(player.getName());

            this.databaseVanish(false);
            ModesStatus.showStatus(player);
            Moderation.getChatLogger().log(Component.text(player.getName() + " a désactivé son vanish."));
        }
    }

    private void databaseVanish(boolean vanish){
        accountManager.getAccountQuery().update(UpdateOperators.set("vanish", vanish)).execute();
    }

    public void sendInStaffChat(Component message) {
        var provider = Bukkit.getServicesManager().getRegistration(AccountSystem.class);
        if(provider != null){
            if(!player.hasPermission("xmas.moderation.staffchat")){
                this.player.sendMessage(Component.text("Vous n'avez pas la permission de faire ça.").color(NamedTextColor.RED));
                return;
            }

            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("xmas.moderation.staffchat.see"))
                    .forEach(player -> player.sendMessage(
                    Component.text()
                            .append(Component.text("(").color(NamedTextColor.DARK_GRAY))
                            .append(Component.text("Staff").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                            .append(Component.text(") ").color(NamedTextColor.DARK_GRAY))
                            .append(Component.text(this.player.getName()).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                            .append(Component.text(" : ").color(NamedTextColor.GRAY))
                            .resetStyle()
                            .append(message)
                            .build()
            ));
        }

    }

    @Override
    public boolean receiveLogs() {
        return accountManager.getAccount().isModerationMode();
    }
}