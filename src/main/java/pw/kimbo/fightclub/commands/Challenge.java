package pw.kimbo.fightclub.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.kimbo.fightclub.FightClubPlugin;
import pw.kimbo.fightclub.exceptions.FightException;
import pw.kimbo.fightclub.lib.Arena;
import pw.kimbo.fightclub.lib.Fight;


public class Challenge implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info("That command may only be used by players.");
            return true;
        }
        Player player = (Player)sender;
        if (args.length != 2) {
            player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "Wrong number of arguments. The syntax is /" + label + " <player> <arena>. To get a list of valid arenas not in use, type /listarenas");
            return true;
        }
        if (!Arena.getArenas().containsKey(args[1])) {
            player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "Invalid arena. Type /listarenas to get a list of valid arenas not in use.");
            return true;
        }
        Arena arena = Arena.getArenas().get(args[1]);
        if (arena.isInUse()) {
            player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "That arena is currently in use.");
            return true;
        }
        Player other = Bukkit.getPlayer(args[0]);
        if (other == null) {
            player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "That player does not exist.");
            return true;
        }
        try {
            Fight.request(arena, player, other);
        } catch (FightException e) {
            player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + e.getMessage());
        }
        return true;
    }
}
