package net.arianna.smp.gsit.util;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Player;

public class PlayerUtil implements IPlayerUtil {

    public void teleport(Player P, Location L) { }
    public void teleport(Player P, Location L, boolean D) { }
    public void pos(org.bukkit.entity.Entity E, Location L) {
        try {
            ((CraftEntity)E).getHandle().setPos(L.getX(), L.getY(), L.getZ());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}