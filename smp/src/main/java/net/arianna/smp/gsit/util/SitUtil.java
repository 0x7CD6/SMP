package net.arianna.smp.gsit.util;

import net.arianna.smp.SMP;
import net.arianna.smp.gsit.manager.ISitManager;
import net.arianna.smp.gsit.manager.SitManager;
import net.arianna.smp.gsit.objects.GSeat;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;

public class SitUtil {
    public boolean isSeatBlock(Block B) { return B.hasMetadata("Arcane"); }

    @SuppressWarnings("unchecked")
    public List<GSeat> getSeats(Block B) {
        List<GSeat> seats = new ArrayList<>();
        if(isSeatBlock(B)) {
            MetadataValue m = B.getMetadata("Arcane").stream().filter(s -> SMP.getInstance().equals(s.getOwningPlugin())).findFirst().orElse(null);
            if(m != null) seats = new ArrayList<>((List<GSeat>) m.value());
        }
        return seats;
    }

    public List<GSeat> getSeats(List<Block> B) {
        List<GSeat> seats = new ArrayList<>();
        for(Block b : B) for(GSeat c : getSeats(b)) if(!seats.contains(c)) seats.add(c);
        return seats;
    }

    public void setSeatBlock(Block B, GSeat S) {
        List<GSeat> seats = getSeats(B);
        if(!seats.contains(S)) seats.add(S);
        B.setMetadata("Arcane", new FixedMetadataValue(SMP.getInstance(), seats));
    }

    public void removeSeatBlock(Block B, GSeat S) {
        List<GSeat> seats = getSeats(B);
        seats.remove(S);
        if(seats.size() > 0) B.setMetadata("Arcane", new FixedMetadataValue(SMP.getInstance(), seats));
        else B.removeMetadata("Arcane", SMP.getInstance());
    }

    public GSeat createSeatForStair(Block Block, Player Player) {
        GSeat seat = null;
        Stairs bd = (Stairs) Block.getBlockData();
        if(bd.getHalf() != Bisected.Half.BOTTOM) return SMP.getInstance().getSitManager().createSeat(Block, Player);
        BlockFace f = bd.getFacing().getOppositeFace();
        Stairs.Shape s = bd.getShape();
        if(bd.getShape() == Stairs.Shape.STRAIGHT) {
            switch(f) {
                case EAST: seat = SMP.getInstance().getSitManager().createSeat(Block, Player, false, ISitManager.STAIR_XZ_OFFSET, -ISitManager.STAIR_Y_OFFSET, 0d, -90f, true, SMP.getInstance().GET_UP_SNEAK);break;
                case SOUTH: seat = SMP.getInstance().getSitManager().createSeat(Block, Player, false, 0d, -ISitManager.STAIR_Y_OFFSET, ISitManager.STAIR_XZ_OFFSET, 0f, true, SMP.getInstance().GET_UP_SNEAK);break;
                case WEST: seat = SMP.getInstance().getSitManager().createSeat(Block, Player, false, -ISitManager.STAIR_XZ_OFFSET, -ISitManager.STAIR_Y_OFFSET, 0d, 90f, true, SMP.getInstance().GET_UP_SNEAK);break;
                case NORTH: seat = SMP.getInstance().getSitManager().createSeat(Block, Player, false, 0d, -ISitManager.STAIR_Y_OFFSET, -ISitManager.STAIR_XZ_OFFSET, 180f, true, SMP.getInstance().GET_UP_SNEAK);break;
                default: break;
            }
        } else {
            if(f == BlockFace.NORTH && s == Stairs.Shape.OUTER_RIGHT || f == BlockFace.EAST && s == Stairs.Shape.OUTER_LEFT || f == BlockFace.NORTH && s == Stairs.Shape.INNER_RIGHT || f == BlockFace.EAST && s == Stairs.Shape.INNER_LEFT) seat = SMP.getInstance().getSitManager().createSeat(Block, Player, false, ISitManager.STAIR_XZ_OFFSET, -ISitManager.STAIR_Y_OFFSET, -ISitManager.STAIR_XZ_OFFSET, -135f, true, SMP.getInstance().GET_UP_SNEAK);
            else if(f == BlockFace.NORTH && s == Stairs.Shape.OUTER_LEFT || f == BlockFace.WEST && s == Stairs.Shape.OUTER_RIGHT || f == BlockFace.NORTH && s == Stairs.Shape.INNER_LEFT || f == BlockFace.WEST && s == Stairs.Shape.INNER_RIGHT) seat = SMP.getInstance().getSitManager().createSeat(Block, Player, false, -ISitManager.STAIR_XZ_OFFSET, -ISitManager.STAIR_Y_OFFSET, -ISitManager.STAIR_XZ_OFFSET, 135f, true, SMP.getInstance().GET_UP_SNEAK);
            else if(f == BlockFace.SOUTH && s == Stairs.Shape.OUTER_RIGHT || f == BlockFace.WEST && s == Stairs.Shape.OUTER_LEFT || f == BlockFace.SOUTH && s == Stairs.Shape.INNER_RIGHT || f == BlockFace.WEST && s == Stairs.Shape.INNER_LEFT) seat = SMP.getInstance().getSitManager().createSeat(Block, Player, false, -ISitManager.STAIR_XZ_OFFSET, -ISitManager.STAIR_Y_OFFSET, ISitManager.STAIR_XZ_OFFSET, 45f, true, SMP.getInstance().GET_UP_SNEAK);
            else if(f == BlockFace.SOUTH && s == Stairs.Shape.OUTER_LEFT || f == BlockFace.EAST && s == Stairs.Shape.OUTER_RIGHT || f == BlockFace.SOUTH && s == Stairs.Shape.INNER_LEFT || f == BlockFace.EAST && s == Stairs.Shape.INNER_RIGHT) seat = SMP.getInstance().getSitManager().createSeat(Block, Player, false, ISitManager.STAIR_XZ_OFFSET, -ISitManager.STAIR_Y_OFFSET, ISitManager.STAIR_XZ_OFFSET, -45f, true, SMP.getInstance().GET_UP_SNEAK);
        }

        return seat;
    }

}