package descentmodel;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ControlPointGamemode {
	
	public static int blueCapPoints = 0;
	public static int redCapPoints = 0;
	
	public static int capPoints = 0;
	
	public static String cpOwner = "neutral";
	
	public static void startControl(Player pl) {
		
		healthPackSpawning(pl.getWorld(), 0.5, 21, 76.5);
		healthPackSpawning(pl.getWorld(), 0.5, 29, 124.5);
		healthPackSpawning(pl.getWorld(), 31.5, 24, 104.5);
		healthPackSpawning(pl.getWorld(), -30.5, 24, 104.5);
		
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
    		@Override
    	    public void run() {
    			
    			int bluePlayerCap = 0;
    			int redPlayerCap = 0;
    			boolean contested = false;
    			
    			ScoreboardManager m = Bukkit.getScoreboardManager();
    			Scoreboard b = m.getMainScoreboard();
    			
    			Objective points = b.getObjective("capturepoints");
    			
	    		Collection<Entity> entities = pl.getWorld().getNearbyEntities(new Location(pl.getWorld(), Main.controlX, Main.controlY, Main.controlZ).add(new Location(pl.getWorld(), 0.5, 0, 0.5)), 4, 5, 4);
    			
	    		for(Entity entity : entities) {
	    			
	    			if(entity instanceof Player) {
	    				
	    				Player plc = (Player)entity;
	    				
	    				if(plc.getGameMode() == GameMode.SURVIVAL) {
	    					if(cpOwner == "neutral")  {
	    					
	    						if(b.getEntryTeam(plc.getName()).getName() == "blue") {
	    							bluePlayerCap++;
	    						}
	    						if(b.getEntryTeam(plc.getName()).getName() == "red") {
	    							redPlayerCap++;
	    						}
	    				
	    					} else if(cpOwner == "blue") {
	    					
	    						if(b.getEntryTeam(plc.getName()).getName() == "blue") {
	    							bluePlayerCap++;
	    						}
	    						if(b.getEntryTeam(plc.getName()).getName() == "red") {
	    							redPlayerCap++;
	    						}
	    					
	    					} else if(cpOwner == "red") {
	    					
	    						if(b.getEntryTeam(plc.getName()).getName() == "blue") {
	    							bluePlayerCap++;
	    						}
	    						if(b.getEntryTeam(plc.getName()).getName() == "red") {
	    							redPlayerCap++;
	    						}	
	    					}
	    				}
	    			}
	    		}
	    		
	    		if(bluePlayerCap > 0 && redPlayerCap == 0 && (cpOwner == "red" || cpOwner == "neutral")) {
	    			
	    			capPoints += bluePlayerCap;
	    			
	    		} else if(redPlayerCap > 0 && bluePlayerCap == 0 && (cpOwner == "blue" || cpOwner == "neutral")) {
	    			
	    			capPoints -= redPlayerCap;
	    			
	    		} else if(bluePlayerCap > 0 && redPlayerCap > 0) {
	    			
	    			//Contested (Do nothing)
	    			contested = true;
	    			
	    		} else {
	    		
	    			if(capPoints > 0)
	    				capPoints--;
	    			if(capPoints < 0)
	    				capPoints++;
	    			
    			}
	    		//Sends action bar messages about the capture progress.
	    		for(Entity entity : entities) {
	    			
	    			if(entity instanceof Player) {
	    				
	    				Player plc = (Player)entity;
	    				
	    				if(plc.getGameMode() == GameMode.SURVIVAL) {
	    					
	    					if(b.getEntryTeam(plc.getName()).getName() == "blue" && (cpOwner == "red" || cpOwner == "neutral") && capPoints > 0 && contested == false)
	    						plc.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Capture Progress: " + capPoints + "/10", ChatColor.YELLOW));
	    					if(b.getEntryTeam(plc.getName()).getName() == "red" && (cpOwner == "blue" || cpOwner == "neutral") && capPoints < 0 && contested == false)
	    						plc.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Capture Progress: " + Math.abs(capPoints) + "/10", ChatColor.YELLOW));
	    					if(contested == true)
	    						plc.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Contested!", ChatColor.YELLOW));
	    					
	    				}
	    			}
	    		}
	    		
	    		b.resetScores(ChatColor.YELLOW + "Reactor: " + cpOwner);
	    		
	    		if(capPoints >= 10) {
	    		
	    			cpOwner = "blue";
	    			capPoints = 0;
	    			Bukkit.broadcastMessage(ChatColor.BLUE + "Blue has taken the Nether Reactor!");
	    			Bukkit.getWorld("world").getBlockAt(new Location(Bukkit.getWorld("world"), Main.controlX, Main.controlY, Main.controlZ)).setType(Material.BLUE_GLAZED_TERRACOTTA);
	    			for(Player pl : Bukkit.getOnlinePlayers()) {
	    				pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
	    			}
	    			
	    		} else if(capPoints <= -10) {
	    			
	    			cpOwner = "red";
	    			capPoints = 0;
	    			Bukkit.broadcastMessage(ChatColor.RED + "Red has taken the Nether Reactor!");
	    			Bukkit.getWorld("world").getBlockAt(new Location(Bukkit.getWorld("world"), Main.controlX, Main.controlY, Main.controlZ)).setType(Material.RED_GLAZED_TERRACOTTA);
	    			for(Player pl : Bukkit.getOnlinePlayers()) {
	    				pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
	    			}
    			}	
	    			
	    		b.resetScores(ChatColor.BLUE + "Blue: " + blueCapPoints);
	    		b.resetScores(ChatColor.RED + "Red: " + redCapPoints);
	    		
	    		if(cpOwner == "blue" && blueCapPoints < 100) {
	    			
	    			blueCapPoints++;
	    			
	    		} else if(cpOwner == "red" && redCapPoints < 100) {
	    			
	    			redCapPoints++;
	    			
	    		}
	    		
	    		Score pscore = points.getScore(ChatColor.YELLOW + "Reactor: " + cpOwner);
    			Score bscore = points.getScore(ChatColor.BLUE + "Blue: " + blueCapPoints);
    			Score rscore = points.getScore(ChatColor.RED + "Red: " + redCapPoints);
    			
    			pscore.setScore(3);
    			bscore.setScore(2);
    			rscore.setScore(1);
    			
    			if(blueCapPoints == 100 && capPoints == 0 && cpOwner == "blue" && redPlayerCap == 0) {
    				
    				b.resetScores(ChatColor.BLUE + "Blue: " + ControlPointGamemode.blueCapPoints);
    				b.resetScores(ChatColor.RED + "Red: " + ControlPointGamemode.redCapPoints);
    				
    				blueCapPoints = 0;
    				redCapPoints = 0;
    				
    				capPoints = 0;
    				
    				cpOwner = "neutral";
    				
    				Bukkit.broadcastMessage(ChatColor.BLUE + "Blue has maintained control of the reactor!");
    				Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue wins!");
    				
    				Bukkit.getScheduler().cancelTasks(Main.getPlugin(Main.class));
    				
    				PlayerTeams.unregisterTeams();
    				PlayerTeams.registerTeams();
    				
    				PlayerTeams.closeSpawnDoors();
    				
    				for (Player player : Bukkit.getOnlinePlayers()) {
    					
    					player.getInventory().clear();
    					player.setGameMode(GameMode.SURVIVAL);
    					player.teleport(player.getWorld().getSpawnLocation());
    					
    				}

    			} 
    			if(redCapPoints == 100 && capPoints == 0 && cpOwner == "red" && bluePlayerCap == 0) {
    				
    				b.resetScores(ChatColor.BLUE + "Blue: " + ControlPointGamemode.blueCapPoints);
    				b.resetScores(ChatColor.RED + "Red: " + ControlPointGamemode.redCapPoints);
    				
    				blueCapPoints = 0;
    				redCapPoints = 0;
    				
    				capPoints = 0;
    				
    				cpOwner = "neutral";
    				
    				Bukkit.broadcastMessage(ChatColor.RED + "Red has maintained control of the reactor!");
    				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red wins!");
    				
    				Bukkit.getScheduler().cancelTasks(Main.getPlugin(Main.class));
    				
    				for(BukkitTask task : Bukkit.getScheduler().getPendingTasks()) {
    					task.cancel();
    				}
    				
    				PlayerTeams.unregisterTeams();
    				PlayerTeams.registerTeams();
    				
    				PlayerTeams.closeSpawnDoors();
    				
    				for (Player player : Bukkit.getOnlinePlayers()) {
    					
    					player.getInventory().clear();
    					player.setGameMode(GameMode.SURVIVAL);
    					player.teleport(player.getWorld().getSpawnLocation());
    					
    				}
    				
    			}
    			
    	    }
    	}, 0L, 20L);
		
	}
	public static void healthPackSpawning(World w, double x, double y, double z) {
		
		new BukkitRunnable() {
        	public void run() {
        		
        		for(Entity ent : w.getNearbyEntities(new Location(w, x, y, z), 1, 1, 1))
        			if(ent instanceof Item)
        				ent.remove();

        		Item i = w.dropItem(new Location(w, x, y, z), new ItemStack(Material.DIAMOND));
        		
        		if(x == 0.5 && z == 76.5)
        			i.setCustomName("bot");
        		if(x == 0.5 && z == 124.5)
        			i.setCustomName("top");
        		if(x == 31.5 && z == 104.5)
        			i.setCustomName("blue");
        		if(x == -30.5 && z == 104.5)
        			i.setCustomName("red");
        		
        		i.setVelocity(new Vector());
        		i.setCustomNameVisible(true);
        		w.spawnParticle(Particle.CRIT_MAGIC, x, y, z, 15, 1, 1, 1, 0.1);
        		w.playSound(new Location(w, x, y, z), Sound.ENTITY_GHAST_SCREAM, 1f, 1f);
        		
        	}
		}.runTaskLater(Main.getPlugin(Main.class), 200);
	}
}
