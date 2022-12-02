package net.arianna.smp.gsit.event;

import net.arianna.smp.gsit.objects.GetUpReason;
import net.arianna.smp.gsit.objects.IGPoseSeat;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerGetUpPoseEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final IGPoseSeat p;

    private final GetUpReason r;

    public PlayerGetUpPoseEvent(IGPoseSeat PoseSeat, GetUpReason Reason) {
        super(PoseSeat.getSeat().getPlayer());
        p = PoseSeat;
        r = Reason;
    }

    public IGPoseSeat getPoseSeat() { return p; }

    public GetUpReason getReason() { return r; }

    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

}