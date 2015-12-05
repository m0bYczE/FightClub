package pw.kimbo.fightclub.lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import pw.kimbo.fightclub.FightClubMetadata;
import pw.kimbo.fightclub.FightClubPlugin;
import pw.kimbo.fightclub.event.FightEndEvent;
import pw.kimbo.fightclub.event.FightStartEvent;
import pw.kimbo.fightclub.exceptions.FightException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Fight {

    public enum FightState {
        REQUESTED,
        ACCEPTED,
        STARTED
    }

    private final Arena arena;
    private final UUID playerOne, playerTwo;

    private FightState state;
    private FightData fightInfo;

    private static final Map<UUID, Fight> fights;

    static {
        fights = new HashMap<UUID, Fight>();
    }

    protected Fight(Arena arena, UUID one, UUID two) {
        this.arena = arena;
        playerOne = one;
        playerTwo = two;
        state = FightState.REQUESTED;
        fightInfo = null;
    }



    public UUID getPlayerOne() {
        return playerOne;
    }

    public UUID getPlayerTwo() {
        return playerTwo;
    }

    public FightState getState() {
        return state;
    }

    public void setState(FightState state) {
        this.state = state;
    }

    public static Fight request(final Arena arena, final Player one, final Player two) throws FightException {
        if (arena.isInUse())
            throw new FightException("That arena is already in use!");
        if (one.hasMetadata(FightClubMetadata.PLAYER_REQUESTED))
            throw new FightException("You have already requested a fight with someone!");
        if (one.hasMetadata(FightClubMetadata.PLAYER_FIGHTING) || one.hasMetadata(FightClubMetadata.PLAYER_COUNTDOWN))
            throw new FightException("You are already fighting!");
        if (two.hasMetadata(FightClubMetadata.PLAYER_REQUESTED_BY))
            throw new FightException("That player has already been challenged to a fight!");
        if (two.hasMetadata(FightClubMetadata.PLAYER_FIGHTING) || two.hasMetadata(FightClubMetadata.PLAYER_COUNTDOWN))
            throw new FightException("That player is already fighting!");
        arena.setInUse(true);

        final FightClubPlugin plugin = FightClubPlugin.getInstance();

        one.removeMetadata(FightClubMetadata.PLAYER_REQUESTED, plugin);
        one.setMetadata(FightClubMetadata.PLAYER_REQUESTED, new FixedMetadataValue(plugin, true));
        one.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "The gauntlet has been thrown! You have challenged " + two.getDisplayName() + " to a fistfight.");
        two.setMetadata(FightClubMetadata.PLAYER_REQUESTED_BY, new FixedMetadataValue(plugin, one.getUniqueId()));
        two.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "You have been challenged to a fistfight by " + one.getDisplayName() + "! Type /fightaccept to accept this challenge.");
        two.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "This challenge will expire in 30 seconds if not accepted.");

        final Fight fight = new Fight(arena, one.getUniqueId(), two.getUniqueId());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Fight f = Fight.getPlayerFight(one.getUniqueId());
                if (!f.getState().equals(FightState.REQUESTED))
                    return;
                arena.setInUse(false);

                if (one.isValid()) {
                    one.removeMetadata(FightClubMetadata.PLAYER_REQUESTED, plugin);
                    one.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "Your pending challenge to " + two.getDisplayName() + " has expired.");
                }
                if (two.isValid()) {
                    two.removeMetadata(FightClubMetadata.PLAYER_REQUESTED_BY, plugin);
                    two.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "Your pending challenge by " + one.getDisplayName() + " has expired.");
                }
                fights.remove(one.getUniqueId());
                fights.remove(two.getUniqueId());
            }
        }, 20 * 30);

        fights.put(one.getUniqueId(), fight);
        fights.put(two.getUniqueId(), fight);

        return fight;
    }

    public void accept() {
        setState(FightState.ACCEPTED);
        final Player one = Bukkit.getPlayer(playerOne);
        final Player two = Bukkit.getPlayer(playerTwo);
        fightInfo = new FightData(one, two);

        one.setFireTicks(0);
        two.setFireTicks(0);

        one.setHealth(one.getMaxHealth());
        two.setHealthScale(two.getMaxHealth());

        one.setFoodLevel(20);
        two.setFoodLevel(20);

        one.getInventory().clear();
        one.getInventory().setArmorContents(new ItemStack[] { null, null, null, null});
        two.getInventory().clear();
        two.getInventory().setArmorContents(new ItemStack[] { null, null, null, null});

        one.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        two.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        one.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        two.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        final FightClubPlugin plugin = FightClubPlugin.getInstance();
        one.setMetadata(FightClubMetadata.PLAYER_COUNTDOWN, new FixedMetadataValue(plugin, true));
        two.setMetadata(FightClubMetadata.PLAYER_COUNTDOWN, new FixedMetadataValue(plugin, true));

        one.removeMetadata(FightClubMetadata.PLAYER_REQUESTED, plugin);
        two.removeMetadata(FightClubMetadata.PLAYER_REQUESTED_BY, plugin);


        one.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "Your challenge to " + two.getDisplayName() + " has been accepted!");
        two.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "You have accepted the challenge of " + one.getDisplayName() + "!");

        one.teleport(arena.getLocationOne());
        two.teleport(arena.getLocationTwo());

        one.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "The fight will start in 3 seconds...");
        two.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "The fight will start in 3 seconds...");

        // this is ghetto but the easiest way because of how inflexible Java is :(


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                one.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "The fight will start in 2 seconds...");
                two.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "The fight will start in 2 seconds...");
            }
        }, 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                one.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "The fight will start in 1 second...");
                two.sendMessage(FightClubPlugin.MESSAGE_PREFIX + "The fight will start in 1 second...");
            }
        }, 20 * 2);

        final Fight thisFight = this;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                setState(FightState.STARTED);
                one.removeMetadata(FightClubMetadata.PLAYER_COUNTDOWN, plugin);
                two.removeMetadata(FightClubMetadata.PLAYER_COUNTDOWN, plugin);
                one.setMetadata(FightClubMetadata.PLAYER_FIGHTING, new FixedMetadataValue(plugin, true));
                two.setMetadata(FightClubMetadata.PLAYER_FIGHTING, new FixedMetadataValue(plugin, true));

                one.sendMessage(FightClubPlugin.MESSAGE_PREFIX + ChatColor.GOLD + "Let the fight begin!");
                two.sendMessage(FightClubPlugin.MESSAGE_PREFIX + ChatColor.GOLD + "Let the fight begin!");

                Bukkit.getPluginManager().callEvent(new FightStartEvent(thisFight));
            }
        }, 20 * 3);
    }

    public void finish(Player winner, Player loser) {
        Bukkit.broadcastMessage(FightClubPlugin.MESSAGE_PREFIX +
                ChatColor.GOLD + winner.getDisplayName() +
                ChatColor.RESET + " has bested " + ChatColor.RED +
                loser.getDisplayName() + ChatColor.RESET + " in a fistfight!");
        arena.setInUse(false);
        winner.setHealth(winner.getMaxHealth());
        loser.setHealth(loser.getMaxHealth());

        winner.setFoodLevel(20);
        loser.setFoodLevel(20);

        Bukkit.getPluginManager().callEvent(new FightEndEvent(this));
        fightInfo.restore(Bukkit.getPlayer(playerOne), Bukkit.getPlayer(playerTwo));
        fights.remove(playerOne);
        fights.remove(playerTwo);
    }

    public static boolean isFighting(UUID player) {
        return fights.containsKey(player);
    }

    public static Fight getPlayerFight(UUID player) {
        return fights.get(player);
    }
}
