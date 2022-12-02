package net.arianna.smp.gsit.events;

import net.arianna.smp.SMP;
import net.arianna.smp.gsit.objects.GSeat;
import net.arianna.smp.gsit.objects.GetUpReason;
import net.arianna.smp.gsit.objects.IGPoseSeat;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BPisEE(BlockPistonExtendEvent e) {
        List<GSeat> ml = new ArrayList<>();
        for(Block b : e.getBlocks()) {
            if(SMP.getInstance().getSitUtil().isSeatBlock(b)) {
                for(GSeat s : SMP.getInstance().getSitUtil().getSeats(b)) {
                    if(ml.contains(s)) continue;
                    SMP.getInstance().getSitManager().moveSeat(s, e.getDirection());
                    ml.add(s);
                }
            }
            if(SMP.getInstance().GET_UP_BREAK && SMP.getInstance().getPoseUtil().isPoseBlock(b)) {
                for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(b)) SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BPisRE(BlockPistonRetractEvent e) {
        List<GSeat> ml = new ArrayList<>();
        for(Block b : e.getBlocks()) {
            if(SMP.getInstance().getSitUtil().isSeatBlock(b)) {
                for(GSeat s : SMP.getInstance().getSitUtil().getSeats(b)) {
                    if(ml.contains(s)) continue;
                    SMP.getInstance().getSitManager().moveSeat(s, e.getDirection());
                    ml.add(s);
                }
            }
            if(SMP.getInstance().GET_UP_BREAK && SMP.getInstance().getPoseUtil().isPoseBlock(b)) {
                for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(b)) SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BExpE(BlockExplodeEvent e) {
        if(!SMP.getInstance().GET_UP_BREAK) return;
        for(Block b : new ArrayList<>(e.blockList())) {
            if(SMP.getInstance().getSitUtil().isSeatBlock(b)) {
                for(GSeat s : SMP.getInstance().getSitUtil().getSeats(b)) {
                    boolean r = SMP.getInstance().getSitManager().removeSeat(s, GetUpReason.BREAK);
                    if(!r) e.blockList().remove(b);
                }
            }
            if(SMP.getInstance().getPoseUtil().isPoseBlock(b)) {
                for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(b)) {
                    boolean r = SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
                    if(!r) e.blockList().remove(b);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EExpE(EntityExplodeEvent e) {
        if(!SMP.getInstance().GET_UP_BREAK) return;
        for(Block b : new ArrayList<>(e.blockList())) {
            if(SMP.getInstance().getSitUtil().isSeatBlock(b)) {
                for(GSeat s : SMP.getInstance().getSitUtil().getSeats(b)) {
                    boolean r = SMP.getInstance().getSitManager().removeSeat(s, GetUpReason.BREAK);
                    if(!r) e.blockList().remove(b);
                }
            }
            if(SMP.getInstance().getPoseUtil().isPoseBlock(b)) {
                for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(b)) {
                    boolean r = SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
                    if(!r) e.blockList().remove(b);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BFadE(BlockFadeEvent e) {
        if(!SMP.getInstance().GET_UP_BREAK) return;
        if(SMP.getInstance().getSitUtil().isSeatBlock(e.getBlock())) {
            for(GSeat s : SMP.getInstance().getSitUtil().getSeats(e.getBlock())) {
                boolean r = SMP.getInstance().getSitManager().removeSeat(s, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
        if(SMP.getInstance().getPoseUtil().isPoseBlock(e.getBlock()) && !e.isCancelled()) {
            for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(e.getBlock())) {
                boolean r = SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void LDecE(LeavesDecayEvent e) {
        if(!SMP.getInstance().GET_UP_BREAK) return;
        if(SMP.getInstance().getSitUtil().isSeatBlock(e.getBlock())) {
            for(GSeat s : SMP.getInstance().getSitUtil().getSeats(e.getBlock())) {
                boolean r = SMP.getInstance().getSitManager().removeSeat(s, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
        if(SMP.getInstance().getPoseUtil().isPoseBlock(e.getBlock()) && !e.isCancelled()) {
            for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(e.getBlock())) {
                boolean r = SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BBurE(BlockBurnEvent e) {
        if(!SMP.getInstance().GET_UP_BREAK) return;
        if(SMP.getInstance().getSitUtil().isSeatBlock(e.getBlock())) {
            for(GSeat s : SMP.getInstance().getSitUtil().getSeats(e.getBlock())) {
                boolean r = SMP.getInstance().getSitManager().removeSeat(s, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
        if(SMP.getInstance().getPoseUtil().isPoseBlock(e.getBlock()) && !e.isCancelled()) {
            for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(e.getBlock())) {
                boolean r = SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EChaBE(EntityChangeBlockEvent e) {
        if(!SMP.getInstance().GET_UP_BREAK) return;
        if(SMP.getInstance().getSitUtil().isSeatBlock(e.getBlock())) {
            for(GSeat s : SMP.getInstance().getSitUtil().getSeats(e.getBlock())) {
                boolean r = SMP.getInstance().getSitManager().removeSeat(s, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
        if(SMP.getInstance().getPoseUtil().isPoseBlock(e.getBlock()) && !e.isCancelled()) {
            for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(e.getBlock())) {
                boolean r = SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
    }

    /*@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BPhyE(BlockPhysicsEvent e) {
        if(!SMP.getInstance().GET_UP_BREAK) return;
        if(!e.getSourceBlock().getType().name().endsWith("AIR")) return;
        if(SMP.getInstance().getSitUtil().isSeatBlock(e.getSourceBlock())) {
            for(GSeat s : SMP.getInstance().getSitUtil().getSeats(e.getSourceBlock())) {
                boolean r = SMP.getInstance().getSitManager().removeSeat(s, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
        if(SMP.getInstance().getPoseUtil().isPoseBlock(e.getSourceBlock()) && !e.isCancelled()) {
            for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(e.getSourceBlock())) {
                boolean r = SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
    }*/

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BBreE(BlockBreakEvent e) {
        if(!SMP.getInstance().GET_UP_BREAK) return;
        if(SMP.getInstance().getSitUtil().isSeatBlock(e.getBlock())) {
            for(GSeat s : SMP.getInstance().getSitUtil().getSeats(e.getBlock())) {
                boolean r = SMP.getInstance().getSitManager().removeSeat(s, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
        if(SMP.getInstance().getPoseUtil().isPoseBlock(e.getBlock()) && !e.isCancelled()) {
            for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(e.getBlock())) {
                boolean r = SMP.getInstance().getPoseManager().removePose(p, GetUpReason.BREAK);
                if(!r) e.setCancelled(true);
            }
        }
    }

}