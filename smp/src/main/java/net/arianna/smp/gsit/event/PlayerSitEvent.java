package net.arianna.smp.gsit.event;

import net.arianna.smp.gsit.objects.GSeat;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerSitEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GSeat s;

    public PlayerSitEvent(GSeat Seat) {
        super(Seat.getPlayer());
        s = Seat;
    }

    public GSeat getSeat() { return s; }

    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

}