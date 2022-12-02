package net.arianna.smp.gsit.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IPlayerUtil {

    void teleport(Player P, Location L);

    void teleport(Player P, Location L, boolean D);

    void pos(Entity E, Location L);

}