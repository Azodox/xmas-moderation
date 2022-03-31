package fr.olten.moderation.commands;

import fr.olten.moderation.Moderation;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import net.valneas.account.rank.RankUnit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {

    private final Moderation moderation;

    public StaffChatCommand(Moderation moderation) {
        this.moderation = moderation;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            var accountSystem = (AccountSystem) Bukkit.getPluginManager().getPlugin("AccountSystem");
            var accountManager = new AccountManager(accountSystem, player);
            var rank = accountManager.newRankManager();

            if(rank.hasAtLeast(RankUnit.STAFF)){
                if(args.length == 0) {
                    if(moderation.getStaffChatToggle().contains(player.getUniqueId())){
                        moderation.getStaffChatToggle().remove(player.getUniqueId());
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cStaffChat §f§lDÉSACTIVÉ"));
                    }else {
                        moderation.getStaffChatToggle().add(player.getUniqueId());
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cStaffChat §f§lACTIVÉ"));
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
