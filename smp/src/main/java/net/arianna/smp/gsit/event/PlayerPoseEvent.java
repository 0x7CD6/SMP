package net.arianna.smp.gsit.event;

import net.arianna.smp.gsit.objects.IGPoseSeat;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPoseEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final IGPoseSeat p;

    public PlayerPoseEvent(IGPoseSeat PoseSeat) {
        super(PoseSeat.getSeat().getPlayer());
        p = PoseSeat;
    }

    public IGPoseSeat getPoseSeat() { return p; }

    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

}