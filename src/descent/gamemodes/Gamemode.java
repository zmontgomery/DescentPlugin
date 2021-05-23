package descent.gamemodes;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Gamemode {
	void start();
	List<Location> getSpawnPoints();
	void respawn(Player player);
}
