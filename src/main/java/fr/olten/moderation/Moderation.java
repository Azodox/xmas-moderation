package fr.olten.moderation;

import fr.olten.moderation.commands.StaffChatCommand;
import fr.olten.moderation.listener.PlayerChatListener;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import net.valneas.account.rank.RankUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Moderation extends JavaPlugin {

    public static final String[] SENSITIVE_WORDS = {
            "anus",
            "arse",
            "ass",
            "asshole",
            "bastard",
            "bitch",
            "bitchass",
            "bitchtits",
            "bitches",
            "blowjob",
            "blowjobs",
            "bollocks",
            "boner",
            "boob",
            "boobs",
            "bullshit",
            "cock",
            "cocks",
            "cum",
            "cumming",
            "shit",
            "shits",
            "shitting",
            "dick",
            "dicks",
            "dildo",
            "dildos",
            "pussy",
            "pussies",
            "pd",
            "connard",
            "enculé",
            "enculer",
            "enfoiré",
            "enfoirée",
            "enfoirés",
            "nique",
            "pute",
            "putes",
            "putain",
            "putains",
            "salaud",
            "salauds",
            "salope",
            "salopes",
            "connasse",
            "connasses",
            "connard",
            "connards",
            "cons",
            "con"
    };

    public static final String STAFF_CHAT_PREFIX = "@";

    private final List<UUID> staffChatToggle = new ArrayList<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);

        getCommand("staffchat").setExecutor(new StaffChatCommand(this));

        getLogger().info("Moderation is now enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Moderation is now disabled!");
    }

    public List<UUID> getStaffChatToggle() {
        return staffChatToggle;
    }

    public void sendInStaffChat(Player sender, String message) {
        var accountSystem = (AccountSystem) Bukkit.getPluginManager().getPlugin("AccountSystem");
        Bukkit.getOnlinePlayers().stream().filter(p -> {
            var accountManager = new AccountManager(accountSystem, p);
            return accountManager.newRankManager().hasAtLeast(RankUnit.STAFF);
        }).forEach(p -> {
            var accountManager = new AccountManager(accountSystem, p);
            var rank = accountManager.newRankManager().getMajorRank();
            p.sendMessage(ChatColor.DARK_GRAY + "(" + ChatColor.AQUA + "" + ChatColor.BOLD + "Staff" + ChatColor.DARK_GRAY + ") " + ChatColor.GRAY + ChatColor.stripColor(rank.getPrefix()) + sender.getName() + " : " + ChatColor.RESET + message);
        });
    }
}
