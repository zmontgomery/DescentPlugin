package descent.gamemodes;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import descent.Main;
import descent.champions.Champ;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class KingOfTheHill extends Gamemode {

	private String name;

	private Location redSpawn;
	private Location blueSpawn;

	private ScoreboardManager manager;
	private Scoreboard board;
	private Team red;
	private Team blue;

	private final float RESPAWN_TIME = 5.0f;
	
	
	
	public static int controlX = 0;
	public static int controlY = 20;
	public static int controlZ = 100;
	
	public static int blueX;
	public static int blueY;
	public static int blueZ;
	
	public static int redX;
	public static int redY;
	public static int redZ;
	
	public static int blueDoorX1;
	public static int blueDoorY1;
	public static int blueDoorZ1;
	
	public static int blueDoorX2;
	public static int blueDoorY2;
	public static int blueDoorZ2;
	
	public static int redDoorX1;
	public static int redDoorY1;
	public static int redDoorZ1;
	
	public static int redDoorX2;
	public static int redDoorY2;
	public static int redDoorZ2;
	
	public static int blueCapPoints = 0;
	public static int redCapPoints = 0;
	
	public static int capPoints = 0;
	
	public static String cpOwner = "neutral";
	
	

	@Override
	public void start() {
		manager = Bukkit.getScoreboardManager();
		board = manager.getMainScoreboard();
		name = "King of the Hill";
		World world = Bukkit.getWorld("world");
		redSpawn = new Location(world, -55.5, 23, 100.5);
		blueSpawn = new Location(world, 56.5, 23, 100.5);
		
		
		Objective points = board.registerNewObjective("cp", "dummy", "Control Point");
		points.setDisplaySlot(DisplaySlot.SIDEBAR);
			
	    blue = board.registerNewTeam("blue");
		red = board.registerNewTeam("red");
//		Team spec = board.registerNewTeam("spec");
		
		blue.setColor(ChatColor.BLUE);
		blue.setAllowFriendlyFire(false);
		blue.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
		blue.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
		blue.setDisplayName("Blue");
		blue.setCanSeeFriendlyInvisibles(true);
		
		red.setColor(ChatColor.RED);
		red.setAllowFriendlyFire(false);
		red.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
		red.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
		red.setDisplayName("Red");
		red.setCanSeeFriendlyInvisibles(true);
		
//		spec.setColor(ChatColor.WHITE);
//		spec.setAllowFriendlyFire(false);
//		spec.setNameTagVisibility(NameTagVisibility.NEVER);
//		spec.setDisplayName("Spectator");
//		spec.setCanSeeFriendlyInvisibles(true);
		for(Player player : Bukkit.getOnlinePlayers()) {
			Champ champ = Champ.getChamp(player);
			champ.teamSelect();
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
    		@Override
    	    public void run() {
    			
    			int bluePlayerCap = 0;
    			int redPlayerCap = 0;
    			boolean contested = false;
    			
    			Objective points = board.getObjective("cp");
    			
	    		Collection<Entity> entities = world.getNearbyEntities(new Location(world, controlX, controlY, controlZ).add(new Location(world, 0.5, 0, 0.5)), 4, 5, 4);
    			
	    		for(Entity entity : entities) {
	    			
	    			if(entity instanceof Player) {
	    				Player plc = (Player)entity;
	    				
	    				if(plc.getGameMode() == GameMode.SURVIVAL) {
	    					if(cpOwner == "neutral")  {
	    					
	    						if(board.getEntryTeam(plc.getName()).getName() == "blue") {
	    							bluePlayerCap++;
	    						}
	    						if(board.getEntryTeam(plc.getName()).getName() == "red") {
	    							redPlayerCap++;
	    						}
	    				
	    					} else if(cpOwner == "blue") {
	    					
	    						if(board.getEntryTeam(plc.getName()).getName() == "blue") {
	    							bluePlayerCap++;
	    						}
	    						if(board.getEntryTeam(plc.getName()).getName() == "red") {
	    							redPlayerCap++;
	    						}
	    					
	    					} else if(cpOwner == "red") {
	    					
	    						if(board.getEntryTeam(plc.getName()).getName() == "blue") {
	    							bluePlayerCap++;
	    						}
	    						if(board.getEntryTeam(plc.getName()).getName() == "red") {
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
	    					
	    					if(board.getEntryTeam(plc.getName()).getName() == "blue" && (cpOwner == "red" || cpOwner == "neutral") && capPoints > 0 && contested == false)
	    						plc.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Capture Progress: " + capPoints + "/10"));
	    					if(board.getEntryTeam(plc.getName()).getName() == "red" && (cpOwner == "blue" || cpOwner == "neutral") && capPoints < 0 && contested == false)
	    						plc.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Capture Progress: " + Math.abs(capPoints) + "/10"));
	    					if(contested == true)
	    						plc.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Contested!"));
	    					
	    				}
	    			}
	    		}
	    		
	    		board.resetScores(ChatColor.YELLOW + "Reactor: " + cpOwner);
	    		
	    		if(capPoints >= 10) {
	    		
	    			cpOwner = "blue";
	    			capPoints = 0;
	    			Bukkit.broadcastMessage(ChatColor.BLUE + "Blue has taken the Nether Reactor!");
	    			Bukkit.getWorld("world").getBlockAt(new Location(Bukkit.getWorld("world"), controlX, controlY, controlZ)).setType(Material.BLUE_GLAZED_TERRACOTTA);
	    			for(Player pl : Bukkit.getOnlinePlayers()) {
	    				pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
	    			}
	    			
	    		} else if(capPoints <= -10) {
	    			
	    			cpOwner = "red";
	    			capPoints = 0;
	    			Bukkit.broadcastMessage(ChatColor.RED + "Red has taken the Nether Reactor!");
	    			Bukkit.getWorld("world").getBlockAt(new Location(Bukkit.getWorld("world"), controlX, controlY, controlZ)).setType(Material.RED_GLAZED_TERRACOTTA);
	    			for(Player pl : Bukkit.getOnlinePlayers()) {
	    				pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
	    			}
    			}	
	    			
	    		board.resetScores(ChatColor.BLUE + "Blue: " + blueCapPoints);
	    		board.resetScores(ChatColor.RED + "Red: " + redCapPoints);
	    		
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
    				
    				board.resetScores(ChatColor.BLUE + "Blue: " + ControlPointGamemode.blueCapPoints);
    				board.resetScores(ChatColor.RED + "Red: " + ControlPointGamemode.redCapPoints);
    				
    				blueCapPoints = 0;
    				redCapPoints = 0;
    				
    				capPoints = 0;
    				
    				cpOwner = "neutral";
    				
    				Bukkit.broadcastMessage(ChatColor.BLUE + "Blue has maintained control of the reactor!");
    				Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue wins!");
    				
    				Bukkit.getScheduler().cancelTasks(Main.getPlugin(Main.class));
    				
    				
    				for (Player player : Bukkit.getOnlinePlayers()) {
    					
    					player.getInventory().clear();
    					player.setGameMode(GameMode.SURVIVAL);
    					player.teleport(player.getWorld().getSpawnLocation());
    					
    				}

    			} 
    			if(redCapPoints == 100 && capPoints == 0 && cpOwner == "red" && bluePlayerCap == 0) {
    				
    				board.resetScores(ChatColor.BLUE + "Blue: " + ControlPointGamemode.blueCapPoints);
    				board.resetScores(ChatColor.RED + "Red: " + ControlPointGamemode.redCapPoints);
    				
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
    				
    				
    				for (Player player : Bukkit.getOnlinePlayers()) {
    					
    					player.getInventory().clear();
    					player.setGameMode(GameMode.SURVIVAL);
    					player.teleport(player.getWorld().getSpawnLocation());
    					
    				}
    				
    			}
    			
    	    }
    	}, 0L, 20L);

	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public Location respawnLocation(Player player) {
		Location spawnLoc = null;
		if(board.getEntryTeam(player.getName()).getName().equals("blue")){
			spawnLoc = blueSpawn;
		} else if(board.getEntryTeam(player.getName()).getName().equals("red")){
			spawnLoc = redSpawn;
		} else {
			spawnLoc = redSpawn;
		}
		return spawnLoc;
	}

	@Override
	public float getRespawnTime() {
		return RESPAWN_TIME;
	}

	@Override
	public void joinTeam(String team, Player player) {
		if(team.equals("red")) {
			red.addEntry(player.getName());
		} else if(team.equals("blue")) {
			blue.addEntry(player.getName());
		}
		return;
	}
	
	@Override
	public void stop() {
		return;
	}

}
