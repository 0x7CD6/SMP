package net.arianna.smp.gsit.manager;

import net.arianna.smp.gsit.objects.GetUpReason;
import net.arianna.smp.gsit.objects.IGPoseSeat;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.List;

public interface IPoseManager {

    int getFeatureUsedCount();

    void resetFeatureUsedCount();

    List<IGPoseSeat> getPoses();

    boolean isPosing(Player Player);

    IGPoseSeat getPose(Player Player);

    void clearPoses();

    boolean kickPose(Block Block, Player Player);

    IGPoseSeat createPose(Block Block, Player Player, Pose Pose);

    IGPoseSeat createPose(Block Block, Player Player, Pose Pose, double XOffset, double YOffset, double ZOffset, float SeatRotation, boolean SitAtBlock, boolean GetUpSneak);

    boolean removePose(IGPoseSeat PoseSeat, GetUpReason Reason);

    boolean removePose(IGPoseSeat PoseSeat, GetUpReason Reason, boolean Safe);

}