package net.arianna.smp.gsit.event;

import net.arianna.smp.gsit.objects.IGCrawl;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerCrawlEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final IGCrawl c;

    public PlayerCrawlEvent(IGCrawl Crawl) {
        super(Crawl.getPlayer());
        c = Crawl;
    }

    public IGCrawl getCrawl() { return c; }

    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

}