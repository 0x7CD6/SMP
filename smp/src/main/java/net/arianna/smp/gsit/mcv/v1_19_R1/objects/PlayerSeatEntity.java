package net.arianna.smp.gsit.mcv.v1_19_R1.objects;

import net.minecraft.world.entity.AreaEffectCloud;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_19_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

public class PlayerSeatEntity extends AreaEffectCloud {

    public PlayerSeatEntity(Location Location) {
        super(((CraftWorld) Location.getWorld()).getHandle(), Location.getX(), Location.getY(), Location.getZ());
        setRadius(0);
        setNoGravity(true);
        setInvulnerable(true);
        setDuration(Integer.MAX_VALUE);
        setParticle(CraftParticle.toNMS(Particle.BLOCK_CRACK, Material.AIR.createBlockData()));
        setWaitTime(0);
    }

    public boolean canChangeDimensions() { return false; }

    public boolean isPushable() { return false; }

    public boolean rideableUnderWater() { return true; }

}