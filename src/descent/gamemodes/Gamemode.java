package descent.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public abstract class Gamemode {
	private void preInit() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.teleport(player.getWorld().getSpawnLocation().add(new Location(player.getWorld(), 0.5, 0, 0.5)));
		}
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		for(Team team : board.getTeams()) {
			team.unregister();
		}
		for(Objective objective : board.getObjectives()) {
			objective.unregister();
		}
		
	}
	public void initialize() {
		preInit();
		start();
	}
	public abstract void joinTeam(String team, Player player);
	public abstract void start();
	public abstract void stop();
	public abstract Location respawnLocation(Player player);
	public abstract float getRespawnTime();
}
