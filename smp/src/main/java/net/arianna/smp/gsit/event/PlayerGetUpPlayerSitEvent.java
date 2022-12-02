package net.arianna.smp.gsit.event;

import net.arianna.smp.gsit.objects.GetUpReason;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerGetUpPlayerSitEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GetUpReason r;

    public PlayerGetUpPlayerSitEvent(Player Player, GetUpReason Reason) {
        super(Player);
        r = Reason;
    }

    public GetUpReason getReason() { return r; }

    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

}