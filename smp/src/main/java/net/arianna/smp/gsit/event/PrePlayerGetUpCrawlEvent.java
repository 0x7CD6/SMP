package net.arianna.smp.gsit.event;

import net.arianna.smp.gsit.objects.GetUpReason;
import net.arianna.smp.gsit.objects.IGCrawl;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PrePlayerGetUpCrawlEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancel = false;

    private final IGCrawl c;

    private final GetUpReason r;

    public PrePlayerGetUpCrawlEvent(IGCrawl Crawl, GetUpReason Reason) {
        super(Crawl.getPlayer());
        c = Crawl;
        r = Reason;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public IGCrawl getCrawl() { return c; }

    public GetUpReason getReason() { return r; }

    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

}