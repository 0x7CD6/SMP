package net.arianna.smp.gsit.manager;

import net.arianna.smp.gsit.objects.GetUpReason;
import net.arianna.smp.gsit.objects.IGCrawl;
import org.bukkit.entity.Player;

import java.util.List;

public interface ICrawlManager {

    int getFeatureUsedCount();

    void resetFeatureUsedCount();

    List<IGCrawl> getCrawls();

    boolean isCrawling(Player Player);

    IGCrawl getCrawl(Player Player);

    void clearCrawls();

    IGCrawl startCrawl(Player Player);

    boolean stopCrawl(IGCrawl Crawl, GetUpReason Reason);

}