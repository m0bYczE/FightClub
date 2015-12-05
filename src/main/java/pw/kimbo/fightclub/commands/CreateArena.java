package pw.kimbo.fightclub.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import pw.kimbo.fightclub.FightClubMetadata;
import pw.kimbo.fightclub.FightClubPlugin;
import pw.kimbo.fightclub.lib.Arena;

import java.util.ArrayList;
import java.util.List;


public class CreateArena implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info("That command may only be used by players.");
            return true;
        }
        final Player player = (Player)sender;
        final FightClubPlugin plugin = FightClubPlugin.getInstance();
        if (command.getName().equals("createarena")) {

            if (args.length != 1) {
                player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "Wrong number of arguments. Syntax is /" + label + " <name>");
                return true;
            }
            if (Arena.getArenas().containsKey(args[0])) {
                player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "There already exists an arena by that name!");
                return true;
            }
            player.setMetadata(FightClubMetadata.ARENACREATE_STEP, new FixedMetadataValue(plugin, 0));
            player.setMetadata(FightClubMetadata.ARENACREATE_ARENA, new FixedMetadataValue(plugin, new Arena(args[0])));
            player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "Arena created! Type /setarenaspawn to set the first arena spawn where you stand.");
        } else if (command.getName().equals("setarenaspawn")) {
            if (!player.hasMetadata(FightClubMetadata.ARENACREATE_STEP)) {
                player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "You are not currently creating an arena.");
                return true;
            }
            int step = player.getMetadata(FightClubMetadata.ARENACREATE_STEP).get(0).asInt();
            final Arena arena = (Arena)player.getMetadata(FightClubMetadata.ARENACREATE_ARENA).get(0).value();
            switch (step) {
                case 0:
                    arena.setLocationOne(player.getLocation());
                    player.removeMetadata(FightClubMetadata.ARENACREATE_STEP, plugin);
                    player.setMetadata(FightClubMetadata.ARENACREATE_STEP, new FixedMetadataValue(plugin, 1));
                    player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "First arena spawn set. Type /setarenaspawn again to set the second arena spawn and complete this process.");
                    break;
                case 1:
                    arena.setLocationTwo(player.getLocation());
                    player.removeMetadata(FightClubMetadata.ARENACREATE_STEP, plugin);
                    player.removeMetadata(FightClubMetadata.ARENACREATE_ARENA, plugin);
                    Arena.getArenas().put(arena.getName(), arena);
                    player.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "Second arena spawn set. Process complete! You have successfully added an arena.");
                    plugin.getConfig().set("arenas", new ArrayList<Arena>(Arena.getArenas().values()));
                    plugin.saveConfig();
                    break;
            }
        }
        return true;
    }
}
