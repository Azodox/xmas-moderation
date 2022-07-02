package fr.olten.moderation;

import co.aikar.commands.PaperCommandManager;
import fr.olten.moderation.commands.ModCommand;
import fr.olten.moderation.commands.StaffChatCommand;
import fr.olten.moderation.commands.VanishCommand;
import fr.olten.moderation.listener.PlayerChatListener;
import fr.olten.moderation.listener.PlayerJoinListener;
import fr.olten.moderation.modes.ModesStatus;
import fr.olten.moderation.util.ActionBar;
import fr.olten.moderation.util.Moderator;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.valneas.account.AccountManager;
import net.valneas.account.AccountSystem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

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
    public static final List<UUID> STAFF_CHAT_TOGGLE = new ArrayList<>();

    private @Getter Component prefix;
    private @Getter static Team vanishTeam;


    @Override
    public void onEnable() {
        saveDefaultConfig();

        var prefixPath = getConfig().getString("prefix");
        if(prefixPath != null){
            this.prefix = MiniMessage.miniMessage().deserialize(prefixPath);
        }

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);

        vanishTeam =
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("vanish") == null ?
                        Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("vanish") :
                        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("vanish");
        vanishTeam.color(NamedTextColor.GRAY);
        vanishTeam.suffix(Component.text(" (vanish)").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));

        var provider = getServer().getServicesManager().getRegistration(AccountSystem.class);
        if (provider != null) {
            getServer().getOnlinePlayers().forEach(player -> {
                var accountManager = new AccountManager(provider.getProvider(), player);
                if (accountManager.getAccount().isVanish()) {
                    new Moderator(player).vanish(true, false);
                }
            });
        }

        var commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new StaffChatCommand(this));
        commandManager.registerCommand(new VanishCommand(this));
        commandManager.registerCommand(new ModCommand());

        getLogger().info("Moderation is now enabled!");
    }

    @Override
    public void onDisable() {
        ModesStatus.clear();
        ActionBar.clear();
        getLogger().info("Moderation is now disabled!");
    }
}