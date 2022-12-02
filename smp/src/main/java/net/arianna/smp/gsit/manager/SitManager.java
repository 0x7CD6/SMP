package net.arianna.smp.gsit.manager;

import net.arianna.smp.SMP;
import net.arianna.smp.UtilColor;
import net.arianna.smp.gsit.event.PlayerGetUpSitEvent;
import net.arianna.smp.gsit.event.PlayerSitEvent;
import net.arianna.smp.gsit.event.PrePlayerGetUpSitEvent;
import net.arianna.smp.gsit.event.PrePlayerSitEvent;
import net.arianna.smp.gsit.objects.GSeat;
import net.arianna.smp.gsit.objects.GetUpReason;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SitManager implements ISitManager {
    private int feature_used = 0;

    public int getFeatureUsedCount() { return feature_used; }

    public void resetFeatureUsedCount() { feature_used = 0; }

    private final List<GSeat> seats = new ArrayList<>();

    private final HashMap<GSeat, BukkitRunnable> rotate = new HashMap<>();

    public List<GSeat> getSeats() { return new ArrayList<>(seats); }

    public boolean isSitting(Player Player) { return getSeat(Player) != null; }

    public GSeat getSeat(Player Player) {
        for(GSeat s : getSeats()) if(Player.equals(s.getPlayer())) return s;
        return null;
    }

    public void clearSeats() { for(GSeat s : getSeats()) removeSeat(s, GetUpReason.PLUGIN); }

    public boolean kickSeat(Block Block, Player Player) {

        if(SMP.getInstance().getSitUtil().isSeatBlock(Block)) {
            for(GSeat s : SMP.getInstance().getSitUtil().getSeats(Block)) if(!removeSeat(s, GetUpReason.KICKED)) return false;
        }

        return true;
    }

    public GSeat createSeat(Block Block, Player Player) { return createSeat(Block, Player, true, 0d, 0d, 0d, Player.getLocation().getYaw(), SMP.getInstance().CENTER_BLOCK, true); }

    public GSeat createSeat(Block Block, Player Player, boolean Rotate, double XOffset, double YOffset, double ZOffset, float SeatRotation, boolean SitAtBlock, boolean GetUpSneak) {
        PrePlayerSitEvent pplase = new PrePlayerSitEvent(Player, Block);
        Bukkit.getPluginManager().callEvent(pplase);
        if(pplase.isCancelled()) return null;
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
        sa.setMetadata("Arcane", new FixedMetadataValue(SMP.getInstance(), seat));
        seats.add(seat);
        SMP.getInstance().getSitUtil().setSeatBlock(Block, seat);
        if(Rotate) startRotateSeat(seat);
        feature_used++;
        Bukkit.getPluginManager().callEvent(new PlayerSitEvent(seat));
        return seat;
    }

    public void moveSeat(GSeat Seat, BlockFace Face) {

        new BukkitRunnable() {
            @Override
            public void run() {

                SMP.getInstance().getSitUtil().removeSeatBlock(Seat.getBlock(), Seat);

                Seat.setBlock(Seat.getBlock().getRelative(Face));

                Seat.setLocation(Seat.getLocation().add(Face.getModX(), Face.getModY(), Face.getModZ()));

                SMP.getInstance().getSitUtil().setSeatBlock(Seat.getBlock(), Seat);

                SMP.getInstance().getPlayerUtil().pos(Seat.getEntity(), Seat.getLocation());
            }
        }.runTaskLater(SMP.getInstance(), 0);
    }

    protected void startRotateSeat(GSeat Seat) {

        if(rotate.containsKey(Seat)) stopRotateSeat(Seat);

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {

                if(!seats.contains(Seat) || Seat.getEntity().getPassengers().isEmpty()) {
                    cancel();
                    return;
                }

                Location l = Seat.getEntity().getPassengers().get(0).getLocation();
                Seat.getEntity().setRotation(l.getYaw(), l.getPitch());

            }
        };

        r.runTaskTimer(SMP.getInstance(), 0, 2);

        rotate.put(Seat, r);
    }

    protected void stopRotateSeat(GSeat Seat) {

        if(!rotate.containsKey(Seat)) return;

        BukkitRunnable r = rotate.get(Seat);

        if(r != null) r.cancel();

        rotate.remove(Seat);
    }

    public boolean removeSeat(GSeat Seat, GetUpReason Reason) { return removeSeat(Seat, Reason, true); }

    public boolean removeSeat(GSeat Seat, GetUpReason Reason, boolean Safe) {

        PrePlayerGetUpSitEvent pplaguse = new PrePlayerGetUpSitEvent(Seat, Reason);

        Bukkit.getPluginManager().callEvent(pplaguse);

        if(pplaguse.isCancelled()) return false;

        SMP.getInstance().getSitUtil().removeSeatBlock(Seat.getBlock(), Seat);

        seats.remove(Seat);

        stopRotateSeat(Seat);

        Location l = (SMP.getInstance().GET_UP_RETURN ? Seat.getReturn() : Seat.getLocation().add(0d, 0.2d + (Tag.STAIRS.isTagged(Seat.getBlock().getType()) ? STAIR_Y_OFFSET : 0d) - SMP.getInstance().S_SITMATERIALS.getOrDefault(Seat.getBlock().getType(), 0d), 0d));

        if(!SMP.getInstance().GET_UP_RETURN) {
            l.setYaw(Seat.getPlayer().getLocation().getYaw());
            l.setPitch(Seat.getPlayer().getLocation().getPitch());
        }

        if(Seat.getPlayer().isValid() && Safe) {
            SMP.getInstance().getPlayerUtil().pos(Seat.getPlayer(), l);
            SMP.getInstance().getPlayerUtil().teleport(Seat.getPlayer(), l, true);
        }

        if(Seat.getEntity().isValid()) {
            SMP.getInstance().getPlayerUtil().pos(Seat.getEntity(), l);
            Seat.getEntity().remove();
        }
        Bukkit.getPluginManager().callEvent(new PlayerGetUpSitEvent(Seat, Reason));
        return true;
    }

}