package net.arianna.smp.gsit.events;

import net.arianna.smp.SMP;
import net.arianna.smp.gsit.objects.GetUpReason;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class PlayerEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void PQuiE(PlayerQuitEvent event) {
        if(SMP.getInstance().getSitManager().isSitting(event.getPlayer())) SMP.getInstance().getSitManager().removeSeat(SMP.getInstance().getSitManager().getSeat(event.getPlayer()), GetUpReason.QUIT, true);
        if(SMP.getInstance().getPoseManager().isPosing(event.getPlayer())) SMP.getInstance().getPoseManager().removePose(SMP.getInstance().getPoseManager().getPose(event.getPlayer()), GetUpReason.QUIT, true);
        if(SMP.getInstance().getCrawlManager().isCrawling(event.getPlayer())) SMP.getInstance().getCrawlManager().stopCrawl(SMP.getInstance().getCrawlManager().getCrawl(event.getPlayer()), GetUpReason.QUIT);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void PTelE(PlayerTeleportEvent e) {

        Player p = e.getPlayer();

        if(SMP.getInstance().getSitManager().isSitting(p)) {
            if(!SMP.getInstance().getSitManager().removeSeat(SMP.getInstance().getSitManager().getSeat(p), GetUpReason.TELEPORT, false)) e.setCancelled(true);
        }

        if(SMP.getInstance().getPoseManager().isPosing(p)) {
            if(!SMP.getInstance().getPoseManager().removePose(SMP.getInstance().getPoseManager().getPose(p), GetUpReason.TELEPORT, false)) e.setCancelled(true);
        }

        if(SMP.getInstance().getCrawlManager().isCrawling(p)) {
            if(!SMP.getInstance().getCrawlManager().stopCrawl(SMP.getInstance().getCrawlManager().getCrawl(p), GetUpReason.TELEPORT)) e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void EDisE(EntityDismountEvent e) {
        Entity E = e.getEntity();
        if(!(E instanceof Player)) return;
        Player p = (Player) E;

        if(SMP.getInstance().getSitManager().isSitting(p)) {
            if(!SMP.getInstance().GET_UP_SNEAK) e.setCancelled(true);
            else {
                if(!SMP.getInstance().getSitManager().removeSeat(SMP.getInstance().getSitManager().getSeat(p), GetUpReason.GET_UP, true)) e.setCancelled(true);
            }
        }

        if(SMP.getInstance().getPoseManager().isPosing(p)) {
            if(!SMP.getInstance().GET_UP_SNEAK) e.setCancelled(true);
            else {
                if(!SMP.getInstance().getPoseManager().removePose(SMP.getInstance().getPoseManager().getPose(p), GetUpReason.GET_UP, true)) e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EDamE(EntityDamageEvent e) {
        if(!SMP.getInstance().GET_UP_DAMAGE || !(e.getEntity() instanceof Player) || e.getFinalDamage() <= 0d) return;
        if(SMP.getInstance().getSitManager().isSitting((Player) e.getEntity())) SMP.getInstance().getSitManager().removeSeat(SMP.getInstance().getSitManager().getSeat((Player) e.getEntity()), GetUpReason.DAMAGE, true);
        if(SMP.getInstance().getPoseManager().isPosing((Player) e.getEntity())) SMP.getInstance().getPoseManager().removePose(SMP.getInstance().getPoseManager().getPose((Player) e.getEntity()), GetUpReason.DAMAGE, true);
        if(SMP.getInstance().getCrawlManager().isCrawling((Player) e.getEntity())) SMP.getInstance().getCrawlManager().stopCrawl(SMP.getInstance().getCrawlManager().getCrawl((Player) e.getEntity()), GetUpReason.DAMAGE);
    }
}