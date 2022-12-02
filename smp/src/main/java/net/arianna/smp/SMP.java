package net.arianna.smp;

import net.arianna.smp.gsit.events.BlockEvents;
import net.arianna.smp.gsit.events.InteractEvents;
import net.arianna.smp.gsit.events.PlayerEvents;
import net.arianna.smp.gsit.events.PlayerSitEvents;
import net.arianna.smp.gsit.manager.*;
import net.arianna.smp.gsit.mcv.v1_19_R1.manager.CrawlManager;
import net.arianna.smp.gsit.mcv.v1_19_R1.manager.PoseManager;
import net.arianna.smp.gsit.objects.GetUpReason;
import net.arianna.smp.gsit.util.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public final class SMP extends JavaPlugin {

    /**
     * This is the main class of the plugin. It is the entry point for the plugin.
     * 
     * This was a project for a Minecraft SMP servers for a couple of old friends of mine.
     * This was the last version of the code and is not fully finished however is fully functional excluding a few webhook urls missing etc... 
     * I'm not sure if I'll ever finish this project, but I'm putting it up here for posterity.
     * 
     * It's not clean code, but it's not terrible either. I'm not sure if I'll ever come back to this.
     * Only reason it's not clean is because it was a personal project for a couple of friends so I didn't really care much about the quality of the code.
     * 
     * This code is licensed under the MIT license.
     * @Author 0x7CD6 (Arianna)
     * @Version 0.0.1
     */

    public boolean GET_UP_DAMAGE = false;
    public boolean GET_UP_SNEAK = true;
    public boolean GET_UP_RETURN = false;
    public boolean GET_UP_BREAK = true;
    public boolean ALLOW_UNSAFE = false;
    public boolean SAME_BLOCK_REST = false;
    public boolean CENTER_BLOCK = false;
    public final HashMap<Material, Double> S_SITMATERIALS = new HashMap<>();
    public boolean S_EMPTY_HAND_ONLY = true;
    public double S_MAX_DISTANCE = 0.0;
    public boolean S_SIT_MESSAGE = false;
    public boolean S_DEFAULT_SIT_MODE = true;
    public boolean PS_ALLOW_SIT = true;
    public boolean PS_ALLOW_SIT_NPC = false;
    public long PS_MAX_STACK = 0;
    public boolean PS_SNEAK_EJECTS = false;
    public boolean PS_EMPTY_HAND_ONLY = true;
    public boolean PS_SIT_MESSAGE = false;
    public boolean PS_DEFAULT_SIT_MODE = true;
    public boolean P_POSE_MESSAGE = false;
    public boolean P_INTERACT=false;
    public boolean P_LAY_REST = true;
    public boolean P_LAY_SNORING_SOUNDS = true;
    public boolean P_LAY_SNORING_NIGHT_ONLY = true;
    public boolean P_LAY_NIGHT_SKIP = false;
    public boolean C_GET_UP_SNEAK = true;
    private static SMP SMP;
    public static SMP getInstance() { return SMP; }
    private ISitManager sitmanager;
    public ISitManager getSitManager() { return sitmanager; }
    private IPoseManager posemanager;
    public IPoseManager getPoseManager() { return posemanager; }
    private IPlayerSitManager playersitmanager;
    public IPlayerSitManager getPlayerSitManager() { return playersitmanager; }
    private ICrawlManager crawlmanager;
    public ICrawlManager getCrawlManager() { return crawlmanager; }
    private ToggleManager togglemanager;
    public ToggleManager getToggleManager() { return togglemanager; }
    private PassengerUtil passengerutil;
    public PassengerUtil getPassengerUtil() { return passengerutil; }
    private SitUtil situtil;
    public SitUtil getSitUtil() { return situtil; }
    private PoseUtil poseutil;
    public PoseUtil getPoseUtil() { return poseutil; }
    private ISpawnUtil spawnutil;
    public ISpawnUtil getSpawnUtil() { return spawnutil; }
    private IPlayerUtil playerutil;
    public IPlayerUtil getPlayerUtil() { return playerutil; }


    // This is used for the UHC alert every 5 minutes
    Timer timer = new Timer();
    Timer dragonCrystals = new Timer();
    Timer witherHealth = new Timer();
    static boolean dev = false;

    public void onEnable() {
        if (new CustomConfig("server_startup").get().getBoolean("dev_mode")) {
            dev = true;
            SMP = this;
            new Hearts();
            Bukkit.getPluginManager().registerEvents(new Chat(), this);
            Bukkit.getPluginManager().registerEvents(new JoinLeave(), this);
            Bukkit.getPluginManager().registerEvents(new RegisterDeath(), this);
            Bukkit.getPluginManager().registerEvents(new Vanish(), this);
            Bukkit.getPluginManager().registerEvents(new Teams(), this);
            Bukkit.getPluginManager().registerEvents(new AchievementUnlock(), this);
            Bukkit.getPluginManager().registerEvents(new Webhook(), this);
            Bukkit.getPluginManager().registerEvents(new ScoreboardTeams(), this);
            Bukkit.getPluginManager().registerEvents(new BossesRegister(), this);
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            new Teams().registerTeams(board);
            Bukkit.getOnlinePlayers().forEach(player -> {
                new Teams().registerTeamsToPlayers(player, board);
                System.out.println(player.getName());
            });
            Objects.requireNonNull(getCommand("prefix")).setExecutor(new ImmortalPrefixCommand());
            Objects.requireNonNull(getCommand("taunt")).setExecutor(new Taunt());
            Objects.requireNonNull(getCommand("gm")).setExecutor(new GamemodeCommand());
            Objects.requireNonNull(getCommand("uhc")).setExecutor(new UHCCommand());
            Objects.requireNonNull(getCommand("setlevel")).setExecutor(new LevelsCommand());
            Objects.requireNonNull(getCommand("teleport")).setExecutor(new TeleportCommand());
            Objects.requireNonNull(getCommand("achievements")).setExecutor(new AchievementCommand());
            Objects.requireNonNull(getCommand("vanish")).setExecutor(new Vanish());
            Objects.requireNonNull(getCommand("teams")).setExecutor(new Teams());

            Objects.requireNonNull(getCommand("bosstest_phase1")).setExecutor(new BossesRegister.newBoss());
        } else {
            SMP = this;
            new ToggleManager().loadToggleData();
            posemanager = new PoseManager();
            crawlmanager = new CrawlManager();
            spawnutil = new SpawnUtil();
            playerutil = new PlayerUtil();

            playersitmanager = new PlayerSitManager();
            togglemanager = new ToggleManager();
            passengerutil = new PassengerUtil();
            situtil = new SitUtil();
            poseutil = new PoseUtil();

            sitmanager = new SitManager();

            new Hearts();
            Bukkit.getPluginManager().registerEvents(new Chat(), this);
            Bukkit.getPluginManager().registerEvents(new JoinLeave(), this);
            Bukkit.getPluginManager().registerEvents(new RegisterDeath(), this);
            Bukkit.getPluginManager().registerEvents(new Vanish(), this);
            Bukkit.getPluginManager().registerEvents(new Teams(), this);
            Bukkit.getPluginManager().registerEvents(new AchievementUnlock(), this);
            Bukkit.getPluginManager().registerEvents(new Webhook(), this);
            Bukkit.getPluginManager().registerEvents(new ScoreboardTeams(), this);
            Bukkit.getPluginManager().registerEvents(new BossesRegister(), this);
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            new Teams().registerTeams(board);
            Bukkit.getOnlinePlayers().forEach(player -> {
                new Teams().registerTeamsToPlayers(player, board);
                System.out.println(player.getName());
            });


            getServer().getPluginManager().registerEvents(new PlayerEvents(), getInstance());
            getServer().getPluginManager().registerEvents(new PlayerSitEvents(), getInstance());
            getServer().getPluginManager().registerEvents(new BlockEvents(), getInstance());
            getServer().getPluginManager().registerEvents(new InteractEvents(), getInstance());

//        getServer().getPluginManager().registerEvents(new LeashEvents(), getInstance());

//        Bukkit.getPluginManager().registerEvents(new Scoreboard(), this);
            Objects.requireNonNull(getCommand("prefix")).setExecutor(new ImmortalPrefixCommand());
            Objects.requireNonNull(getCommand("taunt")).setExecutor(new Taunt());
            Objects.requireNonNull(getCommand("gm")).setExecutor(new GamemodeCommand());
            Objects.requireNonNull(getCommand("uhc")).setExecutor(new UHCCommand());
            Objects.requireNonNull(getCommand("setlevel")).setExecutor(new LevelsCommand());
            Objects.requireNonNull(getCommand("teleport")).setExecutor(new TeleportCommand());
            Objects.requireNonNull(getCommand("achievements")).setExecutor(new AchievementCommand());
            Objects.requireNonNull(getCommand("vanish")).setExecutor(new Vanish());
            Objects.requireNonNull(getCommand("teams")).setExecutor(new Teams());

            Objects.requireNonNull(getCommand("sit")).setExecutor(new GSitRemake.SitEmote());
            Objects.requireNonNull(getCommand("lay")).setExecutor(new GSitRemake.LayEmote());
            Objects.requireNonNull(getCommand("crawl")).setExecutor(new GSitRemake.CrawlEmote());
            Objects.requireNonNull(getCommand("bellyflop")).setExecutor(new GSitRemake.BellyFlopEmote());
            Objects.requireNonNull(getCommand("spin")).setExecutor(new GSitRemake.SpinEmote());

            timer.schedule(new TimerTask() {
                public void run() {

                    TextComponent advancement = new TextComponent(UtilColor.white + UtilColor.bold + " A game of " + UtilColor.yellow + UtilColor.bold + "UHC Remastered" + UtilColor.white + UtilColor.bold + " is about to start! ");
                    HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(UtilColor.blue + "Worst game ever btw... ngl").create());
                    advancement.setHoverEvent(hoverEvent);

                    Bukkit.broadcastMessage(" ");
                    Bukkit.getOnlinePlayers().forEach(p -> p.spigot().sendMessage(advancement));
                    Bukkit.broadcastMessage(" ");
                    Bukkit.getOnlinePlayers().forEach(p1 -> p1.playNote(p1.getLocation(), Instrument.PLING, Note.sharp(1, Note.Tone.E)));
                }
            }, 0L, 300000L);

            List<Location> crystals = new ArrayList<>();

            crystals.add(new Location(Bukkit.getWorld("world_the_end"), 33, 83, -25));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), 42, 89, 0));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), 33, 98, 24));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), 12, 80, 39));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), -13, 101, 39));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), -34, 95, 24));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), -42, 104, -1));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), -34, 77, -25));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), -13, 92, -40));
            crystals.add(new Location(Bukkit.getWorld("world_the_end"), 12, 86, -40));

            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getInstance(),() -> {
                if (Bukkit.getWorld("world_the_end").getPlayers().isEmpty()) return;
                if (Bukkit.getWorld("world_the_end").getEnderDragonBattle().getEnderDragon() == null) return;
                if (Bukkit.getWorld("world_the_end").getEnderDragonBattle().getEnderDragon().isDead()) return;


                crystals.forEach(location -> location.getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL));
            }, 0, (1 * 20 * 65));
        }
    }

    public void onDisable() {
        if (new CustomConfig("server_startup").get().getBoolean("dev_mode")) System.out.println("disabled");
        else {
            timer.cancel();
            getSitManager().clearSeats();
            if(getPoseManager() != null) getPoseManager().clearPoses();
            if(getCrawlManager() != null) getCrawlManager().clearCrawls();
            togglemanager.saveToggleData();
            System.out.println("disabled");
        }
    }

    class CollisionTeam {
        public Scoreboard board;
        public Team team;
        public CollisionTeam() {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();
            Team team = board.registerNewTeam("NoCollision");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            this.team = team;
            this.board = board;
        }
        public Team getTeam() {return team;}
        public Scoreboard getBoard() {return board;}
    }

    class LeashEvents implements  Listener {
        public List<String> leashed = new ArrayList<>();
        public List<String> leashers = new ArrayList<>();
        public CollisionTeam team = new CollisionTeam();

        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {
            if (getLeashed().contains(event.getEntity().getUniqueId().toString())) {
                Player leashedPlayer = event.getEntity();
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasMetadata(leashedPlayer.getUniqueId().toString())) unleashPlayer(leashedPlayer, players);
                }
            }
        }

        @EventHandler
        public void onLeashEvent(PlayerInteractEntityEvent event) {
            final Player player = event.getPlayer();
            EquipmentSlot slot = event.getHand();
            if (slot.equals(EquipmentSlot.HAND) && event.getRightClicked() instanceof Player) {
                final Player leashedPlayer = (Player) event.getRightClicked();
                if (!getLeashed().contains(leashedPlayer.getUniqueId().toString())) {
                    if (player.getInventory().getItemInMainHand().getType() == Material.LEAD) {
                        leashed.add(leashedPlayer.getUniqueId().toString());
                        leashers.add(player.getUniqueId().toString());
                        if (player.getGameMode() != GameMode.CREATIVE) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        Slime slime = (Slime) leashedPlayer.getWorld().spawnEntity(leashedPlayer.getLocation().add(0.0D, 1.0D, 0.0D), EntityType.SLIME);
                        slime.setSize(0);
                        slime.setAI(false);
                        slime.setMetadata(leashedPlayer.getUniqueId().toString(), new FixedMetadataValue(getInstance(), "NoCollision"));
                        slime.setGravity(false);
                        slime.setLeashHolder(player);
                        slime.setInvulnerable(true);
                        slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 999999));

                        team.getTeam().addEntry(slime.getUniqueId().toString());
                        leashedPlayer.setScoreboard(team.getBoard());
                        player.setMetadata(leashedPlayer.getUniqueId().toString(), new FixedMetadataValue(getInstance(), "Holder"));
                        (new BukkitRunnable() { public void run() {
                                if (!LeashEvents.this.getLeashed().contains(leashedPlayer.getUniqueId().toString())) cancel();
                                if (player.getLocation().distanceSquared(leashedPlayer.getLocation()) > 10.0D) leashedPlayer.setVelocity(player.getLocation().toVector().subtract(leashedPlayer.getLocation().toVector()).multiply(0.05D));
                        }}).runTaskTimer(getInstance(), 0L, 0L);
                    }
                } else unleashPlayer(leashedPlayer, player);
            }
        }

        @EventHandler
        public void onPlayerMoveEvent(PlayerMoveEvent event) {
            if (getLeashed().contains(event.getPlayer().getUniqueId().toString())) {
                Player leashedPlayer = event.getPlayer();
                for (Entity entities : leashedPlayer.getNearbyEntities(5.0D, 5.0D, 5.0D)) {
                    if (entities instanceof Slime && entities.hasMetadata(leashedPlayer.getUniqueId().toString())) {
                        Slime slime = (Slime)entities;
                        slime.teleport(leashedPlayer.getLocation().add(0.0D, 1.0D, 0.0D));
                    }
                }
            }
        }

        @EventHandler
        public void onPlayerDeathEvent(PlayerDeathEvent event) {
            if (getLeashed().contains(event.getEntity().getUniqueId().toString())) {
                Player leashedPlayer = event.getEntity();
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasMetadata(leashedPlayer.getUniqueId().toString()))
                        unleashPlayer(leashedPlayer, players);
                }
            }
        }

        @EventHandler
        public void onPlayerQuitEvent(PlayerQuitEvent event) {
            if (getLeashed().contains(event.getPlayer().getUniqueId().toString())) {
                Player leashedPlayer = event.getPlayer();
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasMetadata(leashedPlayer.getUniqueId().toString()))
                        unleashPlayer(leashedPlayer, players);
                }
            }
        }

        @EventHandler
        public void onHangingPlaceEvent(HangingPlaceEvent event) {
            if (!(event.getEntity() instanceof LeashHitch)) return;
            LeashHitch leash = (LeashHitch)event.getEntity();
            for (Entity entities : leash.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
                if (getLeashed().contains(entities.getUniqueId().toString())) event.setCancelled(true);
            }
        }

        public void unleashPlayer(Player leashedPlayer, Player leashHolder) {
            for (Entity entities : leashedPlayer.getNearbyEntities(1.0D, 1.0D, 1.0D)) {
                if (entities instanceof Slime && entities.hasMetadata(leashedPlayer.getUniqueId().toString())) {
                    Slime slime = (Slime)entities;
                    slime.setLeashHolder(null);
                    team.getTeam().removeEntry(slime.getUniqueId().toString());
                    slime.remove();
                    getLeashed().remove(leashedPlayer.getUniqueId().toString());
                    getLeashers().remove(leashHolder.getUniqueId().toString());
                    if (leashHolder.getGameMode() != GameMode.CREATIVE) leashHolder.getInventory().addItem(new ItemStack(Material.LEAD));
                }
            }
        }

        public List<String> getLeashed() {
            return this.leashed;
        }

        public List<String> getLeashers() {
            return this.leashers;
        }
    }

    class ScoreboardTeams implements Listener {

        public void update(Boolean leave) {
            Vanish vanish = new Vanish();

            AtomicReference<Integer> online = new AtomicReference<>(Bukkit.getOnlinePlayers().size());
            AtomicReference<Integer> onlineVanished = new AtomicReference<>(vanish.vanishedPlayers.size());

            Bukkit.getOnlinePlayers().forEach(player -> {
                CustomConfig config = new CustomConfig(player.getUniqueId().toString());
                Boolean vanishOnline = config.get().getBoolean("vanish");
                if (vanishOnline) onlineVanished.set(Math.addExact(onlineVanished.get(), 1));
            });

            Bukkit.getServer().getScheduler().runTaskLater(getInstance(), () -> {
                online.set(Math.subtractExact(online.get(), onlineVanished.get()));
                if (leave) online.set(online.get()-1);
                String header = UtilColor.aqua + UtilColor.bold + "◇ " + UtilColor.red + UtilColor.bold + "Beans SMP " + UtilColor.aqua + UtilColor.bold + "◇";
                String footer = UtilColor.aqua + UtilColor.bold + "◇ " + UtilColor.darkGreen + UtilColor.bold + "Online: " + UtilColor.yellow + online + UtilColor.aqua + UtilColor.bold + " ◇";
                Bukkit.getOnlinePlayers().forEach(p -> p.setPlayerListHeaderFooter(header, footer));
            }, 10L);
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            update(false);
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            update(true);
        }
    }
    class JoinLeave implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            Vanish vanish = new Vanish();
            if (vanish.isVanished(event.getPlayer())) event.setJoinMessage("");
            else if (event.getPlayer().getName().equalsIgnoreCase("Cutie")) event.setJoinMessage(UtilColor.aqua + UtilColor.bold + "[JOIN] " + UtilColor.darkAqua + event.getPlayer().getName() + " "  + UtilColor.purple + UtilColor.bold + "[CUTIE]");
            else event.setJoinMessage(UtilColor.aqua + UtilColor.bold + "[JOIN] " + UtilColor.darkAqua + event.getPlayer().getName());
            Config config = new Config(event.getPlayer().getUniqueId());
            if (getConfig().get(event.getPlayer().getUniqueId().toString()) == null) {
                config.setInt("immortal", 0);
                config.setInt("deaths", 0);
            }
        }

        @EventHandler
        public void onLeave(PlayerQuitEvent event) {
            Vanish vanish = new Vanish();
            if (vanish.isVanished(event.getPlayer())) event.setQuitMessage("");
            else if (event.getPlayer().getName().equalsIgnoreCase("cutie")) event.setQuitMessage(UtilColor.red + UtilColor.bold + "[QUIT] " + UtilColor.darkAqua + event.getPlayer().getName() + " "  + UtilColor.purple + UtilColor.bold + "[CUTIE]");
            else event.setQuitMessage(UtilColor.blue + "GWEN> " + UtilColor.yellow + event.getPlayer().getName() + UtilColor.gray + " has been removed for suspcious activity.");
                event.setQuitMessage(UtilColor.red + UtilColor.bold + "[QUIT] " + UtilColor.darkAqua + event.getPlayer().getName());
        }
    }

    static class CustomConfig {
        public CustomConfig(String name) {
            file = new File(Bukkit.getServer().getPluginManager().getPlugin("Gio").getDataFolder(), name + ".yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            customFile = YamlConfiguration.loadConfiguration(file);
        }
        public File file;
        public FileConfiguration customFile;


        public FileConfiguration get(){return customFile;}
        public void save(){
            try{
                customFile.save(file);
            }catch (IOException e){
                System.out.println("Couldn't save file");
            }
        }
        public void reload(){customFile = YamlConfiguration.loadConfiguration(file);}
    }

    class AchievementUnlock implements Listener {
        public String[][] advancements = new String[][] {

                // Base Achievements (Story-line)
                { "story/mine_stone", "Stone Age", "Mine stone with your new pickaxe." },
                { "story/upgrade_tools", "Getting an Upgrade", "Construct a better pickaxe." },
                { "story/smelt_iron", "Acquire Hardware", "Smelt an iron ingot." },
                { "story/obtain_armor", "Suit Up", "Protect yourself with a piece of iron armor." },
                { "story/lava_bucket", "Hot Stuff", "Fill a bucket with lava." },
                { "story/iron_tools", "Isn't It Iron Pick", "Upgrade your pickaxe to iron." },
                { "story/deflect_arrow", "Not Today, Thank You", "Block a projectile using your shield." },
                { "story/form_obsidian", "Ice Bucket Challenge", "Obtain a block of obsidian." },
                { "story/mine_diamond", "Diamonds!", "Get your hands on some of those diamonds." },
                { "story/enter_the_nether", "We Need to Go Deeper", "Build, light and enter a Nether Portal." },
                { "story/shiny_gear", "Cover Me With Diamonds", "Put on any type of Diamond Armor." },
                { "story/enchant_item", "Enchanter", "Enchant an item with any enchantment at an " + UtilColor.purple + "Enchantment Table" + UtilColor.yellow + "." },
                { "story/cure_zombie_villager", "Zombie Doctor", "Weaken and then cure a Zombie Villager." },
                { "story/follow_ender_eye", "Eye Spy", "Enter a Stronghold by following an Ender Eye." },
                { "story/enter_the_end", "The End?", "The end has begun." },

                // Nether
                { "nether/return_to_sender", "Return to Sender", "Kill a Ghast with their own fireball." },
                { "nether/find_bastion", "Those Were the Days", "Enter a Bastion Remnant." },
                { "nether/obtain_ancient_debris", "Hidden in the Depths", "Obtain Ancient Debris." },
                { "nether/fast_travel", "Subspace Bubble", "Use the Nether to travel 7 km in the Overworld." },
                { "nether/find_fortress", "A Terrible Fortress", "Enter a Nether Fortress." },
                { "nether/obtain_crying_obsidian", "Who is Cutting Onions?", "Get your hands on some of that Crying Obsidian." },
                { "nether/distract_piglin", "Oh Shiny", "Distract a Piglins with gold." },
                { "nether/ride_strider", "This Boat Has Legs", "Ride a Strider with a Warped Fungus on a Stick." },
                { "nether/uneasy_alliance", "Uneasy Alliance", "Rescue a Ghast from the Nether, bring it safely home to the Overworld... and then kill it." },
                { "nether/loot_bastion", "War Pigs", "Loot a chest in a Bastion Remnant." },
                { "nether/use_lodestone", "Country Lode, Take Me Home", "Use a compass on a Lodestone." },
                { "nether/netherite_armor", "Cover Me in Debris", "Get a full suit of Netherite armor." },
                { "nether/get_wither_skull", "Spooky Scary Skeleton", "Obtain a Wither Skeleton's skull." },
                { "nether/obtain_blaze_rod", "Into Fire", "Relieve a Blaze of its rod." },
                { "nether/charge_respawn_anchor", "Not Quite \"Nine\" Lives", "Charge a Respawn Anchor to the maximum." },
                { "nether/ride_strider_in_overworld_lava", "Feels Like Home", "Take a Strider for a loooong ride on a lava lake in the Overworld." },
                { "nether/explore_nether", "Hot Tourist Destinations", "Explore all Nether biomes." },
                { "nether/summon_wither", "Withering Heights", "Summon the Wither." },
                { "nether/brew_potion", "Local Brewery", "Brew a potion." },
                { "nether/create_beacon", "Bring Home the Beacon", "Construct and place a beacon." },
                { "nether/all_potions", "A Furious Cocktail", "Have every potion effect applied at the same time." },
                { "nether/create_full_beacon", "Beaconator", "Bring a beacon to full power." },
                { "nether/all_effects", "How Did We Get Here?", "Have every effect applied at the same time." },


                // End Achievements
                { "end/kill_dragon", "Free the End", "The End has been saved from enslavement!" },
                { "end/dragon_egg", "The Next Generation", "Hold the Dragon Egg." },
                { "end/enter_end_gateway", "Remote Getaway", "Escape the island." },
                { "end/respawn_dragon", "The End... Again...", "Respawn the Ender Dragon." },
                { "end/dragon_breath", "You Need a Mint", "Collect dragon's breath in a glass bottle." },
                { "end/find_end_city", "The City at the End of the Game", "Enter an end city." },
                { "end/elytra", "Sky's the Limit", "Get your hands on one of those flying Elytra." },
                { "end/levitate", "Great View From Up Here", "Levitate up 50 blocks from the attacks of a Shulker." },

                // Adventure Achievements
                { "adventure/voluntary_exile", "Voluntary Exile", "Kill a raid captain.\nMaybe consider staying away from villages for the time being..." },
                { "adventure/spyglass_at_parrot", "Is It a Bird?", "Look at a parrot through a spyglass, then reconsider your life choices." },
                { "adventure/kill_a_mob", "Monster Hunter", "Kill any hostile monster." },
                { "adventure/trade", "What a Deal!", "Successfully trade with a Villager." },
                { "adventure/honey_block_slide", "Sticky Situation", "Jump into a Honey Block to break your fall." },
                { "adventure/ol_betsy", "Ol' Betsy", "Shoot a crossbow." },
                { "adventure/lightning_rod_with_villager_no_fire", "Surge Protector", "Protect a villager from an undesired shock without starting a fire." },
                { "adventure/fall_from_world_height", "Caves & Cliffs", "Free fall from the top of the world (build limit) to the bottom of the world and survive." },
                { "adventure/avoid_vibration", "Sneak 100", "Sneak near a Sculk Sensor or Warden to prevent it from detecting you." },
                { "adventure/sleep_in_bed", "Sweet Dreams", "Sleep in a bed to change your respawn point." },
                { "adventure/hero_of_the_village", "Hero of the Village", "Successfully defend a village from a raid." },
                { "adventure/spyglass_at_ghast", "Is It a Balloon?", "Look at a Ghast through a spyglass." },
                { "adventure/throw_trident", "A Throwaway Joke", "Throw a trident at something.\nNote: Throwing away your only weapon is not a good idea." },
                { "adventure/kill_mob_near_sculk_catalyst", "It Spreads", "Kill a mob near a Sculk Catalyst." },
                { "adventure/shoot_arrow", "Take Aim", "Shoot something with an arrow." },
                { "adventure/kill_all_mobs", "Monsters Hunted", "Kill one of every hostile monster." },
                { "adventure/totem_of_undying", "Postmortal", "Use a Totem of Undying to cheat death." },
                { "adventure/summon_iron_golem", "Hired Help", "Summon an Iron Golem to help defend a village." },
                { "adventure/trade_at_world_height", "Star Trader", "Trade with a Villager at the build height limit." },
                { "adventure/two_birds_one_arrow", "Two Birds, One Arrow", "Kill two Phantoms with a piercing arrow." },
                { "adventure/whos_the_pillager_now", "Who's the Pillager Now?", "Kill a pillager with a crossbow." },
                { "adventure/arbalistic", "Arbalistic", "Kill five unique mobs with one crossbow shot." },
                { "adventure/adventuring_time", "Adventuring Time", "Discover every biome." },
                { "adventure/play_jukebox_in_meadows", "Sound of Music", "Make the Meadows come alive with the sound of music from a Jukebox." },
                { "adventure/walk_on_powder_snow_with_leather_boots", "Light as a Rabbit", "Walk on powder snow...without sinking in it." },
                { "adventure/spyglass_at_dragon", "Is It a Plane?", "Look at the Ender Dragon through a spyglass." },
                { "adventure/very_very_frightening", "Very Very Frightening", "Strike a Villager with lightning." },
                { "adventure/sniper_duel", "Sniper Duel", "Kill a Skeleton from at least 50 meters away." },
                { "adventure/bullseye", "Bullseye", "Hit the bullseye of a Target block from at least 30 meters away." },

                // Husbandry Achievements
                { "husbandry/safely_harvest_honey", "Bee Our Guest", "Use a Campfire to collect Honey from a Beehive using a Bottle without aggravating the bees." },
                { "husbandry/breed_an_animal", "The Parrots and the Bats", "Breed two animals together." },
                { "husbandry/allay_deliver_item_to_player", "You've Got a Friend in Me", "Have an Allay deliver items to you."},
                { "husbandry/ride_a_boat_with_a_goat", "Whatever Floats Your Goat!", "Get in a Boat and float with a Goat."},
                { "husbandry/tame_an_animal", "Best Friends Forever", "Tame an animal." },
                { "husbandry/make_a_sign_glow", "Glow and Behold!", "Make the text of a sign glow." },
                { "husbandry/fishy_business", "Fishy Business", "Catch a fish." },
                { "husbandry/silk_touch_nest", "Total Beelocation", "Move a Bee Nest, with 3 bees inside, using Silk Touch." },
                { "husbandry/tadpole_in_a_bucket", "Bukkit Bukkit", "Catch a Tadpole in a Bucket." },
                { "husbandry/plant_seed", "A Seedy Place", "Plant a seed and watch it grow." },
                { "husbandry/wax_on", "Wax On", "Apply Honeycomb to a Copper block!" },
                { "husbandry/bred_all_animals", "Two by Two", "Breed all the animals." },
                { "husbandry/allay_deliver_cake_to_note_block", "Birthday Song", "Have an Allay drop a Cake at a Note Block." },
                { "husbandry/complete_catalogue", "A Complete Catalogue", "Tame all cat variants." },
                { "husbandry/tactical_fishing", "Tactical Fishing", "Catch a fish... without a fishing rod!" },
                { "husbandry/leash_all_frog_variants", "When the Squad Hops into Town", "Get each Frog variant on a Lead." },
                { "husbandry/balanced_diet", "A Balanced Diet", "Eat everything that is edible, even if it's not good for you." },
                { "husbandry/obtain_netherite_hoe", "Serious Dedication", "Use a Netherite Ingot to upgrade a hoe, and then reevaluate your life choices." },
                { "husbandry/wax_off", "Wax Off", "Scrape Wax off of a Copper block!" },
                { "husbandry/axolotl_in_a_bucket", "The Cutest Predator", "Catch an Axolotl in a bucket!" },
                { "husbandry/froglights", "With Our Powers Combined!", "Have all Froglights in your inventory." },
                { "husbandry/kill_axolotl_target", "The Healing Power of Friendship!", "Team up with an axolotl and win a fight." }
        };

        public String find(String advancement) {
            int i;
            for (i = 0; i < advancements.length; i++) if (advancement.equals(advancements[i][0])) return advancements[i][1];
            return advancement;
        }

        public String findDescription(String advancement) {
            int i;
            for (i = 0; i < advancements.length; i++) if (advancement.equals(advancements[i][0])) return advancements[i][2];
            return advancement;
        }

        public boolean check(String adv) {
            if (adv.contains("root") || adv.contains("recipes")) return false;
            return true;
        }

        @EventHandler
        public void onRegisteredAchievement(PlayerAdvancementDoneEvent event) {
            if (check(event.getAdvancement().getKey().getKey())) {
                CustomConfig config = new CustomConfig(event.getPlayer().getUniqueId().toString());

                String title = find(event.getAdvancement().getKey().getKey());
                String description = findDescription(event.getAdvancement().getKey().getKey());
                SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                TextComponent advancement = new TextComponent(UtilColor.blue + "Advancement> " + UtilColor.yellow + event.getPlayer().getName() + UtilColor.gray + " has advanced through " + UtilColor.yellow + title + UtilColor.gray + ".");
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(UtilColor.blue + "[Advancement]\n" + UtilColor.gray +
                                "Name: " + UtilColor.yellow + title + UtilColor.gray + "\n" +
                                "Description: " + UtilColor.yellow + description + UtilColor.gray + "\n\n" +
                                "Unlocked by: " + UtilColor.yellow + event.getPlayer().getName() + UtilColor.gray + "\n" +
                                "Time Unlocked: " + UtilColor.yellow + time.format(timestamp) + " (GMT/BST)").create());
                advancement.setHoverEvent(hoverEvent);
                config.get().set("advancement." + title + ".timeunlock", time.format(timestamp) + " (GMT/BST)");
                config.get().set("advancement." + title + ".location.x", event.getPlayer().getLocation().getX());
                config.get().set("advancement." + title + ".location.y", event.getPlayer().getLocation().getY());
                config.get().set("advancement." + title + ".location.z", event.getPlayer().getLocation().getZ());
                config.save();


                Bukkit.getOnlinePlayers().forEach(p -> p.spigot().sendMessage(advancement));
            }
        }
    }

    class AchievementCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
            else {
//                for (String key : new CustomConfig(((Player)sender).getUniqueId().toString()).get().)
            }
            return false;
        }
    }

    static class Taunt implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
            else if (args.length != 1) sender.sendMessage(UtilColor.blue + "Taunt> " + UtilColor.gray + "/taunt [player]");
            else {
                Player player = ((Player)sender).getPlayer();
                Player args2 = Bukkit.getPlayer(args[0]);
                if (!Objects.requireNonNull(args2).isOnline()) sender.sendMessage(UtilColor.blue + "Taunt> " + UtilColor.gray + "/taunt [player]");
                else Bukkit.broadcastMessage(UtilColor.blue + "Taunt> " + UtilColor.yellow + Objects.requireNonNull(player).getName() + UtilColor.gray + " has blown a kiss at " + UtilColor.yellow + args2.getName() + UtilColor.gray + ".");
            }
            return false;
        }
    }

    class ImmortalPrefixCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
            else if (args.length == 0 || args.length == 3) sendHelp(sender);
            else {
                String color;
                if (args.length == 2) color = args[0].toLowerCase() + " " + args[1].toLowerCase();
                else color = args[0].toLowerCase();
                setColor(Objects.requireNonNull(((Player)sender).getPlayer()), color);
            }
            return false;
        }

        public void setColor(Player player, String color) {
            CustomConfig config = new CustomConfig(player.getUniqueId().toString());
            config.get().set("immortal", switchIntColor(color.toLowerCase()));
            config.save();
            player.sendMessage(UtilColor.blue + "Prefix Color> " + UtilColor.gray + "Your new prefix color is " + switchColor(color) + color);
        }

        public Integer switchIntColor(String color) {
            switch (color) {
                case "yellow": return 1;
                case "aqua": return 2;
                case "pink": return 3;
                case "green": return 4;
                case "red": return 5;
                case "dark aqua": return 6;
                case "black": return 7;
                case "white": return 8;
                case "blue": return 9;
                case "dark green": return 10;
                case "gray": return 11;
                case "dark gray": return 12;
            }
            return 0;
        }

        public String swapColor(Integer color) {
            switch (color) {
                case 1: return UtilColor.yellow;
                case 2: return UtilColor.aqua;
                case 3: return UtilColor.purple;
                case 4: return UtilColor.green;
                case 5: return UtilColor.red;
                case 6: return UtilColor.darkAqua;
                case 7: return UtilColor.black;
                case 8: return UtilColor.white;
                case 9: return UtilColor.darkBlue;
                case 10: return UtilColor.darkGreen;
                case 11: return UtilColor.gray;
                case 12: return UtilColor.darkGray;
            }
            return "";
        }

        public String switchColor(String color) {
            switch (color) {
                case "yellow": return UtilColor.yellow;
                case "aqua": return UtilColor.aqua;
                case "pink": return UtilColor.purple;
                case "green": return UtilColor.green;
                case "red": return UtilColor.red;
                case "dark aqua": return UtilColor.darkAqua;
                case "black": return UtilColor.black;
                case "white": return UtilColor.white;
                case "blue": return UtilColor.darkBlue;
                case "dark green": return UtilColor.darkGreen;
                case "gray": return UtilColor.gray;
                case "dark gray": return UtilColor.darkGray;
            }
            return "None";
        }

        public void sendHelp(CommandSender sender) {
            sender.sendMessage(UtilColor.blue + "Prefix Color> " + UtilColor.gray + "Your current prefix color is ");
            sender.sendMessage(UtilColor.blue + "Prefix Color> " + UtilColor.gray + "Options:");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.yellow + "Yellow");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.aqua + "Aqua");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.purple + "Pink");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.green + "Green");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.red + "Red");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.darkAqua + "Dark Aqua");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.black + "Black");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.white + "White");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.darkBlue + "Blue");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.darkGreen + "Dark Green");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.gray + "Gray");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.darkGray + "Dark Gray");
            sender.sendMessage(UtilColor.gray + "- " + UtilColor.white + "Reset");
        }
    }

    static class GamemodeCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
                Bukkit.getConsoleSender().sendMessage("no");
            } else {
                Player player = ((Player)sender).getPlayer();
                assert player != null;
                if (!player.isOp()) player.sendMessage("Unknown command. Type \"/help\" for help.");
                else {
                    switch (player.getGameMode()) {
                        case CREATIVE:
                            player.setGameMode(GameMode.SURVIVAL);
                            player.sendMessage(UtilColor.blue + "Gamemode> " + UtilColor.gray + "Gamemode: " + UtilColor.red + "False");
                            break;
                        case SURVIVAL:
                        case SPECTATOR:
                            player.setGameMode(GameMode.CREATIVE);
                            player.sendMessage(UtilColor.blue + "Gamemode> " + UtilColor.gray + "Gamemode: " + UtilColor.green + "True");
                            break;
                    }
                }
            }
            return false;
        }
    }

    static class UHCCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
            else {
                if (!sender.isOp()) return false;
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage(UtilColor.white + UtilColor.bold + " A game of " + UtilColor.yellow + UtilColor.bold + "UHC Remastered" + UtilColor.white + UtilColor.bold + " is about to start! ");
                Bukkit.broadcastMessage(" ");
                Bukkit.getOnlinePlayers().forEach(p1 -> p1.playNote(p1.getLocation(), Instrument.PLING, Note.sharp(1, Note.Tone.E)));
            }
            return false;
        }
    }

    static class MessageCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
            else if (args.length != 1) sender.sendMessage(UtilColor.blue + "Message> " + UtilColor.gray + "/m [player] <message>");
            else {
                Player player = ((Player)sender).getPlayer();
                assert player != null;
                Player args2 = Bukkit.getPlayer(args[0]);
                assert args2 != null;

                String receiverMsg = ChatColor.LIGHT_PURPLE + sender.getName() + " whispers: ";
                String senderMsg = ChatColor.LIGHT_PURPLE + "To " + args2.getName() + ": " ;

                for (int i = 1; i < args.length; i++) {
                    receiverMsg += args[i] +" ";
                    senderMsg += args[i] + " ";
                }
            }
            return false;
        }
    }

    class LevelsCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
            else {
                if (!sender.isOp()) return false;
                if (args.length != 2) sender.sendMessage(UtilColor.blue + "Levels> " + UtilColor.gray + "/setlevel [player] [0-inf..]");
                else {
                    String name = args[0];
                    int deaths = Integer.parseInt(args[1]);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                    CustomConfig config = new CustomConfig(player.getUniqueId().toString());
                    config.get().set("deaths", deaths);
                    config.save();
                    sender.sendMessage(UtilColor.blue + "Levels> " + UtilColor.gray + "set.");
                }
            }
            return false;
        }
    }

    // idk i never finished such a basic command lmao 
    static class ConsoleSendSayCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) Bukkit.getConsoleSender().sendMessage("no");
            else Bukkit.broadcastMessage(UtilColor.aqua + UtilColor.bold + "StaffRequest " + "Console ");

            return false;
        }
    }

    static class TeleportCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
            else if (sender.isOp()) {
                if (args.length == 1 || args.length == 2) {
                    if (args[0].equalsIgnoreCase("here")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) sender.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "Failed to locate [" + UtilColor.yellow + args[1] + UtilColor.gray + "].");
                        else if (target.getName().equalsIgnoreCase(sender.getName())) sender.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "It seems like you tried to teleport to yourself.");
                        else {
                            Location executorTargetLoc = ((Player)sender).getLocation();
                            target.teleport(executorTargetLoc);
                            sender.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "You have teleported " + UtilColor.yellow + args[1] + UtilColor.gray + " to yourself.");
                            target.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "You have been teleported to " + UtilColor.yellow + sender.getName() + UtilColor.gray + ".");
                        }
                    } else if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("@a")) {
                        sender.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "You have teleported " + UtilColor.yellow + "Everyone" + UtilColor.gray + " to yourself.");
                        Bukkit.getOnlinePlayers().forEach(target -> {
                            Location executorTargetLoc = ((Player)sender).getLocation();
                            target.teleport(executorTargetLoc);
                            target.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "You have been teleported to " + UtilColor.yellow + sender.getName() + UtilColor.gray + ".");
                        });
                    } else {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            sender.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "Failed to locate [" + UtilColor.yellow + args[1] + UtilColor.gray + "].");
                        } else if (args.length == 1) {
                            Location targetLocation = target.getLocation();
                            sender.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "You have teleported yourself to " + UtilColor.yellow + target.getName() + UtilColor.gray + ".");
                            ((Player)sender).teleport(targetLocation);
                        } else {
                            Player target2 = Bukkit.getPlayer(args[1]);
                            if (target2 == null) sender.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "It seems like " + UtilColor.yellow + args[1] + UtilColor.gray + " isn't online right now.");
                            else {
                                Location targetLocation = target2.getLocation();
                                sender.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "You have teleported " + UtilColor.yellow + target.getName() + UtilColor.gray + " to " + UtilColor.yellow + target2.getName() + UtilColor.gray + ".");
                                target.sendMessage(UtilColor.blue + "Teleport> " + UtilColor.gray + "You have been teleported to " + UtilColor.yellow + target2.getName() + UtilColor.gray + ".");
                                target.teleport(targetLocation);
                            }
                        }
                    }
                }
            }
            return false;
        }
    }

    class Config {
        UUID uuid;

        public Config(UUID uuid) {this.uuid = uuid;}

        public Object getProperty(String property) {return getConfig().get(uuid.toString() + "." + property);}

        public String getPropertyString(String property) {return Objects.requireNonNull(getConfig().get(uuid.toString() + "." + property)).toString();}

        public void setProperty(String property, String replacement) {getConfig().set(uuid.toString() + "." + property, replacement);}

        public void setInt(String property, Integer replacement) {
            getConfig().set(uuid.toString() + "." + property, replacement);
            saveConfig();
        }

        public void setString(String property, String replacement) {
            getConfig().set(uuid.toString() + "." + property, replacement);
            saveConfig();
        }
    }

    class Webhook implements Listener {

        DiscordWebhook webhook;
        DiscordWebhook.EmbedObject object;
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            if (dev) return;
            // Join - SMP Logs
            webhook = new DiscordWebhook("<webhook_here>");
            object = new DiscordWebhook.EmbedObject();
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            object.setAuthor("[Join]", "", "https://minotar.net/helm/" + event.getPlayer().getName() + "/256.png");
            object.addField("Username", event.getPlayer().getName(), true);
            object.addField("UUID", event.getPlayer().getUniqueId().toString(), true);
            object.addField("IP", event.getPlayer().getAddress().getHostString(), true);
            object.addField("Location Spawned", "X: " + event.getPlayer().getLocation().getX() + " Y: " + event.getPlayer().getLocation().getY() + " Z: " + event.getPlayer().getLocation().getZ(), false);
            object.addField("Time Joined", time.format(timestamp) + " (GMT/BST)", true);
            object.setColor(new java.awt.Color(76, 156, 45));
            webhook.addEmbed(object);

            try {
                webhook.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Vanish vanish = new Vanish();
            if (vanish.isVanished(event.getPlayer())) return;
            // Join - SMP Discord
            webhook = new DiscordWebhook("<webhook_here>");
            object = new DiscordWebhook.EmbedObject();

            object.setAuthor("[Join]", "", "https://minotar.net/helm/" + event.getPlayer().getName() + "/256.png");
            object.addField("Username", event.getPlayer().getName(), true);
            object.addField("UUID", event.getPlayer().getUniqueId().toString(), true);
//            object.addField("IP", event.getPlayer().getAddress().getHostString(), true);
            object.addField("Time Joined", time.format(timestamp) + " (GMT/BST)", true);
            object.setColor(new java.awt.Color(76, 156, 45));
            webhook.addEmbed(object);
            try {
                webhook.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @EventHandler
        public void onLeave(PlayerQuitEvent event) {
            if (dev) return;
            // SMP Logs
            webhook = new DiscordWebhook("<webhook_here>");
            object = new DiscordWebhook.EmbedObject();
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            object.setAuthor("[Quit]", "", "https://minotar.net/helm/" + event.getPlayer().getName() + "/256.png");
            object.addField("Username", event.getPlayer().getName(), true);
            object.addField("UUID", event.getPlayer().getUniqueId().toString(), true);
            object.addField("IP", event.getPlayer().getAddress().getHostString(), true);
            object.addField("Location Left", "X: " + event.getPlayer().getLocation().getX() + " Y: " + event.getPlayer().getLocation().getY() + " Z: " + event.getPlayer().getLocation().getZ(), true);
            object.addField("Time Left", time.format(timestamp) + " (GMT/BST)", true);
            object.setColor(new java.awt.Color(143, 76, 76));
            webhook.addEmbed(object);

            try {
                webhook.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // SMP Server
            webhook = new DiscordWebhook("<webhook_here>");
            object = new DiscordWebhook.EmbedObject();

            object.setAuthor("[Quit]", "", "https://minotar.net/helm/" + event.getPlayer().getName() + "/256.png");
            object.addField("Username", event.getPlayer().getName(), true);
            object.addField("UUID", event.getPlayer().getUniqueId().toString(), true);
            object.addField("Time Left", time.format(timestamp) + " (GMT/BST)", true);
            object.setColor(new java.awt.Color(143, 76, 76));
            webhook.addEmbed(object);

            try {
                webhook.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        @EventHandler
        public void onChatEvent(AsyncPlayerChatEvent event) {
            if (dev) return;
            String url1 = "<webhook_here>";
            String url2 = "<webhook_here>";
            String url3 = "<webhook_here>";
            String url4 = "<webhook_here>";

            object = new DiscordWebhook.EmbedObject();
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            object.setAuthor("[Chat Event]", "", "https://minotar.net/helm/" + event.getPlayer().getName() + "/256.png");
            object.addField("Username", event.getPlayer().getName(), true);
            object.addField("UUID", event.getPlayer().getUniqueId().toString(), true);
            object.addField("IP", event.getPlayer().getAddress().getHostString(), true);
            object.addField("Message Said", event.getMessage(), true);
            object.addField("Time", time.format(timestamp) + " (GMT/BST)", true);
            object.setColor(new java.awt.Color(30, 132, 141));

            try {
                webhook = new DiscordWebhook(url1);
                webhook.addEmbed(object);
                webhook.execute();
            } catch (IOException e) {
                webhook = new DiscordWebhook(url2);
                webhook.addEmbed(object);
                try {
                    webhook.execute();
                } catch (IOException ex) {
                    webhook = new DiscordWebhook(url3);
                    webhook.addEmbed(object);
                    try {
                        webhook.execute();
                    } catch (IOException exc) {
                        webhook = new DiscordWebhook(url4);
                        webhook.addEmbed(object);
                        try {
                            webhook.execute();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }
        }

//        @EventHandler
//        public void onPlayerPlaceBlock(BlockPlaceEvent event) {
//            webhook = new DiscordWebhook("<webhook_here>");
//            object = new DiscordWebhook.EmbedObject();
//            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//            object.setAuthor("[Block Place]", "", "https://minotar.net/helm/" + event.getPlayer().getName() + "/256.png");
//            object.addField("Username", event.getPlayer().getName(), true);
//            object.addField("UUID", event.getPlayer().getUniqueId().toString(), true);
//            object.addField("IP", event.getPlayer().getAddress().getHostString(), true);
//            object.addField("Block Placed", event.getBlockPlaced().getType().toString(), true);
//            object.addField("Block Data", event.getBlockPlaced().getBlockData().getAsString(), true);
//            object.addField("Block Power", Integer.toString(event.getBlockPlaced().getBlockPower()), true);
//            object.addField("Chunk", event.getBlockPlaced().getChunk().toString(), true);
//            object.addField("World", event.getBlockPlaced().getWorld().getName(), true);
//            object.addField("Location of Block Placed", "X: " + event.getBlockPlaced().getLocation().getX() + " Y: " + event.getBlockPlaced().getLocation().getY() + " Z: " + event.getBlockPlaced().getLocation().getZ(), true);
//            object.addField("Time of Death", time.format(timestamp) + " (GMT/BST)", true);
//            object.setColor(new java.awt.Color(94, 65, 62));
//            webhook.addEmbed(object);
//
//            try {
//                webhook.execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @EventHandler
//        public void onPlayerBreakBlock(BlockBreakEvent event) {
//            webhook = new DiscordWebhook("<webhook_here>");
//            object = new DiscordWebhook.EmbedObject();
//            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//            object.setAuthor("[Block Break]", "", "https://minotar.net/helm/" + event.getPlayer().getName() + "/256.png");
//            object.addField("Username", event.getPlayer().getName(), true);
//            object.addField("UUID", event.getPlayer().getUniqueId().toString(), true);
//            object.addField("IP", event.getPlayer().getAddress().getHostString(), true);
//            object.addField("Block Placed", event.getBlock().toString(), true);
//            object.addField("Block Data", event.getBlock().getBlockData().getAsString(), true);
//            object.addField("Block Power", Integer.toString(event.getBlock().getBlockPower()), true);
//            object.addField("Chunk", event.getBlock().getChunk().toString(), true);
//            object.addField("World", event.getBlock().getWorld().getName(), true);
//            object.addField("Location of Block Placed", "X: " + event.getBlock().getLocation().getX() + " Y: " + event.getBlock().getLocation().getY() + " Z: " + event.getBlock().getLocation().getZ(), true);
//            object.addField("Time of Death", time.format(timestamp) + " (GMT/BST)", true);
//            object.setColor(new java.awt.Color(75, 65, 38));
//            webhook.addEmbed(object);
//
//            try {
//                webhook.execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        @EventHandler
//        public void onPlayerDeath(PlayerDeathEvent event) {
//            webhook = new DiscordWebhook("<webhook_here>");
//            object = new DiscordWebhook.EmbedObject();
//            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//            object.setAuthor("[Death]", "", "https://minotar.net/helm/" + event.getEntity().getName() + "/256.png");
//            object.addField("Username", event.getEntity().getName(), true);
//            object.addField("UUID", event.getEntity().getUniqueId().toString(), true);
//            object.addField("IP", event.getEntity().getAddress().getHostString(), true);
//            object.addField("Location Death", "X: " + event.getEntity().getLocation().getX() + " Y: " + event.getEntity().getLocation().getY() + " Z: " + event.getEntity().getLocation().getZ(), true);
//            object.addField("Time of Death", time.format(timestamp) + " (GMT/BST)", true);
//
//            String deathType = "";
//            if (event.getEntity().getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) deathType = "from player";
//            else if (event.getEntity().getLastDamageCause().equals(EntityDamageEvent.DamageCause.FALL)) deathType = "fall damage";
//
//
//
//            object.addField("Death Cause", new DeathSwitch(event.getEntity().getLastDamageCause().getEntity(), event.getEntity().getLastDamageCause().getCause()).switchDeath(), true);
//            object.setColor(new java.awt.Color(84, 27, 50));
//            webhook.addEmbed(object);
//
//            try {
//                webhook.execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    class DeathSwitch {
        EntityDamageEvent.DamageCause cause;
        Entity entity;
        public DeathSwitch(Entity entity, EntityDamageEvent.DamageCause cause) {
            this.entity = entity;
            this.cause = cause;
        }

        public String switchDeath() {
            switch(cause) {
                case FALL: return (entity == null) ? "Fall Damage" : "Fall Damage whilst escaping from " + entity.getName();
                case FIRE: return (entity == null) ? "Fire Damage" : "Fire Damage whilst escaping from " + entity.getName();
                case LAVA: return (entity == null) ? "Lava Damage" : "Lava Damage whilst escaping from " + entity.getName();
                case VOID: return (entity == null) ? "Void Damage" : "Void Damage whilst escaping from " + entity.getName();
                case MAGIC: return (entity == null) ? "?!?!?!?!?!" : "?!?!?!?!?! whilst escaping from " + entity.getName();
                case CUSTOM: return "Custom Damage";
                case DRYOUT: return (entity == null) ? "Dehydration Damage" : "Dehydrated whilst escaping from " + entity.getName();
                case FREEZE: return (entity == null) ? "Freezing" : "Freezing whilst escaping from " + entity.getName();
                case POISON: return (entity == null) ? "Poisoning" : "poisoned whilst escaping from " + entity.getName();
                case THORNS: return (entity == null) ? "This somehow broke uwu" : "trying to damage " + entity.getName();
                case WITHER: return "Wither Damage";
                case CONTACT: return "Player Damage";
                case MELTING: return "Melting to Lava";
                case SUICIDE: return "Suicide";
                case CRAMMING: return "Water Damage";
                case DROWNING: return "Suicide by Water";
                case FIRE_TICK: return "Burning Alive";
                case HOT_FLOOR: return "Entity Orgy";
                case LIGHTNING: return "Lightning Damage";
                case PROJECTILE: return "test 1";
                case SONIC_BOOM: return "test 2";
                case STARVATION: return "Suicide by Starvation";
                case SUFFOCATION: return "Suicide by Suffocation";
                case DRAGON_BREATH: return "Dragons Breath Damage";
                case ENTITY_ATTACK: return "Player Damage";
                case FALLING_BLOCK: return "Falling Block Damage";
                case FLY_INTO_WALL: return "Magma Block Damage";
                case BLOCK_EXPLOSION: return "Explosive Damage";
                case ENTITY_EXPLOSION: return "Void Damage";
                case ENTITY_SWEEP_ATTACK: return "Charged Sweeping Sword";
            }
            return null;
        }
    }


    static class GSitRemake {
        static class SpinEmote implements CommandExecutor {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof ConsoleCommandSender) sender.sendMessage("No.");
                else {
                    Player player = (Player) sender;
                    if (getInstance().getPoseManager().isPosing(player) && getInstance().getPoseManager().getPose(player).getPose() == Pose.SPIN_ATTACK) getInstance().getPoseManager().removePose(getInstance().getPoseManager().getPose(player), GetUpReason.GET_UP);
                    else if (player.isValid() && !player.isSneaking() && player.isOnGround() && !player.isInsideVehicle() && !player.isSleeping()) {
                        Block block = player.getLocation().getBlock().isPassable() ? player.getLocation().subtract(0.0, 0.0625, 0.0).getBlock() : player.getLocation().getBlock();
                        if (getInstance().ALLOW_UNSAFE || block.getRelative(BlockFace.UP).isPassable() && (!block.isPassable() || !getInstance().CENTER_BLOCK)) {
                            if (!getInstance().SAME_BLOCK_REST && !getInstance().getPoseManager().kickPose(block, player)) player.sendMessage(UtilColor.blue + "Spin> " + UtilColor.gray + "Seems like someone is already spinning here!");
                            else {
                                if (getInstance().getPoseManager().createPose(block, player, Pose.SPIN_ATTACK) == null) player.sendMessage(UtilColor.blue + "Spin> " + UtilColor.gray + "This region seems to be off-limits right now!");
                            }
                        } else player.sendMessage(UtilColor.blue + "Spin> " + UtilColor.gray + "Seems like you can't Spin here!");
                    } else player.sendMessage(UtilColor.blue + "Spin> " + UtilColor.gray + "Seems like you can't Spin here!");
                }
                return false;
            }
        }

        static class CrawlEmote implements CommandExecutor {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof ConsoleCommandSender) sender.sendMessage("No.");
                else {
                    Player player = (Player) sender;
                    if (getInstance().getCrawlManager().isCrawling(player)) getInstance().getCrawlManager().stopCrawl(getInstance().getCrawlManager().getCrawl(player), GetUpReason.GET_UP);
                    else if (player.isValid() && !player.isSneaking() && player.isOnGround() && !player.isInsideVehicle() && !player.isSleeping()) {
                        if (getInstance().getCrawlManager().startCrawl(player) == null) player.sendMessage(UtilColor.blue + "Crawl> " + UtilColor.gray + "This region seems to be off-limits right now!");
                    } else player.sendMessage(UtilColor.blue + "Crawl> " + UtilColor.gray + "Seems like you can't Crawl here!");

                }
                return false;
            }
        }

        static class BellyFlopEmote implements CommandExecutor {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof ConsoleCommandSender) sender.sendMessage("No.");
                else {
                    Player player = (Player) sender;
                    if (getInstance().getPoseManager().isPosing(player) && getInstance().getPoseManager().getPose(player).getPose() == Pose.SWIMMING) getInstance().getPoseManager().removePose(getInstance().getPoseManager().getPose(player), GetUpReason.GET_UP);
                    else if (player.isValid() && !player.isSneaking() && player.isOnGround() && !player.isInsideVehicle() && !player.isSleeping()) {
                        Block block = player.getLocation().getBlock().isPassable() ? player.getLocation().subtract(0.0, 0.0625, 0.0).getBlock() : player.getLocation().getBlock();
                        if (getInstance().ALLOW_UNSAFE || block.getRelative(BlockFace.UP).isPassable() && (!block.isPassable() || !getInstance().CENTER_BLOCK)) {
                            if (!getInstance().SAME_BLOCK_REST && !getInstance().getPoseManager().kickPose(block, player)) player.sendMessage(UtilColor.blue + "Belly Flop> " + UtilColor.gray + "Seems like someone is already belly flopped here!");
                            else {
                                if (getInstance().getPoseManager().createPose(block, player, Pose.SWIMMING) == null) player.sendMessage(UtilColor.blue + "Belly Flop> " + UtilColor.gray + "This region seems to be off-limits right now!");
                            }
                        } else player.sendMessage(UtilColor.blue + "Belly Flop> " + UtilColor.gray + "Seems like you can't Belly Flop here!");
                    } else player.sendMessage(UtilColor.blue + "Belly Flop> " + UtilColor.gray + "Seems like you can't Belly Flop here!");
                }
                return false;
            }
        }

        static class LayEmote implements CommandExecutor {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof ConsoleCommandSender) sender.sendMessage("No.");
                else {
                    Player player = (Player) sender;
                    if (getInstance().getPoseManager().isPosing(player) && getInstance().getPoseManager().getPose(player).getPose() == Pose.SLEEPING) getInstance().getPoseManager().removePose(getInstance().getPoseManager().getPose(player), GetUpReason.GET_UP);
                    else if (player.isValid() && !player.isSneaking() && player.isOnGround() && !player.isInsideVehicle() && !player.isSleeping()) {
                        Block block = player.getLocation().getBlock().isPassable() ? player.getLocation().subtract(0.0, 0.0625, 0.0).getBlock() : player.getLocation().getBlock();
                        if (getInstance().ALLOW_UNSAFE || block.getRelative(BlockFace.UP).isPassable() && (!block.isPassable() || !getInstance().CENTER_BLOCK)) {
                            if (!getInstance().SAME_BLOCK_REST && !getInstance().getPoseManager().kickPose(block, player)) player.sendMessage(UtilColor.blue + "Lay> " + UtilColor.gray + "Seems like someone is already laying down here!");
                            else if (getInstance().getPoseManager().createPose(block, player, Pose.SLEEPING) == null) player.sendMessage(UtilColor.blue + "Seats> " + UtilColor.gray + "This region seems to be off-limits right now!");
                        } else player.sendMessage(UtilColor.blue + "Lay> " + UtilColor.gray + "Seems like you can't lay down here!");
                    } else player.sendMessage(UtilColor.blue + "Lay> " + UtilColor.gray + "Seems like you can't lay down here!");
                }
                return false;
            }
        }

        static class SitEmote implements CommandExecutor {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof ConsoleCommandSender) sender.sendMessage("No.");
                else {
                    Player player = (Player) sender;
                    if (getInstance().getSitManager().isSitting(player)) getInstance().getSitManager().removeSeat(getInstance().getSitManager().getSeat(player), GetUpReason.GET_UP);
                    else if (player.isValid() && !player.isSneaking() && player.isOnGround() && !player.isInsideVehicle() && !player.isSleeping()) {
                        Block block = player.getLocation().getBlock().isPassable() ? player.getLocation().subtract(0.0, 0.0625, 0.0).getBlock() : player.getLocation().getBlock();
                        if (getInstance().ALLOW_UNSAFE || block.getRelative(BlockFace.UP).isPassable() && (!block.isPassable() || !getInstance().CENTER_BLOCK)) {
                            if (!getInstance().SAME_BLOCK_REST && !getInstance().getSitManager().kickSeat(block, player)) sender.sendMessage(UtilColor.blue + "Sit> " + UtilColor.gray + "This seat seems to be occupied right now!");
                            else {
                                if (Tag.STAIRS.isTagged(block.getType())) {
                                    if (getInstance().getSitUtil().createSeatForStair(block, player) == null) sender.sendMessage(UtilColor.blue + "Sit> " + UtilColor.gray + "This region seems to be off-limits right now!");
                                } else {
                                    if (getInstance().getSitManager().createSeat(block, player) == null) sender.sendMessage(UtilColor.blue + "Sit> " + UtilColor.gray + "This region seems to be off-limits right now!");
                                }
                            }
                        } else sender.sendMessage(UtilColor.blue + "Sit> " + UtilColor.gray + "Seems like you can't sit down here!");
                    } else sender.sendMessage(UtilColor.blue + "Sit> " + UtilColor.gray + "Seems like you can't sit down here!");
                }
                return true;
            }
        }
    }

    class Teams implements Listener, CommandExecutor {
        ArrayList<Player> teamRed = new ArrayList<>(); // 13
        ArrayList<Player> teamGold = new ArrayList<>(); // 9
        ArrayList<Player> teamYellow = new ArrayList<>(); // 15
        ArrayList<Player> teamDarkAqua = new ArrayList<>(); // 4
        ArrayList<Player> teamAdmins = new ArrayList<>(); // 12

        /**
         * 1 : aqua
         * 2 : black
         * 3 : blue
         * 4 : dark_aqua
         * 5 : dark_blue
         * 6 : dark_green
         * 7 : dark_purple
         * 8 : dark_red
         * 9 : gold
         * 10 : gray
         * 11 : green
         * 12 : light_purple
         * 13 : red
         * 14 : white
         * 15 : yellow
         */

        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
            else if (args.length == 0 || args.length == 1 || args.length == 3)
                sender.sendMessage(UtilColor.blue + "Teams> " + UtilColor.yellow + "/team [name] <0-15>");
            else {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                CustomConfig config = new CustomConfig(player.getUniqueId().toString());

                Integer team = Integer.parseInt(args[1]);
                Integer oldTeam = config.get().getInt("team");

                config.get().set("team", team);
                config.save();
                sender.sendMessage(UtilColor.blue + "Teams> " + UtilColor.gray + "Team has been updated for " + UtilColor.yellow + player.getName() + UtilColor.gray + " to team " + UtilColor.yellow + args[1] + UtilColor.gray + ".");
                if (player.isOnline()) {
                    Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
                    board.getTeam(oldTeam.toString()).removeEntry(player.getName());
                    board.getTeam(team.toString()).addEntry(player.getName());
                }
            }
            return false;
        }


        public void registerTeams(Scoreboard board) {
            if (board.getTeam("0") == null) board.registerNewTeam("0");
            if (board.getTeam("1") == null) board.registerNewTeam("1").setColor(ChatColor.AQUA);
            if (board.getTeam("2") == null) board.registerNewTeam("2").setColor(ChatColor.BLACK);
            if (board.getTeam("3") == null) board.registerNewTeam("3").setColor(ChatColor.BLUE);
            if (board.getTeam("4") == null) board.registerNewTeam("4").setColor(ChatColor.DARK_AQUA);
            if (board.getTeam("5") == null) board.registerNewTeam("5").setColor(ChatColor.DARK_BLUE);
            if (board.getTeam("6") == null) board.registerNewTeam("6").setColor(ChatColor.DARK_GREEN);
            if (board.getTeam("7") == null) board.registerNewTeam("7").setColor(ChatColor.DARK_PURPLE);
            if (board.getTeam("8") == null) board.registerNewTeam("8").setColor(ChatColor.DARK_RED);
            if (board.getTeam("9") == null) board.registerNewTeam("9").setColor(ChatColor.GOLD);
            if (board.getTeam("10") == null) board.registerNewTeam("10").setColor(ChatColor.GRAY);
            if (board.getTeam("11") == null) board.registerNewTeam("11").setColor(ChatColor.GREEN);
            if (board.getTeam("12") == null) board.registerNewTeam("12").setColor(ChatColor.LIGHT_PURPLE);
            if (board.getTeam("13") == null) board.registerNewTeam("13").setColor(ChatColor.RED);
            if (board.getTeam("14") == null) board.registerNewTeam("14").setColor(ChatColor.WHITE);
            if (board.getTeam("15") == null) board.registerNewTeam("15").setColor(ChatColor.YELLOW);

            if (board.getTeam("12") != null) board.getTeam("12").setPrefix(UtilColor.darkAqua + "[A] ");

            board.getTeams().forEach(team -> {
                team.setCanSeeFriendlyInvisibles(false);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            });
        }

        public void registerTeamsToPlayers(Player player, Scoreboard board) {
            CustomConfig config = new CustomConfig(player.getUniqueId().toString());
            if (config.get().get("team") == null) {
                config.get().set("team", 0);
                config.save();
            }

            String teamAssigned = config.get().get("team").toString();
            switch (teamAssigned) {
                case "0": board.getTeam("0").addEntry(player.getName());player.setGlowing(false);break;
                case "1": board.getTeam("1").addEntry(player.getName());player.setGlowing(true);break;
                case "2": board.getTeam("2").addEntry(player.getName());player.setGlowing(true);break;
                case "3": board.getTeam("3").addEntry(player.getName());player.setGlowing(true);break;
                case "4": teamDarkAqua.add(player);board.getTeam("4").addEntry(player.getName());player.setGlowing(true);break;
                case "5": board.getTeam("5").addEntry(player.getName());player.setGlowing(true);break;
                case "6": board.getTeam("6").addEntry(player.getName());player.setGlowing(true);break;
                case "7": board.getTeam("7").addEntry(player.getName());player.setGlowing(true);break;
                case "8": board.getTeam("8").addEntry(player.getName());player.setGlowing(true);break;
                case "9": teamGold.add(player);board.getTeam("9").addEntry(player.getName());player.setGlowing(true);break;
                case "10": board.getTeam("10").addEntry(player.getName());player.setGlowing(true);break;
                case "11": board.getTeam("11").addEntry(player.getName());player.setGlowing(true);break;
                case "12": teamAdmins.add(player);board.getTeam("12").addEntry(player.getName());player.setGlowing(true);player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);break;
                case "13": teamRed.add(player);board.getTeam("13").addEntry(player.getName());player.setGlowing(true);break;
                case "14": board.getTeam("14").addEntry(player.getName());player.setGlowing(true);break;
                case "15": teamYellow.add(player);board.getTeam("15").addEntry(player.getName());player.setGlowing(true);break;
                default: break;
            }
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            registerTeamsToPlayers(player, board);
        }

        @EventHandler
        public void cancelBed(PlayerBedEnterEvent event) {
            if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER || event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerDamage(EntityDamageByEntityEvent event) {
            if (event.getEntity() instanceof Player attacker) {
                if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    if (event.getEntityType() == EntityType.ENDER_DRAGON) {
                        event.setDamage(Math.multiplyExact((int) event.getDamage(), 4));
                        EnderDragon dragon = (EnderDragon) event.getDamager();
//                        switch(dragon.getPhase()) {
//                            case DYING -> dragon.getDragonBattle().getBossBar().setColor(BarColor.RED);
//                            case HOVER -> dragon.getDragonBattle().getBossBar().setColor(BarColor.GREEN);
//                            case  -> dragon.getDragonBattle().getBossBar().setColor(BarColor.YELLOW);
//                            case YELLOW -> dragon.getDragonBattle().getBossBar().setColor(BarColor.PURPLE);
//                            case PURPLE -> dragon.getDragonBattle().getBossBar().setColor(BarColor.WHITE);
//                            case WHITE ->  dragon.getDragonBattle().getBossBar().setColor(BarColor.BLUE);
//                        }
                    } else if (event.getEntityType() == EntityType.DRAGON_FIREBALL) {
                        event.setDamage(Math.multiplyExact((int) event.getDamage(), 20));
                        DragonFireball fireball = (DragonFireball) event.getEntity();
                        fireball.setGlowing(true);
                    } else if (event.getDamager() instanceof Player damagee) {
                        if (teamRed.isEmpty() && teamGold.isEmpty() && teamYellow.isEmpty() && teamDarkAqua.isEmpty() && teamAdmins.isEmpty()) Bukkit.getOnlinePlayers().forEach(players -> registerTeamsToPlayers(players, Bukkit.getScoreboardManager().getMainScoreboard()));
                        else if (teamRed.contains(attacker) && teamRed.contains(damagee)) event.setCancelled(true);
                        else if (teamGold.contains(attacker) && teamGold.contains(damagee)) event.setCancelled(true);
                        else if (teamYellow.contains(attacker) && teamYellow.contains(damagee)) event.setCancelled(true);
                        else if (teamDarkAqua.contains(attacker) && teamDarkAqua.contains(damagee)) event.setCancelled(true);
                        else if (teamAdmins.contains(attacker) || teamAdmins.contains(damagee)) event.setCancelled(true);
                    } else event.setCancelled(false);

                } else event.setCancelled(false);
            } else event.setCancelled(false);
        }
    }


    /**
     * This was for a custom boss idea I had, partially got through it but I'm not sure if I'll ever finish it.
     * I'm not sure if I'll ever finish it.
     * if anyone else wants to finish it, feel free to do so and show me the results :)
     */

    public static class BossesRegister implements Listener {

        class registerRecipes {
            public registerRecipes() {
                Bukkit.getPluginManager().registerEvents(new newBoss(), getInstance());
                Bukkit.addRecipe(new newBoss().craftRecipe());
            }
        }

        static class newBoss implements Listener,CommandExecutor {
            HashMap<Player, Boolean> inFight = new HashMap<>();
            String worldName = "world_warden_fight";

            String bossName = UtilColor.purple + UtilColor.magic + "?!?!?!?!";
            UUID wardenId;
            UUID jerryId;

            @EventHandler
            public void onAnvil(InventoryClickEvent event) {
                if (event.getCurrentItem() == null) return;
                if (event.getCurrentItem().getType() == Material.AIR) return;
                if (event.getInventory().getType() == InventoryType.ANVIL) {
                    if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                        if (event.getCurrentItem().getType() == Material.AXOLOTL_SPAWN_EGG) event.setCancelled(true);
                    }
                }
            }

            @EventHandler
            public void onCraftItem(CraftItemEvent event) {
                Inventory inventory = event.getInventory();

                for (ItemStack item : inventory.getContents()) {
                    if (item != null && item.getType() == Material.AXOLOTL_SPAWN_EGG && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(bossName)) {
                        event.setCancelled(true);
                    }
                }
            }

            @EventHandler
            public void register(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                if (player.getInventory().getItemInMainHand().getType().equals(Material.AXOLOTL_SPAWN_EGG) && Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(bossName)) {
                    if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        spawnBoss(event.getPlayer());
                        inFight.put(event.getPlayer(), true);
                    }
                }
            }

            public void spawnJerry(Player spawner, Integer seconds) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(),() -> {
                    Location location = new Location(spawner.getWorld(), -146, 306, 145, -137, 22);
                    Villager villager = location.getWorld().spawn(location, Villager.class);
                    jerryId = villager.getUniqueId();
                    villager.setAI(false);
                    villager.setProfession(Villager.Profession.NITWIT);
                    villager.setCustomName(UtilColor.red + UtilColor.bold + "Jerry");
                    villager.setCustomNameVisible(true);
                    villager.setVillagerType(Villager.Type.SNOW);C
                }, (20L * seconds));
            }

            public void killJerry(Integer seconds) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(),() -> {
                    if (jerryId == null) return;
                    Bukkit.getEntity(jerryId).remove();
                }, (20L * seconds));
            }

            public void sendMessage(Player player, Integer seconds, String message) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> player.sendMessage(message), (20L * seconds));
            }

            public void modifyBlock(Entity entity, double x, double y, double z, Material material) {
                entity.getWorld().getBlockAt(new Location(entity.getWorld(), x, y, z)).setType(material);
            }
            public void modifyBlock(Entity entity, Integer seconds, double x, double y, double z, Material material) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> entity.getWorld().getBlockAt(new Location(entity.getWorld(), x, y, z)).setType(material), (20L * seconds));
            }

            public void teleportEntity(Entity entity, double x, double y, double z, float pitch, float yaw) {
                entity.teleport(new Location(entity.getWorld(), x, y, z, pitch, yaw));
            }
            public void teleportEntity(Entity entity, double x, double y, double z) {
                entity.teleport(new Location(entity.getWorld(), x, y, z));
            }

            public void teleportEntity(Entity entity, Integer seconds, double x, double y, double z, float pitch, float yaw) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> entity.teleport(new Location(entity.getWorld(), x, y, z, pitch, yaw)), (20L * seconds));
            }
            public void teleportEntity(Entity entity, Integer seconds, double x, double y, double z) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> entity.teleport(new Location(entity.getWorld(), x, y, z)), (20L * seconds));
            }

            public void strikeLightning(Entity entity, Integer seconds, double x, double y, double z, boolean isTrue) {
                if (isTrue) Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> entity.getWorld().strikeLightning(new Location(entity.getWorld(), x, y, z)), (20L * seconds));
                else Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> entity.getWorld().strikeLightningEffect(new Location(entity.getWorld(), x, y, z)), (20L * seconds));
            }
            public void strikeLightning(Entity entity, double x, double y, double z, boolean isTrue) {
                if (isTrue) entity.getWorld().strikeLightning(new Location(entity.getWorld(), x, y, z));
                else entity.getWorld().strikeLightningEffect(new Location(entity.getWorld(), x, y, z));
            }

            public void addPotionEffect(Entity entity, Integer seconds, PotionEffectType type, Integer duration, Integer strength) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> ((Player)entity).addPotionEffect(new PotionEffect(type, duration, strength)), (20L * seconds));
            }
            public void addPotionEffect(Entity entity, PotionEffectType type, Integer duration, Integer strength) {
                ((Player)entity).addPotionEffect(new PotionEffect(type, duration, strength));
            }

            public void removePotionEffect(Entity entity, Integer seconds, PotionEffectType type) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> ((Player)entity).removePotionEffect(type), (20L * seconds));
            }
            public void removePotionEffect(Entity entity, PotionEffectType type) {
                ((Player)entity).removePotionEffect(type);
            }

            public void spawnJerryMinion_Creeper(Player spawner, double x, double y, double z) {
                Location location = new Location(spawner.getWorld(), x, y, z);

                Creeper creeper = (Creeper) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.CREEPER);
            }


            public void spawnJerryMinion_Warden(Player spawner) {
                Location location = new Location(spawner.getWorld(), -66, 305, 36);

                Warden warden = (Warden) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.WARDEN);
                wardenId = warden.getUniqueId();
                warden.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(75000);
                warden.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(Math.multiplyExact((int) warden.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).getBaseValue(), 50));
                warden.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.multiplyExact((int) warden.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue(), 5));
                warden.setHealth(2048);
                warden.setAnger(spawner, 150);
                warden.setAware(true);
                warden.setCustomNameVisible(false);
                warden.setCustomName(bossName);

                ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
                armorStand.setGravity(false);
                armorStand.setCanPickupItems(false);
                armorStand.setCustomName(UtilColor.red + UtilColor.bold + "Jerry's Minion");
                armorStand.setCustomNameVisible(true);
                armorStand.setInvisible(true);
                armorStand.setSmall(true);
                warden.addPassenger(armorStand);
                warden.setAnger(armorStand, 0);
                warden.setAnger(spawner, 150);
            }

            public void resetSpawnPillarWarden(Player entity) {
                // layer1 concrete
                modifyBlock(entity, -67, 303, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 303, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 303, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 303, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 303, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 304, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 304, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 304, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 304, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 305, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 305, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 305, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 305, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 306, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 306, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 306, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 306, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 307, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 307, 35, Material.BLACK_CONCRETE);
                modifyBlock(entity, -67, 307, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 307, 36, Material.BLACK_CONCRETE);
                modifyBlock(entity, -66, 308, 35, Material.SCULK_SHRIEKER);
                modifyBlock(entity, -67, 308, 35, Material.SCULK_SHRIEKER);
                modifyBlock(entity, -66, 308, 36, Material.SCULK_SHRIEKER);
                modifyBlock(entity, -67, 308, 36, Material.SCULK_SHRIEKER);
                modifyBlock(entity, -66, 309, 35, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -67, 309, 36, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -66, 309, 35, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -67, 309, 36, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -68, 310, 35, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -68, 310, 36, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -66, 310, 34, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -67, 310, 34, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -65, 310, 35, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -65, 310, 36, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -66, 310, 37, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -67, 310, 37, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -66, 311, 35, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -67, 311, 35, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -66, 311, 36, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -67, 311, 36, Material.AMETHYST_CLUSTER);
                modifyBlock(entity, -66, 310, 35, Material.AMETHYST_BLOCK);
                modifyBlock(entity, -67, 310, 35, Material.AMETHYST_BLOCK);
                modifyBlock(entity, -66, 310, 36, Material.AMETHYST_BLOCK);
                modifyBlock(entity, -67, 310, 36, Material.AMETHYST_BLOCK);
                modifyBlock(entity, -68, 303, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 303, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 304, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 304, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 305, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 305, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 306, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 306, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 307, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -68, 307, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 303, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 303, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 304, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 304, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 305, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 305, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 306, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 306, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 307, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 307, 37, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 303, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 303, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 304, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 304, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 305, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 305, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 306, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 306, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 307, 35, Material.SCULK_VEIN);
                modifyBlock(entity, -65, 307, 36, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 303, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 303, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 304, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 304, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 305, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 305, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 306, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 306, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -66, 307, 34, Material.SCULK_VEIN);
                modifyBlock(entity, -67, 307, 34, Material.SCULK_VEIN);
            }

            public void startPlayerAnimation(Player entity) {
                entity.setInvisible(true);
                entity.setAllowFlight(true);
                entity.setFlying(true);
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 100));

                // Crystal Spawnings
                teleportEntity(entity, -52.426, 313.64701, 30.140, 72, 33);

                sendMessage(entity, 1, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.gray + "Welcome you pathetic little human!");

                strikeLightning(entity, 1,-67, 310, 36, false);
                strikeLightning(entity, 1,-67, 310, 35, false);
                strikeLightning(entity, 1,-66, 310, 35, false);
                strikeLightning(entity, 1,-66, 310, 36, false);
                modifyBlock(entity, 1,-67, 310, 36, Material.AIR);
                modifyBlock(entity, 1,-67, 310, 35, Material.AIR);
                modifyBlock(entity, 1,-66, 310, 35, Material.AIR);
                modifyBlock(entity, 1,-66, 310, 36, Material.AIR);

                sendMessage(entity, 5, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.gray + "You seem to have fallen into my domain and by doing that have broken the rules of combat!");

                teleportEntity(entity, 7, -62, 305, 38, 120, 0);
                modifyBlock(entity, 7, -66, 311, 36, Material.AIR);
                removePotionEffect(entity, 7, PotionEffectType.SLOW);
                addPotionEffect(entity, 7, PotionEffectType.SLOW, 10000,0);

                modifyBlock(entity, 8, -66, 308, 35, Material.AIR);
                modifyBlock(entity, 8, -67, 308, 35, Material.AIR);
                modifyBlock(entity, 8, -66, 308, 36, Material.AIR);
                modifyBlock(entity, 8, -67, 308, 36, Material.AIR);

                modifyBlock(entity, 9, -66, 307, 35, Material.AIR);
                modifyBlock(entity, 9, -67, 307, 35, Material.AIR);
                modifyBlock(entity, 9, -66, 307, 36, Material.AIR);
                modifyBlock(entity, 9, -67, 307, 36, Material.AIR);

                sendMessage(entity, 9, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.gray + "You do not know of me yet however I know of you pathetic mortals, crawling around your \"land\" that you stole from us many centuries ago!");

                modifyBlock(entity, 10, -66, 306, 35, Material.AIR);
                modifyBlock(entity, 10, -67, 306, 35, Material.AIR);
                modifyBlock(entity, 10, -66, 306, 36, Material.AIR);
                modifyBlock(entity, 10, -67, 306, 36, Material.AIR);

                modifyBlock(entity, 11, -66, 305, 35, Material.AIR);
                modifyBlock(entity, 11, -67, 305, 35, Material.AIR);
                modifyBlock(entity, 11, -66, 305, 36, Material.AIR);
                modifyBlock(entity, 11, -67, 305, 36, Material.AIR);

                modifyBlock(entity, 12, -66, 304, 35, Material.AIR);
                modifyBlock(entity, 12, -67, 304, 35, Material.AIR);
                modifyBlock(entity, 12, -66, 304, 36, Material.AIR);
                modifyBlock(entity, 12, -67, 304, 36, Material.AIR);
                sendMessage(entity, 12, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.gray + "You have used us again and again, stealing our land.");

                modifyBlock(entity, 13, -66, 303, 35, Material.AIR);
                modifyBlock(entity, 13, -67, 303, 35, Material.AIR);
                modifyBlock(entity, 13, -66, 303, 36, Material.AIR);
                modifyBlock(entity, 13, -67, 303, 36, Material.AIR);

                modifyBlock(entity, 13, -66, 303, 35, Material.SCULK_SENSOR);
                modifyBlock(entity, 13, -67, 303, 35, Material.SCULK_SENSOR);
                modifyBlock(entity, 13, -66, 303, 36, Material.SCULK_SENSOR);
                modifyBlock(entity, 13, -67, 303, 36, Material.SCULK_SENSOR);
                sendMessage(entity, 15, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.gray + "Stealing our beds, Stealing our youth, Stealing our loot.");


                teleportEntity(entity, 16, -130, 308, 123, 68, 35);
                strikeLightning(entity, 16, -137, 309, 125, false);
                modifyBlock(entity, 16, -137, 309, 125, Material.AIR);


                teleportEntity(entity, 18, -131, 308, -11, 138, 24);
                strikeLightning(entity, 18, -137, 309, -17, false);
                modifyBlock(entity, 18, -137, 309, -17, Material.AIR);
                sendMessage(entity, 18, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.gray + "So for many centuries, I have been looking deep and wide past the borders of this realm and the next.");


                teleportEntity(entity, 20, -23, 307, -12, -138, 27);
                strikeLightning(entity, 20, -20, 309, -18, false);
                modifyBlock(entity, 20, -20, 309, -18, Material.AIR);
                sendMessage(entity, 20, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.red + UtilColor.bold + "Let me just say, You won't stand a chance getting out alive!");

                teleportEntity(entity, 22, -27, 307, 126, -43, 11);
                strikeLightning(entity, 22, -23, 309, 131, false);
                modifyBlock(entity, 22, -23, 309, 131, Material.AIR);

                sendMessage(entity, 23, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.red + UtilColor.bold + "Let me introduce myself, I am that of which you abuse, that of which you torture for your own pleasure!");
                sendMessage(entity, 26, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.red + UtilColor.bold + "I am the one true villager.");
                sendMessage(entity, 28, UtilColor.purple + "[" + bossName + UtilColor.reset + UtilColor.purple + "] " + UtilColor.red + UtilColor.bold + "I am Jerry.");

                teleportEntity(entity, 28, -141, 301, 140, 44, -32);
                spawnJerry(entity, 28);
                addPotionEffect(entity, 28, PotionEffectType.SLOW, 1000000, 100);
                strikeLightning(entity, 28, -146, 306, 146, false);
                strikeLightning(entity, 28, -147, 306, 145, false);

                sendMessage(entity, 30, UtilColor.purple + "[" + UtilColor.red + UtilColor.bold + "Jerry" + UtilColor.reset + UtilColor.purple + "] " + UtilColor.gray + "You have abused us for years, yearning for our hard work, forcing us into labor for you, enough is enough!");
                killJerry(34);
                teleportEntity(entity, 34, -62, 305, 38, 120, 0);
                sendMessage(entity, 34, UtilColor.purple + "[" + UtilColor.red + UtilColor.bold + "Jerry" + UtilColor.reset + UtilColor.purple + "] " + UtilColor.red + UtilColor.bold + "Prepare your greatest warriors, flesh-bags!");
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> spawnBoss(entity), (20 * 35));
                teleportEntity(entity, 37, -149, 301, 148, -136, 2);
                removePotionEffect(entity, 37, PotionEffectType.SLOW);
                entity.setInvisible(false);

                // Block Deformation TP
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> resetSpawnPillarWarden(entity), (20 * 60));
            }

            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof org.bukkit.command.ConsoleCommandSender) Bukkit.getConsoleSender().sendMessage("no");
                else startPlayerAnimation((Player)sender);
                return false;
            }

            @EventHandler
            public void onPlayerPlaceBlock(BlockPlaceEvent event) {
                if (!inFight.containsKey(event.getPlayer())) return;
                else {
                    if (Bukkit.getEntity(wardenId) == null) return;
                    else event.setCancelled(true);
                }
            }

            @EventHandler
            public void onPlayerBreakBlock(BlockBreakEvent event) {
                if (!inFight.containsKey(event.getPlayer())) return;
                else {
                    if (Bukkit.getEntity(wardenId) == null) return;
                    else event.setCancelled(true);
                }
            }


            public ShapedRecipe craftRecipe() {
                ItemStack item = new ItemStack(Material.AXOLOTL_SPAWN_EGG, 1);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setDisplayName(UtilColor.purple + UtilColor.magic + "?!?!?!?!");
                List<String> lore = new ArrayList<>();
                lore.add(UtilColor.purple + UtilColor.magic + "?!?!?!?!");
                meta.setLore(lore);
                item.setItemMeta(meta);

                NamespacedKey key = new NamespacedKey(getInstance(), "Spawn 01");
                ShapedRecipe sr = new ShapedRecipe(key, item);

                sr.setIngredient('A', Material.ENCHANTED_GOLDEN_APPLE);
                sr.setIngredient('B', Material.NETHER_STAR);
                sr.setIngredient('C', Material.ENCHANTED_GOLDEN_APPLE);

                sr.setIngredient('D', Material.BEACON);
                sr.setIngredient('E', Material.DRAGON_BREATH);
                sr.setIngredient('F', Material.BEACON);

                sr.setIngredient('G', Material.ENCHANTED_GOLDEN_APPLE);
                sr.setIngredient('H', Material.NETHER_STAR);
                sr.setIngredient('I', Material.ENCHANTED_GOLDEN_APPLE);

                return sr;
            }
        }


        @EventHandler
        public void onBossSpawn(EntitySpawnEvent event) {
            if (event.getEntity() instanceof Wither) {
                Wither wither = (Wither)event.getEntity();
                wither.setGlowing(true);
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("1").addEntry(wither.getUniqueId().toString());
                wither.setCustomName(UtilColor.darkRed + UtilColor.bold + "Beany The Wither");
                wither.getBossBar().setColor(BarColor.YELLOW);
                wither.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(75000);
                wither.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(Math.multiplyExact((int) wither.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).getBaseValue(), 50));
                wither.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.multiplyExact((int) wither.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue(), 5));
                wither.setHealth(2048);
                wither.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999999, 300));
                wither.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 999999999, 300_000_000));
            } else if (event.getEntity() instanceof EnderDragon) {
                EnderDragon dragon = (EnderDragon) event.getEntity();
                dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999999, 300));
                dragon.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 300_000_000));
                dragon.setGlowing(true);
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("8").addEntry(dragon.getUniqueId().toString());
                dragon.setCustomName(UtilColor.yellow + UtilColor.bold + "Douglas The Dragon");
                dragon.getDragonBattle().getBossBar().setColor(BarColor.RED);
                dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000000);
                dragon.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(Math.multiplyExact((int) dragon.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).getBaseValue(), 20));
                dragon.setHealth(2048);
            }
        }

        @EventHandler
        public void onEntitySlaughter(EntityDeathEvent event) {
            if (dev) return;
            if (event.getEntity() instanceof Wither) {
                LivingEntity deadEntity = event.getEntity();
                Player player = null;
                if (deadEntity.getKiller() != null) player = deadEntity.getKiller();

                if (player == null) return;
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("1").removeEntry(event.getEntity().getUniqueId().toString());
                DiscordWebhook discordWebhook = new DiscordWebhook("<webhook_url>");
                DiscordWebhook.EmbedObject object = new DiscordWebhook.EmbedObject();
                object.setAuthor("[The Wither Logs]", "", "https://minotar.net/helm/" + player.getName()  + "/256.png");
                object.setDescription(player.getName() + " has slaughtered Beany The Wither!");
                object.setColor(new java.awt.Color(52, 25, 33));
                discordWebhook.addEmbed(object);

                try {
                    discordWebhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (event.getEntity() instanceof EnderDragon) {
                LivingEntity deadEntity = event.getEntity();
                Player player = null;
                if (deadEntity.getKiller() != null) player = deadEntity.getKiller();

                if (player == null) return;
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("8").removeEntry(event.getEntity().getUniqueId().toString());
                DiscordWebhook discordWebhook = new DiscordWebhook("<webhook_url>");
                DiscordWebhook.EmbedObject object = new DiscordWebhook.EmbedObject();
                object.setAuthor("[The End Logs]", "", "https://minotar.net/helm/" + player.getName() + "/256.png");
                object.setDescription(player.getName()  + " has slaughtered Douglas the Dragon!");
                object.setColor(new java.awt.Color(52, 25, 33));
                discordWebhook.addEmbed(object);

                try {
                    discordWebhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    class Hearts {
        public Hearts() {
            Bukkit.getPluginManager().registerEvents(new registerRedemption(), getInstance());
            Bukkit.addRecipe(heartRecipe());
        }

        public ShapedRecipe heartRecipe() {
            ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA, 1);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(UtilColor.yellow + UtilColor.bold + "Redeem");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_RED + "Grants a couple of extra hearts... Use this only if NEEDED.");
            lore.add(ChatColor.LIGHT_PURPLE + "This can only rarely be used in the most severe situations.");
            meta.setLore(lore);
            item.setItemMeta(meta);

            NamespacedKey key = new NamespacedKey(getInstance(), "Redeem");
            ShapedRecipe sr = new ShapedRecipe(key, item);

            sr.shape("ABC", "DEF", "GHI");

            char[] Alphabet = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};

            sr.setIngredient(Alphabet[0], Material.NETHER_STAR);
            sr.setIngredient(Alphabet[1], Material.NETHER_STAR);
            sr.setIngredient(Alphabet[2], Material.NETHER_STAR);
            sr.setIngredient(Alphabet[3], Material.END_CRYSTAL);

            sr.setIngredient(Alphabet[4], Material.DRAGON_EGG);


            sr.setIngredient(Alphabet[5], Material.END_CRYSTAL);
            sr.setIngredient(Alphabet[6], Material.NETHER_STAR);
            sr.setIngredient(Alphabet[7], Material.NETHER_STAR);
            sr.setIngredient(Alphabet[8], Material.NETHER_STAR);

            return sr;
        }

        class registerRedemption implements Listener {
            @EventHandler
            public void onAnvil(InventoryClickEvent event) {
                if (event.getCurrentItem() == null) return;
                if (event.getCurrentItem().getType() == Material.AIR) return;
                if (event.getInventory().getType() == InventoryType.ANVIL) {
                    if(event.getSlotType() == InventoryType.SlotType.RESULT) {
                        if (event.getCurrentItem().getType() == Material.DRAGON_EGG) event.setCancelled(true);
                    }
                }
            }

            @EventHandler
            public void onCraftItem(CraftItemEvent event) {
                Inventory inventory = event.getInventory();

                for (ItemStack item : inventory.getContents()) {
                    if (item != null && item.getType() == Material.DRAGON_EGG && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(UtilColor.red + UtilColor.bold + "Douglas Jr.")) {
                        event.setCancelled(true);
                    }
                }
            }
            @EventHandler
            public void register(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                if (player.getInventory().getItemInMainHand().getType().equals(Material.HEART_OF_THE_SEA) && Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(UtilColor.yellow + UtilColor.bold + "Redeem")) {
                    if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                        ItemStack egg = new ItemStack(Material.DRAGON_EGG);
                        egg.setAmount(1);
                        ItemMeta meta = egg.getItemMeta();
                        assert meta != null;
                        meta.setDisplayName(UtilColor.red + UtilColor.bold + "Douglas Jr.");
                        meta.setLore(Arrays.asList("", UtilColor.gray + "Use this item wisely!", "", UtilColor.yellow + "this item can not be used to craft another heart but can be used to spawn another dragon."));
                        egg.setItemMeta(meta);

                        player.getInventory().addItem(egg);
                        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() >= 60) {
                            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
                            player.sendMessage(UtilColor.blue + "Hearts> " + UtilColor.gray + "You can not redeem more than 3 rows of hearts.");
                        } else {
                            if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() >= 50.0D) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60.0D);
                            else player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 20.0D);
                            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        }
                    }
                }
            }

            @EventHandler
            public void register(PlayerDeathEvent event) {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2.0);
                if (event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() <= 0.0D) event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2.0);
                if (event.getEntity().getKiller() != null) {
                    Player killer = event.getEntity().getKiller();
                    if (!event.getEntity().getKiller().getName().equalsIgnoreCase(event.getEntity().getName())) {
                        if (killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() >= 60) ;
                        else killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2);

                        Integer killerHearts = Math.floorDiv((int) killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), 2);
                        Integer victimHearts = Math.floorDiv((int) event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), 2);

                        if (dev) return;
                        DiscordWebhook discordWebhook = new DiscordWebhook("<webhook_url>");
                        DiscordWebhook.EmbedObject object = new DiscordWebhook.EmbedObject();
                        object.setAuthor("[PvP Logs]", "", "https://minotar.net/helm/" + event.getEntity().getName() + "/256.png");
                        if (victimHearts > 1) object.setDescription(event.getEntity().getName() + " has died whilst fighting " + event.getEntity().getKiller().getName() + " and their hearts have been updated to " + killerHearts + " leaving " + event.getEntity().getName() + " with " + victimHearts + " hearts left.");
                        else object.setDescription(event.getEntity().getName() + " has died whilst fighting " + event.getEntity().getKiller().getName() + " and their hearts have been updated to " + killerHearts + " leaving " + event.getEntity().getName() + " with " + victimHearts + " heart left.");
                        object.setColor(new java.awt.Color(150, 25, 63));
                        discordWebhook.addEmbed(object);

                        try {
                            discordWebhook.execute();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    class Vanish implements Listener, CommandExecutor {
        CustomConfig config;
        ArrayList<String> vanishedPlayers = new ArrayList<>();

        public boolean isVanished(Player player) {
            if (player == null) return false;
            if (!player.isOnline()) return false;
            config = new CustomConfig(player.getUniqueId().toString());
            try {
                config.get().getBoolean("vanish");
            } catch (Exception e) {
                config.get().set("vanish", false);
                config.save();
            }
            if (config.get().getBoolean("vanish")) return true;
            else return false;
        }

        public void setVanish(Player player) {
            vanishedPlayers.add(player.getName());
            CustomConfig config = new CustomConfig(player.getUniqueId().toString());
            config.get().set("vanish", true);
            config.save();
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(UtilColor.blue + "Incognito> " + UtilColor.gray + "You are now " + UtilColor.yellow + "invisible" + UtilColor.gray + " to the SMP.");
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.hidePlayer(getInstance(), player);
                new ScoreboardTeams().update(false);
            });
        }

        public void setVisible(Player player) {
            vanishedPlayers.remove(player.getName());
            CustomConfig config = new CustomConfig(player.getUniqueId().toString());
            config.get().set("vanish", false);
            config.save();
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(UtilColor.blue + "Incognito> " + UtilColor.gray + "You are now " + UtilColor.yellow + "visible" + UtilColor.gray + " to the SMP.");
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.showPlayer(getInstance(), player);
                new ScoreboardTeams().update(false);
            });
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof CommandExecutor) sender.sendMessage("No");
            else {
                if (!sender.isOp()) sender.sendMessage("Unknown command. Type \"/help\" for help.");
                else {
                    if (isVanished(((Player)sender))) setVisible(((Player)sender));
                    else setVanish(((Player)sender));
                }
            }
            return false;
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (isVanished(event.getPlayer())) setVanish(event.getPlayer());
            if (vanishedPlayers.isEmpty()) return;
            else {
                vanishedPlayers.forEach(playerName -> {
                    Player vanishedPlayer = Bukkit.getPlayer(playerName);
                    assert vanishedPlayer != null;
                    player.hidePlayer(getInstance(), vanishedPlayer);
                    Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(getInstance(), player));
                });
            }
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            if (vanishedPlayers.isEmpty()) return;
            if (!vanishedPlayers.contains(event.getPlayer().getName())) return;
            vanishedPlayers.remove(event.getPlayer().getName());
        }
    }


    class Chat implements Listener {
        @EventHandler
        public void onChat(AsyncPlayerChatEvent event) {
            event.setCancelled(true);
            String message = event.getMessage();
            CustomConfig config = new CustomConfig(event.getPlayer().getUniqueId().toString());

            int x = config.get().getInt("immortal");
            int level = config.get().getInt("deaths");
            if (message.contains("%")) message.replaceAll("%", " "); // We have this to patch the % bug with chat async.
            if (message.startsWith("#")) Bukkit.broadcastMessage(UtilColor.white + UtilColor.bold + "TEAM " + new Levels().switchColor(level) + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message.replaceFirst("#", ""));
            else if (message.startsWith("@")) {
                String newMessage1 = message.replaceFirst("@", "");
                if (newMessage1.contains("&l")) newMessage1.replaceAll("&l", "");
                String newMessage = newMessage1.replace("&", ChatColor.COLOR_CHAR + "");
                Bukkit.broadcastMessage(UtilColor.purple + UtilColor.bold + "PARTY " + new Levels().switchColor(level) + UtilColor.white + UtilColor.bold + event.getPlayer().getName() + " " + UtilColor.purple + newMessage);
            } else if (message.startsWith("!")) {
                String newMessage = message.replaceFirst("!", "");
                if (!event.getPlayer().isOp()) Bukkit.broadcastMessage(UtilColor.aqua + UtilColor.bold + "StaffRequest " + UtilColor.darkAqua + UtilColor.bold + event.getPlayer().getName() + " " + UtilColor.aqua + newMessage);
                else Bukkit.broadcastMessage(UtilColor.aqua + UtilColor.bold + "StaffRequest " + UtilColor.darkRed + UtilColor.bold + "Admin Toki " + UtilColor.aqua + newMessage);
            } else if (event.getPlayer().getName().equalsIgnoreCase("daavide")) Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.darkAqua + UtilColor.bold + "IDIOT " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
            else if (event.getPlayer().isOp()) Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.darkRed + event.getPlayer().getName() + UtilColor.white + " " + message);
            else {
                switch (x) {
                    case 1:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.yellow + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 2:
                        Bukkit.broadcastMessage((new Levels()).switchColor(level) + UtilColor.aqua + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 3:
                        Bukkit.broadcastMessage((new Levels()).switchColor(level) + UtilColor.purple + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 4:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.green + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 5:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.red + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 6:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.darkAqua + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 7:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.black + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 8:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.white + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 9:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.darkBlue + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 10:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.darkGreen + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 11:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.gray + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                    case 12:
                        Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.darkGray + UtilColor.bold + "IMMORTAL " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
                        return;
                }
                Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
            }
        }
    }

    static class Levels {
        public boolean isBetween(int x, int lower, int upper) {
            return (lower <= x && x <= upper);
        }

        public String switchColor(Integer level) {
            int x = 1;
            if (isBetween(level, 0, 19)) x = 1;
            else if (isBetween(level, 20, 39)) x = 2;
            else if (isBetween(level, 40, 59)) x = 3;
            else if (isBetween(level, 60, 79)) x = 4;
            else if (isBetween(level, 80, 99)) x = 5;
            else if (isBetween(level, 100, 119)) x = 6;
            else if (isBetween(level, 120, 139)) x = 7;
            else if (isBetween(level, 140, 159)) x = 8;
            else if (isBetween(level, 160, 179)) x = 9;
            else if (isBetween(level, 160, 179)) x = 10;
            else if (isBetween(level, 180, 199)) x = 11;
            else if (level == 200 || level > 6969 || level < 6968) x = 12;
            else if (level == 6969) x = 13;

            switch (x) {
                case 1: return UtilColor.gray + level + " ";
                case 2: return UtilColor.blue + level + " ";
                case 3: return UtilColor.darkGreen + level + " ";
                case 4: return UtilColor.gold + level + " ";
                case 5: return UtilColor.red + level + " ";
                case 6: return UtilColor.darkRed + level + " ";
                case 7: return UtilColor.aqua + level + " ";
                case 8: return UtilColor.white + level + " ";
                case 9: return UtilColor.darkBlue + level + " ";
                case 10: return UtilColor.darkGray + level + " ";
                case 11: return UtilColor.darkPurple + level + " ";
                case 12: return UtilColor.aqua + UtilColor.bold + level + " ";
                case 13: return UtilColor.red + UtilColor.bold + "6" + UtilColor.green + UtilColor.bold + "9" + UtilColor.red + UtilColor.bold + "6" + UtilColor.green + UtilColor.bold + "9 ";
            }
            return null;
        }
    }

    class RegisterDeath implements Listener {
        @EventHandler
        public void onDeath(PlayerDeathEvent event) {
            event.setDeathMessage(UtilColor.blue + "Death> " + UtilColor.red + event.getEntity().getName() + UtilColor.gray + " died due to " + UtilColor.yellow + "being an idiot" + UtilColor.gray + ".");
            CustomConfig config = new CustomConfig(event.getEntity().getUniqueId().toString());
            config.get().set("deaths", Math.addExact(config.get().getInt("deaths"), 1));
            config.save();
        }
    }
}
