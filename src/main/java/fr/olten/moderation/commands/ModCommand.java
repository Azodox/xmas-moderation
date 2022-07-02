package fr.olten.moderation.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.olten.moderation.util.Moderator;
import net.kyori.adventure.text.Component;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Azodox_ (Luke)
 * 29/6/2022.
 */

@CommandAlias("mod|moderation")
@CommandPermission("xmas.moderation.mode")
public class ModCommand extends BaseCommand {

    @Default
    @Description("Toggle moderation mode.")
    public void onMod(Player player){
        var provider = Bukkit.getServicesManager().getRegistration(AccountSystem.class);
        if(provider == null)
            return;

        var accountManager = new AccountManager(provider.getProvider(), player);
        var moderator = new Moderator(player);
        moderator.moderationMode(!accountManager.getAccount().isModerationMode(), false);
    }

    @HelpCommand
    public void onHelp(Player player){
        player.sendMessage(Component.text("§c/mod §f- Toggle moderation mode."));
    }

}
