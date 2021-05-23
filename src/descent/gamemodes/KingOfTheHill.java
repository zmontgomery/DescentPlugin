package descent.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import descent.champions.Champ;
import descent.champions.Generic;

public class KingOfTheHill implements Gamemode{

	private String name;
	private Random rng;
	
	private List<Location> spawnPoints;
	
	@Override
	public void start() {
		name = "King of the Hill";
		rng = new Random();
		World world = Bukkit.getWorld("world");
		spawnPoints = new ArrayList<>();
		
		spawnPoints.add(new Location(world, 5, 21, 106));
		spawnPoints.add(new Location(world, 13, 21, 87));
		spawnPoints.add(new Location(world, 24, 24, 120));
		spawnPoints.add(new Location(world, 0, 24, 114));
		spawnPoints.add(new Location(world, -15, 28, 117));
		spawnPoints.add(new Location(world, 36, 24, 101));
		spawnPoints.add(new Location(world, -20, 25, 85));
		spawnPoints.add(new Location(world, 4, 28, 82));
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			Champ champ = Champ.getChamp(player);
			if(!(champ instanceof Generic)) {
				int rand = rng.nextInt(spawnPoints.size());
				player.teleport(spawnPoints.get(rand));
			}
		}
	}
	
	public List<Location> getSpawnPoints(){
		return spawnPoints;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public Location respawnLocation(Player player) {
		Champ champ = Champ.getChamp(player);
		if(champ instanceof Generic) {
			champ.champSelect();
		}
		int rand = rng.nextInt(spawnPoints.size());
		return spawnPoints.get(rand);
	}
	
}
