package net.arianna.smp.gsit.manager;

import net.arianna.smp.SMP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToggleManager {

    private final File TData = new File("plugins/GSit", "data/t.data");

    private final FileConfiguration TD = YamlConfiguration.loadConfiguration(TData);

    private final List<UUID> t = new ArrayList<>();

    private final List<UUID> pt = new ArrayList<>();

    private BukkitRunnable r;


    public boolean canSit(UUID U) {
        return SMP.getInstance().S_DEFAULT_SIT_MODE != t.contains(U);
    }

    public boolean canPlayerSit(UUID U) {
        return SMP.getInstance().PS_DEFAULT_SIT_MODE != pt.contains(U);
    }

    public void setCanSit(UUID U, boolean T) {
        if ((T && SMP.getInstance().S_DEFAULT_SIT_MODE) || (!T && !SMP.getInstance().S_DEFAULT_SIT_MODE)) {
            t.remove(U);
        } else {
            t.add(U);
        }
    }

    public void setCanPlayerSit(UUID U, boolean P) {
        if ((P && SMP.getInstance().PS_DEFAULT_SIT_MODE) || (!P && !SMP.getInstance().PS_DEFAULT_SIT_MODE)) {
            pt.remove(U);
        } else {
            pt.add(U);
        }
    }


    public void loadToggleData() {
        t.clear();
        pt.clear();
        for (String z : TD.getStringList("T")) t.add(UUID.fromString(z));
        for (String z : TD.getStringList("P")) pt.add(UUID.fromString(z));
        startAutoSave();
    }

    public void saveToggleData() {
        stopAutoSave();
        quickSaveToggleData();
    }

    private void quickSaveToggleData() {
        TD.set("T", null);
        TD.set("P", null);
        List<String> tc = new ArrayList<>();
        for (UUID z : t) tc.add(z.toString());
        TD.set("T", tc);
        List<String> pc = new ArrayList<>();
        for (UUID z : pt) pc.add(z.toString());
        TD.set("P", pc);
        saveFile(TData, TD);
    }

    private void startAutoSave() {
        stopAutoSave();
        r = new BukkitRunnable() {
            @Override
            public void run() {
                quickSaveToggleData();
            }
        };
        long t = 20 * 180;
        r.runTaskTimerAsynchronously(SMP.getInstance(), t, t);
    }

    private void stopAutoSave() {
        if (r != null) r.cancel();
    }

    private void saveFile(File f, FileConfiguration fc) {
        try {
            fc.save(f);
        } catch (IOException ignored) {
        }
    }
}
