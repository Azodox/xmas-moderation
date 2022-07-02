package fr.olten.moderation.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.olten.moderation.Moderation;
import fr.olten.moderation.modes.ModesStatus;
import net.kyori.adventure.text.Component;
import net.valneas.account.AccountSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("staffchat|sc|staff")
@CommandPermission("xmas.moderation.toggle.staffchat")
public class StaffChatCommand extends BaseCommand {

    private final Moderation moderation;

    public StaffChatCommand(Moderation moderation) {
        this.moderation = moderation;
    }

    @Default
    @Description("Toggle staff chat mode.")
    public void onStaffChat(Player player){
        var provider = Bukkit.getServicesManager().getRegistration(AccountSystem.class);
        if(provider == null)
            return;

        if(Moderation.STAFF_CHAT_TOGGLE.contains(player.getUniqueId())){
            Moderation.STAFF_CHAT_TOGGLE.remove(player.getUniqueId());
            ModesStatus.showStatus(player);
        }else {
            Moderation.STAFF_CHAT_TOGGLE.add(player.getUniqueId());
            ModesStatus.showStatus(player);
        }
    }

    @HelpCommand
    public void onHelp(Player player){
        player.sendMessage(Component.text("§c/staffchat §f- Toggle staff chat mode."));
    }
}
