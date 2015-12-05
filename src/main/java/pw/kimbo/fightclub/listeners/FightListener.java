package pw.kimbo.fightclub.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import pw.kimbo.fightclub.FightClubMetadata;
import pw.kimbo.fightclub.FightClubPlugin;
import pw.kimbo.fightclub.lib.Fight;

public class FightListener implements Listener {

    public FightListener() {
        Bukkit.getPluginManager().registerEvents(this, FightClubPlugin.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getType().equals(EntityType.PLAYER) || !event.getEntityType().equals(EntityType.PLAYER))
            return;
        Player damager = (Player)event.getDamager();
        Player victim = (Player)event.getEntity();

        if (damager.hasMetadata(FightClubMetadata.PLAYER_COUNTDOWN) || victim.hasMetadata(FightClubMetadata.PLAYER_COUNTDOWN)) {
            event.setCancelled(true);
            return;
        }

        if (!damager.hasMetadata(FightClubMetadata.PLAYER_FIGHTING) || !victim.hasMetadata(FightClubMetadata.PLAYER_FIGHTING))
            return;

        victim.getWorld().playSound(victim.getLocation(), Sound.CHICKEN_HURT, 1000f, 0.8f);

        Fight f = Fight.getPlayerFight(damager.getUniqueId());
        if (!f.getPlayerOne().equals(victim.getUniqueId()) && !f.getPlayerTwo().equals(victim.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (victim.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            FightClubPlugin plugin = FightClubPlugin.getInstance();

            victim.removeMetadata(FightClubMetadata.PLAYER_FIGHTING, plugin);
            damager.removeMetadata(FightClubMetadata.PLAYER_FIGHTING, plugin);

            f.finish(damager, victim);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile) || !event.getEntityType().equals(EntityType.PLAYER))
            return;
        Projectile projectile = (Projectile)event.getDamager();
        Player victim = (Player)event.getEntity();

        if (!(projectile.getShooter() instanceof Player))
            return;
        Player damager = (Player)projectile.getShooter();

        if (damager.hasMetadata(FightClubMetadata.PLAYER_COUNTDOWN) || victim.hasMetadata(FightClubMetadata.PLAYER_COUNTDOWN))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Fight.isFighting(event.getEntity().getUniqueId())) {
            Fight f = Fight.getPlayerFight(event.getEntity().getUniqueId());
            f.finish(event.getEntity().getUniqueId().equals(f.getPlayerOne()) ? Bukkit.getPlayer(f.getPlayerTwo()) : Bukkit.getPlayer(f.getPlayerOne()), event.getEntity());
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (Fight.isFighting(p.getUniqueId())) {
            Fight f = Fight.getPlayerFight(p.getUniqueId());
            f.finish(p.getUniqueId().equals(f.getPlayerOne()) ? Bukkit.getPlayer(f.getPlayerTwo()) : Bukkit.getPlayer(f.getPlayerOne()), p);
        }
    }
}
