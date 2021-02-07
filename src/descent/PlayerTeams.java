package descent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

@SuppressWarnings("deprecation")
public class PlayerTeams {
	
	public static void registerTeams() {
		
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getMainScoreboard();
		
		Objective points = b.registerNewObjective("capturepoints", "dummy");
		points.setDisplaySlot(DisplaySlot.SIDEBAR);
			
	    Team blue = b.registerNewTeam("blue");
		Team red = b.registerNewTeam("red");
		Team spec = b.registerNewTeam("spec");
		
		blue.setColor(ChatColor.BLUE);
		blue.setAllowFriendlyFire(false);
		blue.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		blue.setDisplayName("Blue");
		blue.setCanSeeFriendlyInvisibles(true);
		
		red.setColor(ChatColor.RED);
		red.setAllowFriendlyFire(false);
		red.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		red.setDisplayName("Red");
		red.setCanSeeFriendlyInvisibles(true);
		
		spec.setColor(ChatColor.WHITE);
		spec.setAllowFriendlyFire(false);
		spec.setNameTagVisibility(NameTagVisibility.NEVER);
		spec.setDisplayName("Spectator");
		spec.setCanSeeFriendlyInvisibles(true);
		
	}
	public static void unregisterTeams() {
		
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getMainScoreboard();
		
		if(b.getTeam("blue") != null)
			b.getTeam("blue").unregister();
		if(b.getTeam("red") != null)
			b.getTeam("red").unregister();
		if(b.getTeam("spec") != null)
			b.getTeam("spec").unregister();
		
		b.resetScores(ChatColor.BLUE + "Blue: " + ControlPointGamemode.blueCapPoints);
		b.resetScores(ChatColor.RED + "Red: " + ControlPointGamemode.redCapPoints);
		b.getObjective("capturepoints").unregister();
		
	}
	public static void addToTeam(Player player, String team) {
		
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getMainScoreboard();
		
		if(team.equals("blue") || team.equals("red") || team.equals("spec")) {
			
			b.getTeam(team).addEntry(player.getName());
			player.sendMessage("You successfully joined team " + team + ".");
			
			if(team.equals("blue"))
				player.teleport(new Location(player.getWorld(), Main.blueX, Main.blueY, Main.blueZ).add(new Location(player.getWorld(), 0.5, 0, 0.5)));
			if(team.equals("red"))
				player.teleport(new Location(player.getWorld(), Main.redX, Main.redY, Main.redZ).add(new Location(player.getWorld(), 0.5, 0, 0.5)));
					
		} else {
			
			player.sendMessage("Please enter a valid team.");
			
		}
	}
	public static void closeSpawnDoors() {
		
		World world = Bukkit.getWorld("world");
		
		for(int x = Main.blueDoorX1; x <= Main.blueDoorX2; x++) {
			for(int y = Main.blueDoorY1; y <= Main.blueDoorY2; y++) {
				for(int z = Main.blueDoorZ1; z <= Main.blueDoorZ2; z++) {
					if(world.getBlockAt(x,y,z).getType() == Material.AIR) {
						world.getBlockAt(x, y, z).setType(Material.GLASS);
					}
				}
			}
		}
		for(int x = Main.redDoorX1; x <= Main.redDoorX2; x++) {
			for(int y = Main.redDoorY1; y <= Main.redDoorY2; y++) {
				for(int z = Main.redDoorZ1; z <= Main.redDoorZ2; z++) {
					if(world.getBlockAt(x,y,z).getType() == Material.AIR) {
						world.getBlockAt(x, y, z).setType(Material.GLASS);
					}
				}
			}
		}
		
	}
	public static void openSpawnDoors() {
		
		World world = Bukkit.getWorld("world");
		
		for(int x = Main.blueDoorX1; x <= Main.blueDoorX2; x++) {
			for(int y = Main.blueDoorY1; y <= Main.blueDoorY2; y++) {
				for(int z = Main.blueDoorZ1; z <= Main.blueDoorZ2; z++) {
					if(world.getBlockAt(x,y,z).getType() == Material.GLASS) {
						world.getBlockAt(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
		for(int x = Main.redDoorX1; x <= Main.redDoorX2; x++) {
			for(int y = Main.redDoorY1; y <= Main.redDoorY2; y++) {
				for(int z = Main.redDoorZ1; z <= Main.redDoorZ2; z++) {
					if(world.getBlockAt(x,y,z).getType() == Material.GLASS) {
						world.getBlockAt(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
		
	}
	
}
