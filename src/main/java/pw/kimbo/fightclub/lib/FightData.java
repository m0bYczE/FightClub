package pw.kimbo.fightclub.lib;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class FightData {

    private final Location previousPlayerOneLocation, previousPlayerTwoLocation;

    private final ItemStack[] playerOneContents, playerTwoContents;
    private final ItemStack[] playerOneArmor, playerTwoArmor;


    public FightData(Player one, Player two) {
        previousPlayerOneLocation = one.getLocation();
        previousPlayerTwoLocation = two.getLocation();
        playerOneContents = one.getInventory().getContents();
        playerTwoContents = two.getInventory().getContents();

        playerOneArmor = one.getInventory().getArmorContents();
        playerTwoArmor = two.getInventory().getArmorContents();
    }

    public void restore(Player one, Player two) {
        restorePlayer(one, playerOneContents, playerOneArmor, previousPlayerOneLocation);
        restorePlayer(two, playerTwoContents, playerTwoArmor, previousPlayerTwoLocation);
    }

    private void restorePlayer(Player player, ItemStack[] contents, ItemStack[] armor, Location previous) {
        if (player == null)
            return;
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[] { null, null, null, null});
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
        player.teleport(previous);
    }
}
