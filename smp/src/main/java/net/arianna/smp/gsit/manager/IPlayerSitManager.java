package net.arianna.smp.gsit.manager;

import net.arianna.smp.gsit.objects.GetUpReason;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IPlayerSitManager {

    int getFeatureUsedCount();

    void resetFeatureUsedCount();

    boolean sitOnPlayer(Player Player, Player Target);

    boolean stopPlayerSit(Entity Entity, GetUpReason Reason);

}