package net.arianna.smp.gsit.events;

import net.arianna.smp.SMP;
import net.arianna.smp.gsit.util.SitUtil;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class InteractEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PIntE(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        Action a = e.getAction();
        Player p = e.getPlayer();
        if(e.getHand() != EquipmentSlot.HAND || a != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getBlockFace() != BlockFace.UP) return;
        if(SMP.getInstance().S_EMPTY_HAND_ONLY && e.getItem() != null) return;
        if(!SMP.getInstance().S_SITMATERIALS.containsKey(b.getType())) return;
        if(!p.isValid() || !p.isOnGround() || p.isSneaking() || p.isInsideVehicle()) return;
        if(!SMP.getInstance().getToggleManager().canSit(p.getUniqueId())) return;
        if(SMP.getInstance().getCrawlManager().isCrawling(p)) return;
        if(SMP.getInstance().S_MAX_DISTANCE > 0d && b.getLocation().add(0.5, 0.5, 0.5).distance(p.getLocation()) > SMP.getInstance().S_MAX_DISTANCE) return;
        if(!SMP.getInstance().ALLOW_UNSAFE && !(b.getRelative(BlockFace.UP).isPassable())) return;
        if(!SMP.getInstance().SAME_BLOCK_REST && !SMP.getInstance().getSitManager().kickSeat(b, p)) return;
        if(Tag.STAIRS.isTagged(b.getType())) {
            if(((Stairs) b.getBlockData()).getHalf() != Half.BOTTOM) return;
            if(new SitUtil().createSeatForStair(b, p) != null) {
                e.setCancelled(true);
                return;
            }
        } else if(Tag.SLABS.isTagged(b.getType())) {
            if(((Slab) b.getBlockData()).getType() != Type.BOTTOM) return;
        }

        if(SMP.getInstance().getSitManager().createSeat(b, p, true, 0d, 0d, 0d, p.getLocation().getYaw(), true, SMP.getInstance().GET_UP_SNEAK) != null) e.setCancelled(true);
    }

}