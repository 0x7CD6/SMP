package net.arianna.smp.gsit.mcv.v1_19_R1.manager;

import net.arianna.smp.gsit.event.PlayerCrawlEvent;
import net.arianna.smp.gsit.event.PrePlayerGetUpCrawlEvent;
import net.arianna.smp.gsit.manager.ICrawlManager;
import net.arianna.smp.gsit.mcv.v1_19_R1.objects.GCrawl;
import net.arianna.smp.gsit.objects.GetUpReason;
import net.arianna.smp.gsit.event.PlayerGetUpCrawlEvent;
import net.arianna.smp.gsit.event.PrePlayerCrawlEvent;
import net.arianna.smp.gsit.objects.IGCrawl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrawlManager implements ICrawlManager {
    private int feature_used = 0;

    public int getFeatureUsedCount() { return feature_used; }

    public void resetFeatureUsedCount() { feature_used = 0; }

    private final List<IGCrawl> crawls = new ArrayList<>();

    public List<IGCrawl> getCrawls() { return new ArrayList<>(crawls); }

    public boolean isCrawling(Player Player) { return getCrawl(Player) != null; }

    public IGCrawl getCrawl(Player Player) {
        for(IGCrawl s : getCrawls()) if(Player.equals(s.getPlayer())) return s;
        return null;
    }

    public void clearCrawls() { for(IGCrawl c : getCrawls()) stopCrawl(c, GetUpReason.PLUGIN); }

    public IGCrawl startCrawl(Player Player) {

        PrePlayerCrawlEvent pplace = new PrePlayerCrawlEvent(Player);

        Bukkit.getPluginManager().callEvent(pplace);

        if(pplace.isCancelled()) return null;

        IGCrawl crawl = new GCrawl(Player);

        crawl.start();

        crawls.add(crawl);

        feature_used++;

        Bukkit.getPluginManager().callEvent(new PlayerCrawlEvent(crawl));

        return crawl;
    }

    public boolean stopCrawl(IGCrawl Crawl, GetUpReason Reason) {

        PrePlayerGetUpCrawlEvent pplaguce = new PrePlayerGetUpCrawlEvent(Crawl, Reason);

        Bukkit.getPluginManager().callEvent(pplaguce);

        if(pplaguce.isCancelled()) return false;

        crawls.remove(Crawl);

        Crawl.stop();

        Bukkit.getPluginManager().callEvent(new PlayerGetUpCrawlEvent(Crawl, Reason));

        return true;
    }

}