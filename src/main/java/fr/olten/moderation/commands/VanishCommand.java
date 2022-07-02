package fr.olten.moderation.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.olten.moderation.Moderation;
import fr.olten.moderation.util.Moderator;
import net.kyori.adventure.text.Component;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("vanish|v")
@CommandPermission("xmas.moderation.toggle.vanish")
public class VanishCommand extends BaseCommand {

    private final Moderation moderation;
    public VanishCommand(Moderation moderation) {
        this.moderation = moderation;
    }

    @Default
    @Description("Toggle vanish mode.")
    public void onVanish(Player player){
        var provider = Bukkit.getServicesManager().getRegistration(AccountSystem.class);
        if(provider == null)
            return;

        var accountManager = new AccountManager(provider.getProvider(), player);
        var moderator = new Moderator(player);
        moderator.vanish(!accountManager.getAccount().isVanish(), false);
    }

    @HelpCommand
    public void onHelp(Player player){
        player.sendMessage(Component.text("§c/vanish §f- Toggle vanish mode."));
    }

}
