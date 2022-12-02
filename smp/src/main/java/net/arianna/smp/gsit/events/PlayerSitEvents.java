package net.arianna.smp.gsit.events;

import net.arianna.smp.SMP;
import net.arianna.smp.gsit.manager.PlayerSitManager;
import net.arianna.smp.gsit.manager.ToggleManager;
import net.arianna.smp.gsit.objects.GetUpReason;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class PlayerSitEvents implements Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PTogSE(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if(!SMP.getInstance().PS_SNEAK_EJECTS || !e.isSneaking() || p.isFlying() || p.isInsideVehicle()) return;
        boolean r = SMP.getInstance().getPlayerSitManager().stopPlayerSit(p, GetUpReason.KICKED);
        if(!r) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void PDeaE(PlayerDeathEvent e) {
        if(e.getEntity().isInsideVehicle()) SMP.getInstance().getPlayerSitManager().stopPlayerSit(e.getEntity(), GetUpReason.DEATH);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void PQuiE(PlayerQuitEvent e) {
        if(e.getPlayer().isInsideVehicle()) SMP.getInstance().getPlayerSitManager().stopPlayerSit(e.getPlayer(), GetUpReason.QUIT);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void EDisE(EntityDismountEvent e) {
        if(!SMP.getInstance().getPlayerSitManager().stopPlayerSit(e.getEntity(), GetUpReason.GET_UP)) e.setCancelled(true);
        SMP.getInstance().getPlayerSitManager().stopPlayerSit(e.getDismounted(), GetUpReason.GET_UP);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PIntAEE(PlayerInteractAtEntityEvent e) {

        Entity E = e.getRightClicked();

        if(!(E instanceof Player)) return;

        Player p = e.getPlayer();

        Player t = (Player) E;

        if(!SMP.getInstance().PS_ALLOW_SIT && !SMP.getInstance().PS_ALLOW_SIT_NPC) return;
        
        if(SMP.getInstance().PS_EMPTY_HAND_ONLY && p.getInventory().getItemInMainHand().getType() != Material.AIR) return;

        if(!p.isValid() || p.isSneaking() || p.isInsideVehicle() || p.getGameMode() == GameMode.SPECTATOR) return;

        if(SMP.getInstance().getCrawlManager().isCrawling(p)) return;

        if(SMP.getInstance().getPassengerUtil().isInPassengerList(t, p) || SMP.getInstance().getPassengerUtil().isInPassengerList(p, t)) return;

        long a = SMP.getInstance().getPassengerUtil().getPassengerAmount(t) + 1 + SMP.getInstance().getPassengerUtil().getVehicleAmount(t) + SMP.getInstance().getPassengerUtil().getPassengerAmount(p);

        if(SMP.getInstance().PS_MAX_STACK > 0 && SMP.getInstance().PS_MAX_STACK <= a) return;

        Entity s = SMP.getInstance().getPassengerUtil().getHighestEntity(t);

        if(!(s instanceof Player)) return;

        Player z = (Player) s;

        if(!new ToggleManager().canPlayerSit(p.getUniqueId()) || !new ToggleManager().canPlayerSit(z.getUniqueId())) return;

        boolean n = SMP.getInstance().getPassengerUtil().isNPC(z);

        if(n && !SMP.getInstance().PS_ALLOW_SIT_NPC) return;

        if(!n && !SMP.getInstance().PS_ALLOW_SIT) return;

        boolean r = SMP.getInstance().getPlayerSitManager().sitOnPlayer(p, z);

        if(r) e.setCancelled(true);
    }

}