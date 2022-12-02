package net.arianna.smp.gsit.manager;

import net.arianna.smp.SMP;
import net.arianna.smp.UtilColor;
import net.arianna.smp.gsit.event.PlayerGetUpPlayerSitEvent;
import net.arianna.smp.gsit.event.PlayerPlayerSitEvent;
import net.arianna.smp.gsit.event.PrePlayerGetUpPlayerSitEvent;
import net.arianna.smp.gsit.event.PrePlayerPlayerSitEvent;
import net.arianna.smp.gsit.objects.GetUpReason;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerSitManager implements IPlayerSitManager {
    private int feature_used = 0;

    public int getFeatureUsedCount() { return feature_used; }

    public void resetFeatureUsedCount() { feature_used = 0; }

    public boolean sitOnPlayer(Player Player, Player Target) {

        PrePlayerPlayerSitEvent pplapse = new PrePlayerPlayerSitEvent(Player, Target);

        Bukkit.getPluginManager().callEvent(pplapse);

        if(pplapse.isCancelled()) return false;

        if(!SMP.getInstance().getSpawnUtil().checkPlayerLocation(Target)) return false;

        Entity sa = SMP.getInstance().getSpawnUtil().createPlayerSeatEntity(Target, Player);

        Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(UtilColor.blue + "Emotes: " + UtilColor.yellow + "Press L.Control to stand up!"));

        sa.setMetadata("Arcane" + "A", new FixedMetadataValue(SMP.getInstance(), Player));

        feature_used++;

        Bukkit.getPluginManager().callEvent(new PlayerPlayerSitEvent(Player, Target));

        return true;
    }

    public boolean stopPlayerSit(Entity Entity, GetUpReason Reason) {

        if(Entity instanceof Player) {

            PrePlayerGetUpPlayerSitEvent pplagupse = new PrePlayerGetUpPlayerSitEvent((Player) Entity, Reason);

            Bukkit.getPluginManager().callEvent(pplagupse);

            if(pplagupse.isCancelled()) return false;

        }

        if(Entity.hasMetadata("Arcane" + "A")) {
            Entity.eject();
            Entity.remove();
        }

        for(Entity e : Entity.getPassengers()) {
            if(e.hasMetadata("Arcane" + "A")) {
                e.eject();
                e.remove();
            }
        }

        if(Entity.isInsideVehicle()) {
            Entity e = Entity.getVehicle();
            if(e.hasMetadata("Arcane" + "A")) {
                e.eject();
                e.remove();
            }
        }

        if(Entity instanceof Player) Bukkit.getPluginManager().callEvent(new PlayerGetUpPlayerSitEvent((Player) Entity, Reason));

        return true;
    }

}