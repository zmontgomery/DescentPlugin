package descent.gamemodes;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Default implements Gamemode{
	
	private String name;
	private final float RESPAWN_TIME = 1.0f;

	@Override
	public void start() {
		name = "Default";
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public Location respawnLocation(Player player) {
		return player.getWorld().getSpawnLocation();
	}

	@Override
	public float getRespawnTime() {
		return RESPAWN_TIME;
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
