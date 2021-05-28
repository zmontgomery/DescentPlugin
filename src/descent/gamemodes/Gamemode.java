package descent.gamemodes;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Gamemode {
	void joinTeam(String team, Player player);
	void start();
	void stop();
	Location respawnLocation(Player player);
	float getRespawnTime();
}
