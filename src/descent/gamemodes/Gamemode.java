package descent.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import descent.Main;

public abstract class Gamemode {
	public static final float DELAY_TIME = 2.0f;
	
	protected void preInit() {
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
		Bukkit.getScheduler().cancelTasks(Main.getPlugin(Main.class));
	}
	public void initialize() {
		preInit();
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
			start();
		}, (long)(20 * DELAY_TIME));
		
	}
	public abstract void joinTeam(String team, Player player);
	public abstract void start();
	public abstract void stop();
	public abstract Location respawnLocation(Player player);
	public abstract float getRespawnTime();
}
