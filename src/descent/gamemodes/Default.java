package descent.gamemodes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Default implements Gamemode{
	
	private String name;

	@Override
	public void start() {
		name = "Default";
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public List<Location> getSpawnPoints() {
		return new ArrayList<>();
	}
	
	@Override
	public Location respawnLocation(Player player) {
		return null;
	}

}
