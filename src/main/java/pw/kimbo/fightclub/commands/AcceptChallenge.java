package pw.kimbo.fightclub.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.kimbo.fightclub.FightClubMetadata;
import pw.kimbo.fightclub.FightClubPlugin;
import pw.kimbo.fightclub.lib.Fight;

public class AcceptChallenge implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info("That command may only be used by players.");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasMetadata(FightClubMetadata.PLAYER_REQUESTED_BY)) {
            player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "You have no pending challenges.");
            return true;
        }
        Fight f = Fight.getPlayerFight(player.getUniqueId());
        f.setState(Fight.FightState.ACCEPTED);
        f.accept();
        return true;
    }
}
