package net.arianna.smp.gsit.mcv.v1_19_R1.manager;

import net.arianna.smp.SMP;
import net.arianna.smp.UtilColor;
import net.arianna.smp.gsit.event.PlayerGetUpPoseEvent;
import net.arianna.smp.gsit.event.PlayerPoseEvent;
import net.arianna.smp.gsit.event.PrePlayerGetUpPoseEvent;
import net.arianna.smp.gsit.event.PrePlayerPoseEvent;
import net.arianna.smp.gsit.manager.IPoseManager;
import net.arianna.smp.gsit.manager.ISitManager;
import net.arianna.smp.gsit.mcv.v1_19_R1.objects.GPoseSeat;
import net.arianna.smp.gsit.objects.GSeat;
import net.arianna.smp.gsit.objects.GetUpReason;
import net.arianna.smp.gsit.objects.IGPoseSeat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PoseManager implements IPoseManager {
    private int feature_used = 0;

    public int getFeatureUsedCount() { return feature_used; }

    public void resetFeatureUsedCount() { feature_used = 0; }

    private final List<IGPoseSeat> poses = new ArrayList<>();

    private final HashMap<IGPoseSeat, BukkitRunnable> rotate = new HashMap<>();

    public List<IGPoseSeat> getPoses() { return new ArrayList<>(poses); }

    public boolean isPosing(Player Player) { return getPose(Player) != null; }

    public IGPoseSeat getPose(Player Player) {
        for(IGPoseSeat p : getPoses()) if(Player.equals(p.getSeat().getPlayer())) return p;
        return null;
    }

    public void clearPoses() { for(IGPoseSeat p : getPoses()) removePose(p, GetUpReason.PLUGIN); }

    public boolean kickPose(Block Block, Player Player) {

        if(SMP.getInstance().getPoseUtil().isPoseBlock(Block)) {
            for(IGPoseSeat p : SMP.getInstance().getPoseUtil().getPoses(Block)) if(!removePose(p, GetUpReason.KICKED)) return false;
        }

        return true;
    }

    public IGPoseSeat createPose(Block Block, Player Player, Pose Pose) { return createPose(Block, Player, Pose, 0d, 0d, 0d, Player.getLocation().getYaw(), SMP.getInstance().CENTER_BLOCK, SMP.getInstance().GET_UP_SNEAK); }

    public IGPoseSeat createPose(Block Block, Player Player, Pose Pose, double XOffset, double YOffset, double ZOffset, float SeatRotation, boolean SitAtBlock, boolean GetUpSneak) {
        PrePlayerPoseEvent pplape = new PrePlayerPoseEvent(Player, Block);
        Bukkit.getPluginManager().callEvent(pplape);
        if(pplape.isCancelled()) return null;
        double o = SitAtBlock ? Block.getBoundingBox().getMinY() + Block.getBoundingBox().getHeight() : 0d;
        o = (SitAtBlock ? o == 0d ? 1d : o - Block.getY() : o) + SMP.getInstance().S_SITMATERIALS.getOrDefault(Block.getType(), 0d);
        Location l = Player.getLocation().clone();
        Location r = l.clone();
        if(SitAtBlock) l = Block.getLocation().clone().add(0.5d + XOffset, YOffset + o - 0.2d, 0.5d + ZOffset);
        else l = l.add(XOffset, YOffset - 0.2d + SMP.getInstance().S_SITMATERIALS.getOrDefault(Block.getType(), 0d), ZOffset);
        if(!SMP.getInstance().getSpawnUtil().checkLocation(l)) return null;
        l.setYaw(SeatRotation);
        Entity sa = SMP.getInstance().getSpawnUtil().createSeatEntity(l, Player);
        Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(UtilColor.blue + "Emotes: " + UtilColor.yellow + "Press L.Control to stand up!"));
        GSeat seat = new GSeat(Block, l, Player, sa, r);
        GPoseSeat poseseat = new GPoseSeat(seat, Pose);
        poseseat.spawn();
        sa.setMetadata("Arcane-P", new FixedMetadataValue(SMP.getInstance(), poseseat));
        poses.add(poseseat);
        SMP.getInstance().getPoseUtil().setPoseBlock(Block, poseseat);
        startRotateSeat(poseseat);
        feature_used++;
        Bukkit.getPluginManager().callEvent(new PlayerPoseEvent(poseseat));
        return poseseat;
    }

    protected void startRotateSeat(IGPoseSeat PoseSeat) {
        if(rotate.containsKey(PoseSeat)) stopRotateSeat(PoseSeat);
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if(!poses.contains(PoseSeat) || PoseSeat.getSeat().getEntity().getPassengers().isEmpty()) {
                    cancel();
                    return;
                }
                Location l = PoseSeat.getSeat().getEntity().getPassengers().get(0).getLocation();
                PoseSeat.getSeat().getEntity().setRotation(l.getYaw(), l.getPitch());
            }
        };
        r.runTaskTimer(SMP.getInstance(), 0, 2);
        rotate.put(PoseSeat, r);
    }

    protected void stopRotateSeat(IGPoseSeat PoseSeat) {
        if(!rotate.containsKey(PoseSeat)) return;
        BukkitRunnable r = rotate.get(PoseSeat);
        if(r != null && !r.isCancelled()) r.cancel();
        rotate.remove(PoseSeat);
    }

    public boolean removePose(IGPoseSeat PoseSeat, GetUpReason Reason) { return removePose(PoseSeat, Reason, true); }

    public boolean removePose(IGPoseSeat PoseSeat, GetUpReason Reason, boolean Safe) {
        PrePlayerGetUpPoseEvent pplagupe = new PrePlayerGetUpPoseEvent(PoseSeat,Reason);
        Bukkit.getPluginManager().callEvent(pplagupe);
        if(pplagupe.isCancelled()) return false;
        SMP.getInstance().getPoseUtil().removePoseBlock(PoseSeat.getSeat().getBlock(), PoseSeat);
        poses.remove(PoseSeat);
        stopRotateSeat(PoseSeat);
        PoseSeat.remove();
        Location l = (SMP.getInstance().GET_UP_RETURN ? PoseSeat.getSeat().getReturn() : PoseSeat.getSeat().getLocation().add(0d, 0.2d + (Tag.STAIRS.isTagged(PoseSeat.getSeat().getBlock().getType()) ? ISitManager.STAIR_Y_OFFSET : 0d) - SMP.getInstance().S_SITMATERIALS.getOrDefault(PoseSeat.getSeat().getBlock().getType(), 0d), 0d));
        if(!SMP.getInstance().GET_UP_RETURN) {
            l.setYaw(PoseSeat.getSeat().getPlayer().getLocation().getYaw());
            l.setPitch(PoseSeat.getSeat().getPlayer().getLocation().getPitch());
        }
        if(PoseSeat.getSeat().getPlayer().isValid() && Safe) {
            SMP.getInstance().getPlayerUtil().pos(PoseSeat.getSeat().getPlayer(), l);
            SMP.getInstance().getPlayerUtil().teleport(PoseSeat.getSeat().getPlayer(), l, true);
        }
        if(PoseSeat.getSeat().getEntity().isValid()) PoseSeat.getSeat().getEntity().remove();
        Bukkit.getPluginManager().callEvent(new PlayerGetUpPoseEvent(PoseSeat, Reason));
        return true;
    }
}