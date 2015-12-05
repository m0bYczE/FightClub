package pw.kimbo.fightclub.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pw.kimbo.fightclub.FightClubPlugin;
import pw.kimbo.fightclub.lib.Arena;


public class ListArenas implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String line = "";
        if (Arena.getArenas().size() == 0) {
            sender.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "No arenas loaded!");
            return true;
        }
        for (Arena a : Arena.getArenas().values()) {
            if (a.isInUse() || !a.isValid())
                continue;
            line += a.getName() + ", ";
        }
        if (line.length() == 0) {
            line = "No arenas available.";
        } else {
            line = line.substring(0, line.length() - 2);
        }

        sender.sendMessage(FightClubPlugin.MESSAGE_PREFIX + line);
        return true;
    }
}
