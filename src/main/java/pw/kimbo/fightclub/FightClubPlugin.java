package pw.kimbo.fightclub;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kimbo.fightclub.commands.AcceptChallenge;
import pw.kimbo.fightclub.commands.Challenge;
import pw.kimbo.fightclub.commands.CreateArena;
import pw.kimbo.fightclub.commands.ListArenas;
import pw.kimbo.fightclub.lib.Arena;
import pw.kimbo.fightclub.lib.SerializableLocation;
import pw.kimbo.fightclub.listeners.FightListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FightClubPlugin extends JavaPlugin {

    private static FightClubPlugin instance;

    public FightClubPlugin() {
        instance = this;
    }

    public static final String MESSAGE_PREFIX = ChatColor.DARK_RED + "[" + ChatColor.DARK_RED + "FightClub" + ChatColor.DARK_RED + "]" + ChatColor.RESET + ": ";

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Arena.class);
        ConfigurationSerialization.registerClass(SerializableLocation.class);
        saveDefaultConfig();
        reloadConfig();
        final FightListener listener = new FightListener();
        List arenas = getConfig().getList("arenas");
        if (arenas != null) {
            for (Object o : arenas) {
                Arena a = (Arena)o;
                Arena.getArenas().put(a.getName(), a);
            }
        }
        AcceptChallenge acceptChallenge = new AcceptChallenge();
        Challenge challenge = new Challenge();
        CreateArena createArena = new CreateArena();
        ListArenas listArenas = new ListArenas();

        getCommand("challenge").setExecutor(challenge);
        getCommand("challengeaccept").setExecutor(acceptChallenge);
        getCommand("fight").setExecutor(challenge);
        getCommand("fightaccept").setExecutor(acceptChallenge);
        getCommand("createarena").setExecutor(createArena);
        getCommand("setarenaspawn").setExecutor(createArena);
        getCommand("listarenas").setExecutor(listArenas);
    }

    @Override
    public void onDisable() {
        getConfig().set("arenas", new ArrayList<Arena>(Arena.getArenas().values()));
        saveConfig();
    }

    public static FightClubPlugin getInstance() {
        return instance;
    }
}
