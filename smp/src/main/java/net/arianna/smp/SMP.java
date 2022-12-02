package net.arianna.smp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class SMP extends JavaPlugin {

    /**
     * This is the main class of the plugin. It is the entry point for the plugin.
     * 
     * This was a project for a Minecraft SMP servers for a couple of old friends of mine.
     * This isn't entirely the last version of the code due to losing it when I was swapping computers. 
     * I'm not sure if I'll ever finish this project, but I'm putting it up here for posterity.
     * 
     * It's not clean code, but it's not terrible either. I'm not sure if I'll ever come back to this.
     * Only reason it's not clean is because it was a personal project for a couple of friends so I didn't really care much about the quality of the code.
     * 
     * This code is licensed under the MIT license.
     * @Author 0x7CD6 (Arianna)
     * @Version 0.0.1
     */

   // This is used for the UHC alert every 5 minutes
   Timer timer = new Timer();
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new Chat(), this);
        Bukkit.getPluginManager().registerEvents(new JoinLeave(), this);
        Bukkit.getPluginManager().registerEvents(new RegisterDeath(), this);
        Bukkit.getPluginManager().registerEvents(new AchievementUnlock(), this);
//        Bukkit.getPluginManager().registerEvents(new Scoreboard(), this);
        Objects.requireNonNull(getCommand("prefix")).setExecutor(new ImmortalPrefixCommand());
        Objects.requireNonNull(getCommand("taunt")).setExecutor(new Taunt());
        Objects.requireNonNull(getCommand("gm")).setExecutor(new GamemodeCommand());
        Objects.requireNonNull(getCommand("uhc")).setExecutor(new UHCCommand());
        Objects.requireNonNull(getCommand("setlevel")).setExecutor(new LevelsCommand());
        Objects.requireNonNull(getCommand("teleport")).setExecutor(new TeleportCommand());
        timer.schedule(new TimerTask() {
            public void run() {
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage(UtilColor.white + UtilColor.bold + " A game of " + UtilColor.yellow + UtilColor.bold + "UHC Remastered" + UtilColor.white + UtilColor.bold + " is about to start! ");
                Bukkit.broadcastMessage(" ");
                Bukkit.getOnlinePlayers().forEach(p1 -> p1.playNote(p1.getLocation(), Instrument.PLING, Note.sharp(1, Note.Tone.E)));
            }
        }, 0L, 300000L);
    }

    public void onDisable() {
        timer.cancel();
    }

    class JoinLeave implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            event.setJoinMessage(UtilColor.aqua + UtilColor.bold + "[JOIN] " + UtilColor.darkAqua + event.getPlayer().getName());
            Config config = new Config(event.getPlayer().getUniqueId());
            // If the user wasn't found in the config, create a new one for them.
            if (getConfig().get(event.getPlayer().getUniqueId().toString()) == null) {
                config.setInt("immortal", 0);
                config.setInt("deaths", 0);
            }
        }

        @EventHandler
        public void onLeave(PlayerQuitEvent event) {
            event.setQuitMessage(UtilColor.red + UtilColor.bold + "[QUIT] " + UtilColor.darkAqua + event.getPlayer().getName());
        }
    }

    static class AchievementUnlock implements Listener {
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

        private boolean check(String adv) {
            if (adv.contains("root") || adv.contains("recipes")) return false;
            return true;
        }

        @EventHandler
        public void onRegisteredAchievement(PlayerAdvancementDoneEvent event) {
            if (check(event.getAdvancement().getKey().getKey())) {
                String title = find(event.getAdvancement().getKey().getKey());
                String description = findDescription(event.getAdvancement().getKey().getKey());
                SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                TextComponent advancement = new TextComponent(UtilColor.blue + "Advancement> " + UtilColor.yellow + event.getPlayer().getName() + UtilColor.gray + " has advanced through " + UtilColor.yellow + title + UtilColor.gray + ".");
                final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(UtilColor.blue + "[Advancement]\n" + UtilColor.gray +
                                "Name: " + UtilColor.yellow + title + UtilColor.gray + "\n" +
                                "Description: " + UtilColor.yellow + description + UtilColor.gray + "\n\n" +
                                "Unlocked by: " + UtilColor.yellow + event.getPlayer().getName() + UtilColor.gray + "\n" +
                                "Time Unlocked: " + UtilColor.yellow + time.format(timestamp) + " (GMT/BST)").create());
                advancement.setHoverEvent(hoverEvent);
                Bukkit.getOnlinePlayers().forEach(p -> p.spigot().sendMessage(advancement));
            }
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
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
                Bukkit.getConsoleSender().sendMessage("no");
            } else if (args.length == 0 || args.length == 3) {
                sendHelp(sender);
            } else {
                String color;
                if (args.length == 2) color = args[0].toLowerCase() + " " + args[1].toLowerCase();
                else color = args[0].toLowerCase();
                setColor(Objects.requireNonNull(((Player)sender).getPlayer()), color);
            }
            return false;
        }

        public void setColor(Player player, String color) {
            Config config = new Config(player.getUniqueId());
            config.setInt("immortal", switchIntColor(color.toLowerCase()));
            player.sendMessage(UtilColor.blue + "Prefix Color> " + UtilColor.gray + "Your new prefix color is " + switchColor(color) + color);
        }

        public Integer switchIntColor(String color) {
            switch (color) {
                case "yellow": return 1;
                case "aqua": return 2;
                case "purple": return 3;
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
                case "purple": return UtilColor.purple;
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
            else if (args.length != 1) sender.sendMessage(UtilColor.blue + "Taunt> " + UtilColor.gray + "/taunt [player]");
            else {
                Player player = ((Player)sender).getPlayer();
                assert player != null;
                Player args2 = Bukkit.getPlayer(args[0]);
                assert args2 != null;
                if (!args2.isOnline()) sender.sendMessage(UtilColor.blue + "Taunt> " + UtilColor.gray + "/taunt [player]");
                else Bukkit.broadcastMessage(UtilColor.blue + "Taunt> " + UtilColor.yellow + player.getName() + UtilColor.gray + " has blown a kiss at " + UtilColor.yellow + args2.getName() + UtilColor.gray + ".");

            }
            return false;
        }
    }

    class LevelsCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
                Bukkit.getConsoleSender().sendMessage("no");
            } else {
                if (!sender.isOp())
                    return false;
                if (args.length != 2) {
                    sender.sendMessage(UtilColor.blue + "Levels> " + UtilColor.gray + "/setlevel [player] [0-inf..]");
                } else {
                    String name = args[0];
                    int deaths = Integer.parseInt(args[1]);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                    Config config = new Config(player.getUniqueId());
                    config.setInt("deaths", deaths);
                    sender.sendMessage(UtilColor.blue + "Levels> " + UtilColor.gray + "set.");
                }
            }
            return false;
        }
    }

    static class ConsoleSendSayCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) Bukkit.getConsoleSender().sendMessage("no");
            else {
                if (!sender.isOp()) return false;
                Bukkit.broadcastMessage(UtilColor.darkRed + "Ariannna ");
            }
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

    class LoadConfig implements Listener {
        @EventHandler
        public void onJoin(AsyncPlayerPreLoginEvent event) {
            Config config = new Config(event.getUniqueId());
            if (config.getProperty(event.getUniqueId().toString()) != null) return;
            config.setInt("immortal", 0);
            config.setInt("deaths", 0);
        }
    }

    class Chat implements Listener {
        @EventHandler
        public void onChat(AsyncPlayerChatEvent event) {
            event.setCancelled(true);
            String message = event.getMessage();
            Config config = new Config(event.getPlayer().getUniqueId());
            int x = Integer.parseInt(config.getPropertyString("immortal"));
            int level = Integer.parseInt(config.getPropertyString("deaths"));
            if (message.contains("%")) message.replaceAll("%", " "); // We have this to patch the % bug with chat async.
            if (message.startsWith("#")) Bukkit.broadcastMessage(UtilColor.white + UtilColor.bold + "TEAM " + new Levels().switchColor(level) + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message.replaceFirst("#", ""));
            else if (message.startsWith("@")) {
                String newMessage1 = message.replaceFirst("@", "");
                if (newMessage1.contains("&l")) newMessage1.replaceAll("&l", "");
                String newMessage = newMessage1.replace("&", ChatColor.COLOR_CHAR + "");
                Bukkit.broadcastMessage(UtilColor.purple + UtilColor.bold + "PARTY " + new Levels().switchColor(level) + UtilColor.white + UtilColor.bold + event.getPlayer().getName() + " " + UtilColor.purple + newMessage);
            } else if (message.startsWith("!")) {
                String newMessage = message.replace("!", "");
                if (!event.getPlayer().isOp()) Bukkit.broadcastMessage(UtilColor.aqua + UtilColor.bold + "StaffRequest " + UtilColor.darkAqua + UtilColor.bold + event.getPlayer().getName() + " " + UtilColor.aqua + newMessage);
                else Bukkit.broadcastMessage(UtilColor.aqua + UtilColor.bold + "StaffRequest " + UtilColor.gold + UtilColor.bold + "Mod CookieBilly " + UtilColor.aqua + newMessage);
            } else if (event.getPlayer().getName().equalsIgnoreCase("daavide")) Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.darkAqua + UtilColor.bold + "IDIOT " + UtilColor.gray + event.getPlayer().getName() + UtilColor.white + " " + message);
            else if (event.getPlayer().isOp()) Bukkit.broadcastMessage(new Levels().switchColor(level) + UtilColor.darkRed + event.getPlayer().getName() + UtilColor.white + " " + message);
            else {
                System.out.println(x);
                System.out.println(level);
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
            new Config(event.getEntity().getUniqueId()).setInt("deaths", Math.addExact((Integer) new Config(event.getEntity().getUniqueId()).getProperty("deaths"), 1));
        }
    }
}
