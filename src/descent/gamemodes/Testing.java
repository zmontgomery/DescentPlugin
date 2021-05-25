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

public class Testing implements Gamemode {

	private String name;
	private Random rng;

	private List<Location> spawnPoints;

	@Override
	public void start() {
		name = "Testing";
		rng = new Random();
		World world = Bukkit.getWorld("world");
		spawnPoints = new ArrayList<>();

		spawnPoints.add(new Location(world, -93, 38, 14));

		for (Player player : Bukkit.getOnlinePlayers()) {
			Champ champ = new Generic(player);
			int rand = rng.nextInt(spawnPoints.size());
			player.teleport(spawnPoints.get(rand));
			
			champ.champSelect();
		}
	}
	
	public Location respawnLocation(Player player) {
		int rand = rng.nextInt(spawnPoints.size());
		Champ champ = Champ.getChamp(player);
		if(champ instanceof Generic) {
			champ.champSelect();
		}
		return spawnPoints.get(rand);	
	}

	public List<Location> getSpawnPoints() {
		return spawnPoints;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
