package descent.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import descent.champions.Champ;
import descent.champions.Generic;

public class Deathmatch implements Gamemode {

	private String name;
	private Random rng;
	
	private ScoreboardManager manager;
	private Scoreboard board;

	private List<Location> spawnPoints;
	
	private final float RESPAWN_TIME = 0.5f;;

	@Override
	public void start() {
		
		manager = Bukkit.getScoreboardManager();
		board = manager.getMainScoreboard();
		
		for(Team team : board.getTeams()) {
			team.unregister();
		}
		
		name = "Deathmatch";
		rng = new Random();
		World world = Bukkit.getWorld("world");
		spawnPoints = new ArrayList<>();

		spawnPoints.add(new Location(world, 5, 21, 106));
		spawnPoints.add(new Location(world, 13, 21, 87));
		spawnPoints.add(new Location(world, 24, 23, 120));
		spawnPoints.add(new Location(world, 0, 24, 114));
		spawnPoints.add(new Location(world, -15, 28, 117));
		spawnPoints.add(new Location(world, 36, 24, 101));
		spawnPoints.add(new Location(world, -20, 24, 85));
		spawnPoints.add(new Location(world, 4, 28, 82));

		for (Player player : Bukkit.getOnlinePlayers()) {
			Champ champ = new Generic(player);
			int rand = rng.nextInt(spawnPoints.size());
			player.teleport(spawnPoints.get(rand));
			
			boolean notFound = true;
			for(Team team : board.getTeams()) {
				if(team.getName().equals(player.getName())) {
					notFound = false;
				}
			}
			if(notFound) {
				Team team = board.registerNewTeam(player.getName());
				team.setColor(ChatColor.RED);
				team.setAllowFriendlyFire(false);
				team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
				team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
				team.setDisplayName(player.getName());
				team.setCanSeeFriendlyInvisibles(true);
				team.addEntry(player.getName());
			}
			champ.champSelect();
		}
	}
	
	public Location respawnLocation(Player player) {
		int rand = rng.nextInt(spawnPoints.size());
		return spawnPoints.get(rand);	
	}

	public List<Location> getSpawnPoints() {
		return spawnPoints;
	}

	@Override
	public float getRespawnTime() {
		return RESPAWN_TIME;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public void joinTeam(String team, Player player) {
		return;
	}

	@Override
	public void stop() {
		return;
	}

}
