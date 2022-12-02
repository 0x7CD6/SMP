package net.arianna.smp.gsit.util;

import net.arianna.smp.SMP;
import net.arianna.smp.gsit.objects.IGPoseSeat;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;

public class PoseUtil {
    public boolean isPoseBlock(Block B) { return B.hasMetadata("Arcane" + "P"); }

    @SuppressWarnings("unchecked")
    public List<IGPoseSeat> getPoses(Block B) {
        List<IGPoseSeat> poses = new ArrayList<>();
        if(isPoseBlock(B)) {
            MetadataValue m = B.getMetadata("Arcane" + "P").stream().filter(s -> SMP.getInstance().equals(s.getOwningPlugin())).findFirst().orElse(null);
            if(m != null) poses = new ArrayList<>((List<IGPoseSeat>) m.value());
        }
        return poses;
    }

    public List<IGPoseSeat> getPoses(List<Block> B) {
        List<IGPoseSeat> poses = new ArrayList<>();
        for(Block b : B) for(IGPoseSeat c : getPoses(b)) if(!poses.contains(c)) poses.add(c);
        return poses;
    }

    public void setPoseBlock(Block B, IGPoseSeat P) {
        List<IGPoseSeat> poses = getPoses(B);
        if(!poses.contains(P)) poses.add(P);
        B.setMetadata("Arcane" + "P", new FixedMetadataValue(SMP.getInstance(), poses));
    }

    public void removePoseBlock(Block B, IGPoseSeat P) {
        List<IGPoseSeat> poses = getPoses(B);
        poses.remove(P);
        if(poses.size() > 0) B.setMetadata("Arcane" + "P", new FixedMetadataValue(SMP.getInstance(), poses));
        else B.removeMetadata("Arcane" + "P", SMP.getInstance());
    }

}